package top.mrxiaom.sweet.warehouse.database;

import top.mrxiaom.pluginbase.database.IDatabase;
import top.mrxiaom.sweet.warehouse.SweetWarehouse;
import top.mrxiaom.sweet.warehouse.func.AbstractPluginHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsDatabase extends AbstractPluginHolder implements IDatabase {
    public static class Item {
        public final String item;
        public final int amount;

        public Item(String item, int amount) {
            this.item = item;
            this.amount = amount;
        }
    }
    private String TABLE_NAME;
    public ItemsDatabase(SweetWarehouse plugin) {
        super(plugin);
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
