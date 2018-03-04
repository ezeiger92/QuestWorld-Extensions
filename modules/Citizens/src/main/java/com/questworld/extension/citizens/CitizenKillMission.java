package com.questworld.extension.citizens;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.Decaying;
import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.util.ItemBuilder;

import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenKillMission extends MissionType implements Listener, Decaying {
	public CitizenKillMission() {
		super("KILL_NPC", true, new ItemStack(Material.IRON_AXE));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemBuilder(SkullType.PLAYER).get();
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		String name = "N/A";
		NPC npc = Citizens.npcFrom(instance);
		if(npc != null)
			name = npc.getName();
		String times = "";
		if(instance.getAmount() > 1)
			times = " " + instance.getAmount() + " times";
		
		return "&7Kill " + name + times;
	}
	
	@EventHandler
	public void onInteract(NPCDeathEvent e) {
		Player killer = ((LivingEntity)e.getNPC().getEntity()).getKiller();
		if(killer == null)
			return;
		
		for(MissionEntry r : QuestWorld.getMissionEntries(this, killer))
			if(r.getMission().getCustomInt() == e.getNPC().getId())
				r.addProgress(1);
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(10, CitizenButton.select(changes));
		putButton(17, MissionButton.amount(changes));
	}
}
