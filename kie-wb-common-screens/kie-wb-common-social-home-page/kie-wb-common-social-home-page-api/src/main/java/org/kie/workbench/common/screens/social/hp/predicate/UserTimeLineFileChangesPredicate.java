package org.kie.workbench.common.screens.social.hp.predicate;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.service.SocialPredicate;
import org.kie.workbench.common.screens.social.hp.model.HomePageTypes;

@Portable
public class UserTimeLineFileChangesPredicate implements SocialPredicate<SocialActivitiesEvent> {

    @Override
    public boolean test( SocialActivitiesEvent socialActivitiesEvent ) {
        return socialActivitiesEvent.getType().equals( HomePageTypes.RESOURCE_ADDED_EVENT.name() )
                || socialActivitiesEvent.getType().equals( HomePageTypes.RESOURCE_UPDATE_EVENT.name() );
    }
}