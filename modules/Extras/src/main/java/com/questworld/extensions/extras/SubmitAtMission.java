package com.questworld.extensions.extras;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.Manual;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.extensions.builtin.LocationMission;
import me.mrCookieSlime.QuestWorld.extensions.builtin.SubmitMission;
import me.mrCookieSlime.QuestWorld.listeners.Input;
import me.mrCookieSlime.QuestWorld.listeners.InputType;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;
import me.mrCookieSlime.QuestWorld.utils.Text;

public class SubmitAtMission extends MissionType implements Manual {

	SubmitMission submit = QuestWorld.getMissionType("SUBMIT");
	LocationMission locate = QuestWorld.getMissionType("REACH_LOCATION");
	
	public SubmitAtMission() {
		super("SUBMIT_AT", false, false, new ItemStack(Material.MAP));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		Location location = instance.getLocation();
		String locationName = instance.getCustomString();
		if(locationName.isEmpty())
			locationName = LocationMission.coordinateString(location);
		
		return "&7Submit "+instance.getAmount()+"x "
				+ Text.niceName(instance.getMissionItem().getType().name())
				+ " at " + locationName;
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return locate.userDisplayItem(instance);
	}

	@Override
	public int onManual(Player player, IMission mission) {
		
		if(locate.onManual(player, mission) != FAIL)
			return submit.onManual(player, mission);

		return FAIL;
	}

	@Override
	public String getLabel() {
		return "Submit";
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);
		putButton(10, MissionButton.item(changes));
		putButton(17, MissionButton.amount(changes));
		putButton(11, MissionButton.location(changes));
		putButton(12, MissionButton.simpleButton(
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
		putButton(16, MissionButton.simpleButton(
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
