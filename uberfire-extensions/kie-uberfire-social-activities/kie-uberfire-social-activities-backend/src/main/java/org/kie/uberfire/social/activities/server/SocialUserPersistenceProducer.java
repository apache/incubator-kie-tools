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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.persistence.SocialUserClusterMessaging;
import org.kie.uberfire.social.activities.persistence.SocialUserClusterPersistence;
import org.kie.uberfire.social.activities.persistence.SocialUserInstancePersistence;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOService;

@ApplicationScoped
public class SocialUserPersistenceProducer {

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private Gson gson;

    private Type gsonCollectionType;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    SocialEventTypeRepositoryAPI socialEventTypeRepository;

    @Inject
    private SocialUserServicesExtendedBackEndImpl userServicesBackend;

    @Inject
    private UserServicesImpl userServices;

    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @Inject
    private SocialUserClusterMessaging socialUserClusterMessaging;

    @PostConstruct
    public void setup() {
        gsonFactory();
        if ( clusterServiceFactory == null ) {
            socialUserPersistenceAPI = new SocialUserInstancePersistence( userServicesBackend, userServices, ioService, gson );
        } else {
            socialUserPersistenceAPI = new SocialUserClusterPersistence( userServicesBackend, userServices, ioService, gson, socialUserClusterMessaging );
        }
    }

    @Produces
    @Named("socialUserPersistenceAPI")
    public SocialUserPersistenceAPI socialUserPersistenceAPI() {
        return socialUserPersistenceAPI;
    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        gsonCollectionType = new TypeToken<Collection<SocialActivitiesEvent>>() {
        }.getType();
    }

}
