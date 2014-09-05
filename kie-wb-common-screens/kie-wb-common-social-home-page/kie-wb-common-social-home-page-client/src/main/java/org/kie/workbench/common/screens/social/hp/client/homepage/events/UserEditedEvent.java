package org.kie.workbench.common.screens.social.hp.client.homepage.events;

public class UserEditedEvent {

    private final String socialUserName;

    public UserEditedEvent( String socialUserName ) {

        this.socialUserName = socialUserName;
    }

    public String getSocialUserName() {
        return socialUserName;
    }
}
