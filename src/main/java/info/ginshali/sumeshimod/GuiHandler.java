package info.ginshali.sumeshimod;

import cpw.mods.fml.common.network.IGuiHandler;
import info.ginshali.sumeshimod.machines.stonegenerator.ContainerStoneGenerator;
import info.ginshali.sumeshimod.machines.stonegenerator.GuiStoneGenerator;
import info.ginshali.sumeshimod.machines.stonegenerator.TileStoneGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by ginsh_000 on 2014/07/01.
 */
public class GuiHandler implements IGuiHandler
{
    public static final int BLOCK_GENERATOR_GUI_ID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (!world.blockExists(x, y, z))
        {
            return null;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID)
        {
            case BLOCK_GENERATOR_GUI_ID:
                if (tileEntity instanceof TileStoneGenerator)
                {
                    return new ContainerStoneGenerator(player.inventory, (TileStoneGenerator) tileEntity);
                }
                break;
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (!world.blockExists(x, y, z))
        {
            return null;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID)
        {
            case BLOCK_GENERATOR_GUI_ID:
                if (tileEntity instanceof TileStoneGenerator)
                {
                    return new GuiStoneGenerator(player.inventory, (TileStoneGenerator) tileEntity);
                }
                break;
        }

        return null;
    }
}
