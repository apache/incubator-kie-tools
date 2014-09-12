package org.kie.uberfire.social.activities.client.widgets.timeline.regular.model;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

public class UpdateItem {

    private final SocialActivitiesEvent event;
    private ParameterizedCommand<String> userClickCommand;

    public UpdateItem( SocialActivitiesEvent event ) {
        this.event = event;
    }

    public SocialActivitiesEvent getEvent() {
        return event;
    }

    public void setSocialUser(SocialUser social) {
        event.updateSocialUser( social );
    }

    public void setUserClickCommand( ParameterizedCommand<String> userClickCommand ) {
        this.userClickCommand = userClickCommand;
    }

    public ParameterizedCommand<String> getUserClickCommand() {
        return userClickCommand;
    }
}
