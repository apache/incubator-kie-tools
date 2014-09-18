package org.kie.uberfire.social.activities.client.widgets.timeline.simple.model;

import java.util.List;

import com.github.gwtbootstrap.client.ui.NavLink;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialPredicate;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mvp.ParameterizedCommand;

public class SimpleSocialTimelineWidgetModel {

    private SocialEventType socialEventType;
    private SocialUser socialUser;
    private SocialPredicate<SocialActivitiesEvent> predicate;
    private PlaceManager placeManager;
    private SocialPaged socialPaged;
    private NavLink less;
    private NavLink more;
    private List<ClientResourceType> resourceTypes;
    private ParameterizedCommand<String> linkCommand;

    public SimpleSocialTimelineWidgetModel( SocialEventType socialEventType,
                                            SocialPredicate<SocialActivitiesEvent> predicate,
                                            PlaceManager placeManager,
                                            SocialPaged socialPaged ) {
        this.socialEventType = socialEventType;
        this.predicate = predicate;
        this.placeManager = placeManager;
        this.socialPaged = socialPaged;
    }

    public SimpleSocialTimelineWidgetModel( SocialUser socialUser,
                                            SocialPredicate<SocialActivitiesEvent> predicate,
                                            PlaceManager placeManager,
                                            SocialPaged socialPaged ) {
        this.socialUser = socialUser;
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

    public SimpleSocialTimelineWidgetModel withLinkCommand( ParameterizedCommand<String> linkCommand ) {
        this.linkCommand = linkCommand;
        return this;
    }


    public SimpleSocialTimelineWidgetModel withIcons(List<ClientResourceType> resourceTypes ) {
        this.resourceTypes = resourceTypes;
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

    public List<ClientResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public ParameterizedCommand<String> getLinkCommand() {
        if(linkCommand==null){
            return new ParameterizedCommand<String>() {
                @Override
                public void execute( String parameter ) {

                }
            };
        }
        return linkCommand;
    }

}
