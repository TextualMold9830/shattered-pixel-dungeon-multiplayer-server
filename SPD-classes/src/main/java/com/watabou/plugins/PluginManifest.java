package com.watabou.plugins;

import java.util.HashMap;

//May change later
public class PluginManifest {
    public HashMap<String, String> config = new HashMap<>();
    public void setConfig(String config){
        this.config.clear();
        String[] keyValue = config.split("\n");
        for (String string : keyValue){
            String[] split = string.split("=");
            this.config.put(split[0], split[1]);

        }
    }

    public PluginManifest(String content) {
        setConfig(content);
    }
    public PluginManifest() {
    }
    public String mainClass(){
        return config.get("mainclass");
    }

}
