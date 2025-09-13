package be.ephys.netherite_shulkers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NetheriteShulkerBoxTileEntityRenderer implements BlockEntityRenderer<NetheriteShulkerBoxBlockEntity> {
  private final ShulkerModel<?> shulkerModel;
  public static final ResourceLocation NETHERITE_SHULKER_TEXTURE = NetheriteShulkers.id("entity/netherite_shulker");
  public static final Material NETHERITE_SHULKER_MATERIAL = new Material(Sheets.SHULKER_SHEET, NETHERITE_SHULKER_TEXTURE);

  public NetheriteShulkerBoxTileEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.shulkerModel = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
  }

  public void render(NetheriteShulkerBoxBlockEntity shulker, float tickTime, PoseStack stack, MultiBufferSource renderTypeBuffer, int lightColor, int overlay) {
    if (!shulker.hasLevel()) {
      return;
    }

    BlockState blockstate = shulker.getLevel().getBlockState(shulker.getBlockPos());
    if (!(blockstate.getBlock() instanceof NetheriteShulkerBoxBlock)) {
      return;
    }

    if (!blockstate.getValue(NetheriteShulkerBoxBlock.OPEN)) {
      return;
    }

    Direction direction = blockstate.getValue(ShulkerBoxBlock.FACING);

    stack.pushPose();
    stack.translate(0.5D, 0.5D, 0.5D);
    float scale = 0.9995F;
    stack.scale(scale, scale, scale);
    stack.mulPose(direction.getRotation());
    stack.scale(1.0F, -1.0F, -1.0F);
    stack.translate(0.0D, -1.0D, 0.0D);
    ModelPart modelpart = this.shulkerModel.getLid();
    modelpart.setPos(0.0F, 24.0F - shulker.getProgress(tickTime) * 0.5F * 16.0F, 0.0F);
    modelpart.yRot = 270.0F * shulker.getProgress(tickTime) * ((float)Math.PI / 180F);
    VertexConsumer vertexconsumer = NETHERITE_SHULKER_MATERIAL.buffer(renderTypeBuffer, RenderType::entityCutoutNoCull);
    this.shulkerModel.renderToBuffer(stack, vertexconsumer, lightColor, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    stack.popPose();
  }
}
