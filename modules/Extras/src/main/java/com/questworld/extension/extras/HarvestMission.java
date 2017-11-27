package com.questworld.extension.extras;

import java.util.HashMap;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import me.mrCookieSlime.QuestWorld.api.Decaying;
import me.mrCookieSlime.QuestWorld.api.MissionSet;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.Text;

public class HarvestMission extends MissionType implements Listener, Decaying {

	private static HashMap<Material, Material> crops = new HashMap<>();
	
	public HarvestMission() {
		super("HARVEST", true, new ItemStack(Material.WHEAT));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "&7Harvest "+instance.getAmount()+"x "+Text.niceName(userDisplayItem(instance).getType().name());
	}
	
	static {
		crops.put(Material.SEEDS, Material.WHEAT);
		crops.put(Material.CROPS, Material.WHEAT);
		
		crops.put(Material.BEETROOT_SEEDS, Material.BEETROOT);
		crops.put(Material.BEETROOT_BLOCK, Material.BEETROOT);

		crops.put(Material.POTATO, Material.POTATO_ITEM);

		crops.put(Material.CARROT, Material.CARROT_ITEM);

		crops.put(Material.NETHER_WART_BLOCK, Material.NETHER_WARTS);

		crops.put(Material.WHEAT,        Material.WHEAT);
		crops.put(Material.BEETROOT,     Material.BEETROOT);
		crops.put(Material.POTATO_ITEM,  Material.POTATO_ITEM);
		crops.put(Material.CARROT_ITEM,  Material.CARROT_ITEM);
		crops.put(Material.NETHER_WARTS, Material.NETHER_WARTS);
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		Material crop = crops.get(instance.getItem().getType());
		if(crop == null)
			crop = Material.BARRIER;
			
		return new ItemStack(crop);
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		MaterialData data = event.getBlock().getState().getData();
		
		if(data instanceof Crops && ((Crops)data).getState() == CropState.RIPE) {
			Material crop = crops.get(event.getBlock().getType());
			
			for(MissionSet.Result r : MissionSet.of(this, event.getPlayer()))
				if(crop == r.getMission().getItem().getType())
					r.addProgress(1);
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		super.layoutMenu(changes);
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(changes.getDisplayItem()).wrapLore(
						"",
						"&e> Click to change the Crop to the Item you are currently holding").get(),
				event -> {
					ItemStack hand = event.getWhoClicked().getInventory().getItemInMainHand();
					if(hand == null)
						return;
					
					Material crop = crops.get(hand.getType());
					if(crop != null)
						changes.setItem(new ItemStack(crop));
				}
		));
		putButton(17, MissionButton.amount(changes));
	}
}
