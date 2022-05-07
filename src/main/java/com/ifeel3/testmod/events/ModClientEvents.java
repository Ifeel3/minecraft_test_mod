package com.ifeel3.testmod.events;

import com.ifeel3.testmod.TestMod;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {

    private static Entity targetForHurt = null;
    private static Entity targetForDead = null;
    private static PlayerEntity player = null;
    private static boolean critical = false;
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
        if (dead.getClass() == ServerPlayerEntity.class) {
            dead.sendMessage(new StringTextComponent("YOU ARE DEAD!"), dead.getUniqueID());
        } else if (dead == targetForDead){
            String str = "You killed " + dead.getName().getString() + "!!!";
            targetForDead = null;
            messages.put(dead, str);
            if (!player.world.isRemote()) {
                player.sendMessage(new StringTextComponent(str), dead.getUniqueID());
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Entity player = event.getEntityLiving();
        ItemStack[] temp = {new ItemStack(Items.DIAMOND, 1),
                new ItemStack(Items.GOLD_INGOT, 1),
                new ItemStack(Blocks.DIAMOND_BLOCK, 1),
                new ItemStack(Blocks.GOLD_BLOCK, 1),
                new ItemStack(Blocks.OBSIDIAN, 1)};
        player.replaceItemInInventory(9, temp[new Random().nextInt(5)]);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (critical && event.getEntity() == targetForHurt) {
            critical = false;
            targetForDead = targetForHurt;
            targetForHurt = null;
            float damage = event.getAmount();
            event.setAmount(damage * 3);
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        player = event.getPlayer();
        targetForHurt = event.getTarget();
        if (player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.DIAMOND_HELMET.getItem()
                && getRandomChance(5)) {
            if (player.world.isRemote) {
                player.sendMessage(new StringTextComponent("Critical Hit!!!"), player.getUniqueID());
            }
            critical = true;
        }
        if (getRandomChance(50)) {
            ArrayList<ItemStack> temp = getNotAir(player);
            if (temp.size() > 0) {
                ItemStack stack = temp.get(new Random().nextInt(temp.size()));
                player.dropItem(stack, true, false);
                player.inventory.deleteStack(stack);
            }
        }
    }

    private static boolean getRandomChance(int chance) {
        return new Random().nextInt(100) + 1 <= chance;
    }

    public static ArrayList<ItemStack> getNotAir(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>();
        addInventoryToArray(inventory, player.inventory.mainInventory);
        addInventoryToArray(inventory, player.inventory.armorInventory);
        addInventoryToArray(inventory, player.inventory.offHandInventory);
        return inventory;
    }

    private static boolean notEmpty(NonNullList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            if (!(inventory.get(i).getItem() == Items.AIR.getItem())) {
                return true;
            }
        }
        return false;
    }

    private static void addInventoryToArray(ArrayList<ItemStack> array, NonNullList<ItemStack> inventory) {
        if (notEmpty(inventory)) {
            for (ItemStack item : inventory) {
                if (item.getItem() != Items.AIR.getItem()) {
                    array.add(item);
                }
            }
        }
    }
}
