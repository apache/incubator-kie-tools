/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion.util;

import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;

public class BRLColumnUtil {

    private BRLColumnUtil() {
    }

    public static boolean canThisColumnBeSplitToMultiple(final BRLActionColumn brlColumn) {

        for (final IAction iAction : brlColumn.getDefinition()) {
            if (onlyActionFieldListIsAccepted(iAction)
                    || validateActionFieldValues((ActionFieldList) iAction)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canThisColumnBeSplitToMultiple(final BRLConditionColumn brlColumn) {
        for (final IPattern iPattern : brlColumn.getDefinition()) {
            if (onlyFactPatternsAreAccepted(iPattern)
                    || validateFactPatternConstraints(((FactPattern) iPattern).getConstraintList().getConstraints())) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateActionFieldValues(final ActionFieldList iAction) {
        for (ActionFieldValue fieldValue : iAction.getFieldValues()) {
            if (onlyAllowedActionFieldValueTypeIsTemplateType(fieldValue)) {
                return true;
            }
        }
        return false;
    }

    private static boolean validateFactPatternConstraints(final FieldConstraint[] constraints) {
        for (FieldConstraint constraint : constraints) {
            if (acceptOnlySingleFieldConstraints(constraint)) {
                return true;
            }
            if (useOfExpressionsIsNotAllowed((SingleFieldConstraint) constraint)) {
                return true;
            }
            if (onlyAllowedConstraintValueTypeIsTemplateType((SingleFieldConstraint) constraint)) {
                return true;
            }
        }
        return false;
    }

    private static boolean onlyAllowedActionFieldValueTypeIsTemplateType(final ActionFieldValue fieldValue) {
        return fieldValue.getNature() != FieldNatureType.TYPE_TEMPLATE;
    }

    private static boolean onlyActionFieldListIsAccepted(final IAction iAction) {
        return !(iAction instanceof ActionFieldList);
    }

    private static boolean onlyFactPatternsAreAccepted(final IPattern iPattern) {
        return !(iPattern instanceof FactPattern);
    }

    private static boolean onlyAllowedConstraintValueTypeIsTemplateType(final SingleFieldConstraint fieldConstraint) {
        return fieldConstraint.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_TEMPLATE;
    }

    private static boolean useOfExpressionsIsNotAllowed(final SingleFieldConstraint fieldConstraint) {
        return !fieldConstraint.getExpressionValue().isEmpty();
    }

    private static boolean acceptOnlySingleFieldConstraints(final FieldConstraint constraint) {
        return !(constraint instanceof SingleFieldConstraint);
    }
}
