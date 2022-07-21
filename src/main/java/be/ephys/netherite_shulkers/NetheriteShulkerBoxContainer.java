package be.ephys.netherite_shulkers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NetheriteShulkerBoxContainer extends AbstractContainerMenu {
  private final Container container;
  private static final int SLOTS_PER_ROW = 9;

  public NetheriteShulkerBoxContainer(int id, Inventory playerInventory) {
    this(id, playerInventory, new SimpleContainer(NetheriteShulkerBoxBlockEntity.INVENTORY_SIZE));
  }

  public int getRowCount() {
    return NetheriteShulkerBoxBlockEntity.INVENTORY_SIZE / SLOTS_PER_ROW;
  }

  public NetheriteShulkerBoxContainer(int id, Inventory playerInventory, Container inventory) {
    super(NetheriteShulkers.NETHERITE_SHULKER_BOX_CONTAINER.get(), id);
    checkContainerSize(inventory, NetheriteShulkerBoxBlockEntity.INVENTORY_SIZE);
    this.container = inventory;
    inventory.startOpen(playerInventory.player);

    int rows = getRowCount();

    int FIRST_ROW_Y_OFFSET = 18;

    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < SLOTS_PER_ROW; ++col) {

        //                                         slot           x             y
        this.addSlot(new ShulkerBoxSlot(
          inventory,
          col + row * 9,
          8 + col * 18,
          FIRST_ROW_Y_OFFSET + row * 18
        ));
      }
    }

    int lastContainerRowY = FIRST_ROW_Y_OFFSET + (rows * 18);
    int containerInventorySpacing = 13;

    int playerInventoryRows = 3;

    // player inventory
    for (int row = 0; row < playerInventoryRows; ++row) {
      for (int col = 0; col < 9; ++col) {
        this.addSlot(new Slot(
          playerInventory,
          col + row * 9 + 9,
          8 + col * 18,
          lastContainerRowY + containerInventorySpacing + (row * 18)
        ));
      }
    }

    int lastPlayerRowY = lastContainerRowY + containerInventorySpacing + (playerInventoryRows * 18);
    int hotbarSpacing = 4;

    // hotbar
    for (int col = 0; col < 9; ++col) {
      this.addSlot(new Slot(
        playerInventory,
        col,
        8 + col * 18,
        lastPlayerRowY + hotbarSpacing
      ));
    }
  }

  public boolean stillValid(Player player) {
    return this.container.stillValid(player);
  }

  public ItemStack quickMoveStack(Player player, int slotId) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotId);
    if (slot != null && slot.hasItem()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.copy();
      if (slotId < this.container.getContainerSize()) {
        if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return itemstack;
  }

  public void removed(Player player) {
    super.removed(player);
    this.container.stopOpen(player);
  }
}
