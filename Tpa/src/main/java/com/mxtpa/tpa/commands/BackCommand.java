package com.mxtpa.tpa.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mxtpa.tpa.player.PlayerTeleportManager;

import java.awt.*;

public class BackCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand()
    {
        return Commands.literal("back")
                .executes((context) -> {
                    CommandSourceStack source = context.getSource();
                    if (source.getEntity() instanceof ServerPlayer player)
                    {
                        PlayerTeleportManager.teleportBack(player);
                        player.sendSystemMessage(Component.literal("You have been teleported back to your previous location."));
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
