package com.questworld.extensions.extras;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.Text;

public class HarvestMission extends MissionType implements Listener {

	public HarvestMission() {
		super("HARVEST", true, true, new ItemStack(Material.WHEAT));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "&7Harvest "+instance.getAmount()+"x "+Text.niceName(userDisplayItem(instance).getType().name());
	}
	
	private Material cropOf(Material in) {
		switch(in) {
		case SEEDS:
		case CROPS:
			return Material.WHEAT;
			
		case BEETROOT_SEEDS:
		case BEETROOT_BLOCK:
			return Material.BEETROOT;
			
		case POTATO:
			return Material.POTATO_ITEM;
			
		case CARROT:
			return Material.CARROT_ITEM;
			
		case NETHER_WART_BLOCK:
			return Material.NETHER_WARTS;
		
		case WHEAT:
		case BEETROOT:
		case POTATO_ITEM:
		case CARROT_ITEM:
		case NETHER_WARTS:
			return in;
			
		default:
			return null;
		}
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		Material crop = cropOf(instance.getMissionItem().getType());
		if(crop == null)
			crop = Material.BARRIER;
			
		return new ItemStack(crop);
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		MaterialData data = event.getBlock().getState().getData();
		
		if(data instanceof Crops && ((Crops)data).getState() == CropState.RIPE) {
			QuestWorld.getInstance().getManager(event.getPlayer()).forEachTaskOf(this, mission -> {
				Material crop = cropOf(event.getBlock().getType());
				return crop != null && mission.getMissionItem().getType().equals(crop);
			});
		}
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(changes.getDisplayItem()).lore(
						"",
						"&e> Click to change the Crop to",
						"&ethe Item you are currently holding").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					ItemStack mainItem = p.getInventory().getItemInMainHand();
					if(mainItem == null)
						return;
					
					Material crop = cropOf(mainItem.getType());
					if(crop != null)
						changes.setItem(new ItemStack(crop));
				}
		));
		putButton(17, MissionButton.amount(changes));
	}
	
	@Override
	protected boolean migrateFrom(MissionChange changes) {
		if(cropOf(changes.getMissionItem().getType()) == null) {
			changes.setItem(new ItemStack(Material.WHEAT));
			return true;
		}
		return false;
	}
}
