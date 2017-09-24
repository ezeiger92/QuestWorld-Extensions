package com.questworld.extensions.citizens;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenInteractMission extends MissionType implements Listener {
	public CitizenInteractMission() {
		super("CITIZENS_INTERACT", false, false, new ItemStack(Material.ITEM_FRAME));
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
		QuestWorld.getInstance().getManager(e.getClicker()).forEachTaskOf(this, mission -> {
			return mission.getCustomInt() == e.getNPC().getId();
		});
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);
		putButton(10, CitizenButton.rename(changes));
	}
}
