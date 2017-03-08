/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.security.SocialSecurityConstraintsManager;
import org.ext.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

public class SocialTimelineCacheInstancePersistence extends SocialTimelineCachePersistence {

    public SocialTimelineCacheInstancePersistence(final Gson gson,
                                                  final Type gsonCollectionType,
                                                  final IOService ioService,
                                                  final SocialEventTypeRepositoryAPI socialEventTypeRepository,
                                                  final SocialUserPersistenceAPI socialUserService,
                                                  final SocialUserServicesExtendedBackEndImpl userServicesBackend,
                                                  final FileSystem fileSystem,
                                                  final SocialSecurityConstraintsManager socialSecurityConstraintsManager) {
        this.gsonCollectionType = gsonCollectionType;
        this.gson = gson;
        this.ioService = ioService;
        this.socialEventTypeRepository = socialEventTypeRepository;
        this.socialUserPersistenceAPI = socialUserService;
        this.userServicesBackend = userServicesBackend;
        this.fileSystem = fileSystem;
        this.socialSecurityConstraintsManager = socialSecurityConstraintsManager;
        PriorityDisposableRegistry.register(this);
    }

    @Override
    public void persist(SocialActivitiesEvent event) {
        SocialEventType type = findType(event);
        List<SocialActivitiesEvent> typeEvents = typeEventsFreshEvents.get(type);
        if (typeEvents == null) {
            typeEvents = new ArrayList<SocialActivitiesEvent>();
        }
        typeEvents.add(event);
        typeEventsFreshEvents.put(type,
                                  typeEvents);
        cacheControl(event);
    }

    SocialEventType findType(SocialActivitiesEvent event) {
        return socialEventTypeRepository.findType(event.getType());
    }

    @Override
    public void persist(SocialUser user,
                        SocialActivitiesEvent event) {
        List<SocialActivitiesEvent> userEvents = userEventsTimelineFreshEvents.get(user.getUserName());
        if (userEvents == null) {
            userEvents = new ArrayList<SocialActivitiesEvent>();
        }
        userEvents.add(event);
        userEventsTimelineFreshEvents.put(user.getUserName(),
                                          userEvents);
        cacheControl(user);
    }

    @Override
    public void saveAllEvents() {
        saveAllTypeEvents();
        saveAllUserTimelines();
    }

    void cacheControl(SocialUser user) {
        SocialCacheControl socialCacheControl = userEventsCacheControl.get(user.getUserName());
        if (socialCacheControl == null) {
            socialCacheControl = new SocialCacheControl();
            userEventsCacheControl.put(user.getUserName(),
                                       socialCacheControl);
        }
        socialCacheControl.registerNewEvent();
        if (socialCacheControl.needToPersist()) {
            storeTimeLineInFile(user);

            socialCacheControl.reset();
        }
    }

    void cacheControl(SocialActivitiesEvent event) {
        SocialEventType type = findType(event);
        SocialCacheControl socialCacheControl = typeEventsCacheControl.get(type);
        socialCacheControl.registerNewEvent();
        if (socialCacheControl.needToPersist()) {
            storeTimeLineInFile(type);
            socialCacheControl.reset();
        }
    }
}
