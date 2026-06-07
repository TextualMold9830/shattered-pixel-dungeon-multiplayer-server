package com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters;

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;
import org.jetbrains.annotations.NotNull;

public class EmitterBurstSerializer extends BaseEmitterSerializer implements Serializer<Emitter> {

	@Override
	public Object serialize(@NotNull Emitter emitter, @NotNull SerializationContext ctx, @NotNull String profile) {
		return baseObject("emitter_burst", emitter, ctx);
	}
}
