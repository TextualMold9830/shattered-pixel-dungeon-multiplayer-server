package com.shatteredpixel.shatteredpixeldungeon.server.plugins;

public abstract class Plugin {
    public com.shatteredpixel.shatteredpixeldungeon.server.plugins.PluginManifest manifest;
    public abstract void onEnable();
    public abstract void onDisable();
    public void handleEvent(Event event){}
    public String defaultConfig(){
        return null;
    }
}
