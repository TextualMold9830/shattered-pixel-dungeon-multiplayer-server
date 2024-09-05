package com.shatteredpixel.shatteredpixeldungeon.plugins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.watabou.plugins.PluginManifest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginLoader {
    public List<PluginManifest> pluginManifests = new ArrayList<>();

    public PluginLoader(List<PluginManifest> pluginManifests) {
        this.pluginManifests.addAll(pluginManifests);
    }
    public List<Plugin> loadPlugins(){
        ArrayList<Plugin> plugins = new ArrayList<>();
        if (pluginManifests != null && !pluginManifests.isEmpty() ){
            for (PluginManifest manifest: pluginManifests){
                if (manifest != null && manifest.mainClass() != null) {
                    try {
                        URLClassLoader loader = new URLClassLoader(new URL[]{URI.create(manifest.getOriginPath()).toURL()});
                        Class pluginClass = loader.loadClass(manifest.mainClass());
                        if (ClassReflection.isAssignableFrom(Plugin.class, pluginClass) && !ClassReflection.isAbstract(pluginClass)){
                            plugins.add((Plugin) pluginClass.newInstance());
                        } else {
                            Gdx.app.error("PluginLoader", "Not a plugin: " + manifest.mainClass());
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        Gdx.app.error("PluginLoader","invalid class: " + manifest.mainClass());
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Gdx.app.error("PluginLoader", "Invalid manifest in plugin");
                }
            }
        }
        return plugins;
    }
}