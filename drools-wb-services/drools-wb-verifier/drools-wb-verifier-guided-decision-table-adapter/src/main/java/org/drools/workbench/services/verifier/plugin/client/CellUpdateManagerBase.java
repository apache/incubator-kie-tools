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
package org.drools.workbench.services.verifier.plugin.client;

import java.util.HashMap;
import java.util.Map;

import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Actions;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.DateEffectiveRuleAttribute;
import org.drools.verifier.core.index.model.DateExpiresRuleAttribute;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.RuleAttribute;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

import static org.drools.workbench.services.verifier.plugin.client.builders.Utils.getRealCellValue;

public abstract class CellUpdateManagerBase {

    protected final Column column;
    protected final Actions actions;
    protected final Conditions conditions;
    protected final GuidedDecisionTable52 model;
    protected final Coordinate coordinate;
    protected final Map<String, RuleAttribute> ruleAttributes;
    private final Rule rule;

    public CellUpdateManagerBase(final Index index,
                                 final GuidedDecisionTable52 model,
                                 final Coordinate coordinate) {
        this.model = model;
        this.coordinate = coordinate;

        Logger.add("Updating: " + coordinate.toString());

        column = index.getColumns()
                .where(Column.index()
                               .is(coordinate.getCol()))
                .select()
                .first();

        rule = index.getRules()
                .where(Rule.index()
                               .is(coordinate.getRow()))
                .select()
                .first();
        ruleAttributes = new HashMap<>(rule.getRuleAttributes());
        actions = rule.getActions();
        conditions = rule.getConditions();
    }

    /**
     * @return Returns true if the cell content was found and the value changed.
     */
    public boolean update() {
        boolean isAttribute = false;
        for (Map.Entry<String, RuleAttribute> entry : ruleAttributes.entrySet()) {
            if (coordinate.getCol() == entry.getValue().getIndex()) {
                if (updateRelevantAttribute(entry.getKey())) {
                    return true;
                } else {
                    isAttribute = true;
                    break;
                }
            }
        }
        if (isAttribute) {
            return false;
        }

        if (!updateCondition()) {
            return updateAction();
        } else {
            return true;
        }
    }

    private boolean updateRelevantAttribute(final String attributeName) {

        if (DateEffectiveRuleAttribute.NAME.equals(attributeName)) {
            final DTCellValue52 cellValue = getCellValue();
            rule.addRuleAttribute(new DateEffectiveRuleAttribute(coordinate.getCol(), cellValue.getDateValue()));
            return true;
        } else if (DateExpiresRuleAttribute.NAME.equals(attributeName)) {
            final DTCellValue52 cellValue = getCellValue();
            rule.addRuleAttribute(new DateExpiresRuleAttribute(coordinate.getCol(), cellValue.getDateValue()));
            return true;
        } else {
            return false;
        }
    }

    private DTCellValue52 getCellValue() {
        final DTCellValue52 cell = model.getData()
                .get(coordinate.getRow())
                .get(coordinate.getCol());
        final BaseColumn baseColumn = model.getExpandedColumns()
                .get(coordinate.getCol());

        return getRealCellValue((DTColumnConfig52) baseColumn,
                                cell);
    }

    private boolean updateCondition() {

        final Condition condition = conditions.where(Condition.columnUUID()
                                                             .is(column.getUuidKey()))
                .select()
                .first();

        if (condition != null) {
            return updateCondition(condition);
        } else {
            return false;
        }
    }

    private boolean updateAction() {
        final Action action = actions.where(Action.columnUUID()
                                                    .is(column.getUuidKey()))
                .select()
                .first();

        if (action != null) {
            return updateAction(action);
        } else {
            return false;
        }
    }

    protected abstract boolean updateCondition(final Condition condition);

    protected abstract boolean updateAction(final Action action);
}
