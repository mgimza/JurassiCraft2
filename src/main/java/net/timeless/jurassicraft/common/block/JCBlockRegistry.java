package net.timeless.jurassicraft.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.timeless.jurassicraft.common.api.ISubBlocksBlock;
import net.timeless.jurassicraft.common.block.tree.*;
import net.timeless.jurassicraft.common.dinosaur.Dinosaur;
import net.timeless.jurassicraft.common.entity.base.JCEntityRegistry;
import net.timeless.jurassicraft.common.tileentity.*;

import java.util.ArrayList;
import java.util.List;

public class JCBlockRegistry
{
    public static final int numOfTrees = 1;

    //tree blocks
    public static Block[] planks;
    public static Block[] woods;
    public static Block[] leaves;
    public static Block[] saplings;

    //wood products
    public static Block[] doors;
    public static Block[] fences;
    public static Block[] slabs;
    public static Block[] doubleSlabs;
    public static Block[] stairs;

    public static List<BlockFossil> fossils;
    public static List<BlockEncasedFossil> encased_fossils;

    public static BlockCleaningStation cleaning_station;
    public static BlockFossilGrinder fossil_grinder;
    public static BlockDnaSequencer dna_sequencer;
    public static BlockDnaSynthesizer dna_synthesizer;
    public static BlockEmbryonicMachine embryonic_machine;
    public static BlockEmbryoCalcificationMachine embryo_calcification_machine;
    public static BlockIncubator incubator;
    public static BlockDNAExtractor dna_extractor;

    public static BlockAmber amber_ore;

    public static BlockCage cage_small;

    public static Block gypsum_cobblestone;
    public static Block gypsum_stone;
    public static Block gypsum_bricks;

    public void register()
    {
        fossils = new ArrayList<BlockFossil>();
        encased_fossils = new ArrayList<BlockEncasedFossil>();

        cleaning_station = new BlockCleaningStation();
        fossil_grinder = new BlockFossilGrinder();
        dna_sequencer = new BlockDnaSequencer();
        dna_synthesizer = new BlockDnaSynthesizer();
        embryonic_machine = new BlockEmbryonicMachine();
        embryo_calcification_machine = new BlockEmbryoCalcificationMachine();
        incubator = new BlockIncubator();
        dna_extractor = new BlockDNAExtractor();

        amber_ore = new BlockAmber();

        gypsum_stone = new BlockBasic(Material.rock, "Gypsum Stone").setDrop(gypsum_cobblestone).setHardness(1.0F);
        gypsum_cobblestone = new BlockBasic(Material.rock, "Gypsum Cobblestone").setHardness(1.0F);
        gypsum_bricks = new BlockBasic(Material.rock, "Gypsum Bricks").setHardness(1.0F);

        cage_small = new BlockCage("Small");

        List<Dinosaur> dinosaurs = JCEntityRegistry.getDinosaurs();

        int blocksToCreate = (int) (Math.ceil(((float) dinosaurs.size()) / 16.0F));

        for (int i = 0; i < blocksToCreate; i++)
        {
            BlockFossil fossil = new BlockFossil(i * 16);
            BlockEncasedFossil encasedFossil = new BlockEncasedFossil(i * 16);

            fossils.add(fossil);
            encased_fossils.add(encasedFossil);

            registerBlock(fossil, "Fossil Block " + i);
            registerBlock(encasedFossil, "Encased Fossil " + i);
        }

        registerBlock(amber_ore, "Amber Ore");

        registerBlock(gypsum_stone, "Gypsum Stone");
        registerBlock(gypsum_cobblestone, "Gypsum Cobblestone");
        registerBlock(gypsum_bricks, "Gypsum Bricks");

        //initialize EnumType meta lookup
        EnumType.GINKGO.setMetaLookup();

        //initialize arrays
        planks = new Block[numOfTrees];
        woods = new Block[numOfTrees];
        leaves = new Block[numOfTrees];
        saplings = new Block[numOfTrees];
        doors = new Block[numOfTrees];
        fences = new Block[numOfTrees];
        slabs = new Block[numOfTrees];
        doubleSlabs = new Block[numOfTrees];
        stairs = new Block[numOfTrees];

        //initialize blocks within arrays
        for (int i = 0; i < numOfTrees; i++)
        {
            EnumType type = EnumType.getMetaLookup()[i];
            String typeName = type.getName();

            planks[i] = new BlockJCPlanks(type, typeName);
            woods[i] = new BlockJCLog(type, typeName);
            leaves[i] = new BlockJCLeaves(type, typeName);
            saplings[i] = new BlockJCSapling(type, typeName);

            GameRegistry.registerBlock(planks[i], typeName + "_planks");
            GameRegistry.registerBlock(woods[i], typeName + "_log");
            GameRegistry.registerBlock(leaves[i], typeName + "_leaves");
            GameRegistry.registerBlock(saplings[i], typeName + "_sapling");

            OreDictionary.registerOre("logWood", woods[i]);
            OreDictionary.registerOre("plankWood", planks[i]);
            OreDictionary.registerOre("treeLeaves", leaves[i]);
            OreDictionary.registerOre("treeSapling", saplings[i]);

            Blocks.fire.setFireInfo(leaves[i], 30, 60);
            Blocks.fire.setFireInfo(planks[i], 5, 20);
            Blocks.fire.setFireInfo(woods[i], 5, 5);
        }

        registerBlockTileEntity(TileCage.class, cage_small, "Cage Small");

        registerBlockTileEntity(TileCleaningStation.class, cleaning_station, "Cleaning Station");
        registerBlockTileEntity(TileFossilGrinder.class, fossil_grinder, "Fossil Grinder");
        registerBlockTileEntity(TileDnaSequencer.class, dna_sequencer, "DNA Sequencer");
        registerBlockTileEntity(TileDnaSynthesizer.class, dna_synthesizer, "DNA Synthesizer");
        registerBlockTileEntity(TileEmbryonicMachine.class, embryonic_machine, "Embryonic Machine");
        registerBlockTileEntity(TileEmbryoCalcificationMachine.class, embryo_calcification_machine, "Embryo Calcification Machine");
        registerBlockTileEntity(TileDNAExtractor.class, dna_extractor, "DNA Extractor");
        registerBlockTileEntity(TileIncubator.class, incubator, "Incubator");
    }

