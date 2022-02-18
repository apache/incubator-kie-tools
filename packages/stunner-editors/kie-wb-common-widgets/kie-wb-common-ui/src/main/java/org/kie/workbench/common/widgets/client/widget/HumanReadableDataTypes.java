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
package org.kie.workbench.common.widgets.client.widget;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class HumanReadableDataTypes {

    private static Map<String, String> TYPE_DESCRIPTIONS = new HashMap<String, String>() {

        {
            put( "Integer",
                 CommonConstants.INSTANCE.WholeNumberInteger() );
            put( "Boolean",
                 CommonConstants.INSTANCE.TrueOrFalse() );
            put( "String",
                 CommonConstants.INSTANCE.Text() );
            put( "java.util.Date",
                 CommonConstants.INSTANCE.Date() );
            put( "java.math.BigDecimal",
                 CommonConstants.INSTANCE.DecimalNumber() );

        }
    };

    public static Map<String, String> getTypeDescriptions() {
        return TYPE_DESCRIPTIONS;
    }

    public static String getUserFriendlyTypeName( String systemTypeName ) {
        if ( systemTypeName.contains( "." ) ) {
            systemTypeName = systemTypeName.substring( systemTypeName.lastIndexOf( "." ) + 1 );
        }
        final String userFriendlyName = TYPE_DESCRIPTIONS.get( systemTypeName );
        if ( userFriendlyName == null ) {
            return systemTypeName;
        } else {
            return userFriendlyName;
        }
    }

}
