package com.questworld.extension.citizens;

import java.util.HashSet;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.questworld.api.MissionType;
import com.questworld.api.QuestExtension;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.IMission;
import com.questworld.util.Pair;

public class Citizens extends QuestExtension {
	FileConfiguration config;
	private int task = -1;
	private static HashSet<MissionType> types = new HashSet<>();
	
	public static NPC npcFrom(IMission instance) {
		return npcFrom(instance.getCustomInt());
	}
	
	public static NPC npcFrom(int id) {
		return CitizensAPI.getNPCRegistry().getById(id);
	}
	
	public Citizens() {
		super("Citizens");
		setMissionTypes(
			new CitizenAcceptQuestMission(),
			new CitizenInteractMission(),
			new CitizenSubmitMission(),
			new CitizenKillMission());
		
		for(MissionType type : getMissionTypes())
			types.add(type);
	}
	
	@Override
	public void onSave() {
		getResourceLoader().saveConfigNoexcept(config, "config-citizens.yml", true);
	}
	
	@Override
	public void onReload() {
		config = getConfiguration("config-citizens.yml");
		
		if(task >= 0)
			Bukkit.getScheduler().cancelTask(task);
		task = runner(QuestWorld.getPlugin());
	}
	
	@Override
	protected void initialize(Plugin parent) {
		parent.getServer().getPluginManager().registerEvents(new CitizenButton.Listener(), parent);
		
		onReload();
	}
	
	private int runner(Plugin parent) {
		ConfigurationSection particleConfig = config.getConfigurationSection("npc_particles");
		
		return parent.getServer().getScheduler().scheduleSyncRepeatingTask(parent, () -> {
			Particle particleType = Particle.VILLAGER_HAPPY;
			try {
				particleType = Particle.valueOf(particleConfig.getString("type"));
			}
			catch(Exception e) {
			}
			
			Object particleData = config.get("data", null);
			
			if(!particleType.getDataType().isInstance(particleData)) {
				if(particleData instanceof ItemStack) {
					particleData = ((ItemStack)particleData).getData();
					
					if(!particleType.getDataType().isInstance(particleData))
						particleData = null;
				}
				else
					particleData = null;
			}
				
			
			double nearX = particleConfig.getDouble("nearby.x", 20.0);
			double nearY = particleConfig.getDouble("nearby.y", 8.0);
			double nearZ = particleConfig.getDouble("nearby.z", 20.0);
			
			int count = particleConfig.getInt("count", 20);
			int extra = particleConfig.getInt("extra", 0);
			
			double spreadX = particleConfig.getDouble("spread.x", 0.5);
			double spreadY = particleConfig.getDouble("spread.y", 0.7);
			double spreadZ = particleConfig.getDouble("spread.z", 0.5);
			
			HashSet<Pair<Player, Location>> display = new HashSet<>();
			
			for(MissionType type : types)
				for(IMission mission : QuestWorld.getViewer().getMissionsOf(type)) {
					NPC npc = npcFrom(mission);
					
					if (npc != null) {
						Entity npcEnt = npc.getEntity();
						
						if(npcEnt != null) {
							Location entityLoc = npcEnt.getLocation().add(0, 1, 0);
							
							for (Entity n: npcEnt.getNearbyEntities(nearX, nearY, nearZ))
								if (n instanceof Player) {
									Player p = (Player)n;
									
									if(p.isOnline() && QuestWorld.getPlayerStatus(p).isMissionActive(mission))
										display.add(new Pair<>(p, entityLoc));
								}
						}
					}
				}
			
			for(Pair<Player, Location> pair : display)
				pair.getLeft().spawnParticle(particleType, pair.getRight(),
						count, spreadX, spreadY, spreadZ, extra, particleData);
			
		}, 0L, particleConfig.getLong("period", 32L));
	}
}
