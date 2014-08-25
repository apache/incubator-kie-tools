package org.kie.uberfire.social.activities.client.widgets.item.model;

import java.util.Date;

import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

public class SimpleItemWidgetModel {

    private Date timestamp;
    private String linkText;
    private SocialUser socialUser;
    private String description;
    private PlaceManager placeManager;
    private Path linkPath;
    private String itemDescription;
    private String title;


    public SimpleItemWidgetModel( SimpleSocialTimelineWidgetModel model,
                                  Date timestamp,
                                  String linkText,
                                  Path linkPath,
                                  String itemDescription ) {

        this.socialUser = model.getSocialUser();
        this.placeManager = model.getPlaceManager();
        this.timestamp = timestamp;
        this.linkText = linkText;
        this.linkPath = linkPath;
        this.itemDescription = itemDescription;

    }

    public SimpleItemWidgetModel( SimpleSocialTimelineWidgetModel model,
                                  Date timestamp,
                                  String description,
                                  String itemDescription ) {
        this.socialUser = model.getSocialUser();
        this.timestamp = timestamp;
        this.description = description;
        this.itemDescription = itemDescription;

    }

    public SimpleItemWidgetModel( SocialTimelineWidgetModel model,
                                  Date timestamp,
                                  String linkText,
                                  Path linkPath,
                                  String itemDescription ) {
        this.socialUser = model.getSocialUser();
        this.placeManager = model.getPlaceManager();
        this.timestamp = timestamp;
        this.linkText = linkText;
        this.linkPath = linkPath;
        this.itemDescription = itemDescription;
    }

    public SimpleItemWidgetModel( SocialTimelineWidgetModel model,
                                  Date timestamp,
                                  String description,
                                  String itemDescription ) {
        this.socialUser = model.getSocialUser();
        this.timestamp = timestamp;
        this.description = description;
        this.itemDescription = itemDescription;

    }

    public String getDescription() {
        return description;
    }

    public String getLinkText() {
        return linkText;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public Path getLinkPath() {
        return linkPath;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }
}
