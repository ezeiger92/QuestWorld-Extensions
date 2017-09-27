package com.questworld.extensions.citizens;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenKillMission extends MissionType implements Listener {
	public CitizenKillMission() {
		super("KILL_NPC", true, true, new ItemStack(Material.IRON_AXE));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemBuilder(Material.SKULL_ITEM).skull(SkullType.PLAYER).get();
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
		
		QuestWorld.getInstance().getManager(killer).forEachTaskOf(this, mission -> {
			return mission.getCustomInt() == e.getNPC().getId();
		});
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);
		putButton(10, CitizenButton.select(changes));
		putButton(17, MissionButton.amount(changes));
	}
}
