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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

public class Utils {

    public static Pattern resolvePattern( final Index index,
                                          final Rule rule,
                                          final Pattern52 pattern52 ) {

        final Pattern pattern = rule.getPatterns()
                                    .where( Pattern.boundName().is( pattern52.getBoundName() ) )
                                    .select().first();
        if ( pattern == null ) {
            return new PatternBuilder( index,
                                       rule,
                                       pattern52 ).build();
        } else {
            return pattern;
        }
    }

    public static Pattern resolvePattern( final Index index,
                                          final Rule rule,
                                          final String boundName,
                                          final String factType ) {
        final Pattern first = getFirstPattern( rule,
                                               boundName,
                                               factType );

        if ( first == null ) {
            final Pattern pattern = new Pattern( boundName,
                                                 resolveObjectType( index,
                                                                    factType ) );

            rule.getPatterns().add( pattern );

            return pattern;
        } else {
            return first;
        }
    }

    private static Pattern getFirstPattern( final Rule rule,
                                            final String boundName,
                                            final String factType ) {
        if ( boundName != null ) {
            return rule.getPatterns()
                       .where( Pattern.boundName().is( boundName ) )
                       .select().first();
        } else {
            return rule.getPatterns()
                       .where( Pattern.name().is( factType ) )
                       .select().first();
        }
    }

    public static ObjectType resolveObjectType( final Index index,
                                                final String factType ) {
        final ObjectType first = index.objectTypes
                                     .where( ObjectType.type().is( factType ) )
                                     .select().first();

        if ( first == null ) {
            final ObjectType objectType = new ObjectType( factType );
            index.objectTypes.add( objectType );
            return objectType;
        } else {
            return first;
        }
    }

    public static ObjectField resolveObjectField( final ObjectType objectType,
                                                  final String fieldType,
                                                  final String factField ) {
        final ObjectField first = objectType.getFields()
                                            .where( Field.name().is( factField ) )
                                            .select().first();
        if ( first == null ) {
            final ObjectField objectField = new ObjectField( objectType.getType(),
                                                             fieldType,
                                                             factField );
            objectType.getFields().add( objectField );
            return objectField;
        } else {
            return first;
        }
    }

    public static Field resolveField( final Pattern pattern,
                                      final String fieldType,
                                      final String factField ) {
        final Field first = pattern.getFields()
                                      .where( Field.name().is( factField ) )
                                      .select().first();

        if ( first == null ) {
            final Field field = new Field( resolveObjectField( pattern.getObjectType(),
                                                               fieldType,
                                                               factField ),
                                           pattern.getName(),
                                           fieldType,
                                           factField );
            pattern.getFields().add( field );
            return field;
        } else {
            return first;
        }
    }

    public static boolean rowHasIndex( final int columnIndex,
                                       final List<DTCellValue52> row ) {
        return columnIndex > 0 && columnIndex < row.size();
    }

    public static DTCellValue52 getRealCellValue( final DTColumnConfig52 config52,
                                                  final DTCellValue52 visibleCellValue ) {
        if ( config52 instanceof LimitedEntryCol ) {
            return (( LimitedEntryCol ) config52).getValue();
        } else {
            return visibleCellValue;
        }
    }

    public static boolean isCellNotBlank( final DTColumnConfig52 config52,
                                          final DTCellValue52 visibleCellValue ) {
        if ( config52 instanceof LimitedEntryCol ) {
            return visibleCellValue.getBooleanValue();
        } else {
            return visibleCellValue.hasValue();
        }
    }
}
