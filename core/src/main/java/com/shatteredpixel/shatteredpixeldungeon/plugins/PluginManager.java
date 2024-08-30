package com.shatteredpixel.shatteredpixeldungeon.plugins;

import java.util.List;

public class PluginManager {
    private final PluginLoader loader;
    public List<Plugin> plugins;
    public PluginManager(PluginLoader loader) {
        this.loader = loader;
        plugins = loader.loadPlugins();
    }
    public void initialize(){
        if (plugins != null && !plugins.isEmpty()){
            for (Plugin plugin: plugins){
                if (plugin != null){
                    plugin.onEnable();
                }
            }
        }
    }
    public void shutdown(){
        if (plugins != null && !plugins.isEmpty()){
            for (Plugin plugin: plugins){
                if (plugin != null){
                    plugin.onDisable();
                }
            }
        }
    }
    public void fireEvent(Event event){
        if (plugins != null && !plugins.isEmpty()){
            for (Plugin plugin: plugins){
                if (plugin != null){
                    plugin.handleEvent(event);
                }
            }
        }
    }

}
