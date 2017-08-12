package com.questworld.extensions.extras;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.listeners.Input;
import me.mrCookieSlime.QuestWorld.listeners.InputType;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;

public class ClickBlockMission extends MissionType {

	public ClickBlockMission() {
		super("CLICK_BLOCK", false, false, new ItemStack(Material.STONE_BUTTON));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "Click block at X Y Z";
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);

		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemStack(Material.BEDROCK),
				event -> {
					Player p = (Player)event.getWhoClicked();
				}
		));
		
		putButton(11, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).display("&r" + changes.getCustomString()).lore(
						 "",
						 "&e> Give your Location a Name").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					QuestWorld.getInstance().storeInput(p.getUniqueId(), new Input(InputType.LOCATION_NAME, changes.getSource()));
					PlayerTools.sendTranslation(p, true, Translation.location_rename);
					p.closeInventory();
				}
		));
		putButton(17, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.COMPASS).display("&7Radius: &a" + changes.getCustomInt()).lore(
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
}
