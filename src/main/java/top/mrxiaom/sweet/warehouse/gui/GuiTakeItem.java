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
import top.mrxiaom.sweet.warehouse.database.ItemsDatabase;

public class GuiTakeItem implements IGui {
    final SweetWarehouse plugin;
    final Player player;
    final ItemsDatabase.Item itemInfo;
    public GuiTakeItem(SweetWarehouse plugin, Player player, ItemsDatabase.Item itemInfo) {
        this.plugin = plugin;
        this.player = player;
        this.itemInfo = itemInfo;
    }
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory newInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, "取出物品");
        // TODO: 设计界面
        return inv;
    }

    @Override
    public void onClick(InventoryAction action, ClickType click, InventoryType.SlotType type, int slot, ItemStack current, ItemStack cursor, InventoryView view, InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
