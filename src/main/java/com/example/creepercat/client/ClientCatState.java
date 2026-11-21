package com.example.creepercat.client;

import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientCatState {
    public static final Map<UUID, NbtCompound> DATA = new HashMap<>();

    public static NbtCompound get(UUID uuid) {
        return DATA.computeIfAbsent(uuid, k -> new NbtCompound());
    }

    public static boolean isCat(UUID uuid) { return get(uuid).getBoolean("isCat"); }
    public static int getEvo(UUID uuid) { return get(uuid).getInt("evo"); }
    public static int getAnim(UUID uuid) { return get(uuid).getInt("anim"); }
    public static int getStage(UUID uuid) {
        int e = getEvo(uuid);
        return e >= 15 ? 3 : e >= 5 ? 2 : 1;
    }
    public static float stageScale(int s) {
        return s >= 3 ? 2.2f : s == 2 ? 1.5f : 1.0f;
    }
}
