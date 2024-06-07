package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

@Info(name = "HitSelect", category = Category.Combat)
public class HitSelect extends Module {

    private final SliderValue time = new SliderValue("Time", 100, 0, 500, 1);
    private final Cold timer = new Cold();

    public HitSelect() {
        this.registerSetting(time);
    }

    @EventLink
    public void onLiving(LivingEvent e) {
        if (attack()) {
            pressKeybindOnce(mc.gameSettings.keyBindAttack);
            timer.reset();
        }
    }

    private boolean attack() {
        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
            return false;

        Entity target = mc.objectMouseOver.entityHit;
        if (!(target instanceof EntityPlayer))
            return false;

        EntityPlayer entity = (EntityPlayer) target;
        return entity.hurtTime == 0 || entity.hurtTime == 1 || timer.reached(time.getInputToLong());
    }

    private void pressKeybindOnce(KeyBinding key) {
        KeyBinding.setKeyBindState(key.getKeyCode(), true);
        KeyBinding.onTick(key.getKeyCode());
        KeyBinding.setKeyBindState(key.getKeyCode(), false);
        KeyBinding.onTick(key.getKeyCode());
    }
}
