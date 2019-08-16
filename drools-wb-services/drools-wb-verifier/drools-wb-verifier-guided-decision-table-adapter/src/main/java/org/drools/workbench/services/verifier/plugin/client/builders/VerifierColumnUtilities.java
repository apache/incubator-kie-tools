/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.util.ColumnUtilitiesBase;
import org.drools.workbench.services.verifier.plugin.client.Logger;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;

public class VerifierColumnUtilities
        extends ColumnUtilitiesBase {

    private final HeaderMetaData headerMetaData;
    private final FactTypes factTypes;

    public VerifierColumnUtilities(final GuidedDecisionTable52 model,
                                   final HeaderMetaData headerMetaData,
                                   final FactTypes factTypes) {
        super(model);

        this.headerMetaData = PortablePreconditions.checkNotNull("headerMetaData",
                                                                 headerMetaData);
        this.factTypes = PortablePreconditions.checkNotNull("fieldTypes",
                                                            factTypes);
    }

    public String getType(final BaseColumn column,
                          final int columnIndex) throws
            ColumnUtilitiesException {

        PortablePreconditions.checkNotNull("column",
                                           column);

        if (column instanceof BRLConditionVariableColumn) {
            return super.getType(column);
        } else if (column instanceof ConditionCol52) {
            return getType((ConditionCol52) column,
                           columnIndex);
        } else {
            return super.getType(column);
        }
    }

    private String getType(final ConditionCol52 col,
                           final int columnIndex) {

        Logger.add("Looking for index: " + columnIndex + " from header meta data: " + ToString.toString(headerMetaData));

        return getType(headerMetaData.getPatternsByColumnNumber(columnIndex)
                               .getPattern(),
                       col);
    }

    protected String getTypeFromDataOracle(final String factType,
                                           final String fieldName) {

        Logger.add("Calling fieldTypes: " + factTypes.toString() + " with factType: " + factType + " fieldName: " + fieldName);

        return factTypes.getFieldType(factType,
                                      fieldName);
    }
}
