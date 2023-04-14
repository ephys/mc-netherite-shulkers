package be.ephys.netherite_shulkers;

import be.ephys.netherite_shulkers.capabilities.ItemStackHelperItemHandlerProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(NetheriteShulkers.MODID)
public class NetheriteShulkers {
  public static final String MODID = "netherite_shulkers";
  private static final Logger LOGGER = LogManager.getLogger();

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteShulkers.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteShulkers.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, NetheriteShulkers.MODID);
  public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, NetheriteShulkers.MODID);

  public static final RegistryObject<Block> NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("netherite_shulker_box", () -> shulkerBox(null, MaterialColor.COLOR_BLACK));
  public static final RegistryObject<Block> WHITE_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("white_netherite_shulker_box", () -> shulkerBox(DyeColor.WHITE));
  public static final RegistryObject<Block> ORANGE_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("orange_netherite_shulker_box", () -> shulkerBox(DyeColor.ORANGE));
  public static final RegistryObject<Block> MAGENTA_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("magenta_netherite_shulker_box", () -> shulkerBox(DyeColor.MAGENTA));
  public static final RegistryObject<Block> LIGHT_BLUE_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("light_blue_netherite_shulker_box", () -> shulkerBox(DyeColor.LIGHT_BLUE));
  public static final RegistryObject<Block> YELLOW_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("yellow_netherite_shulker_box", () -> shulkerBox(DyeColor.YELLOW));
  public static final RegistryObject<Block> LIME_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("lime_netherite_shulker_box", () -> shulkerBox(DyeColor.LIME));
  public static final RegistryObject<Block> PINK_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("pink_netherite_shulker_box", () -> shulkerBox(DyeColor.PINK));
  public static final RegistryObject<Block> GRAY_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("gray_netherite_shulker_box", () -> shulkerBox(DyeColor.GRAY));
  public static final RegistryObject<Block> LIGHT_GRAY_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("light_gray_netherite_shulker_box", () -> shulkerBox(DyeColor.LIGHT_GRAY));
  public static final RegistryObject<Block> CYAN_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("cyan_netherite_shulker_box", () -> shulkerBox(DyeColor.CYAN));
  public static final RegistryObject<Block> PURPLE_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("purple_netherite_shulker_box", () -> shulkerBox(DyeColor.PURPLE));
  public static final RegistryObject<Block> BLUE_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("blue_netherite_shulker_box", () -> shulkerBox(DyeColor.BLUE));
  public static final RegistryObject<Block> BROWN_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("brown_netherite_shulker_box", () -> shulkerBox(DyeColor.BROWN));
  public static final RegistryObject<Block> GREEN_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("green_netherite_shulker_box", () -> shulkerBox(DyeColor.GREEN));
  public static final RegistryObject<Block> RED_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("red_netherite_shulker_box", () -> shulkerBox(DyeColor.RED));
  public static final RegistryObject<Block> BLACK_NETHERITE_SHULKER_BOX_BLOCK = BLOCKS.register("black_netherite_shulker_box", () -> shulkerBox(DyeColor.BLACK));

  private static NetheriteShulkerBoxBlock shulkerBox(DyeColor color) {
    return shulkerBox(color, color.getMaterialColor());
  }

  private static NetheriteShulkerBoxBlock shulkerBox(@Nullable DyeColor color, MaterialColor mapColor) {
    BlockBehaviour.StatePredicate statePredicate = (blockState, blockGetter, blockPos) -> {
      BlockEntity blockEntity = blockGetter.getBlockEntity(blockPos);
      if (!(blockEntity instanceof NetheriteShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
        return true;
      } else {
        return shulkerBoxBlockEntity.isClosed();
      }
    };

    return new NetheriteShulkerBoxBlock(
      color,
      BlockBehaviour.Properties.of(Material.METAL, mapColor)
        .requiresCorrectToolForDrops()
        .strength(2.0F, 1200.0F)
        .dynamicShape()
        .noOcclusion()
        .sound(SoundType.NETHERITE_BLOCK)
        .isSuffocating(statePredicate)
        .isViewBlocking(statePredicate)
    );
  }

  public static final RegistryObject<Item> NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("netherite_shulker_box", () -> shulkerBoxItem(NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> WHITE_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("white_netherite_shulker_box", () -> shulkerBoxItem(WHITE_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> ORANGE_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("orange_netherite_shulker_box", () -> shulkerBoxItem(ORANGE_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> MAGENTA_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("magenta_netherite_shulker_box", () -> shulkerBoxItem(MAGENTA_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> LIGHT_BLUE_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("light_blue_netherite_shulker_box", () -> shulkerBoxItem(LIGHT_BLUE_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> YELLOW_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("yellow_netherite_shulker_box", () -> shulkerBoxItem(YELLOW_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> LIME_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("lime_netherite_shulker_box", () -> shulkerBoxItem(LIME_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> PINK_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("pink_netherite_shulker_box", () -> shulkerBoxItem(PINK_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> GRAY_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("gray_netherite_shulker_box", () -> shulkerBoxItem(GRAY_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> LIGHT_GRAY_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("light_gray_netherite_shulker_box", () -> shulkerBoxItem(LIGHT_GRAY_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> CYAN_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("cyan_netherite_shulker_box", () -> shulkerBoxItem(CYAN_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> PURPLE_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("purple_netherite_shulker_box", () -> shulkerBoxItem(PURPLE_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> BLUE_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("blue_netherite_shulker_box", () -> shulkerBoxItem(BLUE_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> BROWN_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("brown_netherite_shulker_box", () -> shulkerBoxItem(BROWN_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> GREEN_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("green_netherite_shulker_box", () -> shulkerBoxItem(GREEN_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> RED_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("red_netherite_shulker_box", () -> shulkerBoxItem(RED_NETHERITE_SHULKER_BOX_BLOCK));
  public static final RegistryObject<Item> BLACK_NETHERITE_SHULKER_BOX_ITEM = ITEMS.register("black_netherite_shulker_box", () -> shulkerBoxItem(BLACK_NETHERITE_SHULKER_BOX_BLOCK));

  private static InvulnerableBlockItem shulkerBoxItem(RegistryObject<Block> registryBlock) {
    return new InvulnerableBlockItem(
      registryBlock.get(),
      new Item.Properties()
        .tab(CreativeModeTab.TAB_DECORATIONS)
        .fireResistant()
        .stacksTo(1)
    );
  }

  public static final RegistryObject<BlockEntityType<NetheriteShulkerBoxBlockEntity>> NETHERITE_SHULKER_BOX_TILE_ENTITY = TILE_ENTITY_TYPES.register("netherite_shulker_box", () ->
    BlockEntityType.Builder.of(
      NetheriteShulkerBoxBlockEntity::new,
      NETHERITE_SHULKER_BOX_BLOCK.get(),
      WHITE_NETHERITE_SHULKER_BOX_BLOCK.get(),
      ORANGE_NETHERITE_SHULKER_BOX_BLOCK.get(),
      MAGENTA_NETHERITE_SHULKER_BOX_BLOCK.get(),
      LIGHT_BLUE_NETHERITE_SHULKER_BOX_BLOCK.get(),
      YELLOW_NETHERITE_SHULKER_BOX_BLOCK.get(),
      LIME_NETHERITE_SHULKER_BOX_BLOCK.get(),
      PINK_NETHERITE_SHULKER_BOX_BLOCK.get(),
      GRAY_NETHERITE_SHULKER_BOX_BLOCK.get(),
      LIGHT_GRAY_NETHERITE_SHULKER_BOX_BLOCK.get(),
      CYAN_NETHERITE_SHULKER_BOX_BLOCK.get(),
      PURPLE_NETHERITE_SHULKER_BOX_BLOCK.get(),
      BLUE_NETHERITE_SHULKER_BOX_BLOCK.get(),
      BROWN_NETHERITE_SHULKER_BOX_BLOCK.get(),
      GREEN_NETHERITE_SHULKER_BOX_BLOCK.get(),
      RED_NETHERITE_SHULKER_BOX_BLOCK.get(),
      BLACK_NETHERITE_SHULKER_BOX_BLOCK.get()
    ).build(null)
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

    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, NetheriteShulkers::onAttachItemStackCapabilities);

    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::clientInit);
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
      new ResourceLocation(NetheriteShulkers.MODID, "shulker_box_item_handler_value"),
      new ItemStackHelperItemHandlerProvider(stack)
    );
  }

  public static ResourceLocation id(String id) {
    return new ResourceLocation(NetheriteShulkers.MODID, id);
  }
}
