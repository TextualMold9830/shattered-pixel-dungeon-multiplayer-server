package com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters;

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;
import org.json.JSONObject;

public class EmitterPourSerializer extends BaseEmitterSerializer implements Serializer<Emitter> {

	@Override
	public Object serialize(Emitter emitter, SerializationContext ctx, String profile) {
		JSONObject object = baseObject("emitter_pour", emitter, ctx);
		if (object != null) {
			object.put("id", emitter.networkId());
		}
		return object;
	}
}
