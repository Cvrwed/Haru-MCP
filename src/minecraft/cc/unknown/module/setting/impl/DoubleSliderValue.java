package cc.unknown.module.setting.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.JsonObject;

import cc.unknown.module.setting.Setting;

public class DoubleSliderValue extends Setting {
	private final String name;
    private double valMax, valMin, max, min, interval;

    public DoubleSliderValue(String name, double valMin, double valMax, double min, double max, double intervals) {
        super(name);
        this.name = name;
        this.valMin = valMin;
        this.valMax = valMax;
        this.min = min;
        this.max = max;
        this.interval = intervals;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void resetToDefaults() {
        this.setValueMin(0.0);
        this.setValueMax(0.0);
    }

    @Override
    public JsonObject getConfigAsJson() {
        JsonObject data = new JsonObject();
        data.addProperty("type", getSettingType());
        data.addProperty("valueMin", getInputMin());
        data.addProperty("valueMax", getInputMax());
        return data;
    }

    @Override
    public String getSettingType() {
        return "doubleslider";
    }

    @Override
    public void applyConfigFromJson(JsonObject data) {
        if(!data.get("type").getAsString().equals(getSettingType()))
            return;

        setValueMax(data.get("valueMax").getAsDouble());
        setValueMin(data.get("valueMin").getAsDouble());
    }

    public double getInputMin() {
        return getInputValue(valMin, Double.class);
    }

    public double getInputMax() {
        return getInputValue(valMax, Double.class);
    }

    public long getInputMinToLong() {
        return getInputValue(valMin, Long.class);
    }

    public long getInputMaxToLong() {
        return getInputValue(valMax, Long.class);
    }

    public int getInputMinToInt() {
        return getInputValue(valMin, Integer.class);
    }

    public int getInputMaxToInt() {
        return getInputValue(valMax, Integer.class);
    }

    public float getInputMinToFloat() {
        return getInputValue(valMin, Float.class);
    }

    public float getInputMaxToFloat() {
        return getInputValue(valMax, Float.class);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public void setValueMin(double n) {
        n = correct(n, this.min, this.valMax);
        n = (double)Math.round(n * (1.0D / this.interval)) / (1.0D / this.interval);
        this.valMin = n;
    }

    public void setValueMax(double n) {
        n = correct(n, this.valMin, this.max);
        n = (double)Math.round(n * (1.0D / this.interval)) / (1.0D / this.interval);
        this.valMax = n;
    }

    public static double correct(double val, double min, double max) {
        val = Math.max(min, val);
        val = Math.min(max, val);
        return val;
    }

    public static double round(double val, int p) {
        if (p < 0) {
            return 0.0D;
        } else {
            BigDecimal bd = new BigDecimal(val);
            bd = bd.setScale(p, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
    
    private <T extends Number> T getInputValue(double value, Class<T> returnType) {
        double roundedValue = round(value, 2);
        if (returnType == Double.class) {
            return (T) Double.valueOf(roundedValue);
        } else if (returnType == Long.class) {
            return (T) Long.valueOf((long) roundedValue);
        } else if (returnType == Integer.class) {
            return (T) Integer.valueOf((int) roundedValue);
        } else if (returnType == Float.class) {
            return (T) Float.valueOf((float) roundedValue);
        } else {
            throw new IllegalArgumentException("Unsupported return type");
        }
    }
}
