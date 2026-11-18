package net.emanueljdf09.dtrhmod.block.entity;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.menu.handler.TeapotScreenHandler;
import net.emanueljdf09.dtrhmod.recipe.TeapotRecipe;
import net.emanueljdf09.dtrhmod.recipe.TeapotRecipeInput;
import net.emanueljdf09.dtrhmod.util.AdvancementUtil;
import net.emanueljdf09.dtrhmod.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeapotBlockEntity extends BlockEntity implements ExtendedMenuProvider, ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int BUCKET_SLOT = 3;
    private static final int CUP_SLOT = 4;
    private static final int OUTPUT_SLOT = 5;

    protected final ContainerData propertyDelegate;

    private int progress = 0;
    private int maxProgress = 200;

    public TeapotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEAPOT_BLOCK_ENTITY.get(), pos, state);
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> TeapotBlockEntity.this.progress;
                    case 1 -> TeapotBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> TeapotBlockEntity.this.progress = value;
                    case 1 -> TeapotBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void saveExtraData(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new TeapotScreenHandler(syncId, inventory, this, this.propertyDelegate);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.dtrhmod.teapot");
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        ContainerHelper.loadAllItems(nbt, this.inventory, registries);
        this.progress = nbt.getInt("TeapotProgress");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        ContainerHelper.saveAllItems(nbt, this.inventory, registries);
        nbt.putInt("TeapotProgress", this.progress);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, TeapotBlockEntity blockEntity) {
        if (world.isClientSide()) {
            return;
        }

        if (blockEntity.isOutputSlotEmptyOrRecievable() && blockEntity.hasRecipe() && blockEntity.isHeatSourceUnderneath(world, pos)) {
            blockEntity.increaseCraftProgress();
            setChanged(world, pos, state);

            if (blockEntity.hasCraftingFinished()) {
                blockEntity.craftItem();
                blockEntity.resetProgress();
            }
        } else {
            if (blockEntity.progress > 0) {
                blockEntity.resetProgress();
                setChanged(world, pos, state);
            }
        }
    }

    private boolean isHeatSourceUnderneath(Level world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.below());

        return stateBelow.is(BlockTags.FIRE)
                || stateBelow.is(BlockTags.CAMPFIRES)
                || stateBelow.is(Blocks.MAGMA_BLOCK);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        if (this.getLevel() == null || !this.hasRecipe()) return;

        ItemStack dynamicOutput = this.createDynamicTeaStackGrid();
        ItemStack currentOutput = this.getItem(OUTPUT_SLOT);

        if (currentOutput.isEmpty()) {
            this.setItem(OUTPUT_SLOT, dynamicOutput);
        } else if (currentOutput.is(dynamicOutput.getItem()) && ItemStack.isSameItemSameComponents(currentOutput, dynamicOutput)) {
            currentOutput.grow(dynamicOutput.getCount());
        }

        for (int i = 0; i < 3; i++) {
            if (!this.getItem(i).isEmpty()) {
                this.getItem(i).shrink(1);
            }
        }

        this.getItem(CUP_SLOT).shrink(1);

        ItemStack bucketStack = this.getItem(BUCKET_SLOT);
        if (bucketStack.getItem().hasCraftingRemainingItem()) {
            this.setItem(BUCKET_SLOT, new ItemStack(bucketStack.getItem().getCraftingRemainingItem()));
        } else {
            bucketStack.shrink(1);
        }

        if (!this.getLevel().isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.getLevel();
            AABB box = new AABB(worldPosition).inflate(5.0);
            List<ServerPlayer> nearbyPlayers = serverWorld.getEntitiesOfClass(ServerPlayer.class, box, player -> true);

            for (ServerPlayer player : nearbyPlayers) {
                AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "teapot_craft_adv"));
            }
        }
    }

    private ItemStack createDynamicTeaStackGrid() {
        ItemStack resultCup = new ItemStack(ModItems.FILLED_TEA_CUP);
        CustomData customData = resultCup.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = customData.copyTag();
        ListTag effectsList = new ListTag();

        ItemStack fluidStack = this.getItem(BUCKET_SLOT);
        Item fluidItem = fluidStack.getItem();

        int totalR = 0;
        int totalG = 0;
        int totalB = 0;
        int colorCount = 0;

        for (int i = 0; i < 3; i++) {
            ItemStack slotStack = this.getItem(i);
            if (!slotStack.isEmpty()) {
                SimpleContainer singleSlotInv = new SimpleContainer(6);
                singleSlotInv.setItem(0, slotStack.copy());
                singleSlotInv.setItem(3, new ItemStack(Items.WATER_BUCKET));
                singleSlotInv.setItem(4, this.getItem(CUP_SLOT).copy());

                List<ItemStack> inputList = new ArrayList<>();
                for (int s = 0; s < singleSlotInv.getContainerSize(); s++) {
                    inputList.add(singleSlotInv.getItem(s));
                }
                TeapotRecipeInput recipeInput = new TeapotRecipeInput(inputList);

                Optional<TeapotRecipe> singleRecipe = this.getLevel().getRecipeManager()
                        .getRecipeFor(TeapotRecipe.Type.INSTANCE, recipeInput, this.getLevel())
                        .map(RecipeHolder::value);

                if (singleRecipe.isPresent()) {
                    for (MobEffectInstance baseEffect : singleRecipe.get().getEffects()) {
                        int finalDuration = baseEffect.getDuration();
                        int finalAmplifier = baseEffect.getAmplifier();

                        if (fluidItem == Items.LAVA_BUCKET) {
                            finalAmplifier = finalAmplifier + 1;
                        } else if (fluidItem == Items.MILK_BUCKET) {
                            finalDuration = 400;
                        }

                        MobEffectInstance modifiedEffect = new MobEffectInstance(
                                baseEffect.getEffect(),
                                finalDuration,
                                finalAmplifier,
                                baseEffect.isAmbient(),
                                baseEffect.isVisible()
                        );

                        MobEffectInstance.CODEC.encodeStart(
                                this.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), modifiedEffect
                        ).result().ifPresent(effectsList::add);

                        int effectColor = baseEffect.getEffect().value().getColor();
                        totalR += (effectColor >> 16) & 0xFF;
                        totalG += (effectColor >> 8) & 0xFF;
                        totalB += effectColor & 0xFF;
                        colorCount++;
                    }
                }
            }
        }

        if (!effectsList.isEmpty()) {
            int r = colorCount > 0 ? (totalR / colorCount) : 0x33;
            int g = colorCount > 0 ? (totalG / colorCount) : 0x99;
            int b = colorCount > 0 ? (totalB / colorCount) : 0xFF;

            if (fluidItem == Items.LAVA_BUCKET) {
                r = (int) (r * 0.6 + 0xFF * 0.4);
                g = (int) (g * 0.6 + 0x45 * 0.4);
                b = (int) (b * 0.6 + 0x00 * 0.4);
            } else if (fluidItem == Items.MILK_BUCKET) {
                r = (int) (r * 0.5 + 255 * 0.5);
                g = (int) (g * 0.5 + 255 * 0.5);
                b = (int) (b * 0.5 + 255 * 0.5);
            }

            int finalArgbColor = 0xFF000000 | (r << 16) | (g << 8) | b;

            nbt.put("Effects", effectsList);
            nbt.putString("FluidUsed", BuiltInRegistries.ITEM.getKey(fluidItem).toString());
            nbt.putInt("Color", finalArgbColor);
        }

        resultCup.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return resultCup;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        if (this.getLevel() == null) return false;

        ItemStack fluidStack = this.getItem(BUCKET_SLOT);
        if (fluidStack.isEmpty() || fluidStack.is(Items.BUCKET) || this.getItem(CUP_SLOT).isEmpty()) {
            return false;
        }

        boolean hasValidIngredient = false;
        for (int i = 0; i < 3; i++) {
            ItemStack slotStack = this.getItem(i);
            if (!slotStack.isEmpty()) {
                SimpleContainer singleSlotInv = new SimpleContainer(6);
                singleSlotInv.setItem(0, slotStack.copy());
                singleSlotInv.setItem(3, new ItemStack(Items.WATER_BUCKET));
                singleSlotInv.setItem(4, this.getItem(CUP_SLOT).copy());

                List<ItemStack> inputList = new ArrayList<>();
                for (int s = 0; s < singleSlotInv.getContainerSize(); s++) {
                    inputList.add(singleSlotInv.getItem(s));
                }
                TeapotRecipeInput recipeInput = new TeapotRecipeInput(inputList);

                Optional<TeapotRecipe> singleRecipe = this.getLevel().getRecipeManager()
                        .getRecipeFor(TeapotRecipe.Type.INSTANCE, recipeInput, this.getLevel())
                        .map(RecipeHolder::value);

                if (singleRecipe.isPresent()) {
                    hasValidIngredient = true;
                } else {
                    return false;
                }
            }
        }

        return hasValidIngredient && isOutputSlotEmptyOrRecievable();
    }

    private boolean isOutputSlotEmptyOrRecievable() {
        return this.getItem(OUTPUT_SLOT).isEmpty() || this.getItem(OUTPUT_SLOT).getCount() < this.getItem(OUTPUT_SLOT).getMaxStackSize();
    }
}