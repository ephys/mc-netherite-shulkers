package be.ephys.netherite_shulkers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ShulkerDenyList {
  public static final Tags.IOptionalNamedTag<Item> shulkerBoxNotInsertableTag = ItemTags.createOptional(new ResourceLocation("forge", "shulker_box_not_insertable"));

  public static boolean isInsertableInShulkerBox(ItemStack itemStack) {
    return !itemStack.getItem().is(ShulkerDenyList.shulkerBoxNotInsertableTag);
  }
}
