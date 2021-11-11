package be.ephys.netherite_shulkers;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NetheriteShulkers.MODID)
public class NetheriteShulkers {
  public static final String MODID = "netherite_shulkers";
  private static final Logger LOGGER = LogManager.getLogger();

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteShulkers.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteShulkers.MODID);
  public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, NetheriteShulkers.MODID);
  public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, NetheriteShulkers.MODID);

  public static final RegistryObject<Block> NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("netherite_shulker_box", () ->
    shulkerBox(
      AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
        .requiresCorrectToolForDrops()
        .strength(50.0F, 1200.0F)
        .dynamicShape()
        .noOcclusion()
        .sound(SoundType.NETHERITE_BLOCK)
    )
  );

  private static NetheriteShulkerBoxBlock shulkerBox(AbstractBlock.Properties p_235423_1_) {
    AbstractBlock.IPositionPredicate abstractblock$ipositionpredicate = (p_235444_0_, p_235444_1_, p_235444_2_) -> {
      TileEntity tileentity = p_235444_1_.getBlockEntity(p_235444_2_);
      if (!(tileentity instanceof NetheriteShulkerBoxTileEntity)) {
        return true;
      } else {
        NetheriteShulkerBoxTileEntity shulkerboxtileentity = (NetheriteShulkerBoxTileEntity) tileentity;
        return shulkerboxtileentity.isClosed();
      }
    };

    return new NetheriteShulkerBoxBlock(p_235423_1_.isSuffocating(abstractblock$ipositionpredicate).isViewBlocking(abstractblock$ipositionpredicate));
  }

  public static final RegistryObject<Item> NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("netherite_shulker_box", () ->
    new InvulnerableBlockItem(
      NETHERITE_SHULKER_BOX_BLOCK.get(),
      new Item.Properties()
        .tab(ItemGroup.TAB_DECORATIONS)
        .fireResistant()
        .stacksTo(1)
    )
  );

  public static final RegistryObject<TileEntityType<NetheriteShulkerBoxTileEntity>> NETHERITE_SHULKER_BOX_TILE_ENTITY = TILE_ENTITY_TYPES.register("netherite_shulker_box", () ->
    TileEntityType.Builder.of(NetheriteShulkerBoxTileEntity::new, NETHERITE_SHULKER_BOX_BLOCK.get()).build(null)
  );

  public static final RegistryObject<ContainerType<NetheriteShulkerBoxContainer>> NETHERITE_SHULKER_BOX_CONTAINER = CONTAINERS.register("netherite_shulker_box", () -> {
    return IForgeContainerType.create((pWindowID, pInventory, pData) -> {
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
