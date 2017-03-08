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

package org.ext.uberfire.social.activities.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class SocialEventTypeRepository implements SocialEventTypeRepositoryAPI {

    @Inject
    SocialAdapterRepositoryAPI socialAdapterRepository;

    @Inject
    @Named("socialUserPersistenceAPI")
    SocialUserPersistenceAPI socialUserPersistence;

    private List<String> eventTypesNames;

    private List<SocialEventType> eventTypes;

    @PostConstruct
    public void setup() {
        Map<Class, SocialAdapter> socialAdapters = getSocialAdapters();
        eventTypesNames = new ArrayList<String>();
        eventTypes = new ArrayList<SocialEventType>();
        for (SocialAdapter socialAdapter : socialAdapters.values()) {
            eventTypesNames.add(socialAdapter.socialEventType().name());
            eventTypes.add(socialAdapter.socialEventType());
        }
        eventTypes.add(DefaultTypes.DUMMY_EVENT);
    }

    Map<Class, SocialAdapter> getSocialAdapters() {
        return socialAdapterRepository.getSocialAdapters();
    }

    @Override
    public List<SocialEventType> findAll() {
        return eventTypes;
    }

    @Override
    public SocialEventType findType(String typeName) {
        for (SocialEventType type : eventTypes) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        throw new TypeNotFoundException("Social event of type " + typeName + " could not be found.");
    }

    private class TypeNotFoundException extends RuntimeException {

        public TypeNotFoundException() {
        }

        public TypeNotFoundException(String message) {
            super(message);
        }
    }
}
