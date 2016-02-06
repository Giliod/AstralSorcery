package hellfire.astralSorcery.client.renderer.sky;

import hellfire.astralSorcery.common.util.AssetLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 25.01.2016 16:45
 */
public class RenderAstralSkybox extends IRenderHandler {

    private long worldSeed;
    private boolean initialized = false;

    private static final ResourceLocation MC_DEF_SUN_PNG = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MC_DEF_MOON_PHASES_PNG = new ResourceLocation("textures/environment/moon_phases.png");

    private static final ResourceLocation TEX_STAR_1 = AssetLoader.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");
    private static final ResourceLocation TEX_STAR_2 = AssetLoader.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star2");
    private static final ResourceLocation TEX_STAR_3 = AssetLoader.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star3");
    private static final ResourceLocation TEX_STAR_4 = AssetLoader.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");

    private static int glSkyList = -1; //Sky background vertices.
    private static int glSkyList2 = -1; // - "" -

    private static int glStarList1 = -1;
    private static int glStarList2 = -1;
    private static int glStarList3 = -1;
    private static int glStarList4 = -1;

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if (!isInitialized()) return;

        renderSky(partialTicks);
    }

    public boolean isInitialized() {
        return initialized;
    }

    //Sets up skybox with given seed.
    public void setInitialized(long worldSeed) {
        this.worldSeed = worldSeed;
        setupSkybox();
        setupStarVertices();
        this.initialized = true;
    }

    private void setupStarVertices() {
        if (glStarList1 >= 0) {
            GLAllocation.deleteDisplayLists(glStarList1);
            glStarList1 = -1;
        }
        glStarList1 = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glStarList1, GL11.GL_COMPILE);
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
        Minecraft.getMinecraft().renderEngine.bindTexture(TEX_STAR_1);
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        setupStars(wr, 500, 0, 1);
        Tessellator.getInstance().draw();
        GL11.glEndList();

        if (glStarList2 >= 0) {
            GLAllocation.deleteDisplayLists(glStarList2);
            glStarList2 = -1;
        }
        glStarList2 = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glStarList2, GL11.GL_COMPILE);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEX_STAR_2);
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        setupStars(wr, 400, 1, 1.3);
        Tessellator.getInstance().draw();
        GL11.glEndList();

        if (glStarList3 >= 0) {
            GLAllocation.deleteDisplayLists(glStarList3);
            glStarList3 = -1;
        }
        glStarList3 = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glStarList3, GL11.GL_COMPILE);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEX_STAR_3);
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        setupStars(wr, 200, 2, 1.2);
        Tessellator.getInstance().draw();
        GL11.glEndList();

        if (glStarList4 >= 0) {
            GLAllocation.deleteDisplayLists(glStarList4);
            glStarList4 = -1;
        }
        glStarList4 = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glStarList4, GL11.GL_COMPILE);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEX_STAR_4);
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        setupStars(wr, 100, 3, 1.4);
        Tessellator.getInstance().draw();
        GL11.glEndList();
    }

    private void setupStars(WorldRenderer wr, int amount, long seedModifier, double multiplier) {
        Random random = new Random(worldSeed + seedModifier); //Yea. that's the whole reason we need the seed.
        for (int i = 0; i < amount; ++i) { //Amount of stars.
            double x = (double) (random.nextFloat() * 2.0F - 1.0F);
            double y = (double) (random.nextFloat() * 2.0F - 1.0F);
            double z = (double) (random.nextFloat() * 2.0F - 1.0F);
            double ovrSize = (double) (0.15F + random.nextFloat() * 0.2F); //Size flat increase.
            double d4 = x * x + y * y + z * z;
            if (d4 < 1.0D && d4 > 0.01D) {

                d4 = 1.0D / Math.sqrt(d4);
                x *= d4;
                y *= d4;
                z *= d4;

                double d5 = x * 100.0D;
                double d6 = y * 100.0D;
                double d7 = z * 100.0D;

                double d8 = Math.atan2(x, z);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);

                double d11 = Math.atan2(Math.sqrt(x * x + z * z), y);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);

                //Sizes
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double size = Math.sin(d14) * 2; //Size percentage increase.
                double d16 = Math.cos(d14);

                size *= multiplier;

                //Set 2D vertices
                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[
                    double d19 = (double) ((j + 1 & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[

                    double d21 = d18 * d16 - d19 * size;
                    double d22 = d19 * d16 + d18 * size;
                    double d23 = d21 * d12 + 0.0D * d13;

                    double d24 = 0.0D * d12 - d21 * d13;

                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;

                    wr.pos(d5 + d25, d6 + d23, d7 + d26).tex(((j + 1) & 2) >> 1, ((j + 2) & 2) >> 1).endVertex();
                }
            }
        }
    }

    private void setupSkybox() {
        if (glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList);
            glSkyList = -1;
        }
        glSkyList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glSkyList, GL11.GL_COMPILE);
        setupBackground(false);
        Tessellator.getInstance().draw();
        GL11.glEndList();

        if (glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList2);
            glSkyList2 = -1;
        }
        glSkyList2 = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glSkyList2, GL11.GL_COMPILE);
        setupBackground(true);
        Tessellator.getInstance().draw();
        GL11.glEndList();
    }

    private void setupBackground(boolean invert) {
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float px = k + 64;
                float p = k;
                if (invert) {
                    px = k;
                    p = k + 64;
                }
                wr.pos(p, 16, l).endVertex();
                wr.pos(px, 16, l).endVertex();
                wr.pos(px, 16, l + 64).endVertex();
                wr.pos(p, 16, l + 64).endVertex();
            }
        }
    }

    private void renderSky(float partialTicks) {
        GlStateManager.disableTexture2D();
        Vec3 vec3 = Minecraft.getMinecraft().theWorld.getSkyColor(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);
        float f = (float) vec3.xCoord;
        float f1 = (float) vec3.yCoord;
        float f2 = (float) vec3.zCoord;

        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(f, f1, f2);
        GlStateManager.callList(glSkyList);
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float[] sunsetColors = Minecraft.getMinecraft().theWorld.provider.calcSunriseSunsetColors(Minecraft.getMinecraft().theWorld.getCelestialAngle(partialTicks), partialTicks);
        if (sunsetColors != null) {
            renderSunsetToBackground(sunsetColors, partialTicks);
        }
        renderDefaultCelestials(partialTicks);

        double absPlayerHorizon = Minecraft.getMinecraft().thePlayer.getPositionEyes(partialTicks).yCoord - Minecraft.getMinecraft().theWorld.getHorizon();
        if (absPlayerHorizon < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);
            GlStateManager.callList(glSkyList2);
            GlStateManager.popMatrix();
            float yabs = -((float) (absPlayerHorizon + 65.0D));
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-1.0D, (double) yabs, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) yabs, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) yabs, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, (double) yabs, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) yabs, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) yabs, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, (double) yabs, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, (double) yabs, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
        }

        if (Minecraft.getMinecraft().theWorld.provider.isSkyColored()) {
            GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
        } else {
            GlStateManager.color(f, f1, f2);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float) (absPlayerHorizon - 16.0D)), 0.0F);
        GlStateManager.callList(glSkyList2);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    private void renderDefaultCelestials(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.pushMatrix();
        float f16 = 1.0F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        float f17 = 30.0F;
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_SUN_PNG);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos((double) (-f17), 100.0D, (double) (-f17)).tex(0.0D, 0.0D).endVertex();
        worldRenderer.pos((double) f17, 100.0D, (double) (-f17)).tex(1.0D, 0.0D).endVertex();
        worldRenderer.pos((double) f17, 100.0D, (double) f17).tex(1.0D, 1.0D).endVertex();
        worldRenderer.pos((double) (-f17), 100.0D, (double) f17).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
        f17 = 20.0F;
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_MOON_PHASES_PNG);
        int i = Minecraft.getMinecraft().theWorld.getMoonPhase();
        int k = i % 4;
        int i1 = i / 4 % 2;
        float f22 = (float) (k) / 4.0F;
        float f23 = (float) (i1) / 2.0F;
        float f24 = (float) (k + 1) / 4.0F;
        float f14 = (float) (i1 + 1) / 2.0F;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos((double) (-f17), -100.0D, (double) f17).tex((double) f24, (double) f14).endVertex();
        worldRenderer.pos((double) f17, -100.0D, (double) f17).tex((double) f22, (double) f14).endVertex();
        worldRenderer.pos((double) f17, -100.0D, (double) (-f17)).tex((double) f22, (double) f23).endVertex();
        worldRenderer.pos((double) (-f17), -100.0D, (double) (-f17)).tex((double) f24, (double) f23).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(false);
        float rainDim = 1.0F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks);
        float brightness = Minecraft.getMinecraft().theWorld.getStarBrightness(partialTicks) * rainDim;

        if (brightness > 0.0F) {
            GlStateManager.color(brightness, brightness, brightness, brightness);
            callStarList(glStarList1, TEX_STAR_1);
            callStarList(glStarList2, TEX_STAR_2);
            callStarList(glStarList3, TEX_STAR_3);
            callStarList(glStarList4, TEX_STAR_4);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
    }

    private void callStarList(int glList, ResourceLocation texture) {
        if(glList > 0) {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            GlStateManager.callList(glList);
        }
    }

    private void renderSunsetToBackground(float[] sunsetColors, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(MathHelper.sin(Minecraft.getMinecraft().theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        float f6 = sunsetColors[0];
        float f7 = sunsetColors[1];
        float f8 = sunsetColors[2];

        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = f10;
            f8 = f11;
        }

        worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, sunsetColors[3]).endVertex();
        //int j = 16;

        for (int l = 0; l <= 16; ++l) {
            float f21 = (float) l * (float) Math.PI * 2.0F / 16.0F;
            float f12 = MathHelper.sin(f21);
            float f13 = MathHelper.cos(f21);
            worldRenderer.pos((double) (f12 * 120.0F), (double) (f13 * 120.0F), (double) (-f13 * 40.0F * sunsetColors[3])).color(sunsetColors[0], sunsetColors[1], sunsetColors[2], 0.0F).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(7424);
    }

}
