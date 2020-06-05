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
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.ArrayList;
import java.util.List;

import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

public class RuleNameBuilder
        implements
        GuidedDecisionTableSourceBuilderDirect {

    private List<DTCellValue52> values = new ArrayList<>();

    @Override
    public Code getActionTypeCode() {
        return Code.NAME;
    }

    @Override
    public void populateDecisionTable(final GuidedDecisionTable52 dtable,
                                      final int maxRowCount) {
        if (values.size() < maxRowCount) {
            for (int iRow = values.size(); iRow < maxRowCount; iRow++) {
                values.add(new DTCellValue52(""));
            }
        }

        for (int iRow = 0; iRow < values.size(); iRow++) {
            dtable.getData().get(iRow).add(GuidedDecisionTable52.RULE_NAME_COLUMN_INDEX,
                                           values.get(iRow));
        }
    }

    @Override
    public void addCellValue(final int row,
                             final int column,
                             final String value) {
        values.add(new DTCellValue52(""));
    }

    @Override
    public void clearValues() {
        values.clear();
    }

    @Override
    public boolean hasValues() {
        return !values.isEmpty();
    }

    @Override
    public String getResult() {
        throw new UnsupportedOperationException("RuleNameBuilder does not return DRL.");
    }

    @Override
    public void addTemplate(final int row,
                            final int col,
                            final String content) {
        throw new UnsupportedOperationException("RuleNameBuilder does implement code snippets.");
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public int getColumn() {
        return GuidedDecisionTable52.RULE_NAME_COLUMN_INDEX;
    }
}
