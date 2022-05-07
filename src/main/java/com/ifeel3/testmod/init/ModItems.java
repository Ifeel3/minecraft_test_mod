package com.ifeel3.testmod.init;

import com.ifeel3.testmod.TestMod;
import com.ifeel3.testmod.items.MaterialItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TestMod.MOD_ID);

    public static final RegistryObject<Item> CITRINE = ITEMS.register("citrine", MaterialItem::new);

}
