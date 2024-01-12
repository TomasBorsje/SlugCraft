package io.github.tomasborsje.slugcraft.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class HardRainRenderer extends DimensionSpecialEffects.OverworldEffects {
    public static boolean isHardRain = false;
    private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
    private static final float RAIN_FALL_SPEED_MULTIPLIER = 3.23f;
    private static final float SNOW_FALL_SPEED_MULTIPLIER = 30f;
    private static final int RAIN_COUNT = 13;
    float[] rainSizeX = new float[1024];
    float[] rainSizeZ = new float[1024];

    public HardRainRenderer() {
        super();
        for(int i = 0; i < 32; ++i) {
            for(int j = 0; j < 32; ++j) {
                float f = (float)(j - 16);
                float f1 = (float)(i - 16);
                float f2 = Mth.sqrt(f * f + f1 * f1);
                this.rainSizeX[i << 5 | j] = -f1 / f2;
                this.rainSizeZ[i << 5 | j] = f / f2;
            }
        }
    }


    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if(isHardRain && player != null && level.isRaining() && !minecraft.isPaused()) {
            for(int rainLayer = 0; rainLayer < RAIN_COUNT; rainLayer++) {
                float f = level.getRainLevel(partialTick);
                if (!(f <= 0.0F)) {
                    lightTexture.turnOnLightLayer();
                    int i = Mth.floor(camX);
                    int j = Mth.floor(camY);
                    int k = Mth.floor(camZ);
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tesselator.getBuilder();
                    RenderSystem.disableCull();
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    int l = 5;
                    if (Minecraft.useFancyGraphics()) {
                        l = 10;
                    }

                    RenderSystem.depthMask(Minecraft.useShaderTransparency());
                    int i1 = -1;
                    float f1 = ((float) ticks + partialTick);
                    RenderSystem.setShader(GameRenderer::getParticleShader);
                    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                    for (int j1 = k - l; j1 <= k + l; ++j1) {
                        for (int k1 = i - l; k1 <= i + l; ++k1) {
                            int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                            double d0 = (double) this.rainSizeX[l1] * 0.5;
                            double d1 = (double) this.rainSizeZ[l1] * 0.5;
                            blockpos$mutableblockpos.set((double) k1, camY, (double) j1);
                            Biome biome = (Biome) level.getBiome(blockpos$mutableblockpos).value();
                            if (biome.hasPrecipitation()) {
                                int i2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, k1, j1);
                                int j2 = j - l;
                                int k2 = j + l;
                                if (j2 < i2) {
                                    j2 = i2;
                                }

                                if (k2 < i2) {
                                    k2 = i2;
                                }

                                int l2 = i2;
                                if (i2 < j) {
                                    l2 = j;
                                }

                                if (j2 != k2) {
                                    RandomSource randomsource = RandomSource.create((long) (k1 * k1 * (31211+rainLayer) + k1 * 45233971 ^ j1 * j1 * (418711+rainLayer) + j1 * 13761));
                                    blockpos$mutableblockpos.set(k1, j2, j1);
                                    Biome.Precipitation biome$precipitation = biome.getPrecipitationAt(blockpos$mutableblockpos);
                                    float f6;
                                    float f8;
                                    if (biome$precipitation == Biome.Precipitation.RAIN) {
                                        if (i1 != 0) {
                                            if (i1 >= 0) {
                                                tesselator.end();
                                            }

                                            i1 = 0;
                                            RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                                            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                        }

                                        int i3 = ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                                        f6 = (-((float) i3 + partialTick) / 32.0F * (3.0F + randomsource.nextFloat())) * RAIN_FALL_SPEED_MULTIPLIER;
                                        double d2 = (double) k1 + 0.5 - camX;
                                        double d4 = (double) j1 + 0.5 - camZ;
                                        float f3 = (float) Math.sqrt(d2 * d2 + d4 * d4) / (float) l;
                                        f8 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                                        blockpos$mutableblockpos.set(k1, l2, j1);
                                        int j3 = getLightColor(level, blockpos$mutableblockpos);
                                        bufferbuilder.vertex((double) k1 - camX - d0 + 0.5, (double) k2 - camY, (double) j1 - camZ - d1 + 0.5).uv(0.0F, (float) j2 * 0.25F + f6).color(1.0F, 1.0F, 1.0F, f8).uv2(j3).endVertex();
                                        bufferbuilder.vertex((double) k1 - camX + d0 + 0.5, (double) k2 - camY, (double) j1 - camZ + d1 + 0.5).uv(1.0F, (float) j2 * 0.25F + f6).color(1.0F, 1.0F, 1.0F, f8).uv2(j3).endVertex();
                                        bufferbuilder.vertex((double) k1 - camX + d0 + 0.5, (double) j2 - camY, (double) j1 - camZ + d1 + 0.5).uv(1.0F, (float) k2 * 0.25F + f6).color(1.0F, 1.0F, 1.0F, f8).uv2(j3).endVertex();
                                        bufferbuilder.vertex((double) k1 - camX - d0 + 0.5, (double) j2 - camY, (double) j1 - camZ - d1 + 0.5).uv(0.0F, (float) k2 * 0.25F + f6).color(1.0F, 1.0F, 1.0F, f8).uv2(j3).endVertex();
                                    } else if (biome$precipitation == Biome.Precipitation.SNOW) {
                                        if (i1 != 1) {
                                            if (i1 >= 0) {
                                                tesselator.end();
                                            }

                                            i1 = 1;
                                            RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                                            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                        }

                                        float f5 = (-((float) (ticks & 511) + partialTick) / 512.0F) * SNOW_FALL_SPEED_MULTIPLIER;
                                        f6 = (float) (randomsource.nextDouble() + (double) f1 * 0.01 * (double) ((float) randomsource.nextGaussian()));
                                        float f7 = (float) (randomsource.nextDouble() + (double) (f1 * (float) randomsource.nextGaussian()) * 0.001);
                                        double d3 = (double) k1 + 0.5 - camX;
                                        double d5 = (double) j1 + 0.5 - camZ;
                                        f8 = (float) Math.sqrt(d3 * d3 + d5 * d5) / (float) l;
                                        float f9 = ((1.0F - f8 * f8) * 0.3F + 0.5F) * f;
                                        blockpos$mutableblockpos.set(k1, l2, j1);
                                        int k3 = getLightColor(level, blockpos$mutableblockpos);
                                        int l3 = k3 >> 16 & '\uffff';
                                        int i4 = k3 & '\uffff';
                                        int j4 = (l3 * 3 + 240) / 4;
                                        int k4 = (i4 * 3 + 240) / 4;
                                        bufferbuilder.vertex((double) k1 - camX - d0 + 0.5, (double) k2 - camY, (double) j1 - camZ - d1 + 0.5).uv(0.0F + f6, (float) j2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                        bufferbuilder.vertex((double) k1 - camX + d0 + 0.5, (double) k2 - camY, (double) j1 - camZ + d1 + 0.5).uv(1.0F + f6, (float) j2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                        bufferbuilder.vertex((double) k1 - camX + d0 + 0.5, (double) j2 - camY, (double) j1 - camZ + d1 + 0.5).uv(1.0F + f6, (float) k2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                        bufferbuilder.vertex((double) k1 - camX - d0 + 0.5, (double) j2 - camY, (double) j1 - camZ - d1 + 0.5).uv(0.0F + f6, (float) k2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                    }
                                }
                            }
                        }
                    }

                    if (i1 >= 0) {
                        tesselator.end();
                    }

                    RenderSystem.enableCull();
                    RenderSystem.disableBlend();
                    lightTexture.turnOffLightLayer();
                }
            }
        }

        return super.renderSnowAndRain(level, ticks, partialTick, lightTexture, camX, camY, camZ);
    }

    public static int getLightColor(BlockAndTintGetter p_109542_, BlockPos p_109543_) {
        return getLightColor(p_109542_, p_109542_.getBlockState(p_109543_), p_109543_);
    }

    public static int getLightColor(BlockAndTintGetter p_109538_, BlockState p_109539_, BlockPos p_109540_) {
        if (p_109539_.emissiveRendering(p_109538_, p_109540_)) {
            return 15728880;
        } else {
            int i = p_109538_.getBrightness(LightLayer.SKY, p_109540_);
            int j = p_109538_.getBrightness(LightLayer.BLOCK, p_109540_);
            int k = p_109539_.getLightEmission(p_109538_, p_109540_);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }
}
