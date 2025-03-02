package com.shatteredpixel.shatteredpixeldungeon.network;

import java.util.ArrayList;

public class TrapCache {
    private static final ArrayList<Integer> traps = new ArrayList<>();
    public static boolean contains(int pos){
        return traps.contains(pos);
    }
    public static boolean add(int pos){
        return traps.add(pos);
    }
    public static void remove(int pos){
        if (traps.contains(pos)) {
            traps.remove(Integer.valueOf(pos));
        }
    }
    public static void clear(){
        traps.clear();
    }

}
