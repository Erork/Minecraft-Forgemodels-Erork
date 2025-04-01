package com.mxtpa.tpa.player;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class PlayerTeleportManager {

    private static final class LocationData {
        public final ResourceKey<Level> dimension;
        public final BlockPos position;

        public LocationData(ResourceKey<Level> dimension, BlockPos position) {
            this.dimension = dimension;
            this.position = position;
        }
    }

    private static final HashMap<UUID, LinkedList<LocationData>> playerLocations = new HashMap<>();
    private static final HashMap<UUID, UUID> teleportRequests = new HashMap<>();

    private static LinkedList<LocationData> getPlayerLocationStack(ServerPlayer player) {
        return playerLocations.computeIfAbsent(player.getUUID(), k -> new LinkedList<>());
    }

    public static void recordTeleportLocation(ServerPlayer player) {
        LinkedList<LocationData> stack = getPlayerLocationStack(player);
        LocationData current = new LocationData(player.getLevel().dimension(), player.blockPosition());

        // 避免重复记录同一位置
        if (stack.isEmpty() || !stack.getFirst().position.equals(current.position)) {
            stack.addFirst(current);
            if (stack.size() > 2) {
                stack.removeLast();  // 只保留两个位置
            }
        }
    }

    public static void teleportBack(ServerPlayer player) {
        LinkedList<LocationData> stack = getPlayerLocationStack(player);

        if (stack.size() >= 2) {
            // 弹出当前坐标
            stack.removeFirst();
            LocationData back = stack.getFirst();

            ServerLevel level = player.getServer().getLevel(back.dimension);
            if (level != null) {
                player.teleportTo(level, back.position.getX() + 0.5, back.position.getY(), back.position.getZ() + 0.5,
                        player.getYRot(), player.getXRot());
                player.sendSystemMessage(Component.literal("你已被传送回上一个位置。"));
            }
        } else {
            player.sendSystemMessage(Component.literal("没有记录可返回的位置。"));
        }
    }

    public static void recordDeathLocation(ServerPlayer player) {
        recordTeleportLocation(player);  // 死亡也记为一次传送
    }

    public static void sendTPARequest(ServerPlayer sender, String targetName) {
        MinecraftServer server = sender.getServer();
        ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(targetName);

        if (targetPlayer == null) {
            sender.sendSystemMessage(Component.literal("The player " + targetName + " does not exist!"));
            return;
        }

        targetPlayer.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested to teleport to you."));
    }

    public static boolean acceptTPARequest(ServerPlayer target) {
        UUID targetUUID = target.getUUID();
        if (teleportRequests.containsKey(targetUUID)) {
            UUID senderUUID = teleportRequests.remove(targetUUID);
            ServerPlayer sender = target.getServer().getPlayerList().getPlayer(senderUUID);

            if (sender != null) {
                recordTeleportLocation(sender);
                sender.teleportTo(target.getX(), target.getY(), target.getZ());
                sender.sendSystemMessage(Component.literal("你已被传送到 " + target.getName().getString() + "。"));
                target.sendSystemMessage(Component.literal("你已接受 " + sender.getName().getString() + " 的传送请求。"));
                return true;
            }
        }
        return false;
    }

    public static void rejectTPARequest(ServerPlayer target) {
        UUID targetUUID = target.getUUID();
        if (teleportRequests.containsKey(targetUUID)) {
            UUID senderUUID = teleportRequests.remove(targetUUID);
            ServerPlayer sender = target.getServer().getPlayerList().getPlayer(senderUUID);

            if (sender != null) {
                sender.sendSystemMessage(Component.literal(target.getName().getString() + " 拒绝了你的传送请求。"));
            }
        }
    }
    public static void teleportRandom(ServerPlayer player) {
        ServerLevel world = player.getLevel();
        int x = (int) (Math.random() * 2000 - 1000);
        int z = (int) (Math.random() * 2000 - 1000);
        int y = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY();

        // 限制y值在-60到384之间
        y = Math.max(-60, Math.min(384, y));

        recordTeleportLocation(player);
        player.teleportTo(world, x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
    }

}
