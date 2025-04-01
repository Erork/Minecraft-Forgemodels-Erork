package com.mxtpa.tpa.events;

import com.mxtpa.tpa.player.PlayerTeleportManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.MinecraftForge;

public class PlayerEventHandler {

    // 玩家进入世界（登录）
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 确保记录位置
            PlayerTeleportManager.recordTeleportLocation(player);
            // 添加调试信息，检查位置是否正确记录
            System.out.println("Player " + player.getName().getString() + " logged in, location recorded.");
        }
    }

    // 玩家切换维度（下界/末地）
    @SubscribeEvent
    public void onDimensionChange(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerTeleportManager.recordTeleportLocation(player);
        }
    }

    // 玩家死亡时记录死亡位置
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerTeleportManager.recordTeleportLocation(player);
        }
    }

    // 玩家死亡重生（返回出生点）
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerTeleportManager.recordDeathLocation(player);
        }
    }
}
