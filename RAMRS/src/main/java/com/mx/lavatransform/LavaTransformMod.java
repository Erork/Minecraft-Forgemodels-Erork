package com.mx.lavatransform;

import com.mx.lavatransform.config.LavaTransformConfig;
import com.mx.lavatransform.event.LavaWaterInteractionListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(LavaTransformMod.MOD_ID)
public class LavaTransformMod {
    public static final String MOD_ID = "lava_transform";

    @SuppressWarnings("removal")
    public LavaTransformMod() {
        // 直接使用 ModLoadingContext 注册配置（1.21.4中仍可用，但已弃用）
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LavaTransformConfig.COMMON_CONFIG);

        // 获取模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册通用初始化事件
        modEventBus.addListener(this::setup);

        // 如果是客户端，注册客户端初始化事件
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
        }

        // 在 MinecraftForge 事件总线上注册液体交互事件监听器
        MinecraftForge.EVENT_BUS.register(new LavaWaterInteractionListener());
    }

    private void setup(final FMLCommonSetupEvent event) {
        System.out.println("LavaTransform 模组已加载！");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        System.out.println("LavaTransform 客户端设置完成！");
    }
}
