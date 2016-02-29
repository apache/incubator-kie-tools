/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExamplesPreferencesLoader
        implements ApplicationPreferencesLoader {

    private static final Logger log = LoggerFactory.getLogger( ExamplesPreferencesLoader.class );

    @Override
    public Map<String, String> load() {
        final Map<String, String> preferences = new HashMap<String, String>();
        addSystemProperty( preferences,
                           ExamplesService.EXAMPLES_SYSTEM_PROPERTY );

        return preferences;
    }

    private void addSystemProperty( final Map<String, String> preferences,
                                    final String key ) {
        final String value = System.getProperty( key );
        if ( value != null ) {
            log.info( "Setting preference '" + key + "' to '" + value + "'." );
            preferences.put( key,
                             value );
        } else {
            log.info( "Setting preference '" + key + "' to 'true'." );
            preferences.put( key,
                             "true" );

        }
    }

}
