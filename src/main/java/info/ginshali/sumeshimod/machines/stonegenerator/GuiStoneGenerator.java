package info.ginshali.sumeshimod.machines.stonegenerator;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiStoneGenerator extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("sumeshimod", "textures/gui/container/stoneGenerator.png");
    private TileStoneGenerator tileStoneGenerator;

    public GuiStoneGenerator(InventoryPlayer inventory, TileStoneGenerator tileStoneGenerator)
    {
        super(new ContainerStoneGenerator(inventory, tileStoneGenerator));
        this.tileStoneGenerator = tileStoneGenerator;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String inventoryName = this.tileStoneGenerator.getInventoryName();
        String localizedName = I18n.format(inventoryName);

        this.fontRendererObj.drawString(
                localizedName,
                this.xSize / 2 - this.fontRendererObj.getStringWidth(localizedName) / 2,
                6,
                4210752);

        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        if (this.tileStoneGenerator.isWorking())
        {
            this.drawTexturedModalRect(x + 56, y + 36, 176, 0, 14, 14);
        }

        float progress = this.tileStoneGenerator.getProgressScaled(24.0F);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, (int) progress + 1, 16);
    }
}
