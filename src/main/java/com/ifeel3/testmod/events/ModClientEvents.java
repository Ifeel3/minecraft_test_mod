package com.ifeel3.testmod.events;

import com.ifeel3.testmod.TestMod;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {

    private static HashMap<Entity, String> messages = new HashMap<>();

    @SubscribeEvent
    public static void onDrop(LivingDropsEvent event) {
        if (event.getSource().getDamageType().equals("player")) {
            Collection<ItemEntity> items = event.getDrops();
            for (ItemEntity item : items) {
                event.getEntityLiving().entityDropItem(item.getItem());
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        Entity dead = event.getEntityLiving();
        if (dead instanceof PlayerEntity) {
            dead.sendMessage(new StringTextComponent("YOU ARE DEAD!"), dead.getUniqueID());
        } else if (event.getSource().getTrueSource() instanceof PlayerEntity){
            String str = "You killed " + dead.getName().getString() + "!!!";
            Entity player = event.getSource().getTrueSource();
            messages.put(player, str);
            if (!player.world.isRemote()) {
                player.sendMessage(new StringTextComponent(str), dead.getUniqueID());
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack[] temp = {new ItemStack(Items.DIAMOND, 1),
                new ItemStack(Items.GOLD_INGOT, 1),
                new ItemStack(Blocks.DIAMOND_BLOCK, 1),
                new ItemStack(Blocks.GOLD_BLOCK, 1),
                new ItemStack(Blocks.OBSIDIAN, 1)};
        player.inventory.addItemStackToInventory(temp[ThreadLocalRandom.current().nextInt(5)]);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            if (player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem().equals(Items.DIAMOND_HELMET) &&
            getChance(5)) {
                player.sendMessage(new StringTextComponent("Critical hit!!!"), player.getUniqueID());
                event.setAmount(event.getAmount() * 3);
            }
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            int itemSlot = getItemSlot(player);
            if (itemSlot > 0) {
                player.dropItem(player.inventory.getStackInSlot(itemSlot), false);
                player.inventory.deleteStack(player.inventory.getStackInSlot(itemSlot));
            }
        }
    }

    private static boolean getChance(int chance) {
        return ThreadLocalRandom.current().nextInt(100) + 1 <= chance;
    }

    private static int getItemSlot(PlayerEntity player) {
        for (int i = 0; i < 45; i++) {
            if (player.inventory.getStackInSlot(i) != ItemStack.EMPTY && getChance(50)) {
                return i;
            }
        }
        return -1;
    }
}
