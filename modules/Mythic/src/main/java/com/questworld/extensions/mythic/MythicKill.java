package com.questworld.extensions.mythic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.SinglePrompt;
import com.questworld.api.Translation;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MenuData;
import com.questworld.api.menu.MissionButton;
import com.questworld.api.menu.QuestBook;
import com.questworld.util.ItemBuilder;
import com.questworld.util.PlayerTools;
import com.questworld.util.Text;

import io.lumine.xikage.mythicmobs.MythicMobs;

public class MythicKill extends MissionType implements Listener {

	public MythicKill() {
		super("MYTHIC_KILL", true, new ItemStack(Material.DIAMOND_SWORD));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		String[] parts = instance.getCustomString().split(" ", 2);
		
		String name = parts[parts.length - 1];
		
		return "Kill " + instance.getAmount() + "x " + name;
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}

	@EventHandler
	public void onKill(EntityDeathEvent e) {
		Player killer = e.getEntity().getKiller();
		if (killer == null)
			return;

		for (MissionEntry r : QuestWorld.getMissionEntries(this, killer)) {
			
			String internalName = r.getMission().getCustomString().split(" ", 2)[0];
			
			MythicMobs.inst().getMobManager().getActiveMob(e.getEntity().getUniqueId()).ifPresent(mob -> {
				if(mob.getType().getInternalName().equals(internalName)) {
					r.addProgress(1);
				}
			});
		}
	}
	
	@Override
	public void validate(IMissionState instance) {
		if(instance.getCustomString().isEmpty()) {
			instance.setCustomString("mythic_mob_1");
		}
	}

	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(12, new MenuData(new ItemBuilder(Material.NAME_TAG)
				.wrapText("&7MythicMob name: &r&o" + changes.getCustomString(),
						"",
						"&e> Click to change the Name")
				.get(), event -> {
					Player p = (Player) event.getWhoClicked();

					// TODO: Custom translation
					p.closeInventory();
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, Translation.KILLMISSION_NAME_EDIT), (c, s) -> {
								changes.setCustomString(Text.deserializeNewline(Text.colorize(s)));
								if (changes.apply()) {
									PlayerTools.sendTranslation(p, true, Translation.KILLMISSION_NAME_SET);
								}
								QuestBook.openQuestMissionEditor(p, changes);
								return true;
							}));
				}));
		
		putButton(17, MissionButton.amount(changes));
	}
}
