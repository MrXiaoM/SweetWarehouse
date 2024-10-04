package top.mrxiaom.sweet.warehouse;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;
import top.mrxiaom.sweet.warehouse.database.ItemsDatabase;

public class SweetWarehouse extends BukkitPlugin {
    public static SweetWarehouse getInstance() {
        return (SweetWarehouse) BukkitPlugin.getInstance();
    }

    public SweetWarehouse() {
        super(options()
                .bungee(true)
                .database(true)
                .reconnectDatabaseWhenReloadConfig(true)
                .vaultEconomy(true)
                .scanIgnore("top.mrxiaom.sweet.warehouse.libs")
        );
    }

    private ItemsDatabase itemsDatabase;
    @NotNull
    public EconomyHolder getEconomy() {
        return options.economy();
    }

    public ItemsDatabase getItemsDatabase() {
        return itemsDatabase;
    }

    public boolean isSQLite() {
        return options.database().isSQLite();
    }

    @Override
    protected void beforeEnable() {
        options.registerDatabase(itemsDatabase = new ItemsDatabase(this));
    }

    @Override
    protected void afterEnable() {
        getLogger().info(getDescription().getName() + " 加载完毕");
    }
}
