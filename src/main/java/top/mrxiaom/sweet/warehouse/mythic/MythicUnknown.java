package top.mrxiaom.sweet.warehouse.mythic;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MythicUnknown implements IMythic {
    @Nullable
    @Override
    public String getMythicID(ItemStack item) {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getItem(String mythicId) {
        return null;
    }
}
