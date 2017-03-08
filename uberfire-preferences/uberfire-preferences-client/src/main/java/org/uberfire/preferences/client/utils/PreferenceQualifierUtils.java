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

package org.uberfire.preferences.client.utils;

import java.lang.annotation.Annotation;

import org.uberfire.preferences.client.annotations.ComponentKey;

public class PreferenceQualifierUtils {

    public static String getComponentKeyIfExists(final Annotation[] qualifiers) {
        String componentKey = null;

        for (Annotation annotation : qualifiers) {
            if (annotation.annotationType().equals(ComponentKey.class)) {
                componentKey = ((ComponentKey) annotation).value();
            }
        }

        return componentKey;
    }
}
