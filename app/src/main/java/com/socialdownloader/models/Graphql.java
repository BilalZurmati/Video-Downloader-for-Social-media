package com.socialdownloader.models;

import com.google.gson.annotations.SerializedName;

public class Graphql {
    @SerializedName("shortcode_media")
    ShortcodeMedia shortcode_media;

    public Graphql() {
    }

    public Graphql(ShortcodeMedia shortcode_media) {
        this.shortcode_media = shortcode_media;
    }

    public ShortcodeMedia getShortcode_media() {
        return shortcode_media;
    }

    public void setShortcode_media(ShortcodeMedia shortcode_media) {
        this.shortcode_media = shortcode_media;
    }
}
