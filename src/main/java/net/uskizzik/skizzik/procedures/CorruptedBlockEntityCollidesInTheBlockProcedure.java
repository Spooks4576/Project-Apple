package net.uskizzik.skizzik.procedures;

import net.uskizzik.skizzik.potion.CorruptionPotion;
import net.uskizzik.skizzik.SkizzikModElements;
import net.uskizzik.skizzik.SkizzikMod;

import net.minecraft.world.GameType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.Minecraft;

import java.util.Map;

@SkizzikModElements.ModElement.Tag
public class CorruptedBlockEntityCollidesInTheBlockProcedure extends SkizzikModElements.ModElement {
	public CorruptedBlockEntityCollidesInTheBlockProcedure(SkizzikModElements instance) {
		super(instance, 137);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			if (!dependencies.containsKey("entity"))
				SkizzikMod.LOGGER.warn("Failed to load dependency entity for procedure CorruptedBlockEntityCollidesInTheBlock!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if ((new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayerEntity) {
					return ((ServerPlayerEntity) _ent).interactionManager.getGameType() == GameType.CREATIVE;
				} else if (_ent instanceof PlayerEntity && _ent.world.isRemote()) {
					NetworkPlayerInfo _npi = Minecraft.getInstance().getConnection()
							.getPlayerInfo(((AbstractClientPlayerEntity) _ent).getGameProfile().getId());
					return _npi != null && _npi.getGameType() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity))) {
			if (entity instanceof LivingEntity)
				((LivingEntity) entity).addPotionEffect(new EffectInstance(CorruptionPotion.potion, (int) 1200, (int) 1));
		}
	}
}
