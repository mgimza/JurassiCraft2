package org.jurassicraft.server.item;

import java.util.Locale;

import org.jurassicraft.server.entity.item.AttractionSignEntity;
import org.jurassicraft.server.tab.TabHandler;
import org.jurassicraft.server.util.LangHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AttractionSignItem extends Item {
    public AttractionSignItem() {
        this.setCreativeTab(TabHandler.DECORATIONS);
        this.setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return new LangHelper("item.attraction_sign.name").withProperty("type", "attraction_sign." + (AttractionSignEntity.AttractionSignType.values()[stack.getItemDamage()].name().toLowerCase(Locale.ENGLISH)) + ".name").build();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	ItemStack stack = player.getHeldItem(hand);
        if (side != EnumFacing.DOWN && side != EnumFacing.UP) {
            BlockPos offset = pos.offset(side);

            if (player.canPlayerEdit(offset, side, stack)) {
                AttractionSignEntity sign = new AttractionSignEntity(world, offset, side, AttractionSignEntity.AttractionSignType.values()[stack.getItemDamage()]);

                if (sign.onValidSurface()) {
                    if (!world.isRemote) {
                        world.spawnEntity(sign);
                    }

                    stack.shrink(1);

                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (AttractionSignEntity.AttractionSignType signType : AttractionSignEntity.AttractionSignType.values()) {
            subItems.add(new ItemStack(item, 1, signType.ordinal()));
        }
    }
}
