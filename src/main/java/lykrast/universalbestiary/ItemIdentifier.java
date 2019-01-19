package lykrast.universalbestiary;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIdentifier extends Item {
	
	public ItemIdentifier() {
		setMaxStackSize(1);
	}
	
	@Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
		if (entity.world.isRemote) {
			spawnParticles(entity);
		}
		else if (player instanceof EntityPlayerMP) {
        	BestiaryTrigger.INSTANCE.trigger((EntityPlayerMP) player, entity);
        }
        
        return true;
    }

    private void spawnParticles(EntityLivingBase entity) {
    	Random rand = entity.world.rand;
        for (int i = 0; i < 5; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            entity.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, 
            		entity.posX + rand.nextFloat() * entity.width * 2.0F - entity.width, 
            		entity.posY + rand.nextFloat() * entity.height, 
            		entity.posZ + rand.nextFloat() * entity.width * 2.0F - entity.width, 
            		d0, d1, d2);
        }
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(TextFormatting.GRAY + I18n.format(stack.getTranslationKey() + ".tooltip"));
	}

}
