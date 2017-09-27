package com.teamwizardry.wizardry.api.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by Saad on 8/27/2016.
 */
public final class PosUtils {

	public static final ArrayList<EnumFacing> symmetricFacingValues = new ArrayList<>();
	private static final EnumFacing[] northSouth = {EnumFacing.NORTH, EnumFacing.SOUTH};
	private static final EnumFacing[] eastWest = {EnumFacing.EAST, EnumFacing.WEST};
	private static final EnumFacing[] upDown = {EnumFacing.UP, EnumFacing.DOWN};

	static {
		symmetricFacingValues.add(EnumFacing.UP);
		symmetricFacingValues.add(EnumFacing.DOWN);
		symmetricFacingValues.add(EnumFacing.EAST);
		symmetricFacingValues.add(EnumFacing.WEST);
		symmetricFacingValues.add(EnumFacing.SOUTH);
		symmetricFacingValues.add(EnumFacing.NORTH);
	}

	public static BlockPos checkNeighbor(World world, BlockPos origin, Block desiredBlockToFind) {
		if (world.getBlockState(origin).getBlock() == desiredBlockToFind) return origin;

		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos pos = origin.offset(facing);
			if (world.getBlockState(pos).getBlock() == desiredBlockToFind) return pos;

			for (EnumFacing vertical : upDown) {
				BlockPos pos2 = pos.offset(vertical);
				if (world.getBlockState(pos2).getBlock() == desiredBlockToFind) return pos2;
			}
		}

		for (EnumFacing diagonal1 : northSouth) {
			BlockPos pos1 = origin.offset(diagonal1);
			for (EnumFacing diagnonal2 : eastWest) {
				BlockPos pos2 = pos1.offset(diagnonal2);
				if (world.getBlockState(pos2).getBlock() == desiredBlockToFind) return pos2;

				for (EnumFacing vertical : upDown) {
					BlockPos pos3 = pos2.offset(vertical);
					if (world.getBlockState(pos3).getBlock() == desiredBlockToFind) return pos3;
				}
			}
		}
		return origin;
	}

	public static Vec3d vecFromRotations(float rotationPitch, float rotationYaw) {
		return Vec3d.fromPitchYaw(rotationPitch, rotationYaw);
	}

	public static float[] vecToRotations(Vec3d vec) {
		float yaw = (float) MathHelper.atan2(vec.z, vec.x);
		float pitch = (float) Math.asin(vec.y / vec.lengthVector());
		return new float[]{(float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw) + 90};
	}

	public static class ManaBatteryPositions {

		public Set<BlockPos> takenPoses = new HashSet<>();
		public Set<BlockPos> missingSymmetry = new HashSet<>();
		public Set<BlockPos> fullCircle = new HashSet<>();

		public ManaBatteryPositions(World world, BlockPos battery) {
			for (int i = 0; i < 360; i++) {
				double angle = Math.toRadians(i);
				double cX = Math.cos(angle) * 6;
				double cZ = Math.sin(angle) * 6;
				BlockPos staffPos = new BlockPos(battery.getX() + cX + 0.5, battery.getY() - 2, battery.getZ() + cZ + 0.5);

				if (world.getBlockState(staffPos.down()).getBlock() != ModBlocks.WISDOM_WOOD_PIGMENTED_PLANKS) continue;

				if (!fullCircle.contains(staffPos)) fullCircle.add(staffPos);

				if (takenPoses.contains(staffPos)) continue;
				IBlockState block = world.getBlockState(staffPos);
				if (block.getBlock() != ModBlocks.PEARL_HOLDER) continue;
				TilePearlHolder staff = (TilePearlHolder) world.getTileEntity(staffPos);
				if (staff == null || staff.pearl == null || staff.pearl.isEmpty() || staff.pearl.getItem() != ModItems.MANA_ORB)
					continue;

				takenPoses.add(staffPos);
			}

			fullCircle.removeAll(takenPoses);
		}
	}
	
	public static boolean blockHasItems(World world, BlockPos pos, ItemStack... items)
	{
		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
		if (entities.size() < items.length)
			return false;
		boolean[] found = new boolean[entities.size()];
		for (EntityItem entityItem : entities)
		{
			if (entityItem.getItem() == null)
				continue;
			for (int i = 0; i < items.length; i++)
			{
				if (found[i])
					continue;
				if (ItemStack.areItemStacksEqual(entityItem.getItem(), items[i]) && ItemStack.areItemStackTagsEqual(entityItem.getItem(), items[i]))
				{
					found[i] = true;
					break;
				}
			}
		}
		for (int i = 0; i < found.length; i++)
			if (!found[i])
				return false;
		return true;
	}
	
	public static boolean blockHasItems(World world, BlockPos pos, Item... items)
	{
		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
		if (entities.size() < items.length)
			return false;
		boolean[] found = new boolean[entities.size()];
		for (EntityItem entityItem : entities)
		{
			if (entityItem.getItem() == null)
				continue;
			for (int i = 0; i < items.length; i++)
			{
				if (found[i])
					continue;
				if (entityItem.getItem().getItem() == items[i])
				{
					found[i] = true;
					break;
				}
			}
		}
		for (int i = 0; i < found.length; i++)
			if (!found[i])
				return false;
		return true;
	}
	
	public static EntityItem getItemAtPos(World world, BlockPos pos, Item item)
	{
		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
		for (EntityItem entity : entities)
		{
			if (entity.getItem() == null)
				continue;
			if (entity.getItem().getItem() == item)
				return entity;
		}
		return null;
	}
	
	public static EntityItem getItemAtPos(World world, BlockPos pos, ItemStack item)
	{
		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
		for (EntityItem entity : entities)
		{
			if (entity.getItem() == null)
				continue;
			if (ItemStack.areItemStacksEqual(entity.getItem(), item) && ItemStack.areItemStackTagsEqual(entity.getItem(), item))
				return entity;
		}
		return null;
	}
}
