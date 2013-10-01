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

package org.drools.workbench.screens.guided.rule.client.util;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.rule.FieldNature;

/**
 *
 */
public final class FieldNatureUtil {

    private FieldNatureUtil() {
    }

    public static Map<String, String> toMap( FieldNature[] fields ) {

        final Map<String, String> currentValueMap = new HashMap<String, String>();

        if ( fields != null ) {
            for ( FieldNature currentFieldNature : fields ) {
                currentValueMap.put( currentFieldNature.getField(),
                                     currentFieldNature.getValue() );
            }
        }

        return currentValueMap;
    }
}
