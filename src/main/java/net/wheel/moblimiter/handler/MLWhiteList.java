package net.wheel.moblimiter.handler;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import net.wheel.moblimiter.config.MLConfig;

public class MLWhiteList {

    public static boolean whiteListed(Entity entity) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());

        String modid = id.getNamespace().toLowerCase();
        String full = id.toString().toLowerCase();

        List<String> whitelist = MLConfig.getWhiteList();

        return whitelist.contains(modid) || whitelist.contains(full);
    }
}
