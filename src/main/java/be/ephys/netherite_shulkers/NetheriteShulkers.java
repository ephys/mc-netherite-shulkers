package be.ephys.netherite_shulkers;

import be.ephys.netherite_shulkers.capabilities.ItemStackHelperItemHandlerProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(NetheriteShulkers.MODID)
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = NetheriteShulkers.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class NetheriteShulkers {
  public static final String MODID = "netherite_shulkers";

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteShulkers.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteShulkers.MODID);
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NetheriteShulkers.MODID);
  public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, NetheriteShulkers.MODID);

  public static final RegistryObject<Block> NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("netherite_shulker_box", () ->
    shulkerBox(
      BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BLACK)
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
        .fireResistant()
        .stacksTo(1)
    )
  );

  public static final RegistryObject<BlockEntityType<NetheriteShulkerBoxBlockEntity>> NETHERITE_SHULKER_BOX_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("netherite_shulker_box", () ->
    BlockEntityType.Builder.of(NetheriteShulkerBoxBlockEntity::new, NETHERITE_SHULKER_BOX_BLOCK.get()).build(null)
  );

  public static final RegistryObject<MenuType<NetheriteShulkerBoxContainer>> NETHERITE_SHULKER_BOX_CONTAINER = MENU_TYPES.register("netherite_shulker_box", () -> {
    return IForgeMenuType.create((pWindowID, pInventory, pData) -> {
      return new NetheriteShulkerBoxContainer(pWindowID, pInventory);
    });
  });

  public NetheriteShulkers(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    BLOCKS.register(modEventBus);
    ITEMS.register(modEventBus);
    BLOCK_ENTITY_TYPES.register(modEventBus);
    MENU_TYPES.register(modEventBus);

    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, NetheriteShulkers::onAttachItemStackCapabilities);
  }

  @SubscribeEvent
  public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
      event.accept(NETHERITE_SHULKER_BOX_ITEM.get());
    }
  }

  public static void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    ItemStack stack = event.getObject();
    Item item = stack.getItem();

    if (!(item instanceof BlockItem blockItem)) {
      return;
    }

    if (!(blockItem.getBlock() instanceof NetheriteShulkerBoxBlock)) {
      return;
    }

    event.addCapability(
      ResourceLocation.fromNamespaceAndPath(NetheriteShulkers.MODID, "shulker_box_item_handler_value"),
      new ItemStackHelperItemHandlerProvider(stack)
    );
  }

  public static ResourceLocation id(String id) {
    return ResourceLocation.fromNamespaceAndPath(NetheriteShulkers.MODID, id);
  }
}
