package com.example.creepercat;

import com.example.creepercat.command.GiveCatCoreCommand;
import com.example.creepercat.entity.TornCreeperEntity;
import com.example.creepercat.network.CatStateSync;
import com.example.creepercat.registry.ModEntities;
import com.example.creepercat.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreeperCatMod implements ModInitializer {
    public static final String MODID = "creepercat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        ModEntities.register();
        ModItems.register();
        CatStateSync.registerCommon();
        FabricDefaultAttributeRegistry.register(ModEntities.TORN_CREEPER, TornCreeperEntity.createAttributes());
        CommandRegistrationCallback.EVENT.register((dispatcher, ra, env) -> GiveCatCoreCommand.register(dispatcher));
        ServerTickEvents.END_WORLD_TICK.register(CatHunterManager::tick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CatStateSync.send(handler.getPlayer()));
        LOGGER.info("CreeperCatEvoV2 initialized");
    }
}
