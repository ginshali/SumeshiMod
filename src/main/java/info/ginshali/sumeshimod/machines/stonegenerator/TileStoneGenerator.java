package info.ginshali.sumeshimod.machines.stonegenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by ginsh_000 on 2014/07/01.
 */
public class TileStoneGenerator extends TileEntity implements ISidedInventory
{
    private static final int LAVA_SLOT_ID = 0;
    private static final int WATER_SLOT_ID = 1;
    private static final int OUTPUT_SLOT_ID = 2;
    private static final int SLOT_COUNT = OUTPUT_SLOT_ID + 1;
    private static final int MAX_PROGRESS = 50;

    private static final int[] slotsTop = new int[] {0};
    private static final int[] slotsBottom = new int[] {2, 1};
    private static final int[] slotsSides = new int[] {1};

    public int maxProgress = MAX_PROGRESS;
    public int progress;
    private boolean lastWorkingState;

    private ItemStack[] slots = new ItemStack[3];



    @Override
    public int getSizeInventory()
    {
        return this.slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.slots[slot];
    }

    @Override
    public ItemStack decrStackSize(int slotId, int amount)
    {
        if (this.slots[slotId] != null)
        {
            ItemStack temp;

            if (this.slots[slotId].stackSize <= amount)
            {
                temp = this.slots[slotId];
                this.slots[slotId] = null;
                return temp;
            }
            else
            {
                temp = this.slots[slotId].splitStack(amount);

                if (this.slots[slotId].stackSize == 0)
                {
                    this.slots[slotId] = null;
                }

                return temp;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotId)
    {
        if (this.slots[slotId] != null)
        {
            ItemStack temp = this.slots[slotId];
            this.slots[slotId] = null;
            return temp;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemStack)
    {
        this.slots[slotId] = itemStack;
        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
        {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName()
    {
        return "Stone Generator";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean isWorking()
    {
        return progress > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound comp)
    {
        super.readFromNBT(comp);

        NBTTagCompound lavaComp = (NBTTagCompound) comp.getTag("LavaInventory");
        NBTTagCompound waterComp = (NBTTagCompound) comp.getTag("WaterInventory");
        NBTTagCompound outputComp = (NBTTagCompound) comp.getTag("OutputInventory");

        NBTTagList tagList = comp.getTagList("Items", 10);
        this.slots = new ItemStack[SLOT_COUNT];

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound slotComp = tagList.getCompoundTagAt(i);
            byte slotId = slotComp.getByte("Slot");

            if (slotId >= 0 && slotId < SLOT_COUNT)
            {
                this.slots[slotId] = ItemStack.loadItemStackFromNBT(slotComp);
            }
        }

        this.progress = comp.getInteger("Progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound comp)
    {
        super.writeToNBT(comp);

        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < this.slots.length; i++)
        {
            if (this.slots[i] != null)
            {
                NBTTagCompound slotComp = new NBTTagCompound();
                slotComp.setByte("Slot", (byte)i);
                this.slots[i].writeToNBT(slotComp);
                tagList.appendTag(slotComp);
            }
        }

        comp.setTag("Items", tagList);
        comp.setInteger("Progress", this.progress);
    }

    @SideOnly(Side.CLIENT)
    public float getProgressScaled(float scale)
    {
        return ((float)this.progress / (float)this.maxProgress) * scale;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.worldObj.isRemote)
        {
            if (this.progress > 0)
            {
                this.progress++;
            }
            return;
        }

        boolean dirtyFlag = false;
        boolean workingState = getWorkingState();
        int initialProgress = this.progress;

        if (workingState)
        {
            this.progress++;

            if (this.progress >= this.maxProgress)
            {
                this.progress = 0;
                dirtyFlag = true;

                if (this.slots[OUTPUT_SLOT_ID] == null)
                {
                    this.slots[OUTPUT_SLOT_ID] = new ItemStack(Blocks.cobblestone, 1);
                }
                else
                {
                    this.slots[OUTPUT_SLOT_ID].stackSize++;
                }

                if (this.slots[OUTPUT_SLOT_ID].stackSize >= this.slots[OUTPUT_SLOT_ID].getMaxStackSize())
                {
                    workingState = false;
                }
            }
        }
        else
        {
            this.progress = 0;
        }

        if (workingState != lastWorkingState)
        {
            dirtyFlag = true;
            BlockStoneGenerator.updateStoneGeneratorBlockState(workingState, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }

        lastWorkingState = workingState;

        if (dirtyFlag || initialProgress != this.progress)
        {
            this.markDirty();
        }
    }

    private boolean getWorkingState()
    {
        boolean lavaAvailable =
                this.slots[LAVA_SLOT_ID] != null
                        && this.slots[LAVA_SLOT_ID].getItem() == Items.lava_bucket
                        && this.slots[LAVA_SLOT_ID].stackSize > 0;
        boolean waterAvailable =
                this.slots[WATER_SLOT_ID] != null
                        && this.slots[WATER_SLOT_ID].getItem() == Items.water_bucket
                        && this.slots[WATER_SLOT_ID].stackSize > 0;
        boolean hasOutputSpace =
                this.slots[OUTPUT_SLOT_ID] == null ||
                        (this.slots[OUTPUT_SLOT_ID] != null
                         && this.slots[OUTPUT_SLOT_ID].stackSize < this.slots[OUTPUT_SLOT_ID].getMaxStackSize());

        return lavaAvailable && waterAvailable && hasOutputSpace;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this)
        {
            return false;
        }
        else
        {
            double distance = player.getDistanceSq(
                    (double) this.xCoord + 0.5D,
                    (double) this.yCoord + 0.5D,
                    (double) this.zCoord + 0.5D);

            return distance <= 64.0;
        }
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack)
    {
        if (slot == LAVA_SLOT_ID)
        {
            return itemStack.getItem() == Items.lava_bucket;
        }
        else if (slot == WATER_SLOT_ID)
        {
            return itemStack.getItem() == Items.water_bucket;
        }
        else if (slot == OUTPUT_SLOT_ID)
        {
            return false;
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side == 0 ? slotsBottom : (side == 1 ? slotsTop : slotsSides);
    }

    @Override
    public boolean canInsertItem(int slotId, ItemStack itemStack, int side)
    {
        return this.isItemValidForSlot(slotId, itemStack);
    }

    @Override
    public boolean canExtractItem(int slotId, ItemStack itemStack, int side)
    {
        if (slotId == 2 && itemStack.getItem() == Item.getItemFromBlock(Blocks.cobblestone)) { return true; }
        else { return false; }
    }
}
