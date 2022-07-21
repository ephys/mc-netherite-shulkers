package be.ephys.netherite_shulkers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NetheriteShulkers.MODID)
public class NetheriteShulkers {
  public static final String MODID = "netherite_shulkers";
  private static final Logger LOGGER = LogManager.getLogger();

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteShulkers.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteShulkers.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, NetheriteShulkers.MODID);
  public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, NetheriteShulkers.MODID);

  public static final RegistryObject<Block> NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("netherite_shulker_box", () ->
    shulkerBox(
      BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
        .requiresCorrectToolForDrops()
        .strength(2.0F, 1200.0F)
        .dynamicShape()
        .noOcclusion()
        .sound(SoundType.NETHERITE_BLOCK)
    )
  );

  private static NetheriteShulkerBoxBlock shulkerBox(BlockBehaviour.Properties properties) {
    BlockBehaviour.StatePredicate statePredicate = (blockState, blockGetter, blockPos) -> {
      BlockEntity blockEntity = blockGetter.getBlockEntity(blockPos);
      if (!(blockEntity instanceof NetheriteShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
        return true;
      } else {
        return shulkerBoxBlockEntity.isClosed();
      }
    };

    return new NetheriteShulkerBoxBlock(properties.isSuffocating(statePredicate).isViewBlocking(statePredicate));
  }

  public static final RegistryObject<Item> NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("netherite_shulker_box", () ->
    new InvulnerableBlockItem(
      NETHERITE_SHULKER_BOX_BLOCK.get(),
      new Item.Properties()
        .tab(CreativeModeTab.TAB_DECORATIONS)
        .fireResistant()
        .stacksTo(1)
    )
  );

  public static final RegistryObject<BlockEntityType<NetheriteShulkerBoxBlockEntity>> NETHERITE_SHULKER_BOX_TILE_ENTITY = TILE_ENTITY_TYPES.register("netherite_shulker_box", () ->
    BlockEntityType.Builder.of(NetheriteShulkerBoxBlockEntity::new, NETHERITE_SHULKER_BOX_BLOCK.get()).build(null)
  );

  public static final RegistryObject<MenuType<NetheriteShulkerBoxContainer>> NETHERITE_SHULKER_BOX_CONTAINER = CONTAINERS.register("netherite_shulker_box", () -> {
    return IForgeMenuType.create((pWindowID, pInventory, pData) -> {
      return new NetheriteShulkerBoxContainer(pWindowID, pInventory);
    });
  });

  public NetheriteShulkers() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(modEventBus);
    ITEMS.register(modEventBus);
    TILE_ENTITY_TYPES.register(modEventBus);
    CONTAINERS.register(modEventBus);

    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::clientInit);
  }

  public static ResourceLocation id(String id) {
    return new ResourceLocation(NetheriteShulkers.MODID, id);
  }
}
