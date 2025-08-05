package net.wheel.moblimiter.handler;

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
        if (!MLConfig.isEnabled())
            return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity) || entity instanceof Player)
            return;

        ChunkPos chunkPos = entity.chunkPosition();
        int[] count = { 0 };

        serverLevel.getEntities((Entity) null, entity.getBoundingBox().inflate(16), e -> {
            if (!(e instanceof LivingEntity) || e instanceof Player)
                return true;

            ChunkPos otherChunk = e.chunkPosition();
            if (otherChunk.x == chunkPos.x && otherChunk.z == chunkPos.z) {
                count[0]++;
                return count[0] < MLConfig.getMobLimit();
            }

            return true;
        });

        if (count[0] >= MLConfig.getMobLimit()) {
            event.setCanceled(true);
        }
    }
}
