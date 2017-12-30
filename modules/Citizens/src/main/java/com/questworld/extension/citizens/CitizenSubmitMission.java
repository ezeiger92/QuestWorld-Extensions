package com.questworld.extension.citizens;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
import me.mrCookieSlime.QuestWorld.util.Text;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenSubmitMission extends MissionType implements Listener {
	public CitizenSubmitMission() {
		super("CITIZENS_SUBMIT", false, new ItemStack(Material.EMERALD));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return instance.getItem();
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		String name = "N/A";
		NPC npc = Citizens.npcFrom(instance);
		if(npc != null)
			name = npc.getName();
		
		return "&7Give " + instance.getAmount() + "x " + Text.itemName(instance.getDisplayItem()) + " to " + name;
	}
	
	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		Player p = e.getClicker();
		ItemStack hand = PlayerTools.getMainHandItem(p);
		
		for(MissionEntry r : QuestWorld.getMissionEntries(this, p)) {
			IMission mission = r.getMission();
			
			if(mission.getCustomInt() != e.getNPC().getId()
					|| !ItemBuilder.compareItems(hand, mission.getItem()))
				continue;
			
			int amount = hand.getAmount();
			if(r.getRemaining() >= amount)
				p.getInventory().setItemInMainHand(null);
			else
				hand.setAmount(amount - r.getRemaining());
			
			r.addProgress(amount);
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(10, CitizenButton.select(changes));
		putButton(11, MissionButton.item(changes));
		putButton(17, MissionButton.amount(changes));
	}
}
