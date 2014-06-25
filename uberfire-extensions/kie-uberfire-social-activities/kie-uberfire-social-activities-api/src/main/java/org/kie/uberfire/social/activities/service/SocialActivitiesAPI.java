package org.kie.uberfire.social.activities.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;

@Remote
public interface SocialActivitiesAPI {

    void register( SocialActivitiesEvent event );
}
