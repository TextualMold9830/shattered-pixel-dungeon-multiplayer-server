package com.shatteredpixel.shatteredpixeldungeon.network;

import java.util.ArrayList;

public class PlantCache {
    private static final ArrayList<Integer> plants = new ArrayList<>();
    public static boolean contains(int pos){
        return plants.contains(pos);
    }
    public static boolean add(int pos){
        return plants.add(pos);
    }
    public static void remove(int pos){
        if (plants.contains(pos)) {
            plants.remove(Integer.valueOf(pos));
        }
    }
     public static void clear(){
        plants.clear();
    }

}
