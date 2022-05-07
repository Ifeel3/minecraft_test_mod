package com.ifeel3.testmod;

import com.ifeel3.testmod.events.ModClientEvents;
import com.ifeel3.testmod.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("testmod")
public class TestMod
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "testmod";

    public TestMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);

        ModItems.ITEMS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModClientEvents.class);
    }

    private void setup(final FMLCommonSetupEvent event) { }

    private void doClientStuff(final FMLClientSetupEvent event) { }
}
