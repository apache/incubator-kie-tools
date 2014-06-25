package org.kie.uberfire.social.activities.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.social.activities.model.SocialEventType;

@Remote
public interface SocialEventTypeRepositoryAPI {

    List<SocialEventType> findAll();

    SocialEventType findType( String selectText );

}
