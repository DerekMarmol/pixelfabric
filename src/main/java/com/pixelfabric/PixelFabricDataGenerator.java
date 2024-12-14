package com.pixelfabric;

import com.pixelfabric.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PixelFabricDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModModelProvider::new);

	}
}
