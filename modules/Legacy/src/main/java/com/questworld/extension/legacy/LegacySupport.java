package com.questworld.extension.legacy;

import com.questworld.api.QuestExtension;
import com.questworld.util.Reflect;

public class LegacySupport extends QuestExtension {
	public LegacySupport() {
		Reflect.addAdapter(new Adapter_1_8_8());
	}
}
