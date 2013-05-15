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

import java.util.Map;

import org.drools.core.util.KeyStoreHelper;
import org.kie.workbench.common.services.shared.config.ApplicationPreferences;

public class ApplicationPreferencesInitializer {

    public static void setSystemProperties( final Map<String, String> preferences ) {
        setSystemProperty( preferences,
                           ApplicationPreferences.DATE_FORMAT );
        setSystemProperty( preferences,
                           ApplicationPreferences.DATE_TIME_FORMAT );
        setSystemProperty( preferences,
                           ApplicationPreferences.DEFAULT_LANGUAGE );
        setSystemProperty( preferences,
                           ApplicationPreferences.DEFAULT_COUNTRY );

        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_SIGN );
        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_PVT_KS_URL );
        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_PVT_KS_PWD );
        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_PVT_ALIAS );
        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_PVT_PWD );
        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_PUB_KS_URL );
        setSystemProperty( preferences,
                           KeyStoreHelper.PROP_PUB_KS_PWD );
    }

    private static void setSystemProperty( final Map<String, String> preferences,
                                           final String value ) {
        if ( preferences.containsKey( value ) ) {
            System.setProperty( value,
                                preferences.get( value ) );
        }
    }
}
