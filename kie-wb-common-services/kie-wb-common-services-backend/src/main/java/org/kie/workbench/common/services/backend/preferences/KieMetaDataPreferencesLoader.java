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

package org.kie.workbench.common.services.backend.preferences;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;
import org.kie.internal.utils.KieMeta;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KieMetaDataPreferencesLoader
        implements ApplicationPreferencesLoader {

    private static final Logger log = LoggerFactory.getLogger( KieMetaDataPreferencesLoader.class );

    @Override
    public Map<String, String> load() {
        final Map<String, String> preferences = new HashMap<String, String>();
        final boolean isProductized = KieMeta.isProductized();

        log.info( "Setting preference '" + ApplicationPreferences.KIE_PRODUCTIZED + "' to '" + Boolean.toString( isProductized ) + "'." );

        preferences.put( ApplicationPreferences.KIE_PRODUCTIZED,
                         Boolean.toString( isProductized ) );

        return preferences;
    }

}
