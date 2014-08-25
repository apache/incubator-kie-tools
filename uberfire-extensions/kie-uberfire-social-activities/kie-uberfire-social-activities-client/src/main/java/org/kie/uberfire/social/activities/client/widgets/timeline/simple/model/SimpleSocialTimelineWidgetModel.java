package org.kie.uberfire.social.activities.client.widgets.timeline.simple.model;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialPredicate;
import org.uberfire.client.mvp.PlaceManager;

public class SimpleSocialTimelineWidgetModel {

    private SocialEventType socialEventType;
    private SocialUser socialUser;
    private String title;
    private SocialPredicate<SocialActivitiesEvent> predicate;
    private PlaceManager placeManager;
    private SocialPaged socialPaged;

    public SimpleSocialTimelineWidgetModel( SocialEventType socialEventType,
                                            String title,
                                            SocialPredicate<SocialActivitiesEvent> predicate ,PlaceManager placeManager, SocialPaged socialPaged) {
        this.socialEventType = socialEventType;
        this.title = title;
        this.predicate = predicate;
        this.placeManager = placeManager;
        this.socialPaged = socialPaged;
    }

    public SimpleSocialTimelineWidgetModel( SocialUser socialUser,
                                            String title,
                                            SocialPredicate<SocialActivitiesEvent> predicate ,PlaceManager placeManager) {
        this.socialUser = socialUser;
        this.title = title;
        this.predicate = predicate;
        this.placeManager = placeManager;
    }

    public boolean isSocialTypeWidget(){
        return socialUser==null;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public String getTitle() {
        return title;
    }

    public SocialPredicate<SocialActivitiesEvent> getPredicate() {
        return predicate;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public SocialPaged getSocialPaged() {
        return socialPaged;
    }

    public SocialEventType getSocialEventType() {
        return socialEventType;
    }
}
