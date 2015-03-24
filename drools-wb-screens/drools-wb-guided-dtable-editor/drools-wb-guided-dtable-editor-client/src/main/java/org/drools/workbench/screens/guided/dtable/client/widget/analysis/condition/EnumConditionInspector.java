/*
 * Copyright 2011 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

public class EnumConditionInspector
        extends StringConditionInspector {

    public EnumConditionInspector( Pattern52 pattern,
                                   String factField,
                                   List<String> allValueList,
                                   String value,
                                   String operator ) {
        super( pattern,
               factField,
               value,
               operator );

        if ( !allValueList.contains( value ) ) {
            System.out.println( "Warning: value (" + value + ") is not a valid enum value (" + allValueList + ")." );
        }

    }

}
