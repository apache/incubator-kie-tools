/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.uberfire.social.activities.server;

import java.lang.reflect.Type;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.persistence.SocialClusterMessaging;
import org.kie.uberfire.social.activities.persistence.SocialTimelineCacheClusterPersistence;
import org.kie.uberfire.social.activities.persistence.SocialTimelineCacheInstancePersistence;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelinePersistenceAPI;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOService;

@ApplicationScoped
public class SocialTimelinePersistenceProducer {

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private SocialTimelinePersistenceAPI socialTimelinePersistenceAPI;

    private Gson gson;

    private Type gsonCollectionType;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    SocialEventTypeRepositoryAPI socialEventTypeRepository;

    @Inject
    private SocialClusterMessaging socialClusterMessaging;

    @Inject
    private SocialUserServicesExtendedBackEndImpl userServicesBackend;

    @Inject
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @PostConstruct
    public void setup() {
        gsonFactory();
        if ( clusterServiceFactory == null ) {
            socialTimelinePersistenceAPI = new SocialTimelineCacheInstancePersistence( gson, gsonCollectionType, ioService, socialEventTypeRepository, socialUserPersistenceAPI, userServicesBackend );
        } else {
            socialTimelinePersistenceAPI = new SocialTimelineCacheClusterPersistence( gson, gsonCollectionType, ioService, socialEventTypeRepository, socialUserPersistenceAPI, socialClusterMessaging, userServicesBackend );
        }
        socialTimelinePersistenceAPI.setup();
    }

    @PreDestroy
    public void onShutdown() {
        socialTimelinePersistenceAPI.saveAllEvents();
    }

    @Produces
    @Named("socialTimelinePersistence")
    public SocialTimelinePersistenceAPI socialTimelinePersistenceAPI() {
        return socialTimelinePersistenceAPI;
    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        gsonCollectionType = new TypeToken<Collection<SocialActivitiesEvent>>() {
        }.getType();
    }

}
