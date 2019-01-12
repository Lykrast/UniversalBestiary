package lykrast.universalbestiary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

@Mod(modid = UniversalBestiary.MODID, 
	name = UniversalBestiary.NAME, 
	version = UniversalBestiary.VERSION, 
	acceptedMinecraftVersions = "[1.12, 1.13)")
public class UniversalBestiary {
    public static final String MODID = "universalbestiary";
    public static final String NAME = "Universal Bestiary";
    public static final String VERSION = "@VERSION@";

	public static Logger logger = LogManager.getLogger(MODID);
	
	public static Item identifier;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		CriteriaTriggers.register(BestiaryTrigger.INSTANCE);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	//Flags for checking which mobs exist
    	IPatchouliAPI patchouli = PatchouliAPI.instance;
    	for (ResourceLocation rl : ForgeRegistries.ENTITIES.getKeys()) {
    		patchouli.setConfigFlag(MODID + ":entity:" + rl, true);
    		logger.debug("Found entity " + rl);
    	}
    }
}
