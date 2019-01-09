package lykrast.universalbestiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class BestiaryTrigger implements ICriterionTrigger<BestiaryTrigger.Instance> {
	//Original copied from Botania, by Vazkii
	//https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/advancements/RelicBindTrigger.java
	public static final ResourceLocation ID = new ResourceLocation(UniversalBestiary.MODID, "unlock");
	public static final BestiaryTrigger INSTANCE = new BestiaryTrigger();
	private final Map<PlayerAdvancements, PlayerTracker> playerTrackers = new HashMap<>();
	
	public static final EntityPredicate NULL_PREDICATE = new NullEntityPredicate();

	private BestiaryTrigger() {}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(@Nonnull PlayerAdvancements player, @Nonnull ICriterionTrigger.Listener<BestiaryTrigger.Instance> listener) {
		playerTrackers.computeIfAbsent(player, PlayerTracker::new).listeners.add(listener);
	}

	@Override
	public void removeListener(@Nonnull PlayerAdvancements player, @Nonnull ICriterionTrigger.Listener<BestiaryTrigger.Instance> listener) {
		PlayerTracker tracker = playerTrackers.get(player);

		if(tracker != null) {
			tracker.listeners.remove(listener);

			if(tracker.listeners.isEmpty()) {
				this.playerTrackers.remove(player);
			}
		}
	}

	@Override
	public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
		playerTrackers.remove(playerAdvancementsIn);
	}

	@Nonnull
	@Override
	public Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
		return new Instance(safeEntityPredicate(json.get("entity")));
	}
	
	//Like EntityPredicate.deserialize but instead of exceptionning if the entity isn't registered it just never happens
    public static EntityPredicate safeEntityPredicate(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonObject jsonobject = JsonUtils.getJsonObject(element, "entity");
            ResourceLocation resourcelocation = null;

            if (jsonobject.has("type")) {
                resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "type"));
                if (!EntityList.isRegistered(resourcelocation)) {
                	UniversalBestiary.logger.debug("Unregistered entity " + resourcelocation + ", making trigger dormant");
                	return NULL_PREDICATE;
                }
            }

            DistancePredicate distancepredicate = DistancePredicate.deserialize(jsonobject.get("distance"));
            LocationPredicate locationpredicate = LocationPredicate.deserialize(jsonobject.get("location"));
            MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(jsonobject.get("effects"));
            NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
            return new EntityPredicate(resourcelocation, distancepredicate, locationpredicate, mobeffectspredicate, nbtpredicate);
        }
        else return EntityPredicate.ANY;
    }

	static class PlayerTracker {
		private final PlayerAdvancements playerAdvancements;
		final Set<Listener<Instance>> listeners = new HashSet<>();

		PlayerTracker(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public void trigger(EntityPlayerMP player, Entity entity) {
			List<Listener<Instance>> list = new ArrayList<>();

			for(Listener<BestiaryTrigger.Instance> listener : this.listeners) {
				if(listener.getCriterionInstance().test(player, entity)) {
					list.add(listener);
				}
			}

			for(Listener<BestiaryTrigger.Instance> listener : list) {
				listener.grantCriterion(this.playerAdvancements);
			}
		}
	}

	public void trigger(EntityPlayerMP player, Entity entity) {
		PlayerTracker tracker = playerTrackers.get(player.getAdvancements());
		if(tracker != null) {
			tracker.trigger(player, entity);
		}
	}

	static class Instance implements ICriterionInstance {
		private final EntityPredicate predicate;

		Instance(EntityPredicate predicate) {
			this.predicate = predicate;
		}

		@Nonnull
		@Override
		public ResourceLocation getId() {
			return ID;
		}

		boolean test(EntityPlayerMP player, Entity entity) {
			return predicate.test(player, entity);
		}
	}
	
	private static class NullEntityPredicate extends EntityPredicate {
		public NullEntityPredicate() {
			super(null, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY);
		}
		
		@Override
	    public boolean test(EntityPlayerMP player, @Nullable Entity entity) {
			return false;
		}
		
	}

}
