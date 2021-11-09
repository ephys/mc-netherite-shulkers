package be.ephys.netherite_shulkers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NetheriteShulkers.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetheriteShulkerBoxTileEntityRenderer extends TileEntityRenderer<NetheriteShulkerBoxTileEntity> {
  private static final ShulkerModel<?> SHULKER_MODEL = new ShulkerModel<>();
  public static final ResourceLocation NETHERITE_SHULKER_TEXTURE = NetheriteShulkers.id("model/netherite_shulker");

  public NetheriteShulkerBoxTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  public void render(NetheriteShulkerBoxTileEntity shulker, float tickTime, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int lightColor, int overlay) {
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
    RenderMaterial material = new RenderMaterial(Atlases.SHULKER_SHEET, NETHERITE_SHULKER_TEXTURE);

    stack.pushPose();
    stack.translate(0.5D, 0.5D, 0.5D);
    float f = 0.9995F;
    stack.scale(f, f, f);
    stack.mulPose(direction.getRotation());
    stack.scale(1.0F, -1.0F, -1.0F);
    stack.translate(0.0D, -1.0D, 0.0D);
    IVertexBuilder ivertexbuilder = material.buffer(renderTypeBuffer, RenderType::entityCutoutNoCull);
    SHULKER_MODEL.getBase().render(stack, ivertexbuilder, lightColor, overlay);
    stack.translate(0.0D, (-shulker.getProgress(tickTime) * 0.5F), 0.0D);
    stack.mulPose(Vector3f.YP.rotationDegrees(270.0F * shulker.getProgress(tickTime)));
    SHULKER_MODEL.getLid().render(stack, ivertexbuilder, lightColor, overlay);
    stack.popPose();
  }

  @SubscribeEvent
  public static void onStitch(final TextureStitchEvent.Pre event) {
    if (!event.getMap().location().equals(Atlases.SHULKER_SHEET)) {
      return;
    }

    event.addSprite(NETHERITE_SHULKER_TEXTURE);
  }
}
