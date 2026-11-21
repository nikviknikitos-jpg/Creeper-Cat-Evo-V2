package com.example.creepercat.client;

import com.example.creepercat.network.CatStateSync;
import com.example.creepercat.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.nbt.NbtCompound;

public class CreeperCatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.TORN_CREEPER, TornCreeperRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(CatStateSync.CHANNEL, (client, handler, buf, responseSender) -> {
            NbtCompound tag = buf.readNbt();
            if (tag != null && client.player != null) {
                ClientCatState.DATA.put(client.player.getUuid(), tag);
            }
        });
    }
}
