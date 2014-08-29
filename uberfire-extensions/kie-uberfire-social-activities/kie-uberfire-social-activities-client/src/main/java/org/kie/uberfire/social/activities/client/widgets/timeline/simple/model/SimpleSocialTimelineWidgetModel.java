package org.kie.uberfire.social.activities.client.widgets.timeline.simple.model;

import com.github.gwtbootstrap.client.ui.NavLink;
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
    private NavLink less;
    private NavLink more;

    public SimpleSocialTimelineWidgetModel( SocialEventType socialEventType,
                                            String title,
                                            SocialPredicate<SocialActivitiesEvent> predicate,
                                            PlaceManager placeManager,
                                            SocialPaged socialPaged ) {
        this.socialEventType = socialEventType;
        this.title = title;
        this.predicate = predicate;
        this.placeManager = placeManager;
        this.socialPaged = socialPaged;
    }

    public SimpleSocialTimelineWidgetModel( SocialUser socialUser,
                                            String title,
                                            SocialPredicate<SocialActivitiesEvent> predicate,
                                            PlaceManager placeManager,
                                            SocialPaged socialPaged ) {
        this.socialUser = socialUser;
        this.title = title;
        this.predicate = predicate;
        this.placeManager = placeManager;
        this.socialPaged = socialPaged;
    }

    public SimpleSocialTimelineWidgetModel withPagination( NavLink less,
                                                           NavLink more ) {
        this.less = less;
        this.more = more;
        return this;
    }

    public SimpleSocialTimelineWidgetModel withOnlyMorePagination( NavLink more ) {
        this.more = more;
        return this;
    }

    public boolean isSocialTypeWidget() {
        return socialUser == null;
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

    public NavLink getLess() {
        return less;
    }

    public NavLink getMore() {
        return more;
    }

    public void updateSocialPaged( SocialPaged socialPaged ) {
        this.socialPaged = socialPaged;
    }
}
