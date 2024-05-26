package cc.unknown.module.impl.visuals;

import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

@Register(name = "Animations", category = Category.Visuals)
public class Animations extends Module {

    public final ModeValue blockMode = new ModeValue("Block Mode", "1.7", "1.7", "1.8", "Astolfo", "Spin");

    public final SliderValue animationSpeed = new SliderValue("Animation Speed", 1.0D, 0.1D, 3.0D, 0.1D);
    public final SliderValue spinSpeed = new SliderValue("Spin Speed", 0.5D, 0.1D, 2.0D, 0.1D);
    public  final SliderValue xValue = new  SliderValue("X", 0.0D, -1.0D, 1.0D, 0.05D);
    public  final SliderValue yValue = new  SliderValue("Y", 0.0D, -1.0D, 1.0D, 0.05D);
    public  final SliderValue zValue = new  SliderValue("Z", 0.0D, -1.0D, 1.0D, 0.05D);

    public Animations() {
        registerSetting(blockMode, animationSpeed, spinSpeed, xValue, yValue, zValue);
    }

}
