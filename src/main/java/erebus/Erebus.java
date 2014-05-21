package erebus;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import erebus.client.render.entity.MobGrabbingHealthBarRemoval;
import erebus.client.render.entity.RenderRhinoBeetleChargeBar;
import erebus.client.sound.AmbientMusicManager;
import erebus.client.sound.EntitySoundEvent;
import erebus.core.handler.BucketHandler;
import erebus.core.handler.CommonTickHandler;
import erebus.core.handler.ConfigHandler;
import erebus.core.handler.HomingBeeconTextureHandler;
import erebus.core.handler.PlayerTeleportHandler;
import erebus.core.proxy.CommonProxy;
import erebus.creativetab.CreativeTabErebus;
import erebus.creativetab.CreativeTabErebusBlock;
import erebus.creativetab.CreativeTabErebusGear;
import erebus.creativetab.CreativeTabErebusItem;
import erebus.creativetab.CreativeTabErebusSpecialItem;
import erebus.entity.util.RandomMobNames;
import erebus.integration.FMBIntegration;
import erebus.integration.IModIntegration;
import erebus.lib.Reference;
import erebus.network.PacketHandler;
import erebus.recipes.AltarRecipe;
import erebus.recipes.BCFacadeManager;
import erebus.recipes.RecipeHandler;
import erebus.world.WorldProviderErebus;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
@NetworkMod(channels = { Reference.CHANNEL }, clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class)
public class Erebus {

	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
	public static CommonProxy proxy;

	@Instance(Reference.MOD_ID)
	public static Erebus instance;

	public static CreativeTabErebus tabErebusBlock = new CreativeTabErebusBlock("erebus.block");
	public static CreativeTabErebus tabErebusItem = new CreativeTabErebusItem("erebus.item");
	public static CreativeTabErebus tabErebusGear = new CreativeTabErebusGear("erebus.gear");
	public static CreativeTabErebus tabErebusSpecial = new CreativeTabErebusSpecialItem("erebus.special");

	public static PlayerTeleportHandler teleportHandler = new PlayerTeleportHandler();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.loadConfig(event);

		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new EntitySoundEvent());
			MinecraftForge.EVENT_BUS.register(new RenderRhinoBeetleChargeBar());
			MinecraftForge.EVENT_BUS.register(new HomingBeeconTextureHandler());
			MinecraftForge.EVENT_BUS.register(new MobGrabbingHealthBarRemoval());
			AmbientMusicManager.register();
		}

		ModBlocks.init();
		ModItems.init();
		ModEntities.init();

		GameRegistry.registerPlayerTracker(teleportHandler);
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);

		DimensionManager.registerProviderType(ConfigHandler.erebusDimensionID, WorldProviderErebus.class, true);
		DimensionManager.registerDimension(ConfigHandler.erebusDimensionID, ConfigHandler.erebusDimensionID);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerKeyHandlers();
		proxy.registerTileEntities();
		proxy.registerRenderInformation();

		ModBiomes.init();
		RecipeHandler.init();
		AltarRecipe.init();

		MinecraftForge.EVENT_BUS.register(ModBlocks.bambooShoot);
		MinecraftForge.EVENT_BUS.register(ModBlocks.erebusSapling);
		MinecraftForge.EVENT_BUS.register(ModBlocks.flowerPlanted);
		MinecraftForge.EVENT_BUS.register(ModBlocks.quickSand);
		MinecraftForge.EVENT_BUS.register(ModBlocks.insectRepellent);
		MinecraftForge.EVENT_BUS.register(ModBlocks.erebusHoneyBlock);
		MinecraftForge.EVENT_BUS.register(ModItems.armorGlider);
		MinecraftForge.EVENT_BUS.register(ModItems.jumpBoots);
		BucketHandler.INSTANCE.buckets.put(ModBlocks.erebusHoneyBlock, ModItems.bucketHoney);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

		if (ConfigHandler.randomNames)
			MinecraftForge.EVENT_BUS.register(RandomMobNames.instance);

		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
		BCFacadeManager.registerFacades();
		if (Loader.isModLoaded("ForgeMicroblock"))
			FMBIntegration.integrate();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		try {
			for (ClassInfo clsInfo : ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("erebus.integration")) {
				Class cls = clsInfo.load();

				if (IModIntegration.class.isAssignableFrom(cls) && !cls.isInterface())
					try {
						IModIntegration obj = (IModIntegration) cls.newInstance();
						if (Loader.isModLoaded(obj.getModId()))
							obj.integrate();
					} catch (Throwable e) {
						e.printStackTrace();
					}
			}
		} catch (Exception e) {
		}
	}
}