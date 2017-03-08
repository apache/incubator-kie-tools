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

package org.ext.uberfire.social.activities.server;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialActivitiesAPI;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class SocialActivitiesEventObserver {

    @Inject
    BeanManager beanManager;

    @Inject
    Event<SocialActivitiesEvent> socialActivitiesEvent;

    @Inject
    SocialAdapterRepositoryAPI socialAdapterRepository;

    private Map<Class, SocialAdapter> socialAdapters;

    @Inject
    private SocialActivitiesAPI socialAPI;

    @Inject
    private SocialConfiguration socialConfiguration;

    @PostConstruct
    public void setup() {
        socialAdapters = socialAdapterRepository.getSocialAdapters();
    }

    public void handleSocialActivitiesEvent(@Observes SocialActivitiesEvent event) {
        if (socialConfiguration.isSocialEnable()) {
            socialAPI.register(event);
        }
    }

    public void observeAllEvents(@Observes(notifyObserver = Reception.IF_EXISTS) Object event) {
        if (socialConfiguration.isSocialEnable()) {
            if (socialAdapters == null) {
                return;
            }
            for (Map.Entry<Class, SocialAdapter> entry : socialAdapters.entrySet()) {
                SocialAdapter adapter = entry.getValue();
                if (adapter.shouldInterceptThisEvent(event)) {
                    SocialActivitiesEvent socialEvent = adapter.toSocial(event);
                    socialActivitiesEvent.fire(socialEvent);
                }
            }
        }
    }
}
