package be.ephys.netherite_shulkers.mixins;

import be.ephys.netherite_shulkers.ShulkerDenyList;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {
  @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
  public void mayPlace$tagDenyList(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    System.out.println("MAY PLACE");
    if (!cir.getReturnValue()) {
      return;
    }

    if (!ShulkerDenyList.isInsertableInShulkerBox(stack)) {
      cir.setReturnValue(false);
    }
  }
}
