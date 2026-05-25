package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ActorSerializer implements Serializer<Actor> {

    @Override
    public Object serialize(Actor actor, SerializationContext ctx, String profile) {
        Log.w(
                "ActorSerializer",
                "Unexpected ActorSerializer call for type " + actor.getClass().getName()
                        + " with profile '" + profile + "'. Stacktrace:\n" + stackTrace()
        );
        return new JSONObject();
    }

    private String stackTrace() {
        StringWriter stackTrace = new StringWriter();
        new Exception().printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }
}
