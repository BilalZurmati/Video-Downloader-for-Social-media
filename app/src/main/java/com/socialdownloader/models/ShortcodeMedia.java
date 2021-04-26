package com.socialdownloader.models;

import java.util.List;

public class ShortcodeMedia {
    String _typename,accessibility_caption,display_url,id,video_url,thumbnail_src;
    Boolean caption_is_edited,commenting_disabled_for_viewer,comments_disabled,is_ad,is_video;
    Dimensions dimensions;
    List<DisplayResource> display_resources;

    public ShortcodeMedia() {
    }

    public ShortcodeMedia(String _typename, String accessibility_caption, String display_url, String id, String video_url, String thumbnail_src, Boolean caption_is_edited, Boolean commenting_disabled_for_viewer, Boolean comments_disabled, Boolean is_ad, Boolean is_video, Dimensions dimensions, List<DisplayResource> display_resources) {
        this._typename = _typename;
        this.accessibility_caption = accessibility_caption;
        this.display_url = display_url;
        this.id = id;
        this.video_url = video_url;
        this.thumbnail_src = thumbnail_src;
        this.caption_is_edited = caption_is_edited;
        this.commenting_disabled_for_viewer = commenting_disabled_for_viewer;
        this.comments_disabled = comments_disabled;
        this.is_ad = is_ad;
        this.is_video = is_video;
        this.dimensions = dimensions;
        this.display_resources = display_resources;
    }

    public String get_typename() {
        return _typename;
    }

    public void set_typename(String _typename) {
        this._typename = _typename;
    }

    public String getAccessibility_caption() {
        return accessibility_caption;
    }

    public void setAccessibility_caption(String accessibility_caption) {
        this.accessibility_caption = accessibility_caption;
    }

    public String getDisplay_url() {
        return display_url;
    }

    public void setDisplay_url(String display_url) {
        this.display_url = display_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumbnail_src() {
        return thumbnail_src;
    }

    public void setThumbnail_src(String thumbnail_src) {
        this.thumbnail_src = thumbnail_src;
    }

    public Boolean getCaption_is_edited() {
        return caption_is_edited;
    }

    public void setCaption_is_edited(Boolean caption_is_edited) {
        this.caption_is_edited = caption_is_edited;
    }

    public Boolean getCommenting_disabled_for_viewer() {
        return commenting_disabled_for_viewer;
    }

    public void setCommenting_disabled_for_viewer(Boolean commenting_disabled_for_viewer) {
        this.commenting_disabled_for_viewer = commenting_disabled_for_viewer;
    }

    public Boolean getComments_disabled() {
        return comments_disabled;
    }

    public void setComments_disabled(Boolean comments_disabled) {
        this.comments_disabled = comments_disabled;
    }

    public Boolean getIs_ad() {
        return is_ad;
    }

    public void setIs_ad(Boolean is_ad) {
        this.is_ad = is_ad;
    }

    public Boolean getIs_video() {
        return is_video;
    }

    public void setIs_video(Boolean is_video) {
        this.is_video = is_video;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public List<DisplayResource> getDisplay_resources() {
        return display_resources;
    }

    public void setDisplay_resources(List<DisplayResource> display_resources) {
        this.display_resources = display_resources;
    }
}
