package org.kie.uberfire.social.activities.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialFileSelectedEvent {

    private String uri;

    public SocialFileSelectedEvent(){}

    public SocialFileSelectedEvent(String uri){
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
