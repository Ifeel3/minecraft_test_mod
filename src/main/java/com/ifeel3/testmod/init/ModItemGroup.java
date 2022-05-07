package com.ifeel3.testmod.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {

    public static final ItemGroup TESTMOD_GROUP = new ItemGroup("testmod_items") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.CITRINE.get());
        }
    };
}
