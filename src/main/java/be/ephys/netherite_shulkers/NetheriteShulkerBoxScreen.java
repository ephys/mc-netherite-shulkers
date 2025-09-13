package be.ephys.netherite_shulkers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NetheriteShulkerBoxScreen extends AbstractContainerScreen<NetheriteShulkerBoxContainer> {
  private static final ResourceLocation CONTAINER_TEXTURE = NetheriteShulkers.id("textures/gui/container/netherite_shulker_box.png");

  public NetheriteShulkerBoxScreen(NetheriteShulkerBoxContainer container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    this.imageHeight = 114 + container.getRowCount() * 18;
    this.inventoryLabelY = this.imageHeight - 96;
  }

  @Override
  public void render(GuiGraphics p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
    this.renderBackground(p_230430_1_);
    super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
  }

  @Override
  protected void renderBg(GuiGraphics p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    int i = (this.width - this.imageWidth) / 2;
    int j = (this.height - this.imageHeight) / 2;
    p_230450_1_.blit(CONTAINER_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
  }
}
