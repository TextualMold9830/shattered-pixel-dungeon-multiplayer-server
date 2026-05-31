package com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters;

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;

public class EmitterBurstSerializer extends BaseEmitterSerializer implements Serializer<Emitter> {

	@Override
	public Object serialize(Emitter emitter, SerializationContext ctx, String profile) {
		return baseObject("emitter_burst", emitter, ctx);
	}
}
