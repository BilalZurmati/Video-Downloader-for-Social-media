package com.socialdownloader.models;

public class DisplayResource {
    int config_height,config_width;
    String src;

    public DisplayResource() {
    }

    public DisplayResource(int config_height, int config_width, String src) {
        this.config_height = config_height;
        this.config_width = config_width;
        this.src = src;
    }

    public int getConfig_height() {
        return config_height;
    }

    public void setConfig_height(int config_height) {
        this.config_height = config_height;
    }

    public int getConfig_width() {
        return config_width;
    }

    public void setConfig_width(int config_width) {
        this.config_width = config_width;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
