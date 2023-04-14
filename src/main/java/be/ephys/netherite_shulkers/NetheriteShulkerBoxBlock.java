package be.ephys.netherite_shulkers;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class NetheriteShulkerBoxBlock extends BaseEntityBlock {
  public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
  public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

  @Nullable
  private final DyeColor color;

  public NetheriteShulkerBoxBlock(@Nullable DyeColor p_56188_, Properties p_i48334_2_) {
    super(p_i48334_2_);
    this.color = p_56188_;
    this.registerDefaultState(
      this.getStateDefinition().any()
        .setValue(OPEN, false)
        .setValue(FACING, Direction.UP)
    );
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
    return new NetheriteShulkerBoxBlockEntity(pos, blockState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    return createTickerHelper(blockEntityType, NetheriteShulkers.NETHERITE_SHULKER_BOX_TILE_ENTITY.get(), NetheriteShulkerBoxBlockEntity::tick);
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return blockState.getValue(BlockStateProperties.OPEN)
      ? RenderShape.ENTITYBLOCK_ANIMATED
      : RenderShape.MODEL;
  }

  @Override
  public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand p_225533_5_, BlockHitResult p_225533_6_) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    if (player.isSpectator()) {
      return InteractionResult.CONSUME;
    }

    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof NetheriteShulkerBoxBlockEntity shulkerTe) {
      if (canOpen(blockState, level, pos, shulkerTe)) {
        player.openMenu(shulkerTe);
        player.awardStat(Stats.OPEN_SHULKER_BOX);
        PiglinAi.angerNearbyPiglins(player, true);
      }

      return InteractionResult.CONSUME;
    } else {
      return InteractionResult.PASS;
    }
  }

  private static boolean canOpen(BlockState p_154547_, Level p_154548_, BlockPos p_154549_, NetheriteShulkerBoxBlockEntity p_154550_) {
    if (p_154550_.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
      return true;
    } else {
      AABB aabb = Shulker.getProgressDeltaAabb(p_154547_.getValue(FACING), 0.0F, 0.5F).move(p_154549_).deflate(1.0E-6D);
      return p_154548_.noCollision(aabb);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getClickedFace());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, OPEN);
  }

  @Override
  public void playerWillDestroy(Level world, BlockPos pos, BlockState blockState, Player player) {
    BlockEntity blockEntity = world.getBlockEntity(pos);

    if (blockEntity instanceof NetheriteShulkerBoxBlockEntity shulkerBoxBlockEntity) {
      if (!world.isClientSide && player.isCreative() && !shulkerBoxBlockEntity.isEmpty()) {
        ItemStack itemstack = new ItemStack(getBlockByColor(this.color), 1);
        shulkerBoxBlockEntity.saveToItem(itemstack);
        if (shulkerBoxBlockEntity.hasCustomName()) {
          itemstack.setHoverName(shulkerBoxBlockEntity.getCustomName());
        }

        ItemEntity itementity = new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
        itementity.setDefaultPickUpDelay();
        world.addFreshEntity(itementity);
      } else {
        shulkerBoxBlockEntity.unpackLootTable(player);
      }
    }

    super.playerWillDestroy(world, pos, blockState, player);
  }

  @Override
  public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder lootContext) {
    BlockEntity blockEntity = lootContext.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
    if (!(blockEntity instanceof NetheriteShulkerBoxBlockEntity shulkerBlockEntity)) {
      return super.getDrops(blockState, lootContext);
    }

    lootContext = lootContext.withDynamicDrop(ShulkerBoxBlock.CONTENTS, (lootContext1, itemStackConsumer) -> {
      for (int i = 0; i < shulkerBlockEntity.getContainerSize(); ++i) {
        itemStackConsumer.accept(shulkerBlockEntity.getItem(i));
      }
    });

    List<ItemStack> drops = super.getDrops(blockState, lootContext);

    // TODO: use loot context instead
    CompoundTag tileStackNbt = shulkerBlockEntity.getTileData().getCompound("PersistedItemNbt");

    for (ItemStack drop : drops) {
      if (!(drop.getItem() instanceof BlockItem blockItem)) {
        continue;
      }

      if (!(blockItem.getBlock() instanceof NetheriteShulkerBoxBlock)) {
        continue;
      }

      CompoundTag existingTag = drop.getTag();
      if (existingTag == null) {
        drop.setTag(tileStackNbt.copy());
      } else {
        drop.setTag(existingTag.merge(tileStackNbt));
      }
    }

    return drops;
  }

  @Override
  public void setPlacedBy(Level p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
    if (p_180633_5_.hasCustomHoverName()) {
      BlockEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
      if (tileentity instanceof NetheriteShulkerBoxBlockEntity) {
        ((NetheriteShulkerBoxBlockEntity) tileentity).setCustomName(p_180633_5_.getHoverName());
      }
    }
  }

  @Override
  public void onRemove(BlockState p_196243_1_, Level p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
    if (!p_196243_1_.is(p_196243_4_.getBlock())) {
      BlockEntity tileentity = p_196243_2_.getBlockEntity(p_196243_3_);
      if (tileentity instanceof NetheriteShulkerBoxBlockEntity) {
        p_196243_2_.updateNeighbourForOutputSignal(p_196243_3_, p_196243_1_.getBlock());
      }

      super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
    }
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> hoverTexts, TooltipFlag tooltipFlag) {
    super.appendHoverText(itemStack, blockGetter, hoverTexts, tooltipFlag);
    CompoundTag nbt = BlockItem.getBlockEntityData(itemStack);
    if (nbt != null) {
      if (nbt.contains("LootTable", 8)) {
        hoverTexts.add(new TextComponent("???????"));
      }

      if (nbt.contains("Items", 9)) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, nonnulllist);
        int i = 0;
        int j = 0;

        for (ItemStack itemstack : nonnulllist) {
          if (!itemstack.isEmpty()) {
            ++j;
            if (i <= 4) {
              ++i;
              MutableComponent text = itemstack.getHoverName().copy();
              text.append(" x").append(String.valueOf(itemstack.getCount()));
              hoverTexts.add(text);
            }
          }
        }

        if (j - i > 0) {
          hoverTexts.add((new TranslatableComponent("container.shulkerBox.more", j - i)).withStyle(ChatFormatting.ITALIC));
        }
      }
    }

  }

  @Override
  public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
    return PushReaction.DESTROY;
  }

  @Override
  public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
    BlockEntity tileentity = p_220053_2_.getBlockEntity(p_220053_3_);
    return tileentity instanceof NetheriteShulkerBoxBlockEntity
      ? Shapes.create(((NetheriteShulkerBoxBlockEntity) tileentity).getBoundingBox(p_220053_1_))
      : Shapes.block();
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState p_180641_1_, Level p_180641_2_, BlockPos p_180641_3_) {
    return AbstractContainerMenu.getRedstoneSignalFromContainer((Container) p_180641_2_.getBlockEntity(p_180641_3_));
  }

  @Override
  public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState blockState) {
    ItemStack itemstack = super.getCloneItemStack(blockGetter, pos, blockState);
    blockGetter.getBlockEntity(pos, BlockEntityType.SHULKER_BOX).ifPresent((p_187446_) -> {
      p_187446_.saveToItem(itemstack);
    });

    return itemstack;
  }

  @Override
  public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
    return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
    return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
  }

  public static Block getBlockByColor(@Nullable DyeColor color) {
    if (color == null) {
      return NetheriteShulkers.NETHERITE_SHULKER_BOX_BLOCK.get();
    }

    return switch (color) {
      case WHITE -> NetheriteShulkers.WHITE_NETHERITE_SHULKER_BOX_BLOCK.get();
      case ORANGE -> NetheriteShulkers.ORANGE_NETHERITE_SHULKER_BOX_BLOCK.get();
      case MAGENTA -> NetheriteShulkers.MAGENTA_NETHERITE_SHULKER_BOX_BLOCK.get();
      case LIGHT_BLUE -> NetheriteShulkers.LIGHT_BLUE_NETHERITE_SHULKER_BOX_BLOCK.get();
      case YELLOW -> NetheriteShulkers.YELLOW_NETHERITE_SHULKER_BOX_BLOCK.get();
      case LIME -> NetheriteShulkers.LIME_NETHERITE_SHULKER_BOX_BLOCK.get();
      case PINK -> NetheriteShulkers.PINK_NETHERITE_SHULKER_BOX_BLOCK.get();
      case GRAY -> NetheriteShulkers.GRAY_NETHERITE_SHULKER_BOX_BLOCK.get();
      case LIGHT_GRAY -> NetheriteShulkers.LIGHT_GRAY_NETHERITE_SHULKER_BOX_BLOCK.get();
      case CYAN -> NetheriteShulkers.CYAN_NETHERITE_SHULKER_BOX_BLOCK.get();
      case PURPLE -> NetheriteShulkers.PURPLE_NETHERITE_SHULKER_BOX_BLOCK.get();
      case BLUE -> NetheriteShulkers.BLUE_NETHERITE_SHULKER_BOX_BLOCK.get();
      case BROWN -> NetheriteShulkers.BROWN_NETHERITE_SHULKER_BOX_BLOCK.get();
      case GREEN -> NetheriteShulkers.GREEN_NETHERITE_SHULKER_BOX_BLOCK.get();
      case RED -> NetheriteShulkers.RED_NETHERITE_SHULKER_BOX_BLOCK.get();
      case BLACK -> NetheriteShulkers.BLACK_NETHERITE_SHULKER_BOX_BLOCK.get();
    };
  }
}
