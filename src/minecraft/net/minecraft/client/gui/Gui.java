package net.minecraft.client.gui;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.utils.Loona;
import cc.unknown.utils.client.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.vec.AxisAlignedBB;
import net.minecraft.util.vec.Vec3;

public class Gui implements Loona {
	public static final ResourceLocation optionsBackground = new ResourceLocation(
			"textures/gui/options_background.png");
	public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
	public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
	protected float zLevel;

	/**
	 * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
	 */
	protected void drawHorizontalLine(int startX, int endX, int y, int color) {
		if (endX < startX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		drawRect(startX, y, endX + 1, y + 1, color);
	}

	/**
	 * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
	 */
	protected void drawVerticalLine(int x, int startY, int endY, int color) {
		if (endY < startY) {
			int i = startY;
			startY = endY;
			endY = i;
		}

		drawRect(x, startY + 1, x + 1, endY, color);
	}

	/**
	 * Draws a solid color rectangle with the specified coordinates and color (ARGB
	 * format). Args: x1, y1, x2, y2, color
	 */

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}
		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}
		float f3 = (color >> 24 & 0xFF) / 255.0F;
		float f = (color >> 16 & 0xFF) / 255.0F;
		float f1 = (color >> 8 & 0xFF) / 255.0F;
		float f2 = (color & 0xFF) / 255.0F;
		Tessellator t = Tessellator.getInstance();
		WorldRenderer w = t.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		w.func_181668_a(7, DefaultVertexFormats.field_181705_e);
		w.func_181662_b(left, bottom, 0.0D).func_181675_d();
		w.func_181662_b(right, bottom, 0.0D).func_181675_d();
		w.func_181662_b(right, top, 0.0D).func_181675_d();
		w.func_181662_b(left, top, 0.0D).func_181675_d();
		t.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(resourceLocation);
		Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0, 0, (int) width, (int) height, width, height);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void drawImage(DynamicTexture image, float x, float y, float width, float height,
			ResourceLocation id) {
		mc.getTextureManager().loadTexture(id, image);
		drawImage(id, x, y, width, height);
	}
	
	public static void drawBorderedRoundedRect(float x, float y, float x1, float y1, float radius, float borderSize,
			int borderC, int insideC) {
		drawRoundedRect(x, y, x1, y1, radius, insideC);
		drawRoundedOutline(x, y, x1, y1, radius, borderSize, borderC);
	}

	public static void drawRoundedRect(float x, float y, float x1, float y1, final float radius, final int color) {
		drawRoundedRect(x, y, x1, y1, radius, color, new boolean[] { true, true, true, true });
	}

	public static void drawRoundedRect(float x, float y, float x1, float y1, final float radius, final int color,
			boolean[] round) {
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);
		x *= 2.0;
		y *= 2.0;
		x1 *= 2.0;
		y1 *= 2.0;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		ColorUtil.setColor(color);
		GL11.glEnable(2848);
		GL11.glBegin(GL11.GL_POLYGON);
		round(x, y, x1, y1, radius, round);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glScaled(2.0, 2.0, 2.0);
		GL11.glEnable(3042);
		GL11.glPopAttrib();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void roundHelper(float x, float y, float radius, int pn, int pn2, int originalRotation,
			int finalRotation) {
		for (int i = originalRotation; i <= finalRotation; i += 3)
			GL11.glVertex2d(x + (  radius * -pn) + (Math.sin((i * Math.PI) / 180.0) * radius * pn),
					y + (radius * pn2) + (Math.cos((i * Math.PI) / 180.0) * radius * pn));
	}

	public static void drawRoundedOutline(float x, float y, float x1, float y1, final float radius,
			final float borderSize, final int color) {
		drawRoundedOutline(x, y, x1, y1, radius, borderSize, color, new boolean[] { true, true, true, true });
	}

	public static void round(float x, float y, float x1, float y1, float radius, final boolean[] round) {
		if (round[0])
			roundHelper(x, y, radius, -1, 1, 0, 90);
		else
			GL11.glVertex2d(x, y);

		if (round[1])
			roundHelper(x, y1, radius, -1, -1, 90, 180);
		else
			GL11.glVertex2d(x, y1);

		if (round[2])
			roundHelper(x1, y1, radius, 1, -1, 0, 90);
		else
			GL11.glVertex2d(x1, y1);

		if (round[3])
			roundHelper(x1, y, radius, 1, 1, 90, 180);
		else
			GL11.glVertex2d(x1, y);
	}

	public static void drawRoundedOutline(float x, float y, float x1, float y1, final float radius,
			final float borderSize, final int color, boolean[] drawCorner) {
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);
		x *= 2.0;
		y *= 2.0;
		x1 *= 2.0;
		y1 *= 2.0;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		ColorUtil.setColor(color);
		GL11.glEnable(2848);
		GL11.glLineWidth(borderSize);
		GL11.glBegin(2);
		round(x, y, x1, y1, radius, drawCorner);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		GL11.glScaled(2.0, 2.0, 2.0);
		GL11.glPopAttrib();
		GL11.glLineWidth(1.0f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void rect(float x1, float y1, float x2, float y2, int fill) {
		GlStateManager.color(0, 0, 0);
		GL11.glColor4f(0, 0, 0, 0);
		float f = (fill >> 24 & 0xFF) / 255.0F;
		float f1 = (fill >> 16 & 0xFF) / 255.0F;
		float f2 = (fill >> 8 & 0xFF) / 255.0F;
		float f3 = (fill & 0xFF) / 255.0F;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glPushMatrix();
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glBegin(7);
		GL11.glVertex2d(x2, y1);
		GL11.glVertex2d(x1, y1);
		GL11.glVertex2d(x1, y2);
		GL11.glVertex2d(x2, y2);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}

	public static void drawBorderedRect(float f, float f1, float f2, float f3, float f4, int i, int j) {
		mc.currentScreen.drawRect(f, f1, f2, f3, j);
		float f5 = (float) (i >> 24 & 255) / 255.0F;
		float f6 = (float) (i >> 16 & 255) / 255.0F;
		float f7 = (float) (i >> 8 & 255) / 255.0F;
		float f8 = (float) (i & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glPushMatrix();
		GL11.glColor4f(f6, f7, f8, f5);
		GL11.glLineWidth(f4);
		GL11.glBegin(1);
		GL11.glVertex2d(f, f1);
		GL11.glVertex2d(f, f3);
		GL11.glVertex2d(f2, f3);
		GL11.glVertex2d(f2, f1);
		GL11.glVertex2d(f, f1);
		GL11.glVertex2d(f2, f1);
		GL11.glVertex2d(f, f3);
		GL11.glVertex2d(f2, f3);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	public static void startDrawing() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
	}

	public static void drawChestBox(BlockPos bp, int color, boolean shade) {
		if (bp != null) {
			double x = (double) bp.getX() - mc.getRenderManager().viewerPosX;
			double y = (double) bp.getY() - mc.getRenderManager().viewerPosY;
			double z = (double) bp.getZ() - mc.getRenderManager().viewerPosZ;
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3042);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(3553);
			GL11.glDisable(2929);
			GL11.glDepthMask(false);
			float a = (float) (color >> 24 & 255) / 255.0F;
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			GL11.glColor4d((double) r, (double) g, (double) b, (double) a);
			RenderGlobal.func_181561_a(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));
			if (shade) {
				dbb(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), r, g, b);
			}

			GL11.glEnable(3553);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
		}
	}

    public static void drawBoxAroundEntity(Entity e, int type, double expand, double shift, int color, boolean damage) {
        if (e instanceof EntityLivingBase) {
            double x = (e.lastTickPosX + ((e.posX - e.lastTickPosX) * (double) mc.timer.renderPartialTicks))
                    - mc.getRenderManager().viewerPosX;
            double y = (e.lastTickPosY + ((e.posY - e.lastTickPosY) * (double) mc.timer.renderPartialTicks))
                    - mc.getRenderManager().viewerPosY;
            double z = (e.lastTickPosZ + ((e.posZ - e.lastTickPosZ) * (double) mc.timer.renderPartialTicks))
                    - mc.getRenderManager().viewerPosZ;
            float d = (float) expand / 40.0F;
            if ((e instanceof EntityPlayer) && damage && (((EntityPlayer) e).hurtTime != 0))
                color = Color.RED.getRGB();

            GlStateManager.pushMatrix();
            int teamColor = getTeamColor((EntityPlayer) e);
            if (type == 3) {
                GL11.glTranslated(x, y - 0.2D, z);
                GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                GlStateManager.disableDepth();
                GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                int outline = Color.black.getRGB();

                Gui.drawRect(-20, -1, -26, 75, outline);
                Gui.drawRect(20, -1, 26, 75, outline);
                Gui.drawRect(-20, -1, 21, 5, outline);
                Gui.drawRect(-20, 70, 21, 75, outline);
                if (color != 0) {
                    Gui.drawRect(-21, 0, -25, 74, color);
                    Gui.drawRect(21, 0, 25, 74, color);
                    Gui.drawRect(-21, 0, 24, 4, color);
                    Gui.drawRect(-21, 71, 25, 74, color);
                } else {
                    int st = ColorUtil.rainbowDraw(2L, 0L);
                    int en = ColorUtil.rainbowDraw(2L, 1000L);
                    dGR(-21, 0, -25, 74, st, en);
                    dGR(21, 0, 25, 74, st, en);
                    Gui.drawRect(-21, 0, 21, 4, en);
                    Gui.drawRect(-21, 71, 21, 74, st);
                }

                GlStateManager.enableDepth();
            } else {
                int i;
                if (type == 4) {
                    EntityLivingBase en = (EntityLivingBase) e;
                    double r = en.getHealth() / en.getMaxHealth();
                    int b = (int) (74.0D * r);
                    int hc = r < 0.3D ? Color.red.getRGB()
                            : (r < 0.5D ? Color.orange.getRGB()
                                    : (r < 0.7D ? Color.yellow.getRGB() : Color.green.getRGB()));
                    GL11.glTranslated(x, y - 0.2D, z);
                    GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    GlStateManager.disableDepth();
                    GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                    i = (int) (21.0D + (shift * 2.0D));
                    Gui.drawRect(i, -1, i + 5, 75, Color.black.getRGB());
                    Gui.drawRect(i + 1, b, i + 4, 74, Color.darkGray.getRGB());
                    Gui.drawRect(i + 1, 0, i + 4, b, hc);
                    GlStateManager.enableDepth();
                } else if (type == 6)
                    d3p(x, y, z, 0.699999988079071D, 45, 1.5F, color, color == 0);
                else {
                    if (color == 0)
                        color = ColorUtil.rainbowDraw(2L, 0L);

                    float a = (float) ((color >> 24) & 255) / 255.0F;
                    float r = (float) ((color >> 16) & 255) / 255.0F;
                    float g = (float) ((color >> 8) & 255) / 255.0F;
                    float b = (float) (color & 255) / 255.0F;
                    if (type == 5) {
                        GL11.glTranslated(x, y - 0.2D, z);
                        GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                        GlStateManager.disableDepth();
                        GL11.glScalef(0.03F + d, 0.03F, 0.03F + d);
                        d2p(0.0D, 95.0D, 10, 3, Color.black.getRGB());

                        for (i = 0; i < 6; ++i)
                            d2p(0.0D, 95 + (10 - i), 3, 4, Color.black.getRGB());

                        for (i = 0; i < 7; ++i)
                            d2p(0.0D, 95 + (10 - i), 2, 4, color);

                        d2p(0.0D, 95.0D, 8, 3, color);
                        GlStateManager.enableDepth();
                    } else {
                        AxisAlignedBB bbox = e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand,
                                0.1D + expand);
                        AxisAlignedBB axis = new AxisAlignedBB((bbox.minX - e.posX) + x, (bbox.minY - e.posY) + y,
                                (bbox.minZ - e.posZ) + z, (bbox.maxX - e.posX) + x, (bbox.maxY - e.posY) + y,
                                (bbox.maxZ - e.posZ) + z);
                        GL11.glBlendFunc(770, 771);
                        GL11.glEnable(3042);
                        GL11.glDisable(3553);
                        GL11.glDisable(2929);
                        GL11.glDepthMask(false);
                        GL11.glLineWidth(2.0F);
                        GL11.glColor4f(r, g, b, a);
                        if (type == 1)
                            RenderGlobal.func_181561_a(axis);
                        else if (type == 2)
                            dbb(axis, r, g, b);
                        else if (type == 7)
                            dsbbt(axis, teamColor);

                        GL11.glEnable(3553);
                        GL11.glEnable(2929);
                        GL11.glDepthMask(true);
                        GL11.glDisable(3042);
                    }
                }
            }

            GlStateManager.popMatrix();
        }
    }
    
    private static void d2p(double x, double y, int radius, int sides, int color) {
        float a = (float) ((color >> 24) & 255) / 255.0F;
        float r = (float) ((color >> 16) & 255) / 255.0F;
        float g = (float) ((color >> 8) & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(r, g, b, a);
        worldrenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);

        for (int i = 0; i < sides; ++i) {
            double angle = ((6.283185307179586D * (double) i) / (double) sides) + Math.toRadians(180.0D);
            worldrenderer.func_181662_b(x + (Math.sin(angle) * (double) radius), y + (Math.cos(angle) * (double) radius), 0.0D)
            .func_181675_d();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    private static void dbb(AxisAlignedBB abb, float r, float g, float b) {
        float a = 0.25F;
        Tessellator ts = Tessellator.getInstance();
        WorldRenderer vb = ts.getWorldRenderer();
        vb.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vb.func_181662_b(abb.minX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vb.func_181662_b(abb.maxX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vb.func_181662_b(abb.minX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vb.func_181662_b(abb.minX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vb.func_181662_b(abb.minX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vb.func_181662_b(abb.minX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.minX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.minZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.maxY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        vb.func_181662_b(abb.maxX, abb.minY, abb.maxZ).func_181666_a(r, g, b, a).func_181675_d();
        ts.draw();
    }
    
    private static void d3p(double x, double y, double z, double radius, int sides, float lineWidth, int color,
            boolean chroma) {
        float a = (float) ((color >> 24) & 255) / 255.0F;
        float r = (float) ((color >> 16) & 255) / 255.0F;
        float g = (float) ((color >> 8) & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        if (!chroma)
            GL11.glColor4f(r, g, b, a);

        GL11.glBegin(1);
        long d = 0L;
        long ed = 15000L / (long) sides;
        long hed = ed / 2L;

        for (int i = 0; i < (sides * 2); ++i) {
            if (chroma) {
                if ((i % 2) != 0) {
                    if (i == 47)
                        d = hed;

                    d += ed;
                }

                int c = ColorUtil.rainbowDraw(2L, d);
                float r2 = (float) ((c >> 16) & 255) / 255.0F;
                float g2 = (float) ((c >> 8) & 255) / 255.0F;
                float b2 = (float) (c & 255) / 255.0F;
                GL11.glColor3f(r2, g2, b2);
            }

            double angle = ((6.283185307179586D * (double) i) / (double) sides) + Math.toRadians(180.0D);
            GL11.glVertex3d(x + (Math.cos(angle) * radius), y, z + (Math.sin(angle) * radius));
        }

        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        mc.entityRenderer.enableLightmap();
    }

	private static void dsbbt(AxisAlignedBB var0, int teamColor) {
		Tessellator var1 = Tessellator.getInstance();
		WorldRenderer var2 = var1.getWorldRenderer();

		// Set color based on teamColor
		float red = ((teamColor >> 16) & 0xFF) / 255.0f;
		float green = ((teamColor >> 8) & 0xFF) / 255.0f;
		float blue = (teamColor & 0xFF) / 255.0f;

		GlStateManager.color(red, green, blue, 1.0F); // Set the color

		// Draw bottom face
		var2.func_181668_a(3, DefaultVertexFormats.field_181705_e);
		var2.func_181662_b(var0.minX, var0.minY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.minY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.minY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.minY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.minY, var0.minZ).func_181675_d();
		var1.draw();

		// Draw top face
		var2.func_181668_a(3, DefaultVertexFormats.field_181705_e);
		var2.func_181662_b(var0.minX, var0.maxY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.maxY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.maxY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.maxY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.maxY, var0.minZ).func_181675_d();
		var1.draw();

		// Draw vertical edges
		var2.func_181668_a(1, DefaultVertexFormats.field_181705_e);
		var2.func_181662_b(var0.minX, var0.minY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.maxY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.minY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.maxY, var0.minZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.minY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.maxX, var0.maxY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.minY, var0.maxZ).func_181675_d();
		var2.func_181662_b(var0.minX, var0.maxY, var0.maxZ).func_181675_d();
		var1.draw();

		// Reset color to default
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private static void dGR(int left, int top, int right, int bottom, int startColor, int endColor) {
		int j;
		if (left < right) {
			j = left;
			left = right;
			right = j;
		}

		if (top < bottom) {
			j = top;
			top = bottom;
			bottom = j;
		}

		float f = (float) ((startColor >> 24) & 255) / 255.0F;
		float f1 = (float) ((startColor >> 16) & 255) / 255.0F;
		float f2 = (float) ((startColor >> 8) & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) ((endColor >> 24) & 255) / 255.0F;
		float f5 = (float) ((endColor >> 16) & 255) / 255.0F;
		float f6 = (float) ((endColor >> 8) & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
		worldrenderer.func_181662_b(right, top, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
		worldrenderer.func_181662_b(left, top, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
		worldrenderer.func_181662_b(left, bottom, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
		worldrenderer.func_181662_b(right, bottom, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	private static int getTeamColor(EntityPlayer player) {
		Scoreboard scoreboard = player.getWorldScoreboard();
		ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getName());

		if (playerTeam != null) {
			String color = playerTeam.getColorPrefix();
			if (color.length() < 2) {
				return Color.WHITE.getRGB();
			}
			char colorChar = color.charAt(1);
			if (colorChar == '4' || colorChar == 'c') {
				return Color.RED.getRGB();
			}
			if (colorChar == '6' || colorChar == 'e') {
				return Color.YELLOW.getRGB();
			}
			if (colorChar == '2' || colorChar == 'a') {
				return Color.GREEN.getRGB();
			}
			if (colorChar == 'b' || colorChar == '3') {
				return Color.CYAN.getRGB();
			}
			if (colorChar == '9' || colorChar == '1') {
				return Color.BLUE.getRGB();
			}
			if (colorChar == 'd' || colorChar == '5') {
				return Color.MAGENTA.getRGB();
			}
			if (colorChar == 'f' || colorChar == '7') {
				return Color.WHITE.getRGB();
			}
			if (colorChar == '8' || colorChar == '0') {
				return Color.BLACK.getRGB();
			}
		}
		return Color.WHITE.getRGB();

	}
	
    public static void drawBox(Entity entity, Vec3 realPos, Vec3 lastPos, Color color) {
        final RenderManager renderManager = mc.getRenderManager();
        final Timer timer = mc.timer;

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        final double x = lastPos.xCoord + (realPos.xCoord - lastPos.xCoord) * timer.renderPartialTicks
                - renderManager.renderPosX;
        final double y = lastPos.yCoord + (realPos.yCoord - lastPos.yCoord) * timer.renderPartialTicks
                - renderManager.renderPosY;
        final double z = lastPos.zCoord + (realPos.zCoord - lastPos.zCoord) * timer.renderPartialTicks
                - renderManager.renderPosZ;

        final AxisAlignedBB entityBox = entity.getEntityBoundingBox();
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                entityBox.minX - entity.posX + x - 0.05D,
                entityBox.minY - entity.posY + y,
                entityBox.minZ - entity.posZ + z - 0.05D,
                entityBox.maxX - entity.posX + x + 0.05D,
                entityBox.maxY - entity.posY + y + 0.15D,
                entityBox.maxZ - entity.posZ + z + 0.05D
        );


        glColor(color.getRed(), color.getGreen(), color.getBlue(), 35);
        drawFilledBox(axisAlignedBB);
        GlStateManager.resetColor();
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    public static void glColor(int red, int green, int blue, int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }
    
    public static void drawFilledBox(AxisAlignedBB axisAlignedBB) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();

        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();

        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();

        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();

        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();

        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).func_181675_d();
        worldRenderer.func_181662_b(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).func_181675_d();
        tessellator.draw();
    }

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors (ARGB
	 * format). Args : x1, y1, x2, y2, topColor, bottomColor
	 */
	protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
		worldrenderer.func_181662_b((double) right, (double) top, (double) this.zLevel).func_181666_a(f1, f2, f3, f)
				.func_181675_d();
		worldrenderer.func_181662_b((double) left, (double) top, (double) this.zLevel).func_181666_a(f1, f2, f3, f)
				.func_181675_d();
		worldrenderer.func_181662_b((double) left, (double) bottom, (double) this.zLevel).func_181666_a(f5, f6, f7, f4)
				.func_181675_d();
		worldrenderer.func_181662_b((double) right, (double) bottom, (double) this.zLevel).func_181666_a(f5, f6, f7, f4)
				.func_181675_d();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y,
				color);
	}

	/**
	 * Renders the specified text to the screen. Args : renderer, string, x, y,
	 * color
	 */
	public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width,
	 * height
	 */
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b((double) (x + 0), (double) (y + height), (double) this.zLevel)
				.func_181673_a((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + height) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (x + width), (double) (y + height), (double) this.zLevel)
				.func_181673_a((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (x + width), (double) (y + 0), (double) this.zLevel)
				.func_181673_a((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (x + 0), (double) (y + 0), (double) this.zLevel)
				.func_181673_a((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1))
				.func_181675_d();
		tessellator.draw();
	}

	/**
	 * Draws a textured rectangle using the texture currently bound to the
	 * TextureManager
	 */
	public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b((double) (xCoord + 0.0F), (double) (yCoord + (float) maxV), (double) this.zLevel)
				.func_181673_a((double) ((float) (minU + 0) * f), (double) ((float) (minV + maxV) * f1))
				.func_181675_d();
		worldrenderer
				.func_181662_b((double) (xCoord + (float) maxU), (double) (yCoord + (float) maxV), (double) this.zLevel)
				.func_181673_a((double) ((float) (minU + maxU) * f), (double) ((float) (minV + maxV) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (xCoord + (float) maxU), (double) (yCoord + 0.0F), (double) this.zLevel)
				.func_181673_a((double) ((float) (minU + maxU) * f), (double) ((float) (minV + 0) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (xCoord + 0.0F), (double) (yCoord + 0.0F), (double) this.zLevel)
				.func_181673_a((double) ((float) (minU + 0) * f), (double) ((float) (minV + 0) * f1)).func_181675_d();
		tessellator.draw();
	}

	/**
	 * Draws a texture rectangle using the texture currently bound to the
	 * TextureManager
	 */
	public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn,
			int heightIn) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b((double) (xCoord + 0), (double) (yCoord + heightIn), (double) this.zLevel)
				.func_181673_a((double) textureSprite.getMinU(), (double) textureSprite.getMaxV()).func_181675_d();
		worldrenderer.func_181662_b((double) (xCoord + widthIn), (double) (yCoord + heightIn), (double) this.zLevel)
				.func_181673_a((double) textureSprite.getMaxU(), (double) textureSprite.getMaxV()).func_181675_d();
		worldrenderer.func_181662_b((double) (xCoord + widthIn), (double) (yCoord + 0), (double) this.zLevel)
				.func_181673_a((double) textureSprite.getMaxU(), (double) textureSprite.getMinV()).func_181675_d();
		worldrenderer.func_181662_b((double) (xCoord + 0), (double) (yCoord + 0), (double) this.zLevel)
				.func_181673_a((double) textureSprite.getMinU(), (double) textureSprite.getMinV()).func_181675_d();
		tessellator.draw();
	}

	/**
	 * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height,
	 * textureWidth, textureHeight
	 */
	public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height,
			float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b((double) x, (double) (y + height), 0.0D)
				.func_181673_a((double) (u * f), (double) ((v + (float) height) * f1)).func_181675_d();
		worldrenderer.func_181662_b((double) (x + width), (double) (y + height), 0.0D)
				.func_181673_a((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (x + width), (double) y, 0.0D)
				.func_181673_a((double) ((u + (float) width) * f), (double) (v * f1)).func_181675_d();
		worldrenderer.func_181662_b((double) x, (double) y, 0.0D).func_181673_a((double) (u * f), (double) (v * f1))
				.func_181675_d();
		tessellator.draw();
	}

	/**
	 * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
	 * anywhere in vanilla code.
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight) {
		float f = 1.0F / tileWidth;
		float f1 = 1.0F / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b((double) x, (double) (y + height), 0.0D)
				.func_181673_a((double) (u * f), (double) ((v + (float) vHeight) * f1)).func_181675_d();
		worldrenderer.func_181662_b((double) (x + width), (double) (y + height), 0.0D)
				.func_181673_a((double) ((u + (float) uWidth) * f), (double) ((v + (float) vHeight) * f1))
				.func_181675_d();
		worldrenderer.func_181662_b((double) (x + width), (double) y, 0.0D)
				.func_181673_a((double) ((u + (float) uWidth) * f), (double) (v * f1)).func_181675_d();
		worldrenderer.func_181662_b((double) x, (double) y, 0.0D).func_181673_a((double) (u * f), (double) (v * f1))
				.func_181675_d();
		tessellator.draw();
	}
}
