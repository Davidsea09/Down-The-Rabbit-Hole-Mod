package net.emanueljdf09.dtrhmod.menu.handler;

import net.emanueljdf09.dtrhmod.fabric.block.entity.TeapotBlockEntity;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.menu.ModScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TeapotScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;
    private final TeapotBlockEntity teapotBlockEntity;

    public TeapotScreenHandler(int syncId, Inventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.level().getBlockEntity(pos),
                new SimpleContainerData(2));
    }

    public TeapotScreenHandler(int syncId, Inventory playerInventory,
                               BlockEntity blockEntity, ContainerData propertyDelegate) {
        super(ModScreenHandlers.TEAPOT_SCREEN_HANDLER, syncId);
        checkContainerSize(((Container) blockEntity), 6);
        this.inventory = ((Container) blockEntity);
        inventory.startOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;
        this.teapotBlockEntity = ((TeapotBlockEntity) blockEntity);

        this.addSlot(new Slot(inventory, 0, 27, 34));
        this.addSlot(new Slot(inventory, 1, 45, 34));
        this.addSlot(new Slot(inventory, 2, 63, 34));

        this.addSlot(new Slot(inventory, 3, 27, 12) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.WATER_BUCKET)
                        || stack.is(Items.LAVA_BUCKET)
                        || stack.is(Items.MILK_BUCKET);
            }
        });

        this.addSlot(new Slot(inventory, 4, 63, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.EMPTY_CUP);
            }
        });

        this.addSlot(new Slot(inventory, 5, 130, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });


        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addDataSlots(propertyDelegate);

    }

    public boolean isCrafting() {
        return this.propertyDelegate.get(0) > 0;
    }

    public int getScaledProgress(int maxPixels) {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        return maxProgress != 0 && progress != 0 ? progress * maxPixels / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (slotIndex < 6) {
                if (!this.moveItemStackTo(originalStack, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {

                if (originalStack.is(Items.WATER_BUCKET)) {
                    if (!this.moveItemStackTo(originalStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.LAVA_BUCKET)) {
                    if (!this.moveItemStackTo(originalStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.MILK_BUCKET)) {
                    if (!this.moveItemStackTo(originalStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(ModItems.EMPTY_CUP)) {
                    if (!this.moveItemStackTo(originalStack, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(originalStack, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (originalStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

}

