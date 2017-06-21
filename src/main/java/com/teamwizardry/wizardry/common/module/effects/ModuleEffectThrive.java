package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectThrive extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_thrive";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Thrive";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will heal entities & speed up plant growth";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);
		Vec3d pos = spell.getData(TARGET_HIT);

		if (pos != null)
			spell.world.playSound(null, new BlockPos(pos), ModSounds.HEAL, SoundCategory.NEUTRAL, 1, 1);
		if (targetEntity instanceof EntityLivingBase) {
			double strength = getModifierPower(spell, Attributes.INCREASE_POTENCY, 3, 20, true, true) / 10.0;

			if (!tax(this, spell)) return false;

			((EntityLivingBase) targetEntity).heal((float) strength);
		}

		if (targetPos != null) {
			if (world.getBlockState(targetPos).getBlock() instanceof IGrowable) {
				if (!tax(this, spell)) return false;
				if (caster == null || (caster instanceof EntityPlayer && BlockUtils.hasEditPermission(targetPos, (EntityPlayerMP) caster)))
					ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, targetPos);
			} else if (world.getBlockState(targetPos).getBlock() instanceof IPlantable) {
				IBlockState state = world.getBlockState(targetPos);
				Block block = state.getBlock();
				if (!tax(this, spell)) return false;
				if (caster == null || (caster instanceof EntityPlayer && BlockUtils.hasEditPermission(targetPos, (EntityPlayerMP) caster))) {
					while (world.getBlockState(targetPos.up()).getBlock() == block) {
						targetPos = targetPos.up();
						state = world.getBlockState(targetPos);
						block = state.getBlock();
					}
					world.immediateBlockTick(targetPos, state, RandUtil.random);
				}
			}
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;
		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectThrive());
	}
}