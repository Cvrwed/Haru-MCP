package cc.unknown.module.setting;

import java.util.function.Supplier;

import com.google.gson.JsonObject;

public abstract class Setting {
	
	public String name;
	
	public Setting(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

    public abstract void resetToDefaults();
    
	public abstract JsonObject getConfigAsJson();

	public abstract String getSettingType();

	public abstract void applyConfigFromJson(JsonObject data);
	
}
