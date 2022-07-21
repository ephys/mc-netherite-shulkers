package be.ephys.netherite_shulkers;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class Client {
  static void clientInit() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    modEventBus.addListener(Client::setupClient);
  }

  private static void setupClient(final FMLClientSetupEvent event) {
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
