/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.services.verifier.plugin.client.builders.ColumnUtilitiesBase;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

/**
 * Utilities for Columns
 */
public class ColumnUtilities
        extends ColumnUtilitiesBase {

    private final AsyncPackageDataModelOracle oracle;

    public ColumnUtilities(final GuidedDecisionTable52 model,
                           final AsyncPackageDataModelOracle oracle) {
        super(model);
        this.oracle = PortablePreconditions.checkNotNull("oracle",
                                                         oracle);
    }

    protected String getTypeFromDataOracle(final String factType,
                                           final String fieldName) {
        final String type = oracle.getFieldType(factType,
                                                fieldName);
        return type;
    }

    public static void setColumnLabelStyleWhenHidden(final SmallLabel label,
                                                     final boolean isHidden) {
        if (isHidden) {
            label.addStyleName(GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden());
        } else {
            label.removeStyleName(GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden());
        }
    }

    // Check whether the given column can accept "otherwise" values
    public static boolean canAcceptOtherwiseValues(final BaseColumn column) {
        if (!(column instanceof ConditionCol52)) {
            return false;
        }
        final ConditionCol52 cc = (ConditionCol52) column;

        //Check column contains literal values and uses the equals operator
        if (cc.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL) {
            return false;
        }

        //Check operator is supported
        if (cc.getOperator() == null) {
            return false;
        }
        if (cc.getOperator().equals("==")) {
            return true;
        }
        if (cc.getOperator().equals("!=")) {
            return true;
        }
        return false;
    }
}
