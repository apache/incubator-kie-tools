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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;

@Service
@ApplicationScoped
public class AppConfigServiceImpl implements AppConfigService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationPreferencesLoader.class);

    private Map<String, String> preferences;

    private Instance<ApplicationPreferencesLoader> preferencesLoaders;

    private Instance<SystemPropertiesInitializer> systemPropertiesInitializers;

    public AppConfigServiceImpl() {
    }

    @Inject
    public AppConfigServiceImpl(@Any Instance<ApplicationPreferencesLoader> preferencesLoaders,
                                @Any Instance<SystemPropertiesInitializer> systemPropertiesInitializers) {
        this.preferencesLoaders = preferencesLoaders;
        this.systemPropertiesInitializers = systemPropertiesInitializers;
    }

    public void configureOnEvent(@Observes ApplicationStarted applicationStartedEvent) {
        loadPreferences();
    }

    @Override
    public synchronized Map<String, String> loadPreferences() {
        try {
            if (preferences == null) {
                preferences = new HashMap<>();

                // Load preferences from all stores
                loadPreferencesFromAllStores();

                // Load system properties (only to override previously loaded preferences, if necessary)
                loadSystemProperties();

                // Perform any post-load handling of preferences
                setupSystemPropertiesInitializers();
            }
            return preferences;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private void loadPreferencesFromAllStores() {
        if (preferencesLoaders != null) {
            for (ApplicationPreferencesLoader loader : preferencesLoaders) {
                preferences.putAll(loader.load());
            }
        }
    }

    private void loadSystemProperties() {
        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            final String newValue = System.getProperty(key, value);

            if (value != null && !value.equals(newValue)) {
                logger.info("Overriding preference '" + key + "' to '" + newValue + "' based on system property value.");
                preferences.put(key, newValue);
            }
        }
    }

    private void setupSystemPropertiesInitializers() {
        if (systemPropertiesInitializers != null) {
            for (SystemPropertiesInitializer initializer : systemPropertiesInitializers) {
                initializer.setSystemProperties(preferences);
            }
        }
    }

    @Override
    public long getTimestamp() {
        return new Date().getTime();
    }
}
