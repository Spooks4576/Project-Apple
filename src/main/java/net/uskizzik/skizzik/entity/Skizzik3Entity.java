
package net.uskizzik.skizzik.entity;

import net.uskizzik.skizzik.procedures.Skizzik3ThisEntityKillsAnotherOneProcedure;
import net.uskizzik.skizzik.procedures.Skizzik3PlayerCollidesWithThisEntityProcedure;
import net.uskizzik.skizzik.procedures.Skizzik3OnInitialEntitySpawnProcedure;
import net.uskizzik.skizzik.procedures.Skizzik3EntityIsHurtProcedure;
import net.uskizzik.skizzik.procedures.Skizzik3EntityDiesProcedure;
import net.uskizzik.skizzik.procedures.Skizzik1OnEntityTickUpdateProcedure;
import net.uskizzik.skizzik.item.SkizLauncher2Item;
import net.uskizzik.skizzik.SkizzikModElements;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.World;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.network.IPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;

@SkizzikModElements.ModElement.Tag
public class Skizzik3Entity extends SkizzikModElements.ModElement {
	public static EntityType entity = null;
	public Skizzik3Entity(SkizzikModElements instance) {
		super(instance, 132);
		FMLJavaModLoadingContext.get().getModEventBus().register(new ModelRegisterHandler());
	}

