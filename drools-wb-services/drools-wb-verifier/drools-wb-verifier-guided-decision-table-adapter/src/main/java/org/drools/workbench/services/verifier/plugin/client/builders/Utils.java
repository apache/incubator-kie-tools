/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.services.verifier.plugin.client.builders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.relations.Operator;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;

public class Utils {

    public static Optional<String> findOperatorFromCell(final DTCellValue52 realCellValue) {
        String value = realCellValue.getStringValue();

        if (value != null) {
            String[] split = value.trim().split(" ");
            if (split.length >= 2) {
                if (!Operator.resolve(split[0]).equals(Operator.NONE)) {
                    return Optional.of(split[0]);
                }
            }
        }
        return Optional.empty();
    }

    public static ObjectField resolveObjectField(final ObjectType objectType,
                                                 final String fieldType,
                                                 final String factField,
                                                 final AnalyzerConfiguration configuration) {
        final ObjectField first = objectType.getFields()
                .where(Field.name()
                               .is(factField))
                .select()
                .first();
        if (first == null) {
            final ObjectField objectField = new ObjectField(objectType.getType(),
                                                            fieldType,
                                                            factField,
                                                            configuration);
            objectType.getFields()
                    .add(objectField);
            return objectField;
        } else {
            return first;
        }
    }

    public static DTCellValue52 getRealCellValue(final DTColumnConfig52 config52,
                                                 final DTCellValue52 visibleCellValue) {
        if (config52 instanceof LimitedEntryCol) {
            return ((LimitedEntryCol) config52).getValue();
        } else {
            return visibleCellValue;
        }
    }

    public static Short tryShort(String stringValue) {
        try {
            return new Short(stringValue);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static Long tryLong(String stringValue) {
        try {
            return new Long(stringValue);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static Double tryDouble(String stringValue) {
        try {
            return new Double(stringValue);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static BigInteger tryBigInteger(String stringValue) {
        try {
            return new BigInteger(stringValue);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static BigDecimal tryBigDecimal(String stringValue) {
        try {
            return new BigDecimal(stringValue);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static Integer tryInteger(String stringValue) {
        try {
            return new Integer(stringValue);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static Boolean tryBoolean(String stringValue) {
        if (stringValue == null) {
            return null;
        } else if (stringValue.toLowerCase().equals("true")) {
            return true;
        } else if (stringValue.toLowerCase().equals("false")) {
            return false;
        } else {
            return null;
        }
    }
}
