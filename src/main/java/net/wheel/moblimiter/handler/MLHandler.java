package net.wheel.moblimiter.handler;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.wheel.moblimiter.config.MLConfig;

public class MLHandler {

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!MLConfig.isMobLimitingEnabled())
            return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity) || entity instanceof Player || MLWhiteListHandler.whiteListed(entity)
                || entity.hasCustomName())
            return;

        ChunkPos chunkPos = entity.chunkPosition();
        EntityType<?> t = entity.getType();
        int limit = MLConfig.effectiveSpawnLimit(t);
        int count = 0;

        List<Entity> nearby = serverLevel.getEntities(null, entity.getBoundingBox().inflate(16));
        for (int i = 0, size = nearby.size(); i < size; i++) {
            Entity e = nearby.get(i);
            if (!(e instanceof LivingEntity) || e instanceof Player || MLWhiteListHandler.whiteListed(e)
                    || e.hasCustomName())
                continue;
            ChunkPos other = e.chunkPosition();
            if (other.x != chunkPos.x || other.z != chunkPos.z)
                continue;

            if (matchesSpawnGroup(t, e.getType())) {
                if (++count >= limit) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    private boolean matchesSpawnGroup(EntityType<?> target, EntityType<?> candidate) {
        int byEntity = MLConfig.getEntityStrictSpawn().containsKey(EntityType.getKey(target).toString().toLowerCase())
                ? 1
                : 0;
        if (byEntity == 1)
            return candidate == target;
        String ns = EntityType.getKey(target).getNamespace().toLowerCase();
        if (MLConfig.getModStrictSpawn().containsKey(ns)) {
            return EntityType.getKey(candidate).getNamespace().equalsIgnoreCase(ns);
        }
        return true;
    }
}
