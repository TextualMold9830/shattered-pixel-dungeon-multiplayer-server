package com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.emitters.EmitterAnchor;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class EmitterAnchorSerializer implements Serializer<EmitterAnchor> {

	@Override
	public Object serialize(@NotNull EmitterAnchor anchor, @NotNull SerializationContext ctx, @NotNull String profile) {
		JSONObject object = new JSONObject();
		object.put("type", anchor.type());
		object.put("x", anchor.x());
		object.put("y", anchor.y());
		object.put("width", anchor.width());
		object.put("height", anchor.height());
		object.put("shift_x", anchor.shiftX());
		object.put("shift_y", anchor.shiftY());
		if (anchor.cell() != null) {
			object.put("cell", anchor.cell());
		}
		if (anchor.targetCharId() != null) {
			object.put("target_char", anchor.targetCharId());
			object.put("fill_target", anchor.fillTarget());
		}
		return object;
	}
}
