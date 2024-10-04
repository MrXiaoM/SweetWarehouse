package top.mrxiaom.sweet.warehouse.mythic;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import io.lumine.xikage.mythicmobs.util.jnbt.CompoundTag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic4 implements IMythic {
    MythicMobs inst = MythicMobs.inst();

    @Override
    @Nullable
    public String getMythicID(ItemStack item) {
        CompoundTag tag = inst.getVolatileCodeHandler().getItemHandler().getNBTData(item);
        return tag.containsKey("MYTHIC_TYPE") ? tag.getString("MYTHIC_TYPE") : null;
    }

    @Override
    @Nullable
    public ItemStack getItem(String mythicId) {
        MythicItem item = inst.getItemManager().getItem(mythicId).orElse(null);
        AbstractItemStack i = item == null ? null : item.generateItemStack(1);
        return i == null ? null : BukkitAdapter.adapt(i);
    }
}
