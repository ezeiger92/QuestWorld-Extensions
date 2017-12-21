package com.questworld.extension.extras;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.Manual;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.extension.builtin.LocationMission;
import me.mrCookieSlime.QuestWorld.extension.builtin.SubmitMission;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
import me.mrCookieSlime.QuestWorld.util.Text;

public class SubmitAtMission extends MissionType implements Manual {
	SubmitMission submit = QuestWorld.getMissionType("SUBMIT");
	LocationMission locate = QuestWorld.getMissionType("REACH_LOCATION");
	
	public SubmitAtMission() {
		super("SUBMIT_AT", false, new ItemStack(Material.MAP));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		Location location = instance.getLocation();
		String locationName = instance.getCustomString();
		if(locationName.isEmpty())
			locationName = Text.stringOf(location);
		
		return "&7Submit "+instance.getAmount()+"x "
				+ Text.niceName(instance.getItem().getType().name())
				+ " at " + locationName;
	}

	@Override
	public void onManual(Player player, MissionEntry result) {
		int progress = result.getProgress();
		locate.onManual(player, result);
		
		if(progress != result.getProgress()) {
			result.setProgress(progress);
			submit.onManual(player, result);
		}
	}

	@Override
	public String getLabel() {
		return "&r> Click to submit items";
	}
	
	@Override
	public void validate(IMissionState missionState) {
		locate.validate(missionState);
		submit.validate(missionState);
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(10, MissionButton.item(changes));
		putButton(17, MissionButton.amount(changes));
		putButton(11, MissionButton.location(changes));
		putButton(12, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&r" + changes.getCustomString(),
						 "",
						 "&e> Give your Location a Name").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					p.closeInventory();
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, Translation.LOCMISSION_NAME_EDIT),
							(c,s) -> {
								changes.setCustomString(Text.colorize(s));
								
								if(changes.apply()) {
									PlayerTools.sendTranslation(p, true, Translation.LOCMISSION_NAME_SET);
								}

								QuestBook.openQuestMissionEditor(p, changes.getSource());
								return true;
							}
					));
				}
		));
		putButton(16, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.COMPASS).wrapText(
						"&7Radius: &a" + changes.getCustomInt(),
						"",
						"&rLeft Click: &e+1",
						"&rRight Click: &e-1",
						"&rShift + Left Click: &e+16",
						"&rShift + Right Click: &e-16").get(),
				event -> {
					int amount = MissionButton.clickNumber(changes.getCustomInt(), 16, event);
					changes.setCustomInt(Math.max(amount, 1));
				}
		));
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return locate.userDisplayItem(instance);
	}
}
