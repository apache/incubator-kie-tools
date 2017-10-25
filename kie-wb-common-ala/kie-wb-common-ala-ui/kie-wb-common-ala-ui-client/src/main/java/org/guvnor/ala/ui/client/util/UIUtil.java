/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.util;

import java.util.Map;

import org.guvnor.ala.ui.model.ProviderType;

public class UIUtil {

    public static final String EMPTY_STRING = "";

    public static final String getStringValue(Map map,
                                              String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }

    /**
     * Helper method for showing whe provider type name in a consistent way around the different widgets.
     * @param providerType a provider type.
     * @return Returns a String with the provider name to show in the UI.
     */
    public static String getDisplayableProviderTypeName(ProviderType providerType) {
        return providerType.getName() + " " + providerType.getKey().getVersion();
    }

    public static String trimOrGetEmpty(String value) {
        return value != null ? value.trim() : EMPTY_STRING;
    }
}
