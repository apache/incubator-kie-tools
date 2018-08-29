/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.TimeZone;

import org.guvnor.common.services.backend.preferences.ApplicationPreferencesLoader;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

public class KieTimeZonePreferencesLoader implements ApplicationPreferencesLoader {

    @Override
    public Map<String, String> load() {

        final Map<String, String> preferences = new HashMap<>();
        final int offset = getOffset();
        final String offsetValue = Integer.toString(offset);

        preferences.put(ApplicationPreferences.KIE_TIMEZONE_OFFSET, offsetValue);

        return preferences;
    }

    private int getOffset() {
        final TimeZone timeZone = getTimeZone();
        return timeZone.getOffset(0);
    }

    private TimeZone getTimeZone() {
        final String timezone = getSystemPropertyTimeZone();

        if (timezone == null || timezone.isEmpty()) {
            return TimeZone.getDefault();
        } else {
            return TimeZone.getTimeZone(timezone);
        }
    }

    String getSystemPropertyTimeZone() {
        return System.getProperty("user.timezone");
    }
}
