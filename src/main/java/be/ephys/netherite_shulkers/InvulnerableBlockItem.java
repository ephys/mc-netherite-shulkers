package be.ephys.netherite_shulkers;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class InvulnerableBlockItem extends BlockItem {

  public InvulnerableBlockItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public boolean canBeHurtBy(DamageSource source) {
    return source.is(DamageTypes.OUTSIDE_BORDER) || source.is(DamageTypes.FELL_OUT_OF_WORLD) ;
  }
}
