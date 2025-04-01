package com.mx.lavatransform.util;

import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Random;

public class WeightedRandomPicker {
    private static final Random RANDOM = new Random();

    public static Block pick(List<Block> blocks, List<Double> probabilities) {
        double randomValue = RANDOM.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < blocks.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (randomValue <= cumulativeProbability) {
                return blocks.get(i);
            }
        }
        return blocks.get(blocks.size() - 1);
    }
}
