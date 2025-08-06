package net.wheel.moblimiter.handler;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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
        if (!(entity instanceof LivingEntity) || entity instanceof Player)
            return;

        ChunkPos chunkPos = entity.chunkPosition();
        int limit = MLConfig.getMobLimit();
        int count = 0;

        List<Entity> nearbyEntities = serverLevel.getEntities(null, entity.getBoundingBox().inflate(16));
        for (int i = 0, size = nearbyEntities.size(); i < size; i++) {
            Entity e = nearbyEntities.get(i);
            if (!(e instanceof LivingEntity) || e instanceof Player)
                continue;

            ChunkPos otherChunk = e.chunkPosition();
            if (otherChunk.x == chunkPos.x && otherChunk.z == chunkPos.z) {
                if (++count >= limit) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
