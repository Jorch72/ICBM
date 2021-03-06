package com.builtbroken.icbm.content.fragments;

import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/2016.
 */
@SideOnly(Side.CLIENT)
public class RenderFragment extends Render
{
    /** Backup texture */
    private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    public void doRender(EntityFragment entity, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_)
    {
        FragmentType type = entity.fragmentType;

        if (type == FragmentType.BLOCK)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(xx, yy, zz);
            RenderUtility.renderCube(entity.renderShape.toAABB(), entity.fragmentMaterial == null ? Blocks.stone : entity.fragmentMaterial);
            GL11.glPopMatrix();
        }
        else if (type == FragmentType.BLAZE)
        {
            doRenderFireBall(entity, xx, yy, zz, p_76986_8_, p_76986_9_, 0.3f);
        }
        else
        {
            this.bindEntityTexture(entity);
            doRenderArrow(entity, xx, yy, zz, p_76986_8_, p_76986_9_);
        }
    }

    public void doRenderFireBall(EntityFragment entity, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_, float scale)
    {
        GL11.glPushMatrix();
        this.renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);

        GL11.glTranslatef((float) xx, (float) yy, (float) zz);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(scale / 1.0F, scale / 1.0F, scale / 1.0F);

        IIcon iicon = Items.fire_charge.getIconFromDamage(0);
        Tessellator tessellator = Tessellator.instance;

        float minU = iicon.getMinU();
        float maxU = iicon.getMaxU();
        float minV = iicon.getMinV();
        float maxV = iicon.getMaxV();

        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (0.0F - f9), 0.0D, (double) minU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (0.0F - f9), 0.0D, (double) maxU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (1.0F - f9), 0.0D, (double) maxU, (double) minV);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (1.0F - f9), 0.0D, (double) minU, (double) minV);
        tessellator.draw();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void doRenderArrow(EntityFragment entity, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) xx, (float) yy, (float) zz);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_76986_9_ - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_76986_9_, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float) (0 + b0 * 10) / 32.0F;
        float f5 = (float) (5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float) (5 + b0 * 10) / 32.0F;
        float f9 = (float) (10 + b0 * 10) / 32.0F;
        float f10 = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) f7, (double) f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) f7, (double) f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) f6, (double) f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) f6, (double) f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) f7, (double) f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) f7, (double) f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double) f2, (double) f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double) f3, (double) f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double) f3, (double) f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double) f2, (double) f5);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return arrowTextures;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityFragment) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
