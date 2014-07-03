package info.ginshali.sumeshimod.machines.stonegenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import info.ginshali.sumeshimod.GuiHandler;
import info.ginshali.sumeshimod.SumeshiMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by ginsh_000 on 2014/07/01.
 */
public class BlockStoneGenerator extends BlockContainer
{
    @SideOnly(Side.CLIENT) protected IIcon blockIconFront;
    @SideOnly(Side.CLIENT) protected IIcon blockIconTop;
    private boolean isWorking;

    private static boolean isUpdating;

    public BlockStoneGenerator(boolean isWorking)
    {
        super(Material.rock);
        this.isWorking = isWorking;
    }

    @Override
    public Item getItemDropped(int var1, Random rand, int var3)
    {
        return Item.getItemFromBlock(SumeshiMod.blockStoneGenerator);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        if (side == 1)
        {
            return blockIconTop;
        }
        else if (side == 0)
        {
            return blockIconTop;
        }
        else if (side != metadata)
        {
            return this.blockIcon;
        }
        else
        {
            return this.blockIconFront;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconReg)
    {
        blockIcon = iconReg.registerIcon("sumeshimod:stoneGenerator_side");
        blockIconFront = iconReg.registerIcon("sumeshimod:stoneGenerator_front_" + (this.isWorking ? "on" : "off"));
        blockIconTop = iconReg.registerIcon("sumeshimod:stoneGenerator_top");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            entityPlayer.openGui(SumeshiMod.instance, GuiHandler.BLOCK_GENERATOR_GUI_ID, world, x, y, z);
            return true;
        }
    }

    public static void updateStoneGeneratorBlockState(boolean isWorking, World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);

        isUpdating = true;

        if (isWorking)
        {
            world.setBlock(x, y, z, SumeshiMod.blockStoneGenerator_on);
        }
        else
        {
            world.setBlock(x, y, z, SumeshiMod.blockStoneGenerator);
        }

        isUpdating = false;
        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);

        if (tile != null)
        {
            tile.validate();
            world.setTileEntity(x, y, z, tile);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileStoneGenerator();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
        int side = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;

        if (side == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }
        else if (side == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }
        else if (side == 2)
        {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }
        else if (side == 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
        if (isUpdating) { return; }

        TileStoneGenerator tileStoneGenerator = (TileStoneGenerator) world.getTileEntity(x, y, z);
        if (tileStoneGenerator != null)
        {
            for (int i = 0; i < tileStoneGenerator.getSizeInventory(); i++)
            {
                ItemStack itemStack = tileStoneGenerator.getStackInSlot(i);
                if (itemStack != null)
                {
                    float dx = world.rand.nextFloat() * 0.8F + 0.1F;
                    float dy = world.rand.nextFloat() * 0.8F + 0.1F;
                    float dz = world.rand.nextFloat() * 0.8F + 0.1F;
                    EntityItem dropItem = new EntityItem(world, (float) x + dx, (float) y + dy, (float) z + dz, new ItemStack(itemStack.getItem(), itemStack.stackSize, itemStack.getItemDamage()));
                    float velocity = 0.05F;
                    dropItem.motionX = (float) world.rand.nextGaussian() * velocity;
                    dropItem.motionY = (float) world.rand.nextGaussian() * velocity + 0.2F;
                    dropItem.motionZ = (float) world.rand.nextGaussian() * velocity;
                    world.spawnEntityInWorld(dropItem);
                }
            }
        }

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        if (this.isWorking)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            float centerX = (float)x + 0.5F;
            float centerY = (float)y + (rand.nextFloat() * 6.0F / 16.0F);
            float centerZ = (float)z + 0.5F;
            float faceDist = 0.52F;
            float horizDelta = rand.nextFloat() * 0.6F - 0.3F;

            if (metadata == 4)
            {
                world.spawnParticle("smoke", centerX - faceDist, centerY, centerZ + horizDelta, 0, 0, 0);
            }
            if (metadata == 5)
            {
                world.spawnParticle("smoke", centerX + faceDist, centerY, centerZ + horizDelta, 0, 0, 0);
            }
            if (metadata == 2)
            {
                world.spawnParticle("smoke", centerX + horizDelta, centerY, centerZ - faceDist, 0, 0, 0);
            }
            if (metadata == 3)
            {
                world.spawnParticle("smoke", centerX + horizDelta, centerY, centerZ + faceDist, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int var4)
    {
        return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int par2, int par3, int par4)
    {
        return Item.getItemFromBlock(SumeshiMod.blockStoneGenerator);
    }
}
