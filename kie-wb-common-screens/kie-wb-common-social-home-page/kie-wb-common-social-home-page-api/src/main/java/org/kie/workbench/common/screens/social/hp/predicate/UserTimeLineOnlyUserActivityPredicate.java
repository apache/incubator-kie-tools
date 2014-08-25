package org.kie.workbench.common.screens.social.hp.predicate;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialPredicate;

@Portable
public class UserTimeLineOnlyUserActivityPredicate implements SocialPredicate<SocialActivitiesEvent> {

    private SocialUser socialUser;

    public UserTimeLineOnlyUserActivityPredicate() {

    }

    public UserTimeLineOnlyUserActivityPredicate( SocialUser socialUser ) {

        this.socialUser = socialUser;
    }

    @Override
    public boolean test( SocialActivitiesEvent socialActivitiesEvent ) {
        return socialUser.getUserName().equalsIgnoreCase( socialActivitiesEvent.getSocialUser().getUserName() );
    }
}