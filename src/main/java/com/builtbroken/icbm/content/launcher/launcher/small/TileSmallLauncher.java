package com.builtbroken.icbm.content.launcher.launcher.small;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

/**
 * Mainly a test launcher for devs this tile also can be used by players as a small portable launcher
 * Created by robert on 1/18/2015.
 */
public class TileSmallLauncher extends TileAbstractLauncher implements ISimpleItemRenderer, IGuiTile, IPostInit
{
    public TileSmallLauncher()
    {
        super("smallLauncher", Material.anvil, 1);
        this.hardness = 10f;
        this.resistance = 10f;
        this.bounds = new Cube(0, 0, 0, 1, .5, 1);
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallPortableLauncher), "IIB", "IIB", "CBC", 'I', OreNames.INGOT_IRON, 'B', OreNames.BLOCK_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (!super.onPlayerRightClick(player, side, hit))
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.flint_and_steel)
            {
                if (isServer())
                {
                    if (target != null && target.y() > -1)
                    {
                        double distance = target.distance(new Pos(this));
                        if (distance <= 200 && distance >= 20)
                        {
                            if(fireMissile(target))
                            {
                                ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " fired a missile from " + this);
                            }
                            else
                            {
                                ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " attempted to fire a missile from " + this);
                            }
                        }
                        else
                        {
                            LanguageUtility.addChatToPlayer(player, getInventoryName() + ".invaliddistance");
                        }
                    }
                    else
                    {
                        LanguageUtility.addChatToPlayer(player, getInventoryName() + ".invalidtarget");
                    }
                }
                return true;
            }
            else
            {
                if (isServer())
                {
                    if (player instanceof EntityPlayerMP)
                    {
                        Engine.instance.packetHandler.sendToPlayer(getDescPacket(), (EntityPlayerMP) player);
                    }
                    openGui(player, ICBM.INSTANCE);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.SMALL.ordinal();
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallLauncher();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.gravel.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:portableLauncher.container";
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.PORTABLE_LAUNCHER_MODEL.renderAllExcept("rail");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 2, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.PORTABLE_LAUNCHER_MODEL.renderAll();
        GL11.glPopMatrix();

        IMissile missile = getMissile();
        //Render missile
        if (missile != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.4f, pos.zf() + 0.5f);
            if (missile instanceof ICustomMissileRender)
            {
                GL11.glTranslatef(0, (float) (missile.getHeight() / 2.0), 0);
                if (!((ICustomMissileRender) missile).renderMissileInWorld(0, 0, frame))
                {
                    //TODO Either error or render fake model
                }
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSmallLauncher(player, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSmallLauncher(this, player);
    }

    @Override
    public void doUpdateGuiUsers()
    {
        if (ticks % 3 == 0)
        {
            this.sendPacketToGuiUsers(getDescPacket());
        }
    }
}
