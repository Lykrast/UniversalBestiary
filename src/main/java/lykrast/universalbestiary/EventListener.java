package lykrast.universalbestiary;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = UniversalBestiary.MODID)
public class EventListener {
	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		Entity killer = event.getSource().getTrueSource();
		if (killer == null) return;
		
		Entity killed = event.getEntity();
		
		//Player got killed by a mob
		if (killed instanceof EntityPlayerMP) BestiaryTrigger.INSTANCE.trigger((EntityPlayerMP) killed, killer);
		//Player killed a mob
		if (killer instanceof EntityPlayerMP) BestiaryTrigger.INSTANCE.trigger((EntityPlayerMP) killer, killed);
	}
	
	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getItemStack().getItem() instanceof ItemIdentifier && event.getTarget() instanceof EntityLivingBase) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			event.getItemStack().interactWithEntity(event.getEntityPlayer(), (EntityLivingBase)event.getTarget(), event.getHand());
		}
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		UniversalBestiary.identifier = new ItemIdentifier();
		UniversalBestiary.identifier.setRegistryName(new ResourceLocation(UniversalBestiary.MODID, "identifier"));
		UniversalBestiary.identifier.setTranslationKey(UniversalBestiary.MODID + ".identifier");
		UniversalBestiary.identifier.setCreativeTab(CreativeTabs.MISC);
		event.getRegistry().register(UniversalBestiary.identifier);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent evt) {
		ModelLoader.setCustomModelResourceLocation(UniversalBestiary.identifier, 0, new ModelResourceLocation(UniversalBestiary.identifier.getRegistryName(), "inventory"));
	}

}
