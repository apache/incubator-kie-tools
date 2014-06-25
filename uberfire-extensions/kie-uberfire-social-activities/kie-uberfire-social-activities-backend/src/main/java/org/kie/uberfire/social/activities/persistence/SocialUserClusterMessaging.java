package org.kie.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.backend.server.cluster.ClusterServiceFactoryProducer;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.message.MessageHandler;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;

@ApplicationScoped
public class SocialUserClusterMessaging {

    private Gson gson;

    private Type gsonCollectionType;

    private String cluster = "social-user";

    @Inject
    ClusterServiceFactoryProducer clusterServiceFactoryProducer;

    @Inject
    @Named("socialUserPersistenceAPI")
    SocialUserPersistenceAPI socialUserCachePersistence;

    private ClusterService clusterService;

    @PostConstruct
    public void setup() {
        gsonFactory();
        clusterService = clusterServiceFactoryProducer.clusterServiceFactory().build( new MessageHandlerResolver() {
            @Override
            public String getServiceId() {
                return cluster;
            }

            @Override
            public MessageHandler resolveHandler( String serviceId,
                                                  MessageType type ) {
                return new MessageHandler() {
                    @Override
                    public Pair<MessageType, Map<String, String>> handleMessage( MessageType type,
                                                                                 Map<String, String> content ) {

                        if ( type.equals( SocialUserClusterMessage.SOCIAL_USER_UPDATE ) ) {
                            handleUserUpdate( content );
                        }

                        return new Pair<MessageType, Map<String, String>>( type, content );
                    }
                };
            }
        } );
    }

    private void handleUserUpdate( Map<String, String> content ) {
        for ( final Map.Entry<String, String> entry : content.entrySet() ) {
            if ( entry.getKey().equalsIgnoreCase( SocialUserClusterMessage.UPDATE_USER.name() ) ) {
                SocialUser user = gson.fromJson( entry.getValue(), SocialUser.class );
                SocialUserClusterPersistence socialUserClusterPersistence = (SocialUserClusterPersistence) socialUserCachePersistence;
                socialUserClusterPersistence.sync( user );
            }
        }
    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        gsonCollectionType = new TypeToken<Collection<SocialActivitiesEvent>>() {
        }.getType();
    }

    public void notify( SocialUser user ) {
        Map<String, String> content = new HashMap<String, String>();
        String json = gson.toJson( user );
        content.put( SocialUserClusterMessage.UPDATE_USER.name(), json );
        clusterService.broadcast( cluster, SocialUserClusterMessage.SOCIAL_USER_UPDATE,
                                  content );
    }

    private enum SocialUserClusterMessage implements MessageType {
        UPDATE_USER, SOCIAL_USER_UPDATE;

    }
}
