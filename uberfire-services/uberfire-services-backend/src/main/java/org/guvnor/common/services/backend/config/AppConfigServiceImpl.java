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

package org.guvnor.common.services.backend.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;
import org.guvnor.common.services.backend.preferences.SystemPropertiesInitializer;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.commons.services.cdi.ApplicationStarted;

@Service
@ApplicationScoped
public class AppConfigServiceImpl implements AppConfigService {

    private Map<String, String> preferences;

    @Inject
    @Any
    private Instance<ApplicationPreferencesLoader> preferencesLoaders;

    @Inject
    @Any
    private Instance<SystemPropertiesInitializer> systemPropertiesInitializers;

    public void configureOnEvent(@Observes ApplicationStarted applicationStartedEvent) {
        loadPreferences();
    }

    @Override
    public synchronized Map<String, String> loadPreferences() {
        try {
            if (preferences == null) {
                preferences = new HashMap<String, String>();

                //Load preferences from all stores
                if (preferencesLoaders != null) {
                    for (ApplicationPreferencesLoader loader : preferencesLoaders) {
                        preferences.putAll(loader.load());
                    }
                }

                //Perform any post-load handling of preferences
                if (systemPropertiesInitializers != null) {
                    for (SystemPropertiesInitializer initializer : systemPropertiesInitializers) {
                        initializer.setSystemProperties(preferences);
                    }
                }
            }
            return preferences;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public long getTimestamp() {
        return new Date().getTime();
    }
}
