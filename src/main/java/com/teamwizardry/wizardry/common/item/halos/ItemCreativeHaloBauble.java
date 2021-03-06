package com.teamwizardry.wizardry.common.item.halos;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemCreativeHaloBauble extends ItemModBauble implements IHalo {

	public ItemCreativeHaloBauble() {
		super("halo_creative");
		setMaxStackSize(1);
	}

	@Override
	public void onWornTick(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player) {
		CapManager manager = new CapManager(player).setManualSync(true);

		manager.setMaxMana(ConfigValues.creativeHaloBufferSize);
		manager.setMaxBurnout(ConfigValues.creativeHaloBufferSize);
		manager.setMana(ConfigValues.creativeHaloBufferSize);
		manager.setBurnout(0);

		if (manager.isSomethingChanged())
			manager.sync();
	}


	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@Nonnull ItemStack itemStack) {
		return BaubleType.HEAD;
	}
}
