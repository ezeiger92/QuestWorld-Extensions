package com.questworld.extension.money;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.Manual;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class PayMission extends MissionType implements Manual {

	public PayMission() {
		super("GIVE_MONEY", false, new ItemStack(Material.GOLD_INGOT));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		String currency = Money.formatCurrency(instance.getCustomString(), instance.getAmount());
		
		return "&7Give " + instance.getAmount() + " " + currency;
	}
	
	@Override
	public void validate(IMissionState state) {
		if(state.getCustomString().length() == 0)
			state.setCustomString("dollars,1:dollar");
		
		state.apply();
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(17, MissionButton.amount(changes, 50));
	}

	@Override
	public void onManual(Player p, MissionEntry result) {
		IMission mission = result.getMission();
		Economy e = Money.getEcon();
		if(e.hasAccount(p)) {
			double d = e.getBalance(p);
			if((int)d > 0) {
				EconomyResponse r = e.withdrawPlayer(p, "Quest Payment", (double)mission.getAmount());
				if(r.type == EconomyResponse.ResponseType.SUCCESS) {
					double difference = r.amount - (int)r.amount;
					result.addProgress((int)r.amount);
					e.depositPlayer(p, "Quest Change", difference);
					return;
				}
			}
		}
		
		QuestWorld.getSounds().MISSION_REJECT.playTo(p);
	}

	@Override
	public String getLabel() {
		return "&r> Click to deposit money";
	}

}
