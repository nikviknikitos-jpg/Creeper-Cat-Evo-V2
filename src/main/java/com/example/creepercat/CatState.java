package com.example.creepercat;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CatState {
    public static final Map<UUID, NbtCompound> DATA = new HashMap<>();
    public static final String KEY_CAT = "isCat";
    public static final String KEY_EVO = "evo";
    public static final String KEY_ANIM = "anim";

    public static NbtCompound get(PlayerEntity p) {
        return DATA.computeIfAbsent(p.getUuid(), k -> new NbtCompound());
    }

    public static boolean isCat(PlayerEntity p) { return get(p).getBoolean(KEY_CAT); }
    public static int getEvo(PlayerEntity p) { return get(p).getInt(KEY_EVO); }
    public static int getAnim(PlayerEntity p) { return get(p).getInt(KEY_ANIM); }

    public static void setCat(PlayerEntity p, boolean v) { get(p).putBoolean(KEY_CAT, v); }
    public static void setEvo(PlayerEntity p, int v) { get(p).putInt(KEY_EVO, v); }
    public static void setAnim(PlayerEntity p, int v) { get(p).putInt(KEY_ANIM, v); }

    public static int getStage(int evo) {
        if (evo >= 15) return 3;
        if (evo >= 5) return 2;
        return 1;
    }
    public static float stageScale(int s) {
        if (s >= 3) return 2.2f;
        if (s == 2) return 1.5f;
        return 1.0f;
    }

    public static void clear(PlayerEntity p) {
        setCat(p, false);
        setAnim(p, 0);
    }
}
