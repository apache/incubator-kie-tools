package org.kie.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.model.DefaultTypes;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;

@Service
@ApplicationScoped
public class SocialEventTypeRepository implements SocialEventTypeRepositoryAPI {

    @Inject
    SocialAdapterRepositoryAPI socialAdapterRepository;

    @Inject
    @Named( "socialUserPersistenceAPI" )
    SocialUserPersistenceAPI socialUserPersistence;

    private List<String> eventTypesNames;

    private List<SocialEventType> eventTypes;

    @PostConstruct
    public void setup() {
        Map<Class, SocialAdapter> socialAdapters = getSocialAdapters();
        eventTypesNames = new ArrayList<String>();
        eventTypes = new ArrayList<SocialEventType>(  );
        for ( SocialAdapter socialAdapter : socialAdapters.values() ) {
            eventTypesNames.add( socialAdapter.socialEventType().name() );
            eventTypes.add( socialAdapter.socialEventType() );
        }
        eventTypes.add( DefaultTypes.DUMMY_EVENT );

    }

    Map<Class, SocialAdapter> getSocialAdapters() {
        return socialAdapterRepository.getSocialAdapters();
    }

    @Override
    public List<SocialEventType> findAll() {
        return eventTypes;
    }

    @Override
    public SocialEventType findType( String typeName ) {
        for ( SocialEventType type: eventTypes ){
            if ( type.name().equalsIgnoreCase( typeName ) ){
                return type;
            }
        }
       throw new TypeNotFoundException();
    }

    private class TypeNotFoundException extends RuntimeException{

    }
}
