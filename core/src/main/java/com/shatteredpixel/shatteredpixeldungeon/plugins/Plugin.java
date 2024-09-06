package com.shatteredpixel.shatteredpixeldungeon.plugins;

import com.watabou.plugins.PluginManifest;

public abstract class Plugin {
    public PluginManifest manifest;
    public abstract void onEnable();
    public abstract void onDisable();
    public void handleEvent(Event event){}
    public String defaultConfig(){
        return null;
    }
}
