package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ChatMessageAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ChatMessageActionSerializer extends NetworkActionSerializer<ChatMessageAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull ChatMessageAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("text", obj.text);
        return actionObj;
    }
}
