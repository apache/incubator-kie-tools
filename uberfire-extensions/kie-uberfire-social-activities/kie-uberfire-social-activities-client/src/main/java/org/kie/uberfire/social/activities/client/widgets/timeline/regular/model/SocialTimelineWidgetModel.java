package org.kie.uberfire.social.activities.client.widgets.timeline.regular.model;

import java.util.List;
import java.util.Map;

import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;

public class SocialTimelineWidgetModel {


    private String maxResults;
    private SocialEventType socialEventType;
    private SocialUser socialUser;
    private PlaceManager placeManager;
    private List<ClientResourceType> resourceTypes;
    private Map<String, String> globals;
    private String drlName;

    public SocialTimelineWidgetModel( SocialUser socialUser,
                                      PlaceManager placeManager, List<ClientResourceType> resourceTypes ) {
        this.socialUser = socialUser;
        this.placeManager = placeManager;
        this.resourceTypes = resourceTypes;
    }


    public SocialTimelineWidgetModel(SocialEventType socialEventType,
                                      SocialUser socialUser,
                                      PlaceManager placeManager ) {
        this.socialEventType = socialEventType;
        this.socialUser = socialUser;
        this.placeManager = placeManager;
    }

    public SocialTimelineWidgetModel droolsQuery(Map<String, String> globals,
                                                 String drlName, String maxResults){
        this.globals = globals;
        this.drlName = drlName;
        this.maxResults = maxResults;
        return this;
    }

    public boolean isDroolsQuery(){
        return this.drlName !=null;
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

    public String getMaxResults() {
        return maxResults;
    }

    public List<ClientResourceType> getResourceTypes() {
        return resourceTypes;
    }
}
