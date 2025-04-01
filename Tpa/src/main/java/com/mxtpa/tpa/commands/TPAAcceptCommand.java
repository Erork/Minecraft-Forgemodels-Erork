package com.mxtpa.tpa.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack; // 修改为 CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;  // 使用 Component
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import com.mxtpa.tpa.player.PlayerTeleportManager;

public class TPAAcceptCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("accept")
                .executes((context) -> {
                    CommandSourceStack source = context.getSource();
                    if (source.getEntity() instanceof ServerPlayer player) {
                        if (PlayerTeleportManager.acceptTPARequest(player)) {
                            player.sendSystemMessage(Component.literal("你已接受传送请求。"));
                        } else {
                            player.sendSystemMessage(Component.literal("未找到传送请求。"));
                        }
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }

}
