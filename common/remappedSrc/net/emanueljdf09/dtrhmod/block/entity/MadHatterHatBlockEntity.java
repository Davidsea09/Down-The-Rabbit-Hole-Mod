package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.custom.MadHatterHatBlock;
import net.emanueljdf09.dtrhmod.recipe.HatRitualRecipe;
import net.emanueljdf09.dtrhmod.recipe.HatRitualRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MadHatterHatBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ACTIVE = RawAnimation.begin().thenPlay("active");
    private static final RawAnimation FAIL = RawAnimation.begin().thenPlay("fail");
    private static final RawAnimation PORTAL = RawAnimation.begin().thenPlay("success").thenLoop("spinning");
    private static final RawAnimation STOP = RawAnimation.begin().thenPlay("stopping");

    private boolean playFailAnimation = false;
    private boolean playStopAnimation = false;
    private int animationTimer = 0;

    private UUID portalOwnerUuid;
    private int ritualTicks = 0;
    private int portalCooldownTicks = 300;
    private boolean isSpinning = false;
    private boolean isRecipeValid = false;

    private ResourceKey<Level> originDimension;
    private BlockPos originPos;
    private ResourceKey<Level> targetDimension;
    private boolean isInstanceDimension;
    private ResourceLocation structureId;

    private ItemStack storedRecipeItem = ItemStack.EMPTY;

    public enum AnimState {
        NONE,
        FAIL,
        STOP
    }

    private AnimState currentAnimState = AnimState.NONE;
    private int animTicks = 0;

    public MadHatterHatBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAD_HATTER_HAT, pos, state);
    }

    public void setPortalOwnerUuid(UUID ownerUuid) {
        this.portalOwnerUuid = ownerUuid;
        setChanged();
    }

    public UUID getPortalOwnerUuid() {
        return this.portalOwnerUuid;
    }

    public ResourceKey<Level> getTargetDimension() {
        return this.targetDimension;
    }

    public boolean isInstanceDimension() {
        return this.isInstanceDimension;
    }

    public ResourceLocation getStructureId() {
        return this.structureId;
    }

    public ResourceKey<Level> getOriginDimension() {
        return this.originDimension;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, MadHatterHatBlockEntity entity) {
        if (world.isClientSide) return;

        if (entity.currentAnimState == AnimState.STOP) {
            entity.animTicks++;
            if (entity.animTicks > 100) {
                world.destroyBlock(pos, false);
                return;
            }
        }

        if (entity.currentAnimState == AnimState.FAIL) {
            entity.animTicks++;
            if (entity.animTicks == 40) {
                entity.spawnBurpedItems(world, pos);
            }
            if (entity.animTicks > 80) {
                entity.currentAnimState = AnimState.NONE;
                entity.animTicks = 0;

                var manager = entity.getAnimatableInstanceCache().getManagerForId(entity.hashCode());
                if (manager != null) {
                    var controller = manager.getAnimationControllers().get("controller");
                    if (controller != null) {
                        controller.stop();
                        controller.forceAnimationReset();
                    }
                }

                entity.setChanged();
            }
        }

        MadHatterHatBlock.HatState currentState = state.getValue(MadHatterHatBlock.STATE);

        if (currentState == MadHatterHatBlock.HatState.PORTAL) {
            if (entity.isInstanceDimension) {
                if (entity.targetDimension != null) {
                    var targetWorld = world.getServer().getLevel(entity.targetDimension);
                    int playerCount = (targetWorld != null) ? targetWorld.players().size() : 0;

                    if (playerCount > 0) {
                        entity.portalCooldownTicks = 300;
                    } else {
                        entity.portalCooldownTicks--;
                    }
                }
            } else {
                entity.portalCooldownTicks--;
            }

            if (entity.portalCooldownTicks <= 0) {
                entity.expirePersistentPortalAndRefund(world, pos);
                return;
            }
        }

        if (!entity.isSpinning && currentState == MadHatterHatBlock.HatState.IDLE) {
            AABB box = new AABB(pos).inflate(0.3, 0.5, 0.3);
            List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE);

            if (!items.isEmpty()) {
                boolean hasRedstone = items.stream().anyMatch(item -> item.getItem().is(Items.REDSTONE));

                if (hasRedstone) {
                    for (ItemEntity item : items) {
                        if (item.getItem().is(Items.REDSTONE)) {
                            item.getItem().shrink(1);
                            if (item.getItem().isEmpty()) {
                                item.discard();
                            }
                            break;
                        }
                    }

                    entity.evaluateRecipeAndStructure(world, pos, items);

                    if (entity.isRecipeValid) {
                        var manager = entity.getAnimatableInstanceCache().getManagerForId(entity.hashCode());
                        if (manager != null) {
                            var controller = manager.getAnimationControllers().get("controller");
                            if (controller != null) {
                                controller.stop();
                                controller.forceAnimationReset();
                            }
                        }

                        entity.currentAnimState = AnimState.NONE;
                        entity.portalCooldownTicks = 300;
                        world.setBlockAndUpdate(pos, state.setValue(MadHatterHatBlock.STATE, MadHatterHatBlock.HatState.PORTAL));
                        world.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
                    } else {
                        world.playSound(null, pos, SoundEvents.DONKEY_DEATH, SoundSource.BLOCKS, 1.0f, 0.5f);
                        entity.startFailSequence(world, pos);
                    }
                }
            }
        }
    }

    private void evaluateRecipeAndStructure(Level world, BlockPos pos, List<ItemEntity> items) {
        this.originDimension = world.dimension();
        this.originPos = pos;
        this.isRecipeValid = false;

        if (!checkStructure(world, pos)) {
            return;
        }

        List<ItemStack> inputList = new ArrayList<>();
        for (ItemEntity item : items) {
            inputList.add(item.getItem());
        }
        HatRitualRecipeInput recipeInput = new HatRitualRecipeInput(inputList);

        Optional<HatRitualRecipe> match = world.getRecipeManager()
                .getRecipeFor(HatRitualRecipe.Type.INSTANCE, recipeInput, world)
                .map(RecipeHolder::value);

        if (match.isPresent()) {
            HatRitualRecipe recipe = match.get();
            this.isRecipeValid = true;
            this.targetDimension = ResourceKey.create(Registries.DIMENSION, recipe.getTargetDimension());
            this.isInstanceDimension = recipe.isInstanceDimension();
            this.structureId = recipe.getStructureId();

            this.storedRecipeItem = items.get(0).getItem().copy();

            setChanged();
            consumeItems(items);
        } else {
            this.isRecipeValid = false;
        }
    }

    private void expirePersistentPortalAndRefund(Level world, BlockPos pos) {
        if (this.currentAnimState == AnimState.STOP) return;

        this.currentAnimState = AnimState.STOP;
        this.animTicks = 0;
        setChanged();

        triggerAnim("controller", "stop_anim");
        world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.5f);
    }

    private boolean checkStructure(Level world, BlockPos pos) {
        if (!world.getBlockState(pos.below()).is(ModBlocks.BB_LOG)) {
            return false;
        }

        BlockPos[] surround = { pos.north(), pos.south(), pos.east(), pos.west() };
        for (BlockPos p : surround) {
            if (!world.getBlockState(p).is(BlockTags.CANDLES)) {
                return false;
            }
        }

        return true;
    }

    private void consumeItems(List<ItemEntity> items) {
        for (ItemEntity item : items) {
            item.discard();
        }
    }

    private void startFailSequence(Level world, BlockPos pos) {
        AABB box = new AABB(pos).inflate(0.3, 0.5, 0.3);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE);

        for (ItemEntity item : items) {
            item.discard();
        }

        this.currentAnimState = AnimState.FAIL;
        this.animTicks = 0;

        var manager = this.getAnimatableInstanceCache().getManagerForId(this.hashCode());
        if (manager != null) {
            var controller = manager.getAnimationControllers().get("controller");
            if (controller != null) {
                controller.stop();
                controller.forceAnimationReset();
            }
        }

        triggerAnim("controller", "fail_anim");
        setChanged();
    }

    private void spawnBurpedItems(Level world, BlockPos pos) {
        ItemStack penaltyStack = new ItemStack(Items.ROTTEN_FLESH);

        ItemEntity burpedItem = new ItemEntity(
                world,
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                penaltyStack
        );

        burpedItem.setDeltaMovement(
                (world.random.nextDouble() - 0.5) * 0.3,
                0.4,
                (world.random.nextDouble() - 0.5) * 0.3
        );

        world.addFreshEntity(burpedItem);
        world.playSound(null, pos, SoundEvents.PLAYER_BURP, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        if (originDimension != null) nbt.putString("OriginDim", originDimension.location().toString());
        if (originPos != null) nbt.put("OriginPos", NbtUtils.writeBlockPos(originPos));
        if (portalOwnerUuid != null) nbt.putUUID("PortalOwner", portalOwnerUuid);
        if (targetDimension != null) nbt.putString("TargetDim", targetDimension.location().toString());
        if (structureId != null) nbt.putString("StructureId", structureId.toString());
        if (!storedRecipeItem.isEmpty()) {
            nbt.put("StoredRecipeItem", storedRecipeItem.save(registryLookup, new CompoundTag()));
        }
        nbt.putBoolean("IsInstance", isInstanceDimension);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        if (nbt.contains("OriginDim")) {
            originDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("OriginDim")));
        }
        if (nbt.contains("OriginPos")) {
            originPos = NbtUtils.readBlockPos(nbt, "OriginPos").orElse(null);
        }
        if (nbt.hasUUID("PortalOwner")) {
            portalOwnerUuid = nbt.getUUID("PortalOwner");
        }
        if (nbt.contains("TargetDim")) {
            targetDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("TargetDim")));
        }
        if (nbt.contains("StructureId")) {
            structureId = ResourceLocation.parse(nbt.getString("StructureId"));
        }
        if (nbt.contains("StoredRecipeItem")) {
            storedRecipeItem = ItemStack.parse(registryLookup, nbt.getCompound("StoredRecipeItem")).orElse(ItemStack.EMPTY);
        }
        isInstanceDimension = nbt.getBoolean("IsInstance");
    }

    public void setTargetDimension(ResourceKey<Level> targetDimension) {
        this.targetDimension = targetDimension;
    }

    public void setTargetPosition(BlockPos targetPos) {
        this.originPos = targetPos;
    }

    public void setInstanceDimension(boolean isInstanceDimension) {
        this.isInstanceDimension = isInstanceDimension;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            MadHatterHatBlock.HatState hatState = getBlockState().getValue(MadHatterHatBlock.STATE);

            return switch (hatState) {
                case PORTAL -> state.setAndContinue(PORTAL);
                case IDLE -> {
                    boolean inStructure = checkStructure(getLevel(), getBlockPos());
                    yield inStructure ? state.setAndContinue(ACTIVE) : state.setAndContinue(IDLE);
                }
                default -> state.setAndContinue(IDLE);
            };
        })
                .triggerableAnim("fail_anim", FAIL)
                .triggerableAnim("stop_anim", STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}