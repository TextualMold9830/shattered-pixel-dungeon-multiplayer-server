package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ItemAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ItemActionSerializers {

    @Contract(value = "-> fail", pure = true)
    private ItemActionSerializers() {
        throw new RuntimeException();
    }

    private static JSONArray serializePath(@NotNull java.util.List<Integer> path) {
        JSONArray pathArr = new JSONArray();
        for (int p : path) pathArr.put(p);
        return pathArr;
    }

    public static class Add extends NetworkActionSerializer<ItemAction.Add> {
        @Override
        protected JSONObject serializeInternal(@NotNull ItemAction.Add obj, @NotNull SerializationContext ctx, @NotNull String profile) {
            JSONObject object = new JSONObject();
            object.put("path", serializePath(obj.path));
            object.put("item", Item.packItem(obj.item, obj.hero));
            return object;
        }
    }

    public static class Remove extends NetworkActionSerializer<ItemAction.Remove> {
        @Override
        protected JSONObject serializeInternal(@NotNull ItemAction.Remove obj, @NotNull SerializationContext ctx, @NotNull String profile) {
            JSONObject object = new JSONObject();
            object.put("path", serializePath(obj.path));
            return object;
        }
    }

    public static class Update extends NetworkActionSerializer<ItemAction.Update> {
        @Override
        protected JSONObject serializeInternal(@NotNull ItemAction.Update obj, @NotNull SerializationContext ctx, @NotNull String profile) {
            JSONObject object = new JSONObject();
            object.put("path", serializePath(obj.path));
            object.put("item", Item.packItem(obj.item, obj.hero));
            return object;
        }
    }

    public static class Replace extends NetworkActionSerializer<ItemAction.Replace> {
        @Override
        protected JSONObject serializeInternal(@NotNull ItemAction.Replace obj, @NotNull SerializationContext ctx, @NotNull String profile) {
            JSONObject object = new JSONObject();
            object.put("path", serializePath(obj.path));
            object.put("item", Item.packItem(obj.item, obj.hero));
            return object;
        }
    }
}
