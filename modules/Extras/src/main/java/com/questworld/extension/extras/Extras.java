package com.questworld.extension.extras;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.questworld.api.QuestExtension;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.IMission;
import com.questworld.util.Pair;

public class Extras extends QuestExtension {
	FileConfiguration config;
	private int task = -1;
	private ClickBlockMission clickBlock;
	
	@Override
	protected void initialize(Plugin plugin) {
		clickBlock = new ClickBlockMission();
		setMissionTypes(
			clickBlock,
			new CommandMission(),
			new DoQuestMission(),
			new HarvestMission(),
			new StatisticMission(),
			new SubmitAtMission());
		
		onReload();
	}
	
	@Override
	public void onSave() {
		getResourceLoader().saveConfigNoexcept(config, "config-extras.yml", true);
	}
	
	@Override
	public void onReload() {
		config = getConfiguration("config-extras.yml");
		
		if(task >= 0)
			Bukkit.getScheduler().cancelTask(task);
		task = runner(QuestWorld.getPlugin());
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
			
			for(IMission mission : QuestWorld.getViewer().getMissionsOf(clickBlock)) {
				Location loc = mission.getLocation();
				
				if (loc != null) {
					for (Entity n: loc.getWorld().getNearbyEntities(loc, nearX, nearY, nearZ))
						if (n instanceof Player) {
							Player p = (Player)n;
							
							if(p.isOnline() && QuestWorld.getPlayerStatus(p).isMissionActive(mission))
								display.add(new Pair<>(p, loc));
						}
				}
			}
		
			for(Pair<Player, Location> pair : display)
				pair.getLeft().spawnParticle(particleType, pair.getRight(),
						count, spreadX, spreadY, spreadZ, extra, particleData);
			
		}, 0L, particleConfig.getLong("period", 32L));
	}
}
