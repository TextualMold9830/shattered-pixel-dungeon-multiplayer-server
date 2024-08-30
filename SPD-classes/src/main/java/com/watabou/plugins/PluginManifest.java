package com.watabou.plugins;

import java.util.HashMap;

//May change later
public class PluginManifest {
    public final String originPath;
    public HashMap<String, String> config = new HashMap<>();
    public void setConfig(String config){
        this.config.clear();
        String[] keyValue = config.split("\n");
        for (String string : keyValue){
            String[] split = string.split("=");
            this.config.put(split[0], split[1]);

        }
    }

    public PluginManifest(String content, String originPath) {
        setConfig(content);
        this.originPath = originPath;
    }

    public String getOriginPath() {
        return originPath;
    }

    public String mainClass(){
        return config.get("mainclass");
    }

}
