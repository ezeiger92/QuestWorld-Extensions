package com.questworld.extensions.extras;

import me.mrCookieSlime.QuestWorld.api.Translator;

public enum ExtraTranslation implements Translator {
	test("test.node", "player", "length")
	;
	private String path;
	private String[] placeholders;
	
	ExtraTranslation(String path, String... placeholders) {
		this.path = path;
		this.placeholders = wrap(placeholders);
	}
	
	@Override
	public String path() {
		return path;
	}
	
	@Override
	public String[] placeholders() {
		return placeholders;
	}
}
