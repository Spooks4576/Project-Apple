package com.skizzium.projectapple.init;

import com.skizzium.projectapple.ProjectApple;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

public class PA_SoundEvents {
    public static final Lazy<SoundEvent> FLESH_EQUIP_LAZY = Lazy.of(()-> new SoundEvent(new ResourceLocation(ProjectApple.MOD_ID, "entity.skizzik_flesh.equip")));
    public static final Lazy<SoundEvent> MUSIC_SKIZZIK_LAZY = Lazy.of(()-> new SoundEvent(new ResourceLocation(ProjectApple.MOD_ID, "music.skizzik")));
    public static final Lazy<SoundEvent> MUSIC_SPOOKZIK_LAZY = Lazy.of(()-> new SoundEvent(new ResourceLocation(ProjectApple.MOD_ID, "music.spookzik")));
    public static final Lazy<SoundEvent> FINISH_HIM_LAZY = Lazy.of(()-> new SoundEvent(new ResourceLocation(ProjectApple.MOD_ID, "entity.skizzik.finish_him")));
    public static final Lazy<SoundEvent> DABABY_LAZY = Lazy.of(()-> new SoundEvent(new ResourceLocation(ProjectApple.MOD_ID, "entity.dababy")));

    public static final RegistryObject<SoundEvent> FLESH_EQUIP = PA_Registry.SOUND_EVENTS.register("entity.skizzik_flesh.equip", FLESH_EQUIP_LAZY);
    public static final RegistryObject<SoundEvent> MUSIC_SKIZZIK = PA_Registry.SOUND_EVENTS.register("music.skizzik", MUSIC_SKIZZIK_LAZY);
    public static final RegistryObject<SoundEvent> MUSIC_SPOOKZIK = PA_Registry.SOUND_EVENTS.register("music.spookzik", MUSIC_SPOOKZIK_LAZY);
    public static final RegistryObject<SoundEvent> FINISH_HIM = PA_Registry.SOUND_EVENTS.register("entity.skizzik.finish_him", FINISH_HIM_LAZY);
    public static final RegistryObject<SoundEvent> DABABY = PA_Registry.SOUND_EVENTS.register("entity.dababy", DABABY_LAZY);

    public static void register() {}
}