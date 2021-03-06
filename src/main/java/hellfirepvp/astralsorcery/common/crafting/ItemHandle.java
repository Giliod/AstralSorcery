/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.item.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandle
 * Created by HellFirePvP
 * Date: 26.12.2016 / 15:13
 */
public final class ItemHandle {

    public final Type handleType;

    private List<ItemStack> applicableItems = new LinkedList<>();
    private String oreDictName = null;
    private FluidStack fluidTypeAndAmount = null;

    public ItemHandle(String oreDictName) {
        this.oreDictName = oreDictName;
        this.handleType = Type.OREDICT;
    }

    public ItemHandle(Fluid fluid) {
        this.fluidTypeAndAmount = new FluidStack(fluid, 1000);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(Fluid fluid, int mbAmount) {
        this.fluidTypeAndAmount = new FluidStack(fluid, mbAmount);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(FluidStack compareStack) {
        this.fluidTypeAndAmount = compareStack.copy();
        this.handleType = Type.FLUID;
    }

    public ItemHandle(@Nonnull ItemStack matchStack) {
        this.applicableItems.add(ItemUtils.copyStackWithSize(matchStack, matchStack.stackSize));
        this.handleType = Type.STACK;
    }

    public static ItemHandle getCrystalVariant(boolean hasToBeTuned, boolean hasToBeCelestial) {
        if(hasToBeTuned) {
            if(hasToBeCelestial) {
                return new ItemHandle(new ItemStack(ItemsAS.tunedCelestialCrystal));
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        } else {
            if(hasToBeCelestial) {
                ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.celestialCrystal));
                handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
                return handle;
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.rockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.celestialCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        }
    }

    public List<ItemStack> getApplicableItems() {
        if(oreDictName != null) {
            List<ItemStack> stacks = OreDictionary.getOres(oreDictName);

            List<ItemStack> out = new LinkedList<>();
            for (ItemStack oreDictIn : stacks) {
                if (oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                    oreDictIn.getItem().getSubItems(oreDictIn.getItem(), CreativeTabs.BUILDING_BLOCKS, out);
                } else {
                    out.add(oreDictIn);
                }
            }
            return out;
        } else if(fluidTypeAndAmount != null) {
            return Lists.newArrayList(UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid()));
        } else {
            return Lists.newArrayList(applicableItems);
        }
    }

    public Object getObjectForRecipe() {
        if(oreDictName != null) {
            return oreDictName;
        }
        if(fluidTypeAndAmount != null) {
            return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid());
        }
        return applicableItems.get(0);
    }

    @SideOnly(Side.CLIENT)
    public List<ItemStack> getApplicableItemsForRender() {
        List<ItemStack> applicable = getApplicableItems();
        Iterator<ItemStack> iterator = applicable.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if(stack == null || stack.getItem() == null) continue;
            Item i = stack.getItem();
            if(i instanceof ItemGatedVisibility) {
                if(!((ItemGatedVisibility) i).isSupposedToSeeInRender(stack)) {
                    iterator.remove();
                }
            }
        }
        return applicable;
    }

    @Nullable
    public String getOreDictName() {
        return oreDictName;
    }

    @Nullable
    public FluidStack getFluidTypeAndAmount() {
        return fluidTypeAndAmount;
    }

    public boolean matchCrafting(@Nullable ItemStack stack) {
        if(stack == null) return false;

        if(oreDictName != null) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(id);
                if (name != null && name.equals(oreDictName)) {
                    return true;
                }
            }
            return false;
        } else if(fluidTypeAndAmount != null) {
            return ItemUtils.drainFluidFromItem(stack, fluidTypeAndAmount, false);
        } else {
            for (ItemStack applicable : applicableItems) {
                if(ItemUtils.stackEqualsNonNBT(applicable, stack)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static enum Type {

        OREDICT,
        STACK,
        FLUID

    }

}
