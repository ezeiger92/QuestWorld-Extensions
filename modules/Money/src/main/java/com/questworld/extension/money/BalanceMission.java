package com.questworld.extension.money;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.Ticking;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.Text;
import net.milkbowl.vault.economy.Economy;

public class BalanceMission extends MissionType implements Ticking {

	private enum CheckType {
		AT_LEAST,
		AT_MOST,
		EXACTLY,
		;
		private static String[] v = null;
		public static String[] stringValues() {
			if(v == null) {
				int len = values().length;
				v = new String[len];
				for(int i = 0; i < len; ++i) {
					v[i] = values()[i].toString();
				}
			}
			
			return v;
		}
		
		public static String stringAt(int ind) {
			if(ind < 0 || ind >= values().length)
				ind = 0;
			
			return Text.niceName(values()[ind].toString());
		}
	}
	
	public BalanceMission() {
		super("HAVE_MONEY", false, new ItemStack(Material.GOLD_INGOT));
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		String currency = Money.formatCurrency(instance.getCustomString(), instance.getAmount());
		String checkType = CheckType.stringAt(instance.getCustomInt());
		
		return "&7Have " + checkType.toLowerCase() + " " + instance.getAmount() + " " + currency;
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}
	
	@Override
	public void validate(IMissionState state) {
		if(state.getCustomString().length() == 0)
			state.setCustomString("dollars,1:dollar");
		
		state.apply();
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		int len = CheckType.values().length;
		if(changes.getCustomInt() < 0 || changes.getCustomInt() >= len)
			changes.setCustomInt(0);
		
		putButton(17, MissionButton.amount(changes, 50));
		putButton(11, MissionButton.simpleButton(changes,
				new ItemBuilder(Material.PAPER).display("Check Type:").selector(changes.getCustomInt(), CheckType.stringValues()).get(),
				event -> {
					int newV = changes.getCustomInt() + (event.isRightClick() ? -1 : 1);
					changes.setCustomInt((newV + len) % len);
				}
		));
		putButton(12, MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"Currency Display",
						" &7" + changes.getCustomString(),
						" &ecurrency[,1:singular[,2:custom[, ..]]]",
						"",
						"&cLeft Click to set currency format",
						"&cRight Click to remove format").get(),
				event -> {
					if(event.isRightClick())
						changes.setCustomString("");
				}
		));
	}

	@Override
	public void onManual(Player player, MissionEntry result) {
		Economy e = Money.getEcon();
		if(e.hasAccount(player))
			result.setProgress((int)e.getBalance(player));
	}
}
