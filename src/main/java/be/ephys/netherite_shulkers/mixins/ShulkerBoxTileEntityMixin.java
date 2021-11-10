package be.ephys.netherite_shulkers.mixins;

import be.ephys.netherite_shulkers.ShulkerDenyList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxTileEntity.class)
public class ShulkerBoxTileEntityMixin {
  @Inject(method = "canPlaceItemThroughFace", at= @At("RETURN"), cancellable = true)
  public void canPlaceItemThroughFace$tagDenyList(int slot, ItemStack stack, Direction face, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) {
      return;
    }

    if (!ShulkerDenyList.isInsertableInShulkerBox(stack)) {
      cir.setReturnValue(false);
    }
  }
}
