package icbm.content.tile.emptower;

import cpw.mods.fml.common.Optional;
import icbm.Reference;
import icbm.ICBM;
import icbm.explosion.blast.BlastEMP;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import resonant.api.IRedstoneReceptor;
import resonant.api.map.IRadarDetectable;
import resonant.api.map.RadarRegistry;

import resonant.lib.content.prefab.java.TileElectric;
import resonant.lib.multiblock.reference.IMultiBlock;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketIDReceiver;
import resonant.lib.transform.vector.Vector3;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class TileEMPTower extends TileElectric implements IMultiBlock, IRedstoneReceptor, SimpleComponent, IRadarDetectable, IPacketIDReceiver
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public float rotation = 0;
    private float rotationDelta, prevXuanZhuanLu = 0;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    private int cooldownTicks = 0;

    // The EMP explosion radius
    public int empRadius = 60;

    public TileEMPTower()
    {
        super(Material.iron);
        RadarRegistry.register(this);
        updateCapacity();
    }

    @Override
    public void invalidate()
    {
        RadarRegistry.unregister(this);
        super.invalidate();
    }

    @Override
    public void update()
    {
        super.update();

        if (!isReady())
        {
            cooldownTicks--;
        }

        if (ticks() % 20 == 0 && getEnergyStorage().getEnergy() > 0)
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Reference.PREFIX + "machinehum", 0.5F, (float)(0.85F * getEnergyStorage().getEnergy() / getEnergyStorage().getEnergyCapacity()));

        rotationDelta = (float) (Math.pow(getEnergyStorage().getEnergy() / getEnergyStorage().getEnergyCapacity(), 2) * 0.5);
        rotation += rotationDelta;
        if (rotation > 360)
            rotation = 0;

        prevXuanZhuanLu = rotationDelta;
    }

    @Override
    public boolean read(ByteBuf data, int id, EntityPlayer player, PacketType type)
    {
            switch (id)
            {
                case 0:
                {
                    getEnergyStorage().setEnergy(data.readDouble());
                    empRadius = data.readInt();
                    empMode = data.readByte();
                    return true;
                }
                case 1:
                {
                    empRadius = data.readInt();
                    updateCapacity();
                    return true;
                }
                case 2:
                {
                    empMode = data.readByte();
                    return true;
                }
            }
            return false;

    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public boolean isReady()
    {
        return getCooldown() <= 0;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public int getCooldown()
    {
        return cooldownTicks;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public int getMaxCooldown()
    {
        return 120;
    }

    private void updateCapacity()
    {
        this.dcNode().setCapacity(Math.max(300000000 * (this.empRadius / MAX_RADIUS), 1000000000));
        this.dcNode().setMaxReceive(this.dcNode().getEnergyCapacity() / 50);
        this.dcNode().setMaxExtract((long) (this.dcNode().getEnergyCapacity() / .9));
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, this.dcNode().getEnergy(), this.empRadius, this.empMode);
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.empRadius = par1NBTTagCompound.getInteger("banJing");
        this.empMode = par1NBTTagCompound.getByte("muoShi");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("banJing", this.empRadius);
        par1NBTTagCompound.setByte("muoShi", this.empMode);
    }

    @Callback(limit = 1)
    @Optional.Method(modid = "OpenComputers")
    public boolean fire()
    {
        if (this.getEnergyStorage().checkExtract())
        {
            if (isReady())
            {
                switch (this.empMode)
                {
                    default:
                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
                        break;
                    case 1:
                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
                        break;
                    case 2:
                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
                        break;
                }
                this.getEnergyStorage().extractEnergy();
                this.cooldownTicks = getMaxCooldown();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPowerOn()
    {
        fire();
    }

    @Override
    public void onPowerOff()
    {
    }

    @Override
    public boolean use(EntityPlayer entityPlayer, int side, Vector3 hit)
    {
        if(!this.worldObj.isRemote)
        	entityPlayer.openGui(ICBM.INSTANCE, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public List<Vector3> getMultiBlockVectors()
    {
        List<Vector3> list = new ArrayList();
        list.add(new Vector3(0, 1, 0));
        return  list;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public String getComponentName()
    {
        return "emptower";
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public byte getEmpMode()
    {
        return empMode;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public void setEmpMode(byte empMode)
    {
        if (empMode >= 0 && empMode <= 2)
            this.empMode = empMode;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public void empMissiles()
    {
        this.empMode = 1;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public void empAll()
    {
        this.empMode = 0;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public void empElectronics()
    {
        this.empMode = 2;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public int getEmpRadius()
    {
        return empRadius;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public int getMaxEmpRadius()
    {
        return MAX_RADIUS;
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public void setEmpRadius(int empRadius)
    {
        int prev = getEmpRadius();
        this.empRadius = Math.min(Math.max(empRadius, 0), MAX_RADIUS);
        if (prev != getEmpRadius())
            updateCapacity();
    }

    @Callback
    @Optional.Method(modid = "OpenComputers")
    public static int getMaxRadius()
    {
        return MAX_RADIUS;
    }

	@Override
	public boolean canDetect(TileEntity arg0) {
		if(this.getEnergyStorage().checkExtract())
			return true;
		return false;
	}

}
