package com.mx.lavatransform.config;

import com.google.common.collect.ImmutableList;
import com.mx.lavatransform.util.IslandLevelDataReader;
import net.minecraftforge.common.ForgeConfigSpec;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LavaTransform 配置类
 */
public class LavaTransformConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ----- fluid_replacement 配置 -----
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FLUID_REPLACEMENT_BLOCKS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_REPLACEMENT_PROBABILITIES;

    // ----- basalt_replacement 配置 -----
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BASALT_REPLACEMENT_BLOCKS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> BASALT_REPLACEMENT_PROBABILITIES;

    // ----- 每日fluid替换概率配置 -----
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_MONDAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_TUESDAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_WEDNESDAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_THURSDAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_FRIDAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_SATURDAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Double>> FLUID_PROBABILITIES_SUNDAY;

    private static ImmutableList<Double> cachedDailyProbabilities;

    public static final ForgeConfigSpec COMMON_CONFIG;

    static {
        BUILDER.comment("自定义水与岩浆混合生成圆石/石头时的替换配置").push("fluid_replacement");
        FLUID_REPLACEMENT_BLOCKS = BUILDER.defineList("fluid_replacement_blocks", Arrays.asList("ic2:solar_panel_hv", "minecraft:iron_block"), obj -> obj instanceof String);
        FLUID_REPLACEMENT_PROBABILITIES = BUILDER.defineList("fluid_replacement_probabilities", Arrays.asList(0.6, 0.4), obj -> obj instanceof Double);
        BUILDER.pop();

        BUILDER.comment("自定义玄武岩生成时的替换配置").push("basalt_replacement");
        BASALT_REPLACEMENT_BLOCKS = BUILDER.defineList("basalt_replacement_blocks", Arrays.asList("minecraft:blackstone"), obj -> obj instanceof String);
        BASALT_REPLACEMENT_PROBABILITIES = BUILDER.defineList("basalt_replacement_probabilities", Arrays.asList(1.0), obj -> obj instanceof Double);
        BUILDER.pop();

        BUILDER.comment("根据星期几设定不同的 fluid 替换生成概率").push("daily_fluid_probabilities");
        FLUID_PROBABILITIES_MONDAY = BUILDER.defineList("monday", Arrays.asList(0.6, 0.3, 0.1), obj -> obj instanceof Double);
        FLUID_PROBABILITIES_TUESDAY = BUILDER.defineList("tuesday", Arrays.asList(0.5, 0.4, 0.1), obj -> obj instanceof Double);
        FLUID_PROBABILITIES_WEDNESDAY = BUILDER.defineList("wednesday", Arrays.asList(0.7, 0.2, 0.1), obj -> obj instanceof Double);
        FLUID_PROBABILITIES_THURSDAY = BUILDER.defineList("thursday", Arrays.asList(0.3, 0.6, 0.1), obj -> obj instanceof Double);
        FLUID_PROBABILITIES_FRIDAY = BUILDER.defineList("friday", Arrays.asList(0.8, 0.1, 0.1), obj -> obj instanceof Double);
        FLUID_PROBABILITIES_SATURDAY = BUILDER.defineList("saturday", Arrays.asList(0.4, 0.5, 0.1), obj -> obj instanceof Double);
        FLUID_PROBABILITIES_SUNDAY = BUILDER.defineList("sunday", Arrays.asList(0.5, 0.4, 0.1), obj -> obj instanceof Double);
        BUILDER.pop();

        COMMON_CONFIG = BUILDER.build();
    }

    public static List<String> getFluidReplacementBlockNames() {
        return new ArrayList<>(FLUID_REPLACEMENT_BLOCKS.get());
    }

    public static List<Double> getFluidReplacementProbabilities() {
        return new ArrayList<>(FLUID_REPLACEMENT_PROBABILITIES.get());
    }

    public static List<String> getBasaltReplacementBlockNames() {
        return new ArrayList<>(BASALT_REPLACEMENT_BLOCKS.get());
    }

    public static List<Double> getBasaltReplacementProbabilities() {
        return new ArrayList<>(BASALT_REPLACEMENT_PROBABILITIES.get());
    }

    public static List<Double> getFluidReplacementProbabilitiesForToday() {
        if (cachedDailyProbabilities != null) {
            return cachedDailyProbabilities;
        }

        DayOfWeek day = LocalDate.now().getDayOfWeek();
        List<Double> probabilities = switch (day) {
            case MONDAY -> new ArrayList<>(FLUID_PROBABILITIES_MONDAY.get());
            case TUESDAY -> new ArrayList<>(FLUID_PROBABILITIES_TUESDAY.get());
            case WEDNESDAY -> new ArrayList<>(FLUID_PROBABILITIES_WEDNESDAY.get());
            case THURSDAY -> new ArrayList<>(FLUID_PROBABILITIES_THURSDAY.get());
            case FRIDAY -> new ArrayList<>(FLUID_PROBABILITIES_FRIDAY.get());
            case SATURDAY -> new ArrayList<>(FLUID_PROBABILITIES_SATURDAY.get());
            case SUNDAY -> new ArrayList<>(FLUID_PROBABILITIES_SUNDAY.get());
        };

        int numBlocks = getFluidReplacementBlockNames().size();

        if (probabilities.size() != numBlocks) {
            double equalProbability = 1.0 / numBlocks;
            probabilities.clear();
            for (int i = 0; i < numBlocks; i++) {
                probabilities.add(equalProbability);
            }
        }

        cachedDailyProbabilities = ImmutableList.copyOf(probabilities);
        return cachedDailyProbabilities;
    }

    /**
     * 使用 global_level 替代星期判断；如果读取失败则回退使用星期
     */
    public static List<Double> getFluidReplacementProbabilitiesWithGlobalLevel() {
        Integer level = IslandLevelDataReader.getGlobalLevel();
        List<Double> probabilities;

        if (level != null) {
            List<Double> base = getFluidReplacementProbabilities();
            probabilities = new ArrayList<>();
            for (double val : base) {
                probabilities.add(val * (1.0 + level * 0.05));
            }

            double total = probabilities.stream().mapToDouble(d -> d).sum();
            for (int i = 0; i < probabilities.size(); i++) {
                probabilities.set(i, probabilities.get(i) / total);
            }
        } else {
            probabilities = getFluidReplacementProbabilitiesForToday();
        }

        return probabilities;
    }
}
