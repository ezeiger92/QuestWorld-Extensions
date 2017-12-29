package com.questworld.extension.citizens;

import java.util.ArrayList;
import java.util.HashSet;

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
	private static HashSet<MissionType> types = new HashSet<>();
	
	public static NPC npcFrom(IMission instance) {
		return npcFrom(instance.getCustomInt());
	}
	
	public static NPC npcFrom(int id) {
		return CitizensAPI.getNPCRegistry().getById(id);
	}
	
	public static class CitizensLib extends QuestExtension {
		public CitizensLib() {
			super("Citizens", "CS-CoreLib");
		}
		
		@Override
		protected void initialize(Plugin parent) {
			setMissionTypes(new CitizenAcceptQuestMission());
			
			for(MissionType type : getMissionTypes())
				types.add(type);
		}
	}
	
	public Citizens() {
		super("Citizens");
		setMissionTypes(
			new CitizenInteractMission(),
			new CitizenSubmitMission(),
			new CitizenKillMission());
		
		for(MissionType type : getMissionTypes())
			types.add(type);
	}
	
	@Override
	protected void initialize(Plugin parent) {
		parent.getServer().getPluginManager().registerEvents(new CitizenButton.Listener(), parent);
		
		parent.getServer().getScheduler().scheduleSyncRepeatingTask(parent, new Runnable() {
			
			@Override
			public void run() {
				HashSet<Integer> shown = new HashSet<>();
				
				for(MissionType type : types)
					for(IMission mission : QuestWorld.getViewer().getMissionsOf(type)) {
						NPC npc = npcFrom(mission);
						if (npc != null && npc.getEntity() != null) {
							ArrayList<Player> players = new ArrayList<>();
							
							for (Entity n: npc.getEntity().getNearbyEntities(20D, 8D, 20D)) {
								if (n instanceof Player && ((Player)n).isOnline()) {
									IPlayerStatus manager = QuestWorld.getPlayerStatus((Player)n);
									if (manager.isMissionActive(mission)) {
										players.add((Player) n);
									}
								}
							}
							
							if(!shown.contains(npc.getEntity().getEntityId())) {
								shown.add(npc.getEntity().getEntityId());
								for(Player p : players) {
									p.spawnParticle(Particle.VILLAGER_HAPPY, npc.getEntity().getLocation().add(0, 1, 0), 20, 0.5, 0.7, 0.5, 0);
								}
							}
						}
					}
			}
		}, 0L, 32L);
	}
}
