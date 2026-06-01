package com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters;

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.json.JSONObject;

abstract class BaseEmitterSerializer {

	protected JSONObject baseObject(String actionType, Emitter emitter, SerializationContext ctx) {
		JSONObject object = new JSONObject();
		object.put("action_name", actionType);
		object.put("anchor", ctx.serialize(emitter.anchor()));
		Object factory = ctx.serialize(emitter.networkFactory());
		if (factory == null || factory == JSONObject.NULL) {
			return null;
		}
		object.put("factory", factory);
		object.put("interval", emitter.networkInterval());
		object.put("quantity", emitter.networkQuantity());
		return object;
	}
}
