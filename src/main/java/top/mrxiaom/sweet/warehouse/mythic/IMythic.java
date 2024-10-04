package top.mrxiaom.sweet.warehouse.mythic;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IMythic {
    @Nullable
    String getMythicID(ItemStack item);
    @Nullable
    ItemStack getItem(String mythicId);
}
