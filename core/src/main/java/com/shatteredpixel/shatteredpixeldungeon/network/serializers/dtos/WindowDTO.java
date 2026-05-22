package com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class WindowDTO {
    public final String type;
    public final int windowID;
    @Nullable
    public final JSONObject args;

    public WindowDTO(String type, int windowID, @Nullable JSONObject args) {
        this.type = type;
        this.windowID = windowID;
        this.args = args;
    }
}
