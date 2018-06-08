/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;

public class ToString {

    public static String toString(final Pattern52 pattern) {
        if (pattern == null) {
            return "Pattern was null";
        } else {
            return new StringBuilder().append("Pattern52{")
                    .append("\n")
                    .append("factType='")
                    .append(pattern.getFactType())
                    .append("\n")
                    .append(", boundName='")
                    .append(pattern.getBoundName())
                    .append("\n")
                    .append(", isNegated=")
                    .append(pattern.isNegated())
                    .append(", conditions=")
                    .append(toString(pattern.getChildColumns()))
                    .append(", window=")
                    .append(pattern.getWindow())
                    .append(", entryPointName='")
                    .append(pattern.getEntryPointName())
                    .append("\n")
                    .append('}')
                    .toString();
        }
    }

    public static String toString(final List<ConditionCol52> childColumns) {
        final StringBuilder builder = new StringBuilder();

        for (final ConditionCol52 conditionCol52 : childColumns) {
            builder.append(toString(conditionCol52));
        }

        return builder.toString();
    }

    public static String toString(final ConditionCol52 conditionCol52) {
        return new StringBuilder().append("ConditionCol52{")
                .append("\n")
                .append("constraintValueType=")
                .append(conditionCol52.getConstraintValueType())
                .append(", factField='")
                .append(conditionCol52.getFactField())
                .append("\n")
                .append(", fieldType='")
                .append(conditionCol52.getFieldType())
                .append("\n")
                .append(", operator='")
                .append(conditionCol52.getOperator())
                .append("\n")
                .append(", valueList='")
                .append(conditionCol52.getValueList())
                .append("\n")
                .append(", parameters=")
                .append(conditionCol52.getParameters())
                .append(", binding='")
                .append(conditionCol52.getBinding())
                .append("\n")
                .append('}')
                .toString();
    }

    public static String toString(final DTCellValue52 realCellValue) {
        return new StringBuilder().append("DTCellValue52{")
                .append("valueBoolean=")
                .append(realCellValue.getBooleanValue())
                .append(", valueDataType=")
                .append(realCellValue.getDataType())
                .append(", valueNumeric=")
                .append(realCellValue.getNumericValue())
                .append(", valueString='")
                .append(realCellValue.getStringValue())
                .append('\'')
                .append(", dataType=")
                .append(realCellValue.getDataType())
                .append(", isOtherwise=")
                .append(realCellValue.isOtherwise())
                .append('}')
                .toString();
    }

    public static String toString(final HeaderMetaData headerMetaData) {
        final StringBuilder builder = new StringBuilder();

        builder.append("HeaderMetaData{");
        builder.append("\n");

        for (final Integer integer : headerMetaData.keySet()) {
            builder.append(integer);
            builder.append(":");
            builder.append(toString(headerMetaData.getPatternsByColumnNumber(integer).getPattern()));
            builder.append("\n");
        }

        builder.append("\n");
        builder.append('}');

        return builder.toString();
    }
}
