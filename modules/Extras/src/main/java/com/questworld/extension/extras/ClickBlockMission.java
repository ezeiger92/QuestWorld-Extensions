package com.questworld.extension.extras;

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

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
import me.mrCookieSlime.QuestWorld.util.Text;

public class ClickBlockMission extends MissionType implements Listener {

	private static HashSet<Material> transparent = new HashSet<>();
	private HashMap<UUID, IMissionState> waiting = new HashMap<>();
	
	public ClickBlockMission() {
		super("CLICK_BLOCK", false, new ItemStack(Material.STONE_BUTTON));
	}
	
	static {
		transparent.add(Material.AIR);
		transparent.add(Material.WATER);
		transparent.add(Material.LAVA);
	}
	
	String blockLoc(Location l) {
		return l.getBlockY() + ", " + l.getBlockZ()
		+ " in " + l.getWorld().getName();
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
		IMissionState changes = waiting.remove(p.getUniqueId());
		if(changes != null) {
			event.setCancelled(true);
			if(event.getClickedBlock() == null) {
				p.sendMessage("&4Quest World &a> Cancelled, keeping old block");
			}
			else {
				Location l = event.getClickedBlock().getLocation();
				changes.setLocation(l);
				if(changes.apply()) {
					p.sendMessage("&4Quest World &a> Selected block at " + blockLoc(l));
				}
			}
			QuestBook.openQuestMissionEditor(p, changes.getSource());
			event.setCancelled(true);
			return;
		}
		
		if(event.getClickedBlock() != null) {
			Location l1 = event.getClickedBlock().getLocation();
			
			for(MissionEntry r : QuestWorld.getMissionEntries(this, p)) {
				Location l2 = r.getMission().getLocation();
				if(l1.getWorld() == l2.getWorld()
					&& Math.abs(l1.getX() - l2.getX()) < 0.5
					&& Math.abs(l1.getZ() - l2.getZ()) < 0.5
					&& Math.abs(l1.getY() - l2.getY()) < 0.5)
					r.addProgress(1);
			}
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.MAP).wrapText(
						"&7Location: " + blockLoc(changes.getLocation()),
						"",
						"&e> Click to change block location").get(),
				event -> {
					Player p = (Player) event.getWhoClicked();
					p.closeInventory();
					waiting.put(p.getUniqueId(), changes);
					p.sendMessage("&4Quest World &7> Right click a block to select it, or click air to cancel");
				}
		));
		
		putButton(11, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&r" + changes.getCustomString(),
						 "",
						 "&e> Give your Location a Name").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					p.closeInventory();
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, Translation.LOCMISSION_NAME_EDIT),
							(c,s) -> {
								changes.setCustomString(Text.colorize(s));
								
								if(changes.apply()) {
									PlayerTools.sendTranslation(p, true, Translation.LOCMISSION_NAME_SET);
								}

								QuestBook.openQuestMissionEditor(p, changes.getSource());
								return true;
							}
					));
				}
		));
		putButton(17, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.COMPASS).wrapText(
						"&7Radius: &a" + changes.getCustomInt(),
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
