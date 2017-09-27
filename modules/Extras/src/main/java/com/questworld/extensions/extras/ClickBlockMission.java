package com.questworld.extensions.extras;


import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.quests.QuestBook;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;
import me.mrCookieSlime.QuestWorld.utils.Text;

public class ClickBlockMission extends MissionType implements Listener {

	private static HashSet<Material> transparent = new HashSet<>();
	private HashMap<UUID, MissionChange> waiting = new HashMap<>();
	
	public ClickBlockMission() {
		super("CLICK_BLOCK", false, false, new ItemStack(Material.STONE_BUTTON));
	}
	
	static {
		transparent.add(Material.AIR);
		transparent.add(Material.WATER);
		transparent.add(Material.LAVA);
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "Click block at " + instance.getLocation().toVector().toString()
				+ " in " + instance.getLocation().getWorld().getName();
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		MissionChange changes = waiting.remove(p.getUniqueId());
		if(changes != null) {
			event.setCancelled(true);
			if(event.getClickedBlock() == null) {
				p.sendMessage("Cancelled");
			}
			else {
				p.sendMessage("changing");
				changes.setLocation(event.getClickedBlock().getLocation());
				if(changes.sendEvent()) {
					changes.apply();
					p.sendMessage("changed");
				}
			}
			QuestBook.openQuestMissionEditor(p, changes.getSource());
		}
		
		if(event.getClickedBlock() != null) {
			Location l1 = event.getClickedBlock().getLocation();
					
			QuestWorld.getInstance().getManager(p).forEachTaskOf(this, mission -> {
				Location l2 = mission.getLocation();
				return
						l1.getWorld() == l2.getWorld()
						&& Math.abs(l1.getX() - l2.getX()) < 0.5
						&& Math.abs(l1.getZ() - l2.getZ()) < 0.5
						&& Math.abs(l1.getY() - l2.getY()) < 0.5;
			});
		}
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);

		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemStack(Material.BEDROCK),
				event -> {
					Player p = (Player) event.getWhoClicked();
					PlayerTools.closeInventoryWithEvent(p);
					waiting.put(p.getUniqueId(), changes);
					p.sendMessage("Click a block to set the location, or click air to cancel");
				}
		));
		
		putButton(11, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).display("&r" + changes.getCustomString()).lore(
						 "",
						 "&e> Give your Location a Name").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, Translation.location_rename),
							(c,s) -> {
								changes.setCustomString(Text.colorize(s));
								
								if(changes.sendEvent()) {
									PlayerTools.sendTranslation(p, true, Translation.location_rename);
									changes.apply();
								}

								QuestBook.openQuestMissionEditor(p, changes.getSource());
								return true;
							}
					));

					PlayerTools.closeInventoryWithEvent(p);
				}
		));
		putButton(17, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.COMPASS).display("&7Radius: &a" + changes.getCustomInt()).lore(
						"",
						"&rLeft Click: &e+1",
						"&rRight Click: &e-1",
						"&rShift + Left Click: &e+16",
						"&rShift + Right Click: &e-16").get(),
				event -> {
					int amount = MissionButton.clickNumber(changes.getCustomInt(), 16, event);
					changes.setCustomInt(Math.max(amount, 1));
				}
		));
	}
}
