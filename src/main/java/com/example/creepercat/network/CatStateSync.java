package com.example.creepercat.network;

import com.example.creepercat.CatState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CatStateSync {
    public static final Identifier CHANNEL = new Identifier("creepercat", "state");

    public static void registerCommon() {}

    public static void send(ServerPlayerEntity p) {
        if (!ServerPlayNetworking.canSend(p, CHANNEL)) return;
        NbtCompound tag = CatState.get(p);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(tag);
        ServerPlayNetworking.send(p, CHANNEL, buf);
    }
}
