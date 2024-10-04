package top.mrxiaom.sweet.warehouse.mythic;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic5 implements IMythic {
    MythicBukkit inst = MythicBukkit.inst();

    @Override
    @Nullable
    public String getMythicID(ItemStack item) {
        return inst.getItemManager().getMythicTypeFromItem(item);
    }

    @Override
    @Nullable
    public ItemStack getItem(String mythicId) {
        MythicItem item = inst.getItemManager().getItem(mythicId).orElse(null);
        AbstractItemStack i = item == null ? null : item.generateItemStack(1);
        return i == null ? null : BukkitAdapter.adapt(i);
    }
}
