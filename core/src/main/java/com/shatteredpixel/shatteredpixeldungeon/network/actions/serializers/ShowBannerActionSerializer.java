package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShowBannerAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Locale;

public class ShowBannerActionSerializer extends NetworkActionSerializer<ShowBannerAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull ShowBannerAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("banner", obj.banner.toString().toLowerCase(Locale.ROOT));
        actionObj.put("color", obj.color);
        actionObj.put("fade_time", obj.fadeTime);
        actionObj.put("show_time", obj.showTime);
        return actionObj;
    }
}
