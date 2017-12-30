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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Citizens extends QuestExtension {
	FileConfiguration config;
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
		
		onReload();
	}
	
	@Override
	public void onSave() {
		getResourceLoader().saveConfigNoexcept(config, "config-citizens.yml", true);
	}
	
	@Override
	public void onReload() {
		config = getConfiguration("config-citizens.yml");
	}
	
	@Override
	protected void initialize(Plugin parent) {
		parent.getServer().getPluginManager().registerEvents(new CitizenButton.Listener(), parent);
		
		parent.getServer().getScheduler().scheduleSyncRepeatingTask(parent, new Runnable() {
			
			@Override
			public void run() {
				ConfigurationSection particleConfig = config.getConfigurationSection("npc_particles");
				HashSet<Integer> shown = new HashSet<>();
				
				
				Particle particleType = Particle.VILLAGER_HAPPY;
				try {
					particleType = Particle.valueOf(particleConfig.getString("type"));
				}
				catch(Exception e) {
				}
				
				double nearX = particleConfig.getDouble("nearby.x", 20.0);
				double nearY = particleConfig.getDouble("nearby.y", 8.0);
				double nearZ = particleConfig.getDouble("nearby.z", 20.0);
				
				int count = particleConfig.getInt("count", 20);
				int extra = particleConfig.getInt("extra", 0);
				
				double spreadX = particleConfig.getDouble("spread.x", 0.5);
				double spreadY = particleConfig.getDouble("spread.y", 0.7);
				double spreadZ = particleConfig.getDouble("spread.z", 0.5);
				
				for(MissionType type : types)
					for(IMission mission : QuestWorld.getViewer().getMissionsOf(type)) {
						NPC npc = npcFrom(mission);
						if (npc != null && npc.getEntity() != null) {
							ArrayList<Player> players = new ArrayList<>();
							
							for (Entity n: npc.getEntity().getNearbyEntities(nearX, nearY, nearZ)) {
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
									p.spawnParticle(particleType, npc.getEntity().getLocation().add(0, 1, 0),
											count, spreadX, spreadY, spreadZ, extra);
								}
							}
						}
					}
			}
		}, 0L, 32L);
	}
}
