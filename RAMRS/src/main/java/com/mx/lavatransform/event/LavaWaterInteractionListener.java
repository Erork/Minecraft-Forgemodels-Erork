package com.mx.lavatransform.event;

import com.mx.lavatransform.config.LavaTransformConfig;
import com.mx.lavatransform.util.WeightedRandomPicker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;

@Mod.EventBusSubscriber
public class LavaWaterInteractionListener {
    // 使用同步 Set 存储待替换目标，避免重复和并发问题
    private static final Set<ReplacementTarget> fluidQueue = Collections.synchronizedSet(new HashSet<>());
    private static final Set<ReplacementTarget> basaltQueue = Collections.synchronizedSet(new HashSet<>());

    // 内部类：记录替换目标，包含维度和坐标
    public static class ReplacementTarget {
        public final ResourceKey<Level> dimension;
        public final BlockPos pos;

        public ReplacementTarget(ResourceKey<Level> dimension, BlockPos pos) {
            this.dimension = dimension;
            this.pos = pos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReplacementTarget)) return false;
            ReplacementTarget that = (ReplacementTarget) o;
            return dimension.equals(that.dimension) && pos.equals(that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimension, pos);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        BlockPos pos = event.getPos();
        BlockState newState = event.getNewState();

        // 水与岩浆混合生成圆石或石头的情况
        if (newState.is(Blocks.COBBLESTONE) || newState.is(Blocks.STONE)) {
            fluidQueue.add(new ReplacementTarget(serverLevel.dimension(), pos));
        }
        // 玄武岩生成情况
        if (newState.is(Blocks.BASALT)) {
            basaltQueue.add(new ReplacementTarget(serverLevel.dimension(), pos));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();
        if (server == null) return;

        // 处理 Fluid 替换队列
        List<ReplacementTarget> fluidTargets;
        synchronized (fluidQueue) {
            fluidTargets = new ArrayList<>(fluidQueue);
            fluidQueue.clear();
        }
        for (ReplacementTarget target : fluidTargets) {
            ServerLevel level = server.getLevel(target.dimension);
            if (level == null) continue;
            BlockState currentState = level.getBlockState(target.pos);
            if (currentState.is(Blocks.COBBLESTONE) || currentState.is(Blocks.STONE)) {
                try {
                    Block chosenBlock = chooseFluidReplacementBlock();
                    level.setBlock(target.pos, chosenBlock.defaultBlockState(), 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 处理 Basalt 替换队列
        List<ReplacementTarget> basaltTargets;
        synchronized (basaltQueue) {
            basaltTargets = new ArrayList<>(basaltQueue);
            basaltQueue.clear();
        }
        for (ReplacementTarget target : basaltTargets) {
            ServerLevel level = server.getLevel(target.dimension);
            if (level == null) continue;
            BlockState currentState = level.getBlockState(target.pos);
            if (currentState.is(Blocks.BASALT)) {
                try {
                    Block chosenBlock = chooseBasaltReplacementBlock();
                    level.setBlock(target.pos, chosenBlock.defaultBlockState(), 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Block chooseFluidReplacementBlock() {
        // 获取当天的 fluid 替换概率
        List<Double> probabilities = LavaTransformConfig.getFluidReplacementProbabilitiesForToday();

        // 获取配置中的方块列表
        List<Block> replacementBlocks = getFluidReplacementBlocks();
        if (replacementBlocks.isEmpty() || probabilities.isEmpty()) return Blocks.COBBLESTONE;

        // 根据加权随机算法选择一个方块
        return WeightedRandomPicker.pick(replacementBlocks, probabilities);
    }
    private static List<Block> getFluidReplacementBlocks() {
        // 获取配置中的方块列表，并转换为 Block 对象
        List<String> blockNames = LavaTransformConfig.getFluidReplacementBlockNames();
        List<Block> blocks = new ArrayList<>();
        for (String name : blockNames) {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
            if (b != null) {
                blocks.add(b);
            }
        }
        return blocks;
    }
    private static Block chooseBasaltReplacementBlock() {
        List<String> blockNames = LavaTransformConfig.getBasaltReplacementBlockNames();
        List<Double> probabilities = LavaTransformConfig.getBasaltReplacementProbabilities();
        List<Block> blocks = new ArrayList<>();
        for (String name : blockNames) {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
            if (b != null) {
                blocks.add(b);
            }
        }
        if (blocks.isEmpty() || probabilities.isEmpty()) return Blocks.BASALT;
        return WeightedRandomPicker.pick(blocks, probabilities);
    }

}
