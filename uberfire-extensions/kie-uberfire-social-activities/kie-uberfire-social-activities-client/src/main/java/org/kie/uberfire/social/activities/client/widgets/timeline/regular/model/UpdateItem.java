package org.kie.uberfire.social.activities.client.widgets.timeline.regular.model;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;

public class UpdateItem {

    private final SocialActivitiesEvent event;

    public UpdateItem( SocialActivitiesEvent event ) {
        this.event = event;
    }

    public SocialActivitiesEvent getEvent() {
        return event;
    }
}
