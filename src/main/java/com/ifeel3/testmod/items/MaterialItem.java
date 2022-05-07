package com.ifeel3.testmod.items;

import com.ifeel3.testmod.init.ModItemGroup;
import net.minecraft.item.Item;

public class MaterialItem extends Item {
    public MaterialItem() {
        super(new Item.Properties().maxStackSize(64).group(ModItemGroup.TESTMOD_GROUP));
    }
}
