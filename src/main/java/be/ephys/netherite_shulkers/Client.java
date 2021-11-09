package be.ephys.netherite_shulkers;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class Client {
  static void clientInit() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    modEventBus.addListener(Client::setupClient);
  }

  private static void setupClient(final FMLClientSetupEvent event) {
    ClientRegistry.bindTileEntityRenderer(
      NetheriteShulkers.NETHERITE_SHULKER_BOX_TILE_ENTITY.get(),
      NetheriteShulkerBoxTileEntityRenderer::new
    );
  }
}
