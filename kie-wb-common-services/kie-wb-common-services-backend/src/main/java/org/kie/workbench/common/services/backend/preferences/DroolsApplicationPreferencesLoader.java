/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DroolsApplicationPreferencesLoader implements ApplicationPreferencesLoader {

    private static final Logger log = LoggerFactory.getLogger( DroolsApplicationPreferencesLoader.class );

    @Override
    public Map<String, String> load() {
        final Map<String, String> preferences = new HashMap<String, String>();
        addSystemProperty( preferences,
                           ApplicationPreferences.DATE_FORMAT );
        addSystemProperty( preferences,
                           ApplicationPreferences.DATE_TIME_FORMAT );
        addSystemProperty( preferences,
                           ApplicationPreferences.DEFAULT_LANGUAGE );
        addSystemProperty( preferences,
                           ApplicationPreferences.DEFAULT_COUNTRY );

        // For security Serialization we DO NOT want to set any default
        // as those can be set through other means and we don't want
        // to override or mess with that

        return preferences;
    }

    private void addSystemProperty( final Map<String, String> preferences,
                                    final String key ) {
        final String value = System.getProperty( key );
        if ( value != null ) {
            log.info( "Setting preference '" + key + "' to '" + value + "'." );
            preferences.put( key,
                             value );
        }
    }
}
