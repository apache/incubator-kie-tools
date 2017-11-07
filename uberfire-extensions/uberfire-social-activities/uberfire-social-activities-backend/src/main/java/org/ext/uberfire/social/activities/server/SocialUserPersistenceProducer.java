/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.ext.uberfire.social.activities.server;

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
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.persistence.SocialUserClusterMessaging;
import org.ext.uberfire.social.activities.persistence.SocialUserClusterPersistence;
import org.ext.uberfire.social.activities.persistence.SocialUserInstancePersistence;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class SocialUserPersistenceProducer {

    private Gson gson;

    private Type gsonCollectionType;

    //please do not remove, for the absurd it may sound, this is needed
    //to guarantee the bean initializion order. if removed, doesn't work
    //on WAS. https://bugzilla.redhat.com/show_bug.cgi?id=1266138
    @Inject
    @Named("configIO")
    private IOService ioService;

    //please do not remove, for the absurd it may sound, this is needed
    //to guarantee the bean initializion order. if removed, doesn't work
    //on WAS. https://bugzilla.redhat.com/show_bug.cgi?id=1266138
    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    private SocialEventTypeRepositoryAPI socialEventTypeRepository;

    @Inject
    private UserServicesImpl userServices;

    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @Inject
    private SocialUserClusterMessaging socialUserClusterMessaging;

    private ClusterJMSService clusterJMSService;

    @PostConstruct
    public void setup() {
        gsonFactory();
        clusterJMSService = new ClusterJMSService();
        final IOService _ioService = getConfigIOServiceProducer().configIOService();
        final SocialUserServicesExtendedBackEndImpl userServicesBackend = new SocialUserServicesExtendedBackEndImpl(fileSystem);

        setupSocialUserPersistenceAPI(_ioService,
                                      userServicesBackend);
    }

    void setupSocialUserPersistenceAPI(IOService _ioService,
                                       SocialUserServicesExtendedBackEndImpl userServicesBackend) {
        if (!clusterJMSService.isAppFormerClustered()) {
            socialUserPersistenceAPI = new SocialUserInstancePersistence(userServicesBackend,
                                                                         userServices,
                                                                         _ioService,
                                                                         gson);
        } else {
            socialUserPersistenceAPI = new SocialUserClusterPersistence(userServicesBackend,
                                                                        userServices,
                                                                        _ioService,
                                                                        gson,
                                                                        socialUserClusterMessaging);
            socialUserClusterMessaging.setup(clusterJMSService, socialUserPersistenceAPI);
        }
        socialUserPersistenceAPI.setup();
    }

    ConfigIOServiceProducer getConfigIOServiceProducer() {
        return ConfigIOServiceProducer.getInstance();
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
