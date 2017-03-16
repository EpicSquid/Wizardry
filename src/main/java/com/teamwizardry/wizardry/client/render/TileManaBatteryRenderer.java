package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.client.core.ClientProxy;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class TileManaBatteryRenderer extends TileEntitySpecialRenderer<TileManaBattery> {

	private IBakedModel modelRing;

	public TileManaBatteryRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelRing = null;
	}

	private void getBakedModels() {
		IModel model = null;
		if (modelRing == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal_ring"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelRing = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
	}

	@Override
	public void renderTileEntityAt(TileManaBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		World world = te.getWorld();

		if (te.getBlockType() instanceof IStructure)
			if (!((IStructure) te.getBlockType()).renderBoundries(te.getWorld(), te.getPos())) return;

		PosUtils.ManaBatteryPositions positions = new PosUtils.ManaBatteryPositions(world, te.getPos());
		for (BlockPos pos : positions.fullCircle)
			if (ThreadLocalRandom.current().nextInt(5) == 0)
				LibParticles.MAGIC_DOT(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), -1);

		for (BlockPos pos : positions.missingSymmetry)
			if (ThreadLocalRandom.current().nextInt(10) == 0)
				LibParticles.MAGIC_DOT(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), (float) ThreadLocalRandom.current().nextDouble(1, 4));

		for (BlockPos pos : positions.takenPoses)
			if (ThreadLocalRandom.current().nextInt(10) == 0)
				LibParticles.COLORFUL_BATTERY_BEZIER(world, pos, te.getPos());

		/*GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		getBakedModels();

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();
		GlStateManager.translate(0.5, 0, 0.5);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelRing, 1.0F, 1, 1, 1);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();*/
	}
}
