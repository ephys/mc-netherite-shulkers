package be.ephys.netherite_shulkers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class NetheriteShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
  public static final int INVENTORY_SIZE = 54;

  public static final String ITEMS_TAG = "Items";
  private static final int[] SLOTS = IntStream.range(0, INVENTORY_SIZE).toArray();
  private NonNullList<ItemStack> itemStacks = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
  private int openCount;
  private ShulkerBoxBlockEntity.AnimationStatus animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;

  public NetheriteShulkerBoxBlockEntity(BlockPos pos, BlockState blockState) {
    super(NetheriteShulkers.NETHERITE_SHULKER_BOX_TILE_ENTITY.get(), pos, blockState);
  }

  public static void tick(Level level, BlockPos blockPos, BlockState blockState, NetheriteShulkerBoxBlockEntity blockEntity) {
    blockEntity.updateAnimation(level, blockPos, blockState);
  }

  private void setAnimationStatus(Level level, BlockPos pos, BlockState state, ShulkerBoxBlockEntity.AnimationStatus animationStatus) {
    this.animationStatus = animationStatus;

    if (animationStatus == ShulkerBoxBlockEntity.AnimationStatus.OPENING) {
      level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, true), 1 | 2);
    } else if (animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
      level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, false), 1 | 2);
    }
  }

  protected void updateAnimation(Level level, BlockPos pos, BlockState state) {
    this.progressOld = this.progress;
    switch (this.animationStatus) {
      case CLOSED:
        this.progress = 0.0F;
        break;
      case OPENING:
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          setAnimationStatus(level, pos, state, ShulkerBoxBlockEntity.AnimationStatus.OPENED);
          this.progress = 1.0F;
          doNeighborUpdates(level, pos, state);
        }

        this.moveCollidedEntities(level, pos, state);
        break;
      case CLOSING:
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          setAnimationStatus(level, pos, state, ShulkerBoxBlockEntity.AnimationStatus.CLOSED);
          this.progress = 0.0F;
          doNeighborUpdates(level, pos, state);
        }
        break;
      case OPENED:
        this.progress = 1.0F;
    }

  }

  public ShulkerBoxBlockEntity.AnimationStatus getAnimationStatus() {
    return this.animationStatus;
  }

  public AABB getBoundingBox(BlockState blockState) {
    return Shulker.getProgressAabb(blockState.getValue(NetheriteShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F));
  }

  private void moveCollidedEntities(Level p_155684_, BlockPos p_155685_, BlockState p_155686_) {
    if (p_155686_.getBlock() instanceof NetheriteShulkerBoxBlock) {
      Direction direction = p_155686_.getValue(NetheriteShulkerBoxBlock.FACING);
      AABB aabb = Shulker.getProgressDeltaAabb(direction, this.progressOld, this.progress).move(p_155685_);
      List<Entity> list = p_155684_.getEntities((Entity)null, aabb);
      if (!list.isEmpty()) {
        for(int i = 0; i < list.size(); ++i) {
          Entity entity = list.get(i);
          if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
            entity.move(MoverType.SHULKER_BOX, new Vec3((aabb.getXsize() + 0.01D) * (double)direction.getStepX(), (aabb.getYsize() + 0.01D) * (double)direction.getStepY(), (aabb.getZsize() + 0.01D) * (double)direction.getStepZ()));
          }
        }
      }
    }
  }

  public int getContainerSize() {
    return this.itemStacks.size();
  }

  public boolean triggerEvent(int p_145842_1_, int p_145842_2_) {
    if (p_145842_1_ == 1) {
      this.openCount = p_145842_2_;
      if (p_145842_2_ == 0) {
        setAnimationStatus(getLevel(), getBlockPos(), getBlockState(), ShulkerBoxBlockEntity.AnimationStatus.CLOSING);
        doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
      }

      if (p_145842_2_ == 1) {
        setAnimationStatus(getLevel(), getBlockPos(), getBlockState(), ShulkerBoxBlockEntity.AnimationStatus.OPENING);
        doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
      }

      return true;
    } else {
      return super.triggerEvent(p_145842_1_, p_145842_2_);
    }
  }

  private static void doNeighborUpdates(Level p_155688_, BlockPos p_155689_, BlockState p_155690_) {
    p_155690_.updateNeighbourShapes(p_155688_, p_155689_, 3);
  }

  public void startOpen(Player p_174889_1_) {
    if (p_174889_1_.isSpectator()) {
      return;
    }

    if (this.openCount < 0) {
      this.openCount = 0;
    }

    ++this.openCount;
    this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
    if (this.openCount == 1) {
      this.level.gameEvent(p_174889_1_, GameEvent.CONTAINER_OPEN, this.worldPosition);
      this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
  }

  public void stopOpen(Player p_174886_1_) {
    if (!p_174886_1_.isSpectator()) {
      --this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount <= 0) {
        this.level.gameEvent(p_174886_1_, GameEvent.CONTAINER_CLOSE, this.worldPosition);
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
    }
  }

  protected Component getDefaultName() {
    return new TranslatableComponent("block.netherite_shulkers.netherite_shulker_box");
  }

  public void load(CompoundTag p_155678_) {
    super.load(p_155678_);
    this.loadFromTag(p_155678_);
  }

  protected void saveAdditional(CompoundTag p_187513_) {
    super.saveAdditional(p_187513_);
    if (!this.trySaveLootTable(p_187513_)) {
      ContainerHelper.saveAllItems(p_187513_, this.itemStacks, false);
    }
  }

  public void loadFromTag(CompoundTag p_59694_) {
    this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    if (!this.tryLoadLootTable(p_59694_) && p_59694_.contains("Items", 9)) {
      ContainerHelper.loadAllItems(p_59694_, this.itemStacks);
    }
  }

  protected NonNullList<ItemStack> getItems() {
    return this.itemStacks;
  }

  protected void setItems(NonNullList<ItemStack> p_199721_1_) {
    this.itemStacks = p_199721_1_;
  }

  public int[] getSlotsForFace(Direction p_180463_1_) {
    return SLOTS;
  }

  public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction face) {
    return ShulkerDenyList.isInsertableInShulkerBox(itemStack);
  }

  public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
    return true;
  }

  public float getProgress(float p_190585_1_) {
    return Mth.lerp(p_190585_1_, this.progressOld, this.progress);
  }

  protected AbstractContainerMenu createMenu(int menuId, Inventory playerInventory) {
    return new NetheriteShulkerBoxContainer(menuId, playerInventory, this);
  }

  public boolean isClosed() {
    return this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  }

  @Override
  protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
    return new net.minecraftforge.items.wrapper.SidedInvWrapper(this, Direction.UP);
  }
}
