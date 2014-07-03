package info.ginshali.sumeshimod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import info.ginshali.sumeshimod.machines.stonegenerator.BlockStoneGenerator;
import info.ginshali.sumeshimod.machines.stonegenerator.TileStoneGenerator;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Mod(
        modid = SumeshiMod.MODID,
        name = SumeshiMod.MODNAME,
        version = SumeshiMod.VERSION,
        dependencies = SumeshiMod.DEPENDENCY
)
public class SumeshiMod
{
    public static final String MODID = "SumeshiMod";
    public static final String MODNAME = "SumeshiMod";
    public static final String VERSION = "0.1_alpha";
    public static final String DEPENDENCY = "required-after:Forge@[10.12.2.1121,);";

    public static Block blockStoneGenerator;
    public static Block blockStoneGenerator_on;

    @Instance("SumeshiMod")
    public static SumeshiMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        blockStoneGenerator =
                new BlockStoneGenerator(false)
                        .setHardness(3.5F)
                        .setResistance(17.5F)
                        .setStepSound(Block.soundTypeStone)
                        .setCreativeTab(CreativeTabs.tabDecorations)
                        .setBlockName("stonegenerator");
        blockStoneGenerator.setHarvestLevel("pickaxe", 1);

        blockStoneGenerator_on =
                new BlockStoneGenerator(true)
                        .setHardness(3.5F)
                        .setResistance(17.5F)
                        .setStepSound(Block.soundTypeStone)
                        .setLightLevel(13.0F / 15.0F)
                        .setBlockName("stonegenerator.on");
        blockStoneGenerator_on.setHarvestLevel("pickaxe", 1);

        GameRegistry.registerBlock(blockStoneGenerator, "stonegenerator");
        GameRegistry.registerBlock(blockStoneGenerator_on, "stonegenerator.on");
        GameRegistry.registerTileEntity(TileStoneGenerator.class, "StoneGenerator");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        GameRegistry.addRecipe(
                new ItemStack(blockStoneGenerator),
                "sss",
                "gpg",
                "sss",
                's', Blocks.cobblestone,
                'g', Blocks.glass,
                'p', Items.iron_pickaxe);
    }
}
