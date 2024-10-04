package top.mrxiaom.sweet.warehouse.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pluginbase.gui.IGui;
import top.mrxiaom.sweet.warehouse.SweetWarehouse;

public class GuiPutItem implements IGui {
    final SweetWarehouse plugin;
    final Player player;
    public GuiPutItem(SweetWarehouse plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory newInventory() {
        return Bukkit.createInventory(null, 54, "在这里放入物品，关闭界面存入仓库");
    }

    @Override
    public void onClick(InventoryAction action, ClickType click, InventoryType.SlotType type, int slot, ItemStack current, ItemStack cursor, InventoryView view, InventoryClickEvent event) {
        // do nothing
    }

    @Override
    public void onClose(InventoryView view) {
        // TODO: 将物品存入仓库
        for (ItemStack item : view.getTopInventory().getContents()) {

        }
    }
}
