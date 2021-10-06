package com.skizzium.projectapple.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.skizzium.projectapple.ProjectApple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ProjectApple.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderCustomBossBar {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final ResourceLocation PA_GUI_BARS_LOCATION = new ResourceLocation(ProjectApple.MOD_ID, "textures/gui/pa_bars.png");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static int tickCount;

    @SubscribeEvent
    public static void registerBossBarRendering(RenderGameOverlayEvent.BossInfo event) {
        event.setCanceled(true);
        int i = minecraft.getWindow().getGuiScaledWidth();
        int j = 12;
        
        for (LerpingBossEvent lerpingEvent : minecraft.gui.getBossOverlay().events.values()) {
            int k = i / 2 - 91;
            UUID id = lerpingEvent.getId();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (!(minecraft.gui.getBossOverlay().events.get(id) instanceof PA_LerpingBossEvent)) {
                RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
                drawBar(event.getMatrixStack(), k, j, lerpingEvent);
            } 
            else {
                if (((PA_LerpingBossEvent) lerpingEvent).getCustomTexture() == null) {
                    RenderSystem.setShaderTexture(0, PA_GUI_BARS_LOCATION);
                    drawBar(event.getMatrixStack(), k, j, (PA_LerpingBossEvent) lerpingEvent);
                }
                else {
                    RenderSystem.setShaderTexture(0, ((PA_LerpingBossEvent) lerpingEvent).getCustomTexture());
                    drawCustomBar(event.getMatrixStack(), k, j, event.getPartialTicks(), (PA_LerpingBossEvent) lerpingEvent);
                }
            }

            Component component = lerpingEvent.getName();
            int l = minecraft.font.width(component);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            minecraft.font.drawShadow(event.getMatrixStack(), component, (float) i1, (float) j1, 16777215);
            j += event.getIncrement();
            if (j >= minecraft.getWindow().getGuiScaledHeight() / 3) {
                break;
            }
        }

    }

    private static void drawBar(PoseStack pose, int xPos, int yPos, BossEvent bossEvent) {
        GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getColor().ordinal() * 5 * 2), 182, 5, 256, 256);
        if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2), 182, 5, 256, 256);
        }

        int i2 = (int)(bossEvent.getProgress() * 183.0F);
        if (i2 > 0) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getColor().ordinal() * 5 * 2 + 5), i2, 5, 256, 256);
            if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
                GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + 5), i2, 5, 256, 256);
            }
        }
    }

    private static void drawBar(PoseStack pose, int xPos, int yPos, PA_LerpingBossEvent bossEvent) {
        GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getCustomColor().ordinal() * 5 * 2), 182, 5, 256, 256);
        if (bossEvent.getCustomOverlay() != PA_BossEvent.PA_BossBarOverlay.PROGRESS) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2), 182, 5, 256, 256);
        }

        int i2 = (int)(bossEvent.getProgress() * 183.0F);
        if (i2 > 0) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getCustomColor().ordinal() * 5 * 2 + 5), i2, 5, 256, 256);
            if (bossEvent.getCustomOverlay() != PA_BossEvent.PA_BossBarOverlay.PROGRESS) {
                GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2 + 5), i2, 5, 256, 256);
            }
        }
    }

    private static float yOffset(float partialTicks) {
        tickCount += 16;
        return tickCount;
    }
    
    private static void drawCustomBar(PoseStack pose, int xPos, int yPos, float partialTicks, PA_LerpingBossEvent bossEvent) {
        GuiComponent.blit(pose, xPos, yPos - 5, 0, 0.0F, yOffset(partialTicks), 182, 17, 256, 256);
//        if (bossEvent.getCustomOverlay() != PA_BossEvent.PA_BossBarOverlay.PROGRESS) {
//            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2), 182, 5, 256, 256);
//        }

//        int i2 = (int)(bossEvent.getProgress() * 183.0F);
//        if (i2 > 0) {
//            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getCustomColor().ordinal() * 5 * 2 + 5), i2, 5, 256, 256);
//            if (bossEvent.getCustomOverlay() != PA_BossEvent.PA_BossBarOverlay.PROGRESS) {
//                GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2 + 5), i2, 5, 256, 256);
//            }
//        }
    }
}
