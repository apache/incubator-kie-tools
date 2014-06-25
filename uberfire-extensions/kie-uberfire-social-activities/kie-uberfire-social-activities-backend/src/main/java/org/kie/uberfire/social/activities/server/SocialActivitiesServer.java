package org.kie.uberfire.social.activities.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.repository.SocialTimeLineRepository;
import org.kie.uberfire.social.activities.service.SocialActivitiesAPI;

@Service
@ApplicationScoped
public class SocialActivitiesServer implements SocialActivitiesAPI {

    @Inject
    SocialTimeLineRepository timeLineRepository;

    public SocialActivitiesServer() {

    }

    public SocialActivitiesServer( SocialTimeLineRepository timeLineRepository ) {
        this.timeLineRepository = timeLineRepository;
    }

    @Override
    public void register( SocialActivitiesEvent event ) {
        registerTypeEvent( event );
        registerEventUserTimeLine( event );
    }

    private void registerEventUserTimeLine( SocialActivitiesEvent event ) {
        timeLineRepository.saveUserEvent( event );
    }

    private void registerTypeEvent( SocialActivitiesEvent event ) {
        timeLineRepository.saveTypeEvent( event );
    }
}
