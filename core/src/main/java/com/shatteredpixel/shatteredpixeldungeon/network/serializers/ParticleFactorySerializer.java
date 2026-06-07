package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.ParticleFactoryRegistry;
import com.watabou.noosa.particles.SerializableParticleFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ParticleFactorySerializer implements Serializer<SerializableParticleFactory> {

    @Override
    public Object serialize(@NotNull SerializableParticleFactory factory, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object = baseObject(factory);
        return object;
    }

    public static JSONObject baseObject(SerializableParticleFactory factory) {
        String name = ParticleFactoryRegistry.resolve(factory);
        if (name == null) {
            return null;
        }
        JSONObject object = new JSONObject();
        object.put("path", factory.getClass().getName());
        object.put("factory_type", name);
        object.put("light_mode", lightMode(factory));
        return object;
    }

    private static boolean lightMode(SerializableParticleFactory factory) {
        if (factory instanceof com.nikita22007.multiplayer.noosa.particles.Emitter.Factory) {
            return ((com.nikita22007.multiplayer.noosa.particles.Emitter.Factory) factory).lightMode();
        }
        if (factory instanceof com.watabou.noosa.particles.Emitter.Factory) {
            return ((com.watabou.noosa.particles.Emitter.Factory) factory).lightMode();
        }
        return false;
    }
}
