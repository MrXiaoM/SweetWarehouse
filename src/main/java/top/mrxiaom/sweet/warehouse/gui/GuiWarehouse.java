package top.mrxiaom.sweet.warehouse.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pluginbase.gui.IGui;
import top.mrxiaom.pluginbase.utils.ItemStackUtil;
import top.mrxiaom.sweet.warehouse.SweetWarehouse;
import top.mrxiaom.sweet.warehouse.database.ItemsDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class GuiWarehouse implements IGui {
    final SweetWarehouse plugin;
    final Player player;
    int page = 1;
    int maxPage;
    Map<Integer, ItemsDatabase.Item> itemMap = new HashMap<>();
    public GuiWarehouse(SweetWarehouse plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory newInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, "仓库物品列表");
        refresh(inv);

        ItemStack frame = ItemStackUtil.buildFrameItem(Material.BLACK_STAINED_GLASS_PANE);
        ItemStackUtil.setRowItems(inv, 6, frame);
        ItemStack prev = ItemStackUtil.buildItem(Material.GRAY_STAINED_GLASS_PANE, "&e&l上一页");
        ItemStack next = ItemStackUtil.buildItem(Material.GRAY_STAINED_GLASS_PANE, "&e&l下一页");
        ItemStack putItems = ItemStackUtil.buildItem(Material.HOPPER, "&e&l放入物品");
        inv.setItem(49, putItems);
        if (page > 1) inv.setItem(45, prev);
        if (page < maxPage) inv.setItem(53, next);
        return inv;
    }

    private void refresh(Inventory inv) {
        refresh(inv::setItem);
    }

    private void refresh(InventoryView view) {
        refresh(view::setItem);
        player.updateInventory();
    }

    private void refresh(BiConsumer<Integer, ItemStack> setItem) {
        for (int i = 0; i < 45; i++) {
            setItem.accept(i, null);
        }
        itemMap.clear();
        List<ItemsDatabase.Item> items = plugin.getItemsDatabase().getItems(player);
        maxPage = (int) Math.ceil(items.size() / 45.0);
        int startIndex = (page - 1) * 45;
        for (int i = 0, j = 0; i < items.size(); i++) {
            if (i < startIndex) continue;
            ItemsDatabase.Item itemInfo = items.get(j);
            ItemStack item = itemInfo.generateItem();
            if (item == null) continue;
            itemMap.put(j, itemInfo);
            int stackSize = item.getMaxStackSize();
            int displayAmount = (int) Math.min(stackSize, Math.max(1, Math.ceil(itemInfo.amount / (float)stackSize)));
            item.setAmount(displayAmount);
            List<String> lore = ItemStackUtil.getItemLore(item);
            lore.add("");
            lore.add("&a左键 &7| &f存入背包中所有此类物品 &7(" + itemInfo.getAmountFromPlayerInventory(player) + ")");
            if (itemInfo.isPlayerUnlocked(player)) {
                lore.add("&b右键 &7| &f取出该物品 &7(自定义数量)");
                if (stackSize > 1 && itemInfo.amount >= stackSize)
                    lore.add("&b右键 &7| &f取出一组该物品 &7(" + stackSize + ")");
                int chestSize = stackSize * 27;
                if (itemInfo.amount <= chestSize) lore.add("&eShift+左键 &7| &f取出全部该物品");
                else lore.add("&eShift+左键 &7| &f取出一小箱子该物品 &7(27*" + stackSize + " = " + chestSize + ")");
            } else {
                lore.add("&b右键 &7| &f解锁取出物品权限");
            }
            ItemStackUtil.setItemLore(item, lore);
            setItem.accept(j, item);
            if (++j >= 45) break;
        }
    }

    @Override
    public void onClick(InventoryAction action, ClickType click, InventoryType.SlotType type, int slot, ItemStack current, ItemStack cursor, InventoryView view, InventoryClickEvent event) {
        event.setCancelled(true);
        if (slot >= 45) {
            // 上一页
            if (slot == 45 && page > 1) {
                page--;
                refresh(view);
                return;
            }
            // 下一页
            if (slot == 53 && page < maxPage) {
                page++;
                refresh(view);
                return;
            }
            // 存入物品
            if (slot == 49) {
                new GuiPutItem(plugin, player).open();
                return;
            }
            return;
        }
        ItemsDatabase.Item itemInfo = itemMap.get(slot);
        if (itemInfo != null) {
            // TODO: 点击操作
            if (!click.isShiftClick()) {
                // TODO: 存入背包中所有此类物品
                if (click.isLeftClick()) {

                }
                // TODO: 取出自定义数量物品 或 解锁这个格子
                if (click.isRightClick()) {
                    if (!itemInfo.isPlayerUnlocked(player)) {

                    } else {

                    }
                }
            } else {
                // TODO: 取出一组物品 (如果够)
                if (click.isRightClick() && itemInfo.isPlayerUnlocked(player)) {

                }
                // TODO: 取出所有物品 或 取出一箱子物品 (如果够)
                if (click.isRightClick() && itemInfo.isPlayerUnlocked(player)) {

                }
            }
        }
    }
}
