package com.mxtpa.tpa.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;  // 使用 CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;  // 使用 Component
import net.minecraft.server.level.ServerPlayer;
import com.mxtpa.tpa.player.PlayerTeleportManager;

public class TPRCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() // 使用 CommandSourceStack
    {
        return Commands.literal("tpr")
                .executes((context) -> {
                    CommandSourceStack source = context.getSource(); // 使用 CommandSourceStack
                    if (source.getEntity() instanceof ServerPlayer player)
                    {
                        // 调用传送管理器进行随机传送
                        PlayerTeleportManager.teleportRandom(player);
                        // 使用 sendMessage 发送消息给玩家
                        player.sendSystemMessage(Component.literal("You have been teleported to a random location."));
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
