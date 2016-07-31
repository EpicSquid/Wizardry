package com.teamwizardry.wizardry.common.spell.module.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;

public class ModuleWater extends Module {
	public ModuleWater(ItemStack stack) {
		super(stack);
	}

	@Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Places a water source block at the targeted location.";
    }

    @Override
    public String getDisplayName() {
        return "Water";
    }

    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
    	compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack)
	{
		if (!(caster instanceof SpellEntity))
		{
			caster.worldObj.setBlockState(caster.getPosition(), Blocks.FLOWING_WATER.getDefaultState());
			return true;
		}
		else
		{
			BlockPos pos = caster.getPosition().add(0, 1, 0);
			if (caster.worldObj.isAirBlock(pos))
			{
				caster.worldObj.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
				return true;
			}
		}
		return false;
	}
}