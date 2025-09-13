package be.ephys.netherite_shulkers;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = NetheriteShulkers.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD,
  value = Dist.CLIENT
)
public class Client {
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    BlockEntityRenderers.register(
      NetheriteShulkers.NETHERITE_SHULKER_BOX_TILE_ENTITY.get(),
      NetheriteShulkerBoxTileEntityRenderer::new
    );

    MenuScreens.register(
      NetheriteShulkers.NETHERITE_SHULKER_BOX_CONTAINER.get(),
      NetheriteShulkerBoxScreen::new
    );
  }
}
