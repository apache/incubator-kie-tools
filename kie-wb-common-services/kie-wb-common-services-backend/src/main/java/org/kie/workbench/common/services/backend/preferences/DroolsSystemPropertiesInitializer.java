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

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.drools.core.util.KeyStoreConstants;
import org.guvnor.common.services.backend.preferences.SystemPropertiesInitializer;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

@ApplicationScoped
public class DroolsSystemPropertiesInitializer implements SystemPropertiesInitializer {

    @Override
    public void setSystemProperties(final Map<String, String> preferences) {
        setSystemProperty(preferences,
                          ApplicationPreferences.DATE_FORMAT);
        setSystemProperty(preferences,
                          ApplicationPreferences.DATE_TIME_FORMAT);
        setSystemProperty(preferences,
                          ApplicationPreferences.DEFAULT_LANGUAGE);
        setSystemProperty(preferences,
                          ApplicationPreferences.DEFAULT_COUNTRY);

        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_SIGN);
        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_PVT_KS_URL);
        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_PVT_KS_PWD);
        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_PVT_ALIAS);
        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_PVT_PWD);
        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_PUB_KS_URL);
        setSystemProperty(preferences,
                          KeyStoreConstants.PROP_PUB_KS_PWD);
    }

    private void setSystemProperty(final Map<String, String> preferences,
                                   final String value) {
        if (preferences.containsKey(value)) {
            System.setProperty(value,
                               preferences.get(value));
        }
    }
}
