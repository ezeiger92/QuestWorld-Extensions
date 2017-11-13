package com.questworld.extension.citizens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestExtension;
import me.mrCookieSlime.QuestWorld.api.QuestStatus;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.manager.PlayerManager;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Citizens extends QuestExtension implements Listener {
	public static Map<UUID, IMissionState> link = new HashMap<>();
	
	public static NPC npcFrom(IMission instance) {
		return npcFrom(instance.getCustomInt());
	}
	
	private MissionType[] missions;
	
	public static NPC npcFrom(int id) {
		return CitizensAPI.getNPCRegistry().getById(id);
	}
	
	@Override
	public String[] getDepends() {
		return new String[] { "Citizens" };
	}
	
	@Override
	public void initialize(Plugin parent) {
		missions = new MissionType[] {
			new CitizenInteractMission(),
			new CitizenSubmitMission(),
			new CitizenKillMission(),
			new CitizenAcceptQuestMission(),
		};
		
		parent.getServer().getPluginManager().registerEvents(this, parent);
		
		parent.getServer().getScheduler().scheduleSyncRepeatingTask(parent, new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < missions.length; ++i)
					for(IMission task : QuestWorld.get().getMissionsOf(missions[i])) {
						NPC npc = npcFrom(task);
						if (npc != null && npc.getEntity() != null) {
							List<Player> players = new ArrayList<Player>();
							
							for (Entity n: npc.getEntity().getNearbyEntities(20D, 8D, 20D)) {
								if (n instanceof Player) {
									PlayerManager manager = PlayerManager.of(n);
									if (manager.getStatus(task.getQuest()).equals(QuestStatus.AVAILABLE) && manager.hasUnlockedTask(task) && !manager.hasCompletedTask(task)) {
										players.add((Player) n);
									}
								}
							}
							for(Player p : players) {
								p.spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 1, 0), 20, 0.5, 0.7, 0.5, 0);
							}
							/*if (!players.isEmpty()) {
								try {
									ParticleEffect.VILLAGER_HAPPY.display(npc.getEntity().getLocation().add(0, 1, 0), 0.5F, 0.7F, 0.5F, 0, 20, players);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}*/
						}
					}
			}
		}, 0L, 12L);
	}
	
	@Override
	public MissionType[] getMissions() {
		return missions;
	}

	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		Player p = e.getClicker();
		IMissionState changes = link.remove(p.getUniqueId());
		if (changes != null) {
			changes.setCustomInt(e.getNPC().getId());
			
			if(changes.apply()) {
				PlayerTools.sendTranslation(p, true, CitizenTranslation.CITIZEN_NPC_SET);
			}
			
			QuestBook.openQuestMissionEditor(p, changes.getSource());
		}
	}
}
