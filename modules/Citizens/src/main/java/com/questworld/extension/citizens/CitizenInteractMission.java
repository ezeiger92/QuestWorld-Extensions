package com.questworld.extension.citizens;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenInteractMission extends MissionType implements Listener {
	public CitizenInteractMission() {
		super("CITIZENS_INTERACT", false, new ItemStack(Material.ITEM_FRAME));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		String name = "N/A";
		NPC npc = Citizens.npcFrom(instance);
		if(npc != null)
			name = npc.getName();
		
		return "&7Talk to " + name;
	}
	
	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		for(MissionEntry r : QuestWorld.getMissionEntries(this, e.getClicker()))
			if(r.getMission().getCustomInt() == e.getNPC().getId())
				r.addProgress(1);
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(10, CitizenButton.select(changes));
	}
}
