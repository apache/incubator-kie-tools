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

package org.drools.workbench.services.verifier.plugin.client.testutil;

import java.util.Collection;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

public class LimitedGuidedDecisionTableBuilder
        extends AbstractDecisionTableBuilder {

    public LimitedGuidedDecisionTableBuilder(final String packageName,
                                             final Collection<Import> imports,
                                             final String tableName) {
        table.setPackageName(packageName);
        table.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        table.getImports().getImports().addAll(imports);
        table.setTableName(tableName);
    }

    public LimitedGuidedDecisionTableBuilder withIntegerColumn(final String boundName,
                                                               final String factType,
                                                               final String field,
                                                               final String operator,
                                                               final int value) {
        final Pattern52 pattern = findPattern(boundName, factType);

        final LimitedEntryConditionCol52 condition = new LimitedEntryConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        condition.setFactField(field);
        condition.setHeader("Some header");
        condition.setOperator(operator);
        condition.setValue(
                new DTCellValue52() {
                    {
                        setNumericValue(new Integer(value));
                    }
                });
        pattern.getChildColumns().add(condition);

        addPattern(pattern);

        return this;
    }

    public LimitedGuidedDecisionTableBuilder withAction(String boundName,
                                                        String factField,
                                                        String typeNumericInteger,
                                                        DTCellValue52 value) {
        LimitedEntryActionSetFieldCol52 ins = new LimitedEntryActionSetFieldCol52();
        ins.setBoundName(boundName);
        ins.setFactField(factField);
        ins.setValue(value);
        ins.setType(typeNumericInteger);
        table.getActionCols().add(ins);

        return this;
    }
}