	@Override
	public void initElements() {
		entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, EntityClassification.MONSTER).setShouldReceiveVelocityUpdates(true)
				.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new).immuneToFire().size(3f, 3f)).build("skizzik_3")
						.setRegistryName("skizzik_3");
		elements.entities.add(() -> entity);
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(this::setupAttributes);
	}
	private static class ModelRegisterHandler {
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public void registerModels(ModelRegistryEvent event) {
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> {
				return new MobRenderer(renderManager, new Modelskizzik_3(), 1f) {
					{
						this.addLayer(new GlowingLayer<>(this));
					}
					@Override
					public ResourceLocation getEntityTexture(Entity entity) {
						return new ResourceLocation("skizzik:textures/skizzik.png");
					}
				};
			});
		}
	}
	private void setupAttributes() {
		AttributeModifierMap.MutableAttribute ammma = MobEntity.func_233666_p_();
		ammma = ammma.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3);
		ammma = ammma.createMutableAttribute(Attributes.MAX_HEALTH, 200);
		ammma = ammma.createMutableAttribute(Attributes.ARMOR, 8);
		ammma = ammma.createMutableAttribute(Attributes.ATTACK_DAMAGE, 8);
		ammma = ammma.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 2);
		ammma = ammma.createMutableAttribute(Attributes.FLYING_SPEED, 0.3);
		GlobalEntityTypeAttributes.put(entity, ammma.create());
	}
	public static class CustomEntity extends MonsterEntity implements IRangedAttackMob {
		public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
			this(entity, world);
		}

		public CustomEntity(EntityType<CustomEntity> type, World world) {
			super(type, world);
			experienceValue = 0;
			setNoAI(false);
			enablePersistence();
			this.moveController = new FlyingMovementController(this, 10, true);
			this.navigator = new FlyingPathNavigator(this, this.world);
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		protected void registerGoals() {
			super.registerGoals();
			this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, PlayerEntity.class, false, false));
			this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, AnimalEntity.class, false, false));
			this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, FriendlySkizzieEntity.CustomEntity.class, false, false));
			this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, FriendlyMinigunSkizzieEntity.CustomEntity.class, false, false));
			this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, FriendlyWitchSkizzieEntity.CustomEntity.class, false, false));
			this.targetSelector.addGoal(6, new HurtByTargetGoal(this).setCallsForHelp(this.getClass()));
			this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 0.8, 20) {
				@Override
				protected Vector3d getPosition() {
					Random random = CustomEntity.this.getRNG();
					double dir_x = CustomEntity.this.getPosX() + ((random.nextFloat() * 2 - 1) * 16);
					double dir_y = CustomEntity.this.getPosY() + ((random.nextFloat() * 2 - 1) * 16);
					double dir_z = CustomEntity.this.getPosZ() + ((random.nextFloat() * 2 - 1) * 16);
					return new Vector3d(dir_x, dir_y, dir_z);
				}
			});
			this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
			this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10) {
				@Override
				public boolean shouldContinueExecuting() {
					return this.shouldExecute();
				}
			});
		}

		@Override
		public CreatureAttribute getCreatureAttribute() {
			return CreatureAttribute.UNDEAD;
		}

		@Override
		public boolean canDespawn(double distanceToClosestPlayer) {
			return false;
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.ambient"));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wither.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(""));
		}

		@Override
		public boolean onLivingFall(float l, float d) {
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			Entity sourceentity = source.getTrueSource();
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				Skizzik3EntityIsHurtProcedure.executeProcedure($_dependencies);
			}
			if (source.getImmediateSource() instanceof PotionEntity)
				return false;
			if (source == DamageSource.FALL)
				return false;
			if (source == DamageSource.CACTUS)
				return false;
			if (source == DamageSource.DROWN)
				return false;
			if (source == DamageSource.LIGHTNING_BOLT)
				return false;
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void onDeath(DamageSource source) {
			super.onDeath(source);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity sourceentity = source.getTrueSource();
			Entity entity = this;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				Skizzik3EntityDiesProcedure.executeProcedure($_dependencies);
			}
		}

		@Override
		public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason,
				@Nullable ILivingEntityData livingdata, @Nullable CompoundNBT tag) {
			ILivingEntityData retval = super.onInitialSpawn(world, difficulty, reason, livingdata, tag);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("world", world);
				Skizzik3OnInitialEntitySpawnProcedure.executeProcedure($_dependencies);
			}
			return retval;
		}

		@Override
		public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
			super.awardKillScore(entity, score, damageSource);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity sourceentity = this;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				Skizzik3ThisEntityKillsAnotherOneProcedure.executeProcedure($_dependencies);
			}
		}

		@Override
		public void baseTick() {
			super.baseTick();
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("world", world);
				Skizzik1OnEntityTickUpdateProcedure.executeProcedure($_dependencies);
			}
		}

		@Override
		public void onCollideWithPlayer(PlayerEntity sourceentity) {
			super.onCollideWithPlayer(sourceentity);
			Entity entity = this;
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("sourceentity", sourceentity);
				Skizzik3PlayerCollidesWithThisEntityProcedure.executeProcedure($_dependencies);
			}
		}

		public void attackEntityWithRangedAttack(LivingEntity target, float flval) {
			SkizLauncher2Item.shoot(this, target);
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}
		private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
		@Override
		public void addTrackingPlayer(ServerPlayerEntity player) {
			super.addTrackingPlayer(player);
			this.bossInfo.addPlayer(player);
		}

		@Override
		public void removeTrackingPlayer(ServerPlayerEntity player) {
			super.removeTrackingPlayer(player);
			this.bossInfo.removePlayer(player);
		}

		@Override
		public void updateAITasks() {
			super.updateAITasks();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
		}

		@Override
		protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		}

		@Override
		public void setNoGravity(boolean ignored) {
			super.setNoGravity(true);
		}

		public void livingTick() {
			super.livingTick();
			this.setNoGravity(true);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Random random = this.rand;
			Entity entity = this;
			if (true)
				for (int l = 0; l < 4; ++l) {
					double d0 = (x + random.nextFloat());
					double d1 = (y + random.nextFloat());
					double d2 = (z + random.nextFloat());
					int i1 = random.nextInt(2) * 2 - 1;
					double d3 = (random.nextFloat() - 0.5D) * 2D;
					double d4 = (random.nextFloat() - 0.5D) * 2D;
					double d5 = (random.nextFloat() - 0.5D) * 2D;
					world.addParticle(ParticleTypes.FLAME, d0, d1, d2, d3, d4, d5);
				}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static class GlowingLayer<T extends Entity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
		public GlowingLayer(IEntityRenderer<T, M> er) {
			super(er);
		}

		public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing,
				float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(new ResourceLocation("skizzik:textures/skizzik_glow.png")));
			this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		}
	}

	// Made with Blockbench 3.5.4
	// Exported for Minecraft version 1.15
	// Paste this class into your mod and generate all required imports
	public static class Modelskizzik_3 extends EntityModel<Entity> {
		private final ModelRenderer body;
		private final ModelRenderer body2;
		private final ModelRenderer head1;
		private final ModelRenderer head2;
		private final ModelRenderer head3;
		public Modelskizzik_3() {
			textureWidth = 256;
			textureHeight = 256;
			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 4.9F, -8.5F);
			body.setTextureOffset(73, 100).addBox(-3.0F, -11.0F, 6.0F, 7.0F, 19.0F, 7.0F, 0.0F, false);
			body.setTextureOffset(84, 46).addBox(-11.0F, -9.5F, 7.5F, 23.0F, 4.0F, 4.0F, 0.0F, false);
			body.setTextureOffset(0, 86).addBox(8.0F, -8.5F, -11.5F, 3.0F, 2.0F, 20.0F, 0.0F, false);
			body.setTextureOffset(80, 24).addBox(-10.0F, -8.5F, -11.5F, 3.0F, 2.0F, 20.0F, 0.0F, false);
			body.setTextureOffset(60, 44).addBox(-7.0F, -8.5F, -11.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);
			body.setTextureOffset(0, 42).addBox(5.0F, -8.5F, -11.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);
			body.setTextureOffset(0, 37).addBox(5.0F, 2.5F, -11.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);
			body.setTextureOffset(0, 32).addBox(-7.0F, 2.5F, -11.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);
			body.setTextureOffset(64, 78).addBox(-10.0F, 2.5F, -11.5F, 3.0F, 2.0F, 20.0F, 0.0F, false);
			body.setTextureOffset(90, 76).addBox(8.0F, -3.5F, -11.5F, 3.0F, 3.0F, 19.0F, 0.0F, false);
			body.setTextureOffset(88, 0).addBox(-10.0F, -3.5F, -11.5F, 3.0F, 3.0F, 19.0F, 0.0F, false);
			body.setTextureOffset(76, 54).addBox(8.0F, 2.5F, -11.5F, 3.0F, 2.0F, 20.0F, 0.0F, false);
			body.setTextureOffset(0, 10).addBox(-7.0F, -3.5F, -11.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
			body.setTextureOffset(0, 4).addBox(5.0F, -3.5F, -11.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
			body.setTextureOffset(48, 8).addBox(-11.0F, -4.0F, 7.5F, 23.0F, 4.0F, 4.0F, 0.0F, false);
			body.setTextureOffset(48, 0).addBox(-11.0F, 1.5F, 7.5F, 23.0F, 4.0F, 4.0F, 0.0F, false);
			body.setTextureOffset(0, 32).addBox(-7.0F, -10.0F, -9.0F, 15.0F, 15.0F, 15.0F, 0.0F, false);
			body.setTextureOffset(29, 98).addBox(-2.0F, 5.0F, -11.0F, 5.0F, 1.0F, 17.0F, 0.0F, false);
			body.setTextureOffset(0, 0).addBox(-2.0F, 3.0F, -11.0F, 5.0F, 2.0F, 2.0F, 0.0F, false);
			body2 = new ModelRenderer(this);
			body2.setRotationPoint(1.0F, 15.9F, 4.5F);
			setRotationAngle(body2, 0.6981F, 0.0F, 0.0F);
			body2.setTextureOffset(101, 101).addBox(-4.0F, -7.0026F, -3.3586F, 7.0F, 13.0F, 7.0F, 0.0F, false);
			head1 = new ModelRenderer(this);
			head1.setRotationPoint(1.0F, -16.0F, 0.0F);
			head1.setTextureOffset(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);
			head2 = new ModelRenderer(this);
			head2.setRotationPoint(19.0F, -9.0F, 0.0F);
			head2.setTextureOffset(0, 62).addBox(-6.0F, -10.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);
			head3 = new ModelRenderer(this);
			head3.setRotationPoint(-18.0F, -11.0F, 1.0F);
			head3.setTextureOffset(36, 74).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);
		}

		@Override
		public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue,
				float alpha) {
			body.render(matrixStack, buffer, packedLight, packedOverlay);
			body2.render(matrixStack, buffer, packedLight, packedOverlay);
			head1.render(matrixStack, buffer, packedLight, packedOverlay);
			head2.render(matrixStack, buffer, packedLight, packedOverlay);
			head3.render(matrixStack, buffer, packedLight, packedOverlay);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		public void setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4) {
			this.head1.rotateAngleY = f3 / (180F / (float) Math.PI);
			this.head1.rotateAngleX = f4 / (180F / (float) Math.PI);
		}
	}
}
