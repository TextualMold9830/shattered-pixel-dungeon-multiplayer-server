package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.particles.SerializableParticleFactory;
import org.json.JSONObject;

public class SpeckFactorySerializer implements Serializer<Speck.SpeckFactory> {

    @Override
    public Object serialize(Speck.SpeckFactory factory, SerializationContext ctx, String profile) {
        JSONObject object = (JSONObject) ctx.serializeAs(factory, SerializableParticleFactory.class, profile);
        if (object == null) {
            return null;
        }
        object.put("type", factory.type);
        object.put("lightMode", factory.lightMode);
        return object;
    }
}
