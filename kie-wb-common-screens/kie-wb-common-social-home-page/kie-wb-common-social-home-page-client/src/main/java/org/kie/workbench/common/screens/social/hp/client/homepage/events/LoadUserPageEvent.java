package org.kie.workbench.common.screens.social.hp.client.homepage.events;

public class LoadUserPageEvent {

    private final String socialUserName;

    public LoadUserPageEvent( String socialUserName ) {

        this.socialUserName = socialUserName;
    }

    public String getSocialUserName() {
        return socialUserName;
    }
}
