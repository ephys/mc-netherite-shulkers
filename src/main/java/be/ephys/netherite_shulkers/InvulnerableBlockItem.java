package be.ephys.netherite_shulkers;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.DamageSource;

public final class InvulnerableBlockItem extends BlockItem {

  public InvulnerableBlockItem(Block p_i48527_1_, Properties p_i48527_2_) {
    super(p_i48527_1_, p_i48527_2_);
  }

  @Override
  public boolean canBeHurtBy(DamageSource source) {
    return source.isBypassInvul();
  }
}
