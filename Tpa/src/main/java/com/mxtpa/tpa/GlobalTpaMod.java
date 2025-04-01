package com.mxtpa.tpa;

import com.mxtpa.tpa.events.PlayerEventHandler;
import com.mxtpa.tpa.player.PlayerTeleportManager;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod(GlobalTpaMod.MODID)
public class GlobalTpaMod {

    public static final String MODID = "tpa";  // 将 MODID 设置为 "tpa"
    public static final PlayerTeleportManager teleportManager = new PlayerTeleportManager();

    public GlobalTpaMod() {
        IEventBus modEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::registerCommands);  // 注册命令
         modEventBus.register(new PlayerEventHandler());
    }


    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("back").executes(context -> {
            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                teleportManager.teleportBack(player);  // 调用 PlayerTeleportManager 处理传送
                player.sendSystemMessage(Component.literal("You have been teleported back to your previous location."));
            }
            return 1;
        }));

        event.getDispatcher().register(Commands.literal("tpa").executes(context -> {
            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                String targetName = context.getArgument("player", String.class);
                teleportManager.sendTPARequest(player, targetName);
                player.sendSystemMessage(Component.literal("Teleport request sent to " + targetName));
            }
            return 1;
        }));

        event.getDispatcher().register(Commands.literal("accept").executes(context -> {
            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                teleportManager.acceptTPARequest(player);
                player.sendSystemMessage(Component.literal("You have accepted the teleport request."));
            }
            return 1;
        }));

        event.getDispatcher().register(Commands.literal("reject").executes(context -> {
            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                teleportManager.rejectTPARequest(player);
                player.sendSystemMessage(Component.literal("You have rejected the teleport request."));
            }
            return 1;
        }));

        event.getDispatcher().register(Commands.literal("tpr").executes(context -> {
            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                teleportManager.teleportRandom(player);
                player.sendSystemMessage(Component.literal("You have been teleported to a random location."));
            }
            return 1;
        }));
    }
}
