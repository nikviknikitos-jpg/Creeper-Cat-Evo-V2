package com.example.creepercat.registry;

import com.example.creepercat.CatTransformationHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModItems {
    public static Item FELINE_MUTAGEN;

    public static void register() {
        FELINE_MUTAGEN = Registry.register(Registries.ITEM,
                new Identifier("creepercat", "feline_mutagen"),
                new MutagenItem(new Item.Settings().maxCount(1)));
    }

    public static class MutagenItem extends Item {
        public MutagenItem(Settings settings) { super(settings); }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && user instanceof net.minecraft.server.network.ServerPlayerEntity sp) {
                CatTransformationHandler.toggle(sp);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
    }
}
