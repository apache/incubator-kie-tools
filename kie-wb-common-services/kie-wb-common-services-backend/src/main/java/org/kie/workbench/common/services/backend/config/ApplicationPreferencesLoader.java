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

package org.kie.workbench.common.services.backend.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.kie.workbench.common.services.shared.config.ApplicationPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationService;

public class ApplicationPreferencesLoader {

    @Inject
    private ConfigurationService configurationService;

    private static final Logger log = LoggerFactory.getLogger( ApplicationPreferencesLoader.class );

    public Map<String, String> load() {
        final Map<String, String> preferences = getSystemProperties();
        final List<ConfigGroup> configs = configurationService.getConfiguration( ConfigType.GLOBAL );
        for ( ConfigGroup config : configs ) {
            for ( ConfigItem item : config.getItems() ) {
                final String name = item.getName();
                final String value = config.getConfigItemValue( name );
                log.info( "Setting preference '" + name + "' to '" + value + "'." );
                preferences.put( name,
                                 value );
            }
        }
        return preferences;
    }

    private Map<String, String> getSystemProperties() {
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
