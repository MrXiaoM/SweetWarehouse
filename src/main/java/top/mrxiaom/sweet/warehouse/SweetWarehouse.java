package top.mrxiaom.sweet.warehouse;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;

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

    @NotNull
    public EconomyHolder getEconomy() {
        return options.economy();
    }

    @Override
    protected void beforeEnable() {
        options.registerDatabase(
                // TODO: 在这里添加数据库
        );
    }

    @Override
    protected void afterEnable() {
        getLogger().info(getDescription().getName() + " 加载完毕");
    }
}
