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

package org.kie.workbench.common.screens.datasource.management.util;

import java.util.Properties;

public class ServiceUtil {

    public static String getManagedProperty( Properties properties, String propertyName ) {
        return getManagedProperty( properties, propertyName, null );
    }

    public static String getManagedProperty( Properties properties, String propertyName, String defaultValue ) {
        String propertyValue = System.getProperty( propertyName );
        if ( isEmpty( propertyValue ) ) {
            propertyValue = properties.getProperty( propertyName );
        }
        return propertyValue != null ? propertyValue.trim() : defaultValue;
    }

    public static boolean isEmpty( final String value ) {
        return value == null || value.trim().length() == 0;
    }


}
