package top.mrxiaom.sweet.warehouse.func;

import top.mrxiaom.sweet.warehouse.SweetWarehouse;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<SweetWarehouse> {
    public AbstractPluginHolder(SweetWarehouse plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(SweetWarehouse plugin, boolean register) {
        super(plugin, register);
    }
}