    public BlockFossil getFossilBlock(Dinosaur dinosaur)
    {
        return getFossilBlock(JCEntityRegistry.getDinosaurId(dinosaur));
    }

    private int getBlockId(int dinosaurId)
    {
        return (int) (Math.floor((((float) dinosaurId + 1.0F) / 16.0F) - 0.0625F));
    }

    public BlockEncasedFossil getEncasedFossil(Dinosaur dinosaur)
    {
        return getEncasedFossil(JCEntityRegistry.getDinosaurId(dinosaur));
    }

    public BlockEncasedFossil getEncasedFossil(int id)
    {
        return encased_fossils.get(getBlockId(id));
    }

    public BlockFossil getFossilBlock(int id)
    {
        return fossils.get(getBlockId(id));
    }

    public int getDinosaurId(BlockFossil fossil, int metadata)
    {
        return (fossils.indexOf(fossil) * 15) + metadata;
    }

    public int getDinosaurId(BlockEncasedFossil fossil, int metadata)
    {
        return (encased_fossils.indexOf(fossil) * 16) + metadata;
    }

    public int getMetadata(int id)
    {
        return id % 16;
    }

    public int getMetadata(Dinosaur dinosaur)
    {
        return getMetadata(JCEntityRegistry.getDinosaurId(dinosaur));
    }

    public void registerBlockTileEntity(Class<? extends TileEntity> tileEntity, Block block, String name)
    {
        registerBlock(block, name);

        GameRegistry.registerTileEntity(tileEntity, "jurassicraft:" + name.toLowerCase().replaceAll(" ", "_"));
    }

    public void registerBlock(Block block, String name)
    {
        name = name.toLowerCase().replaceAll(" ", "_");

        if (block instanceof ISubBlocksBlock)
            GameRegistry.registerBlock(block, ((ISubBlocksBlock) block).getItemBlockClass(), name);
        else
            GameRegistry.registerBlock(block, name);
    }
}
