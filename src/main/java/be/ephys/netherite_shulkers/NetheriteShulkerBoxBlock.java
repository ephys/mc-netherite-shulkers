package be.ephys.netherite_shulkers;

import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class NetheriteShulkerBoxBlock extends ContainerBlock {
  public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
  public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

  public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

  public NetheriteShulkerBoxBlock(Properties p_i48334_2_) {
    super(p_i48334_2_);

    this.registerDefaultState(
      this.getStateDefinition().any()
        .setValue(OPEN, false)
        .setValue(FACING, Direction.UP)
    );
  }

  public TileEntity newBlockEntity(IBlockReader world) {
    return new NetheriteShulkerBoxTileEntity();
  }

  public BlockRenderType getRenderShape(BlockState blockState) {
    return blockState.getValue(BlockStateProperties.OPEN)
      ? BlockRenderType.ENTITYBLOCK_ANIMATED
      : BlockRenderType.MODEL;
  }

  public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
    if (world.isClientSide) {
      return ActionResultType.SUCCESS;
    }

    if (player.isSpectator()) {
      return ActionResultType.CONSUME;
    }

    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof NetheriteShulkerBoxTileEntity) {
      NetheriteShulkerBoxTileEntity shulkerTe = (NetheriteShulkerBoxTileEntity) te;
      boolean flag;
      if (shulkerTe.getAnimationStatus() == ShulkerBoxTileEntity.AnimationStatus.CLOSED) {
        Direction direction = blockState.getValue(FACING);
        flag = world.noCollision(ShulkerAABBHelper.openBoundingBox(pos, direction));
      } else {
        flag = true;
      }

      if (flag) {
        player.openMenu(shulkerTe);
        player.awardStat(Stats.OPEN_SHULKER_BOX);
        PiglinTasks.angerNearbyPiglins(player, true);
      }

      return ActionResultType.CONSUME;
    } else {
      return ActionResultType.PASS;
    }
  }

  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.defaultBlockState().setValue(FACING, context.getClickedFace());
  }

  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
    p_206840_1_.add(FACING, OPEN);
  }

  public void playerWillDestroy(World world, BlockPos pos, BlockState blockState, PlayerEntity player) {
    TileEntity tileentity = world.getBlockEntity(pos);
    if (tileentity instanceof NetheriteShulkerBoxTileEntity) {
      NetheriteShulkerBoxTileEntity shulkerboxtileentity = (NetheriteShulkerBoxTileEntity) tileentity;
      if (!world.isClientSide && player.isCreative() && !shulkerboxtileentity.isEmpty()) {
        ItemStack itemstack = new ItemStack(NetheriteShulkers.NETHERITE_SHULKER_BOX_ITEM.get());
        CompoundNBT compoundnbt = shulkerboxtileentity.saveToTag(new CompoundNBT());
        if (!compoundnbt.isEmpty()) {
          itemstack.addTagElement("BlockEntityTag", compoundnbt);
        }

        if (shulkerboxtileentity.hasCustomName()) {
          itemstack.setHoverName(shulkerboxtileentity.getCustomName());
        }

        ItemEntity itementity = new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
        itementity.setDefaultPickUpDelay();
        world.addFreshEntity(itementity);
      } else {
        shulkerboxtileentity.unpackLootTable(player);
      }
    }

    super.playerWillDestroy(world, pos, blockState, player);
  }

  public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
    TileEntity tileentity = p_220076_2_.getOptionalParameter(LootParameters.BLOCK_ENTITY);
    if (tileentity instanceof NetheriteShulkerBoxTileEntity) {
      NetheriteShulkerBoxTileEntity shulkerboxtileentity = (NetheriteShulkerBoxTileEntity) tileentity;
      p_220076_2_ = p_220076_2_.withDynamicDrop(CONTENTS, (p_220168_1_, p_220168_2_) -> {
        for (int i = 0; i < shulkerboxtileentity.getContainerSize(); ++i) {
          p_220168_2_.accept(shulkerboxtileentity.getItem(i));
        }

      });
    }

    return super.getDrops(p_220076_1_, p_220076_2_);
  }

  public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
    if (p_180633_5_.hasCustomHoverName()) {
      TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
      if (tileentity instanceof NetheriteShulkerBoxTileEntity) {
        ((NetheriteShulkerBoxTileEntity) tileentity).setCustomName(p_180633_5_.getHoverName());
      }
    }

  }

  public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
    if (!p_196243_1_.is(p_196243_4_.getBlock())) {
      TileEntity tileentity = p_196243_2_.getBlockEntity(p_196243_3_);
      if (tileentity instanceof NetheriteShulkerBoxTileEntity) {
        p_196243_2_.updateNeighbourForOutputSignal(p_196243_3_, p_196243_1_.getBlock());
      }

      super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
    }
  }

  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
    super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
    CompoundNBT compoundnbt = p_190948_1_.getTagElement("BlockEntityTag");
    if (compoundnbt != null) {
      if (compoundnbt.contains("LootTable", 8)) {
        p_190948_3_.add(new StringTextComponent("???????"));
      }

      if (compoundnbt.contains("Items", 9)) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
        int i = 0;
        int j = 0;

        for (ItemStack itemstack : nonnulllist) {
          if (!itemstack.isEmpty()) {
            ++j;
            if (i <= 4) {
              ++i;
              IFormattableTextComponent iformattabletextcomponent = itemstack.getHoverName().copy();
              iformattabletextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
              p_190948_3_.add(iformattabletextcomponent);
            }
          }
        }

        if (j - i > 0) {
          p_190948_3_.add((new TranslationTextComponent("container.shulkerBox.more", j - i)).withStyle(TextFormatting.ITALIC));
        }
      }
    }

  }

  public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
    return PushReaction.DESTROY;
  }

  public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
    TileEntity tileentity = p_220053_2_.getBlockEntity(p_220053_3_);
    return tileentity instanceof NetheriteShulkerBoxTileEntity
      ? VoxelShapes.create(((NetheriteShulkerBoxTileEntity) tileentity).getBoundingBox(p_220053_1_))
      : VoxelShapes.block();
  }

  public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
    return true;
  }

  public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
    return Container.getRedstoneSignalFromContainer((IInventory) p_180641_2_.getBlockEntity(p_180641_3_));
  }

  public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
    ItemStack itemstack = super.getCloneItemStack(p_185473_1_, p_185473_2_, p_185473_3_);
    NetheriteShulkerBoxTileEntity shulkerboxtileentity = (NetheriteShulkerBoxTileEntity) p_185473_1_.getBlockEntity(p_185473_2_);
    CompoundNBT compoundnbt = shulkerboxtileentity.saveToTag(new CompoundNBT());
    if (!compoundnbt.isEmpty()) {
      itemstack.addTagElement("BlockEntityTag", compoundnbt);
    }

    return itemstack;
  }

  public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
    return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
  }

  public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
    return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
  }
}
