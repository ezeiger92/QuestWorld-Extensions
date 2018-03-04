package com.questworld.extension.citizens;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.util.ItemBuilder;
import com.questworld.util.PlayerTools;
import com.questworld.util.Text;

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
