package info.ginshali.sumeshimod.machines.stonegenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerStoneGenerator extends Container
{
    static final int LAVA_SLOT_ID = 0;
    static final int WATER_SLOT_ID = 1;
    static final int OUTPUT_SLOT_ID = 2;
    static final int INVENTORY_START = OUTPUT_SLOT_ID + 1;
    static final int INVENTORY_SIZE = 27;
    static final int INVENTORY_END = INVENTORY_START + INVENTORY_SIZE - 1;
    static final int HOTBAR_START = INVENTORY_END + 1;
    static final int HOTBAR_SIZE = 9;
    static final int HOTBAR_END = HOTBAR_START + HOTBAR_SIZE - 1;

    private TileStoneGenerator tileStoneGenerator;

    private float lastProgressValue = 0.0F;

    public ContainerStoneGenerator(InventoryPlayer inventory, TileStoneGenerator tileStoneGenerator)
    {
        this.tileStoneGenerator = tileStoneGenerator;

        addSlots(inventory);
    }

    private void addSlots(InventoryPlayer inventoryPlayer)
    {
        // Add machine slots
        this.addSlotToContainer(new Slot(tileStoneGenerator, LAVA_SLOT_ID, 56, 53));
        this.addSlotToContainer(new Slot(tileStoneGenerator, WATER_SLOT_ID, 56, 17));
        this.addSlotToContainer(new SlotFurnace(inventoryPlayer.player, tileStoneGenerator, OUTPUT_SLOT_ID, 116, 35));

        // Add player inventory slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Add player action bar
        for (int i = 0; i < 9; i++)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting)
    {
        super.addCraftingToCrafters(crafting);

        crafting.sendProgressBarUpdate(this, 0, this.tileStoneGenerator.progress);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); i++)
        {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.lastProgressValue != this.tileStoneGenerator.progress)
            {
                crafting.sendProgressBarUpdate(this, 0, this.tileStoneGenerator.progress);
            }
        }

        this.lastProgressValue = this.tileStoneGenerator.progress;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int key, int value)
    {
        super.updateProgressBar(key, value);

        if (key == 0)
        {
            this.tileStoneGenerator.progress = value;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return this.tileStoneGenerator.isUseableByPlayer(var1);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
    {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotItem = slot.getStack();
            itemStack = slotItem.copy();

            if (slotIndex == OUTPUT_SLOT_ID)
            {
                if (!this.mergeItemStack(slotItem, INVENTORY_START, HOTBAR_END + 1, true))
                {
                    return null;
                }
                slot.onSlotChange(slotItem, itemStack);
            } else if (slotIndex != LAVA_SLOT_ID && slotIndex != WATER_SLOT_ID)
            {
                if ((slotItem.getItem() == Items.lava_bucket) && !this.getSlot(LAVA_SLOT_ID).getHasStack())
                {
                    if (!this.mergeItemStack(slotItem, LAVA_SLOT_ID, LAVA_SLOT_ID + 1, false))
                    {
                        return null;
                    }
                } else if ((slotItem.getItem() == Items.water_bucket) && !this.getSlot(WATER_SLOT_ID).getHasStack())
                {
                    if (!this.mergeItemStack(slotItem, WATER_SLOT_ID, WATER_SLOT_ID + 1, false))
                    {
                        return null;
                    }
                } else if (slotIndex >= INVENTORY_START && slotIndex < INVENTORY_END)
                {
                    if (!this.mergeItemStack(slotItem, HOTBAR_START, HOTBAR_END + 1, false))
                    {
                        return null;
                    }
                } else if (slotIndex >= HOTBAR_START && slotIndex < HOTBAR_END)
                {
                    if (!this.mergeItemStack(slotItem, INVENTORY_START, INVENTORY_END + 1, false))
                    {
                        return null;
                    }
                }
            } else
            {
                if (!this.mergeItemStack(slotItem, INVENTORY_START, HOTBAR_END + 1, false))
                {
                    return null;
                }
            }

            if (slotItem.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            } else
            {
                slot.onSlotChanged();
            }

            if (slotItem.stackSize == itemStack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(entityPlayer, slotItem);
        }

        return itemStack;
    }
}
