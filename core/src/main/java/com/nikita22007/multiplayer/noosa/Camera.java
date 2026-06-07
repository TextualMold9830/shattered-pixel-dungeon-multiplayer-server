package com.nikita22007.multiplayer.noosa;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShakeCameraAction;
import org.jetbrains.annotations.Nullable;

public class Camera {
    public static void shake(float magnitude, float duration) {
        shake(magnitude, duration, null);
    }

    public static void shake(float magnitude, float duration, @Nullable Hero heroForVisual) {
        ShakeCameraAction action = new ShakeCameraAction(magnitude, duration);
        if (heroForVisual != null) {
            SendData.sendAction(heroForVisual, action);
        } else {
            SendData.sendActionForAll(action);
        }
    }
}

