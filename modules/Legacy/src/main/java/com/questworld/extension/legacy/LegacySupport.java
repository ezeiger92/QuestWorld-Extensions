package com.questworld.extension.legacy;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import com.questworld.api.QuestExtension;
import com.questworld.util.Reflect;
import com.questworld.util.VersionAdapter;

public class LegacySupport extends QuestExtension {
	public LegacySupport() {
		Reflect.addAdapter(new Adapter_1_8_8());
	}
	
	public static class Adapter_1_8_8 extends VersionAdapter {

		@Override
		protected String forVersion() {
			return "v1_8_r3";
		}

		@SuppressWarnings("deprecation")
		@Override
		public void makeSpawnEgg(ItemStack result, EntityType mob) {
			if(result.getType() == Material.MONSTER_EGG)
				result.setDurability(mob.getTypeId());
		}

		@Override
		public void makePlayerHead(ItemStack result, OfflinePlayer player) {
			if(result.getItemMeta() instanceof SkullMeta) {
				SkullMeta sm = (SkullMeta) result.getItemMeta();
				sm.setOwner(player.getName());
				result.setItemMeta(sm);
			}
			
		}

		@Override
		public ShapelessRecipe shapelessRecipe(String recipeName, ItemStack output) {
			return new ShapelessRecipe(output);
		}

		@Override
		public void sendActionbar(Player player, String message) {
			// TODO: Maybe this works w/nms
			return;
		}

	}
}
