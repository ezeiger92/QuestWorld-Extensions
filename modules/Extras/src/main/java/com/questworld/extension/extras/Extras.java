package com.questworld.extension.extras;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
	private HarvestMission harvest;
	
	@Override
	protected void initialize(Plugin plugin) {
		clickBlock = new ClickBlockMission();
		harvest = new HarvestMission();
		setMissionTypes(
			clickBlock,
			new CommandMission(),
			new DoQuestMission(),
			harvest,
			new StatisticMission(),
			new SubmitAtMission());
		
		onReload();
	}
	
	@Override
	public void onSave() {
		try {
			getResourceLoader().saveConfig(config, "config-extras.yml");
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Override
	public void onReload() {
		config = getConfiguration("config-extras.yml");
		ConfigurationSection cropSection = config.getConfigurationSection("crop_aliases");
		
		for(Map.Entry<String, Object> cropEntry : cropSection.getValues(false).entrySet()) {
			Material crop = Material.matchMaterial(cropEntry.getKey());
			
			if(crop != null) {
				EnumSet<Material> aliases = EnumSet.noneOf(Material.class);
				
				// Why did you give me a non-list?
				if(!List.class.isAssignableFrom(cropEntry.getValue().getClass())) {
					continue;
				}
	
				for(Object s : (List<?>)cropEntry.getValue()) {
					Material alias = Material.matchMaterial(String.valueOf(s));
					
					if(alias != null) {
						aliases.add(alias);
					}
				}
				
				harvest.addCrop(crop, aliases);
			}
		}
		
		if(task >= 0)
			Bukkit.getScheduler().cancelTask(task);
		task = runner(QuestWorld.getPlugin());
	}
	
	private int runner(Plugin parent) {
		ConfigurationSection particleConfig = config.getConfigurationSection("click_block_particles");
		
		long period = particleConfig.getLong("period", 32L);
		
		if(period <= 0) {
			return -1;
		}
		
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
			
		}, 0L, period);
	}
}
