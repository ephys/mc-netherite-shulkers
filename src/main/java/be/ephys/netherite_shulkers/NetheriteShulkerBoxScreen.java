package be.ephys.netherite_shulkers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NetheriteShulkerBoxScreen extends AbstractContainerScreen<NetheriteShulkerBoxContainer> {
  private static final ResourceLocation CONTAINER_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
  private final int containerRows;

  public NetheriteShulkerBoxScreen(NetheriteShulkerBoxContainer container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    this.passEvents = false;
    this.containerRows = container.getRowCount();
    this.imageHeight = 114 + this.containerRows * 18;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
    this.renderBackground(p_230430_1_);
    super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
  }

  protected void renderBg(PoseStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
    int i = (this.width - this.imageWidth) / 2;
    int j = (this.height - this.imageHeight) / 2;
    this.blit(p_230450_1_, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
    this.blit(p_230450_1_, i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
  }
}
