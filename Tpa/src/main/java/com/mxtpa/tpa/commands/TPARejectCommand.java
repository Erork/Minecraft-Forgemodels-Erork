package com.mxtpa.tpa.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;  // 使用 CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;  // 使用 Component
import net.minecraft.server.level.ServerPlayer;
import com.mxtpa.tpa.player.PlayerTeleportManager;

public class TPARejectCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() // 使用 CommandSourceStack
    {
        return Commands.literal("reject")
                .executes((context) -> {
                    CommandSourceStack source = context.getSource(); // 使用 CommandSourceStack
                    if (source.getEntity() instanceof ServerPlayer player)
                    {
                        // 调用拒绝请求的方法
                        PlayerTeleportManager.rejectTPARequest(player);
                        // 使用 sendSystemMessage 发送消息给玩家
                        player.sendSystemMessage(Component.literal("You have rejected the teleport request.")); // 正确使用 sendSystemMessage
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
