package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import org.json.JSONObject;

public class SplashFactorySerializer implements Serializer<Splash.SplashFactory> {

    @Override
    public Object serialize(Splash.SplashFactory factory, SerializationContext ctx, String profile) {
        JSONObject object = ParticleFactorySerializer.baseObject(factory);
        if (object == null) {
            return null;
        }
        object.put("color", factory.color);
        object.put("dir", factory.dir);
        object.put("cone", factory.cone);
        return object;
    }
}
