package com.mxtpa.tpa.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;  // 使用 CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;  // 使用 Component
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;  // 引入 MinecraftServer
import com.mxtpa.tpa.player.PlayerTeleportManager;

public class TPACommand
{
        public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("tpa")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes((context) -> {
                            CommandSourceStack source = context.getSource();
                            String targetName = StringArgumentType.getString(context, "player");

                            if (source.getEntity() instanceof ServerPlayer player) {
                                ServerPlayer targetPlayer = source.getServer().getPlayerList().getPlayerByName(targetName);

                                if (targetPlayer == null) {
                                    player.sendSystemMessage(Component.literal("玩家 " + targetName + " 不在线。"));
                                    return Command.SINGLE_SUCCESS;
                                }

                                PlayerTeleportManager.sendTPARequest(player, targetName);
                                player.sendSystemMessage(Component.literal("传送请求已发送给 " + targetName));
                            }
                            return Command.SINGLE_SUCCESS;
                        }));
    }
    }
