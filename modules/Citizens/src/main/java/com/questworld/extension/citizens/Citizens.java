package com.questworld.extension.citizens;

import java.util.ArrayList;
import java.util.List;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestExtension;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IPlayerStatus;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Citizens extends QuestExtension {
	public static NPC npcFrom(IMission instance) {
		return npcFrom(instance.getCustomInt());
	}
	
	public static NPC npcFrom(int id) {
		return CitizensAPI.getNPCRegistry().getById(id);
	}
	
	public Citizens() {
		super("Citizens", "CS-CoreLib");
		setMissionTypes(
			new CitizenInteractMission(),
			new CitizenSubmitMission(),
			new CitizenKillMission(),
			new CitizenAcceptQuestMission());
	}
	
	@Override
	protected void initialize(Plugin parent) {
		parent.getServer().getPluginManager().registerEvents(new CitizenButton.Listener(), parent);
		
		parent.getServer().getScheduler().scheduleSyncRepeatingTask(parent, new Runnable() {
			
			@Override
			public void run() {
				MissionType[] missions = getMissionTypes();
				for(int i = 0; i < missions.length; ++i)
					for(IMission mission : QuestWorld.getViewer().getMissionsOf(missions[i])) {
						NPC npc = npcFrom(mission);
						if (npc != null && npc.getEntity() != null) {
							List<Player> players = new ArrayList<Player>();
							
							for (Entity n: npc.getEntity().getNearbyEntities(20D, 8D, 20D)) {
								if (n instanceof Player) {
									IPlayerStatus manager = QuestWorld.getPlayerStatus((Player)n);
									if (manager.isMissionActive(mission)) {
										players.add((Player) n);
									}
								}
							}
							for(Player p : players) {
								p.spawnParticle(Particle.VILLAGER_HAPPY, npc.getEntity().getLocation().add(0, 1, 0), 20, 0.5, 0.7, 0.5, 0);
							}
						}
					}
			}
		}, 0L, 12L);
	}
}
