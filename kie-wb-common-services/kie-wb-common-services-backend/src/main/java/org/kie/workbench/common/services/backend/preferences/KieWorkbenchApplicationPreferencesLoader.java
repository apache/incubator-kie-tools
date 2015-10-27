/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.backend.preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;

@ApplicationScoped
public class KieWorkbenchApplicationPreferencesLoader
        implements ApplicationPreferencesLoader {

    private static final String KIE_VERSION_FILENAME = "/kie-version.properties";
    private static final String KIE_VERSION_PROPERTY_NAME = "kie_version";

    @Override
    public Map<String, String> load() {
        final Map<String, String> preferences = new HashMap<String, String>();

        preferences.put( KIE_VERSION_PROPERTY_NAME,
                         getKieVersion() );

        return preferences;
    }

    private String getKieVersion() {
        Properties properties = new Properties();
        try {
            properties.load( KieWorkbenchApplicationPreferencesLoader.class.getResourceAsStream( KIE_VERSION_FILENAME ) );
        } catch (IOException e) {

        }
        return properties.getProperty( KIE_VERSION_PROPERTY_NAME );
    }
}
