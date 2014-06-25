package org.kie.uberfire.social.activities.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PagedSocialQuery implements Serializable {

    private List<SocialActivitiesEvent> socialEvents;
    private SocialPaged socialPaged;

    public PagedSocialQuery() {

    }

    public PagedSocialQuery( List<SocialActivitiesEvent> socialEvents,
                             SocialPaged socialPaged ) {
        this.socialEvents = socialEvents;
        this.socialPaged = socialPaged;
    }

    public SocialPaged socialPaged(){
        return socialPaged;
    }

    public List<SocialActivitiesEvent> socialEvents() {
        return socialEvents;
    }
}
