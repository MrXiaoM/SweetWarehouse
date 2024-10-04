package top.mrxiaom.sweet.warehouse;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;
import top.mrxiaom.sweet.warehouse.database.ItemsDatabase;
import top.mrxiaom.sweet.warehouse.mythic.IMythic;
import top.mrxiaom.sweet.warehouse.mythic.Mythic4;
import top.mrxiaom.sweet.warehouse.mythic.Mythic5;
import top.mrxiaom.sweet.warehouse.mythic.MythicUnknown;

public class SweetWarehouse extends BukkitPlugin {
    public static SweetWarehouse getInstance() {
        return (SweetWarehouse) BukkitPlugin.getInstance();
    }

    public SweetWarehouse() {
        super(options()
                .bungee(true)
                .database(true)
                .reconnectDatabaseWhenReloadConfig(false)
                .vaultEconomy(true)
                .scanIgnore("top.mrxiaom.sweet.warehouse.libs")
        );
    }

    private IMythic mythic;
    private ItemsDatabase itemsDatabase;
    @NotNull
    public EconomyHolder getEconomy() {
        return options.economy();
    }

    public ItemsDatabase getItemsDatabase() {
        return itemsDatabase;
    }

    public IMythic getMythic() {
        return mythic;
    }

    public boolean isSQLite() {
        return options.database().isSQLite();
    }

    @Override
    protected void beforeEnable() {
        options.registerDatabase(itemsDatabase = new ItemsDatabase(this));
        Plugin mythicPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
        String mythicVersion = mythicPlugin == null ? "Unknown" : mythicPlugin.getDescription().getVersion();
        if (mythicVersion.startsWith("5.")) {
            mythic = new Mythic5();
        } else if (mythicVersion.startsWith("4.")) {
            mythic = new Mythic4();
        } else {
            mythic = new MythicUnknown();
        }
    }

    @Override
    protected void afterEnable() {
        getLogger().info(getDescription().getName() + " 加载完毕");
    }

    public void reloadDatabase() {
        options.database().reloadConfig();
        options.database().reconnect();
    }
}
