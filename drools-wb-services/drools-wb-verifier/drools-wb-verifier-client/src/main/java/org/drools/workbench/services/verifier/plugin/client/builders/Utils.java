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
package org.drools.workbench.services.verifier.plugin.client.builders;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.ObjectField;
import org.drools.workbench.services.verifier.api.client.index.ObjectType;
import org.drools.workbench.services.verifier.api.client.index.Pattern;
import org.drools.workbench.services.verifier.api.client.index.Rule;

public class Utils {

    public static ObjectField resolveObjectField( final ObjectType objectType,
                                                  final String fieldType,
                                                  final String factField,
                                                  final AnalyzerConfiguration configuration ) {
        final ObjectField first = objectType.getFields()
                .where( Field.name()
                                .is( factField ) )
                .select()
                .first();
        if ( first == null ) {
            final ObjectField objectField = new ObjectField( objectType.getType(),
                                                             fieldType,
                                                             factField,
                                                             configuration );
            objectType.getFields()
                    .add( objectField );
            return objectField;
        } else {
            return first;
        }
    }

    public static DTCellValue52 getRealCellValue( final DTColumnConfig52 config52,
                                                  final DTCellValue52 visibleCellValue ) {
        if ( config52 instanceof LimitedEntryCol ) {
            return ( (LimitedEntryCol) config52 ).getValue();
        } else {
            return visibleCellValue;
        }
    }
}
