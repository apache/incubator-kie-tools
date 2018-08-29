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

package org.kie.workbench.common.services.shared.preferences;

import java.util.Collections;
import java.util.Map;

/**
 * Storage for global preferences. Preferences effect behaviour and display.
 */
public class ApplicationPreferences {

    public static final String DATE_FORMAT = "drools.dateformat";
    public static final String DATE_TIME_FORMAT = "drools.datetimeformat";
    public static final String DEFAULT_LANGUAGE = "drools.defaultlanguage";
    public static final String DEFAULT_COUNTRY = "drools.defaultcountry";
    public static final String KIE_VERSION_PROPERTY_NAME = "kie_version";
    public static final String KIE_PRODUCTIZED = "kie_productized";
    public static final String KIE_TIMEZONE_OFFSET = "kie_timezone_offset";

    public static ApplicationPreferences instance = new ApplicationPreferences(Collections.<String, String>emptyMap());
    private Map<String, String> preferences = Collections.<String, String>emptyMap();

    private ApplicationPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    public static void setUp(Map<String, String> map) {
        instance = new ApplicationPreferences(map);
    }

    public static boolean getBooleanPref(String name) {
        return Boolean.parseBoolean(instance.preferences.get(name));
    }

    public static String getStringPref(String name) {
        return instance.preferences.get(name);
    }

    public static String getDroolsDateFormat() {
        return getStringPref(DATE_FORMAT);
    }

    public static String getDroolsDateTimeFormat() {
        return getStringPref(DATE_TIME_FORMAT);
    }

    public static String getCurrentDroolsVersion() {
        return instance.preferences.get(KIE_VERSION_PROPERTY_NAME);
    }

    public static int getKieTimezoneOffset() {
        return Integer.parseInt(instance.preferences.get(KIE_TIMEZONE_OFFSET));
    }

    public static boolean isProductized() {
        return Boolean.parseBoolean(instance.preferences.get(KIE_PRODUCTIZED));
    }
}
