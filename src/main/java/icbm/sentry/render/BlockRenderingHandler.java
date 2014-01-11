package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.turret.BlockTurret.TurretType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Renders a block as an item.
 * 
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class BlockRenderingHandler implements ISimpleBlockRenderingHandler
{
	public static final int ID = RenderingRegistry.getNextAvailableRenderId();
	public static ResourceLocation GUN_TURRET_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_neutral.png");
	public static ResourceLocation LASER_TURRET_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_neutral.png");
	public static ResourceLocation AA_TURRET_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "aa_turret_neutral.png");

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			GL11.glPushMatrix();

			if (metadata == TurretType.GUN.ordinal())
			{
				/** Render the gun turret. */
				GL11.glTranslatef(0.1f, 1f, 0f);
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(GUN_TURRET_TEXTURE);
				RenderGunTurret.render(0, 0);
			}
			if (metadata == TurretType.LASER.ordinal())
			{
				/** Render the gun turret. */
				GL11.glTranslatef(0.4f, 1.4f, 0f);
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(LASER_TURRET_TEXTURE);
				RenderLaserTurret.render(0, 0);
			}
			else if (metadata == TurretType.AA.ordinal())
			{
				/** Render the gun turret. */
				GL11.glTranslatef(0.2f, 0.3f, 0);
				GL11.glScalef(0.45f, 0.45f, 0.45f);
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(AA_TURRET_TEXTURE);
				RenderAAGun.render(0, 0);
			}
			else if (metadata == TurretType.RAILGUN.ordinal())
			{
				/** Renders the railgun. */
				GL11.glTranslatef(0f, 0.9f, 0f);
				GL11.glRotatef(180f, 0f, 0f, 1f);

				FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderRailGun.TEXTURE);
				RenderRailGun.MODEL.render(90, 0, 0.0625F);
			}

			GL11.glPopMatrix();
		}

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return ID;
	}

}