package be.ephys.netherite_shulkers.mixins;

import be.ephys.netherite_shulkers.ShulkerDenyList;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
  @Inject(method = "canPlaceItemThroughFace", at= @At("RETURN"), cancellable = true)
  public void canPlaceItemThroughFace$tagDenyList(int p_59663_, ItemStack stack, Direction p_59665_, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) {
      return;
    }

    if (!ShulkerDenyList.isInsertableInShulkerBox(stack)) {
      cir.setReturnValue(false);
    }
  }
}
