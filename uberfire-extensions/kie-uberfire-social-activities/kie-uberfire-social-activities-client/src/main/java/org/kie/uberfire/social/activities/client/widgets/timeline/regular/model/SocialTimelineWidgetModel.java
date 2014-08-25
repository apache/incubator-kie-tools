package org.kie.uberfire.social.activities.client.widgets.timeline.regular.model;

import java.util.Map;

import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.mvp.PlaceManager;

public class SocialTimelineWidgetModel {

    private String title;
    private SocialEventType socialEventType;
    private SocialUser socialUser;
    private PlaceManager placeManager;
    private Map<String, String> globals;
    private String drlName;

    public SocialTimelineWidgetModel( String title,
                                      SocialUser socialUser,
                                      PlaceManager placeManager ) {
        this.title = title;
        this.socialUser = socialUser;
        this.placeManager = placeManager;
    }


    public SocialTimelineWidgetModel( String title,
                                      SocialEventType socialEventType,
                                      SocialUser socialUser,
                                      PlaceManager placeManager ) {
        this.title = title;
        this.socialEventType = socialEventType;
        this.socialUser = socialUser;
        this.placeManager = placeManager;
    }

    public SocialTimelineWidgetModel droolsQuery(Map<String, String> globals,
                                                 String drlName){
        this.globals = globals;
        this.drlName = drlName;
        return this;
    }

    public boolean isDroolsQuery(){
        return this.drlName !=null;
    }

    public String getTitle() {
        return title;
    }

    public SocialEventType getSocialEventType() {
        return socialEventType;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public Map<String,String> getGlobals() {
        return globals;
    }

    public String getDrlName() {
        return drlName;
    }
}
