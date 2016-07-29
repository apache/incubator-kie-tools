/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.backend.server;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Make the "dtable.verification.enabled" System Property available client-side.
 */
@ApplicationScoped
public class GuidedDecisionTableApplicationPreferencesLoader
        implements ApplicationPreferencesLoader {

    private static final Logger log = LoggerFactory.getLogger( GuidedDecisionTableApplicationPreferencesLoader.class );

    @Override
    public Map<String, String> load() {
        final Map<String, String> preferences = new HashMap<String, String>();

        final String property = getProperty();
        log.info( "Setting preference '" + GuidedDecisionTableEditorService.DTABLE_VERIFICATION_ENABLED + "' to '" + property + "'." );
        preferences.put( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_ENABLED,
                         property );

        return preferences;
    }

    private String getProperty() {
        final String property = System.getProperty( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_ENABLED );
        if ( property == null ) {
            return "true";
        } else {
            return property;
        }
    }

}
