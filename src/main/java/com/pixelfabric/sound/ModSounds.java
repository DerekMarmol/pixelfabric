package com.pixelfabric.sound;

import com.pixelfabric.PixelFabric;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent TINNITUS = registerSoundEvent("tinnitus");

    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(PixelFabric.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds(){
        PixelFabric.LOGGER.info("Registrando sonidos para " + PixelFabric.MOD_ID);
    }
}
