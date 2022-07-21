package be.ephys.netherite_shulkers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShulkerDenyList {
  public static final TagKey<Item> shulkerBoxNotInsertableTag = ItemTags.create(new ResourceLocation("forge", "shulker_box_not_insertable"));

  public static boolean isInsertableInShulkerBox(ItemStack itemStack) {
    return !itemStack.is(ShulkerDenyList.shulkerBoxNotInsertableTag);
  }
}
