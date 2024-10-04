package top.mrxiaom.sweet.warehouse.database;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.mrxiaom.pluginbase.database.IDatabase;
import top.mrxiaom.pluginbase.utils.Bytes;
import top.mrxiaom.sweet.warehouse.SweetWarehouse;
import top.mrxiaom.sweet.warehouse.func.AbstractPluginHolder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemsDatabase extends AbstractPluginHolder implements IDatabase, Listener {
    public static class Item {
        public final String item;
        public final int amount;

        public Item(String item, int amount) {
            this.item = item;
            this.amount = amount;
        }
    }
    private String TABLE_NAME;
    private final Map<String, List<Item>> cache = new ConcurrentHashMap<>();
    public ItemsDatabase(SweetWarehouse plugin) {
        super(plugin, true);
        registerEvents();
        registerBungee();
    }

    public void broadcastRemoveCache(String player) {
        List<Player> players = Lists.newArrayList(Bukkit.getOnlinePlayers());
        Player p = players.isEmpty() ? null : players.get(0);
        if (p != null) {
            ByteArrayDataOutput output = Bytes.newDataOutput();
            output.writeUTF("Forward");
            output.writeUTF("ALL");
            output.writeUTF("SweetWarehouse");
            try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(bytes)) {
                out.writeInt(0);
                out.writeUTF(player.toLowerCase());
                output.writeShort(bytes.toByteArray().length);
                output.write(bytes.toByteArray());
            } catch (IOException e) {
                warn(e);
                return;
            }
            p.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
        }
    }

    @Override
    public void receiveBungee(String subChannel, DataInputStream in) throws IOException {
        if (subChannel.equals("SweetWarehouse")) {
            int type = in.readInt();
            if (type == 0) {
                cache.remove(in.readUTF());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        cache.remove(e.getName().toLowerCase());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        onPlayerLeave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        onPlayerLeave(e.getPlayer());
    }

    private void onPlayerLeave(Player player) {
        cache.remove(player.getName().toLowerCase());
    }

    @Override
    public void reload(Connection connection, String s) {
        TABLE_NAME = "`" + (s + "items").toUpperCase() + "`";
        try (Connection conn = connection;
            PreparedStatement ps = conn.prepareStatement(
                    "CREATE TABLE if NOT EXISTS " + TABLE_NAME + "(" +
                            "`player` VARCHAR(48)," +
                            "`item` LONGTEXT," +
                            "`amount` BIGINT," +
                            "PRIMARY KEY (`player`, `item`)" +
                        ");"
            )
        ) {
            ps.execute();
        } catch (SQLException e) {
            warn(e);
        }
    }

    public List<Item> getItems(String player) {
        List<Item> cacheItems = cache.get(player.toLowerCase());
        if (cacheItems != null) return cacheItems;
        try (Connection conn = plugin.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM " + TABLE_NAME + " WHERE `player`=?"
             )
        ) {
            ps.setString(1, player);
            try (ResultSet results = ps.executeQuery()) {
                List<Item> items = new ArrayList<>();
                while (results.next()) {
                    String item = results.getString("item");
                    int amount = results.getInt("amount");
                    items.add(new Item(item, amount));
                }
                return items;
            }
        } catch (SQLException e) {
            warn(e);
            return new ArrayList<>();
        }
    }

    public void putItem(String player, String item, int amount) {
        boolean sqLite = plugin.isSQLite();
        try (Connection conn = plugin.getConnection();
             PreparedStatement psGet = conn.prepareStatement(
                    "SELECT * FROM " + TABLE_NAME + " WHERE `player`=? AND `item`=?"
             );
             PreparedStatement psPut = conn.prepareStatement(sqLite
                     ? ("INSERT OR REPLACE INTO " + TABLE_NAME + "(`player`,`item`,`amount`) VALUES(?, ?, ?)")
                     : ("INSERT INTO " + TABLE_NAME + "(`player`,`item`,`amount`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `amount`=?"))
        ) {
            broadcastRemoveCache(player);
            psGet.setString(1, player);
            psGet.setString(2, item);
            ResultSet resultGet = psGet.executeQuery();
            int old = Math.max(0, resultGet.next() ? resultGet.getInt("amount") : 0);
            int newAmount = old + amount;
            psPut.setString(1, player);
            psPut.setString(2, item);
            psPut.setInt(3, newAmount);
            if (!sqLite) psPut.setInt(4, newAmount);
            psPut.execute();
        } catch (SQLException e) {
            warn(e);
        }
    }

    public boolean takeItem(String player, String item, int amount) {
        boolean sqLite = plugin.isSQLite();
        try (Connection conn = plugin.getConnection();
             PreparedStatement psGet = conn.prepareStatement(
                     "SELECT * FROM " + TABLE_NAME + " WHERE `player`=? AND `item`=?"
             );
        ) {
            broadcastRemoveCache(player);
            psGet.setString(1, player);
            psGet.setString(2, item);
            ResultSet resultGet = psGet.executeQuery();
            int old = Math.max(0, resultGet.next() ? resultGet.getInt("amount") : 0);
            int newAmount = old - amount;
            if (newAmount < 0) return false;
            try (PreparedStatement psPut = conn.prepareStatement(sqLite
                    ? ("INSERT OR REPLACE INTO " + TABLE_NAME + "(`player`,`item`,`amount`) VALUES(?, ?, ?)")
                    : ("INSERT INTO " + TABLE_NAME + "(`player`,`item`,`amount`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `amount`=?"))
            ) {
                psPut.setString(1, player);
                psPut.setString(2, item);
                psPut.setInt(3, newAmount);
                if (!sqLite) psPut.setInt(4, newAmount);
                psPut.execute();
            }
            return true;
        } catch (SQLException e) {
            warn(e);
            return false;
        }
    }
}
