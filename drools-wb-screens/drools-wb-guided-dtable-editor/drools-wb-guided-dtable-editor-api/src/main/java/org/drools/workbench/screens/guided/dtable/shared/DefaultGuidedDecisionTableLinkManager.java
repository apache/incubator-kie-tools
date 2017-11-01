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

package org.drools.workbench.screens.guided.dtable.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.visitors.RuleModelVisitor;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager;

public class DefaultGuidedDecisionTableLinkManager implements GuidedDecisionTableLinkManager {

    static class RHSTypeFieldsExtractor {

        private Map<String, List<String>> typeFields = new HashMap<>();

        public Map<String, List<String>> extract(final GuidedDecisionTable52 dtable,
                                                 final List<IAction> fragment) {
            final BRLRuleModel rm = new BRLRuleModel(dtable);

            fragment.stream()
                    .filter(iAction -> iAction instanceof ActionFieldList)
                    .map(iAction -> (ActionFieldList) iAction)
                    .forEach(iAction -> extract(rm,
                                                iAction));
            return typeFields;
        }

        private void extract(final RuleModel ruleModel,
                             final ActionFieldList actionFieldList) {
            final Optional<String> type = getType(ruleModel,
                                                  actionFieldList);
            type.ifPresent(t -> {
                final ActionFieldValue[] actionFieldValues = actionFieldList.getFieldValues();
                for (ActionFieldValue actionFieldValue : actionFieldValues) {
                    final List<String> fields = typeFields.computeIfAbsent(t,
                                                                           s -> new ArrayList<>());
                    fields.add(actionFieldValue.getField());
                }
            });
        }

        private Optional<String> getType(final RuleModel ruleModel,
                                         final ActionFieldList actionFieldList) {
            if (actionFieldList instanceof ActionInsertFact) {
                return Optional.of(((ActionInsertFact) actionFieldList).getFactType());
            } else if (actionFieldList instanceof ActionSetField) {
                final String var = ((ActionSetField) actionFieldList).getVariable();
                return Optional.ofNullable(ruleModel.getLHSBindingType(var));
            }
            return Optional.empty();
        }
    }

    private final RHSTypeFieldsExtractor rhsTypeFieldsExtractor = new RHSTypeFieldsExtractor();

    @Override
    public void link(final GuidedDecisionTable52 model,
                     final GuidedDecisionTable52 otherModel,
                     final LinkFoundCallback callback) {
        if (model == null) {
            return;
        }
        if (otherModel == null) {
            return;
        }
        if (callback == null) {
            return;
        }
        final BRLRuleModel helper = new BRLRuleModel(model);

        //Re-create links to other Decision Tables
        for (CompositeColumn<? extends BaseColumn> otherDecisionTableConditions : otherModel.getConditions()) {
            if (otherDecisionTableConditions instanceof Pattern52) {
                final Pattern52 otherDecisionTablePattern = (Pattern52) otherDecisionTableConditions;
                for (ConditionCol52 otherDecisionTableCondition : otherDecisionTablePattern.getChildColumns()) {
                    final String factType = otherDecisionTablePattern.getFactType();
                    final String fieldName = otherDecisionTableCondition.getFactField();
                    final ActionCol52 linkedActionColumn = getLinkedActionColumn(factType,
                                                                                 fieldName,
                                                                                 model,
                                                                                 helper);
                    if (linkedActionColumn != null) {
                        final int sourceColumnIndex = model.getExpandedColumns().indexOf(linkedActionColumn);
                        final int targetColumnIndex = otherModel.getExpandedColumns().indexOf(otherDecisionTableCondition);
                        callback.link(sourceColumnIndex,
                                      targetColumnIndex);
                    }
                }
            } else if (otherDecisionTableConditions instanceof BRLConditionColumn) {
                final BRLConditionColumn fragment = (BRLConditionColumn) otherDecisionTableConditions;
                for (BRLConditionVariableColumn var : fragment.getChildColumns()) {
                    final String factType = var.getFactType();
                    final String fieldName = var.getFactField();
                    final ActionCol52 linkedActionColumn = getLinkedActionColumn(factType,
                                                                                 fieldName,
                                                                                 model,
                                                                                 helper);
                    if (linkedActionColumn != null) {
                        final int sourceColumnIndex = model.getExpandedColumns().indexOf(linkedActionColumn);
                        final int targetColumnIndex = otherModel.getExpandedColumns().indexOf(var);
                        callback.link(sourceColumnIndex,
                                      targetColumnIndex);
                    }
                }
            }
        }
    }

    private ActionCol52 getLinkedActionColumn(final String factType,
                                              final String fieldName,
                                              final GuidedDecisionTable52 model,
                                              final BRLRuleModel helper) {
        if (factType == null || fieldName == null) {
            return null;
        }

        for (ActionCol52 ac : model.getActionCols()) {
            if (ac instanceof ActionInsertFactCol52) {
                final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                if (factType.equals(aif.getFactType()) && fieldName.equals(aif.getFactField())) {
                    return ac;
                }
            } else if (ac instanceof ActionSetFieldCol52) {
                final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                final String binding = asf.getBoundName();
                final String asfFactType = helper.getLHSBindingType(binding);
                if (factType.equals(asfFactType) && fieldName.equals(asf.getFactField())) {
                    return ac;
                }
            } else if (ac instanceof BRLActionColumn) {
                final BRLActionColumn fragment = (BRLActionColumn) ac;

                if (hasTemplateKeys(fragment)) {
                    return getLinkedTemplateKeyColumn(fragment,
                                                      factType,
                                                      fieldName);
                } else {
                    return getLinkedDefinitionColumn(model,
                                                     fragment,
                                                     factType,
                                                     fieldName);
                }
            }
        }
        return null;
    }

    private boolean hasTemplateKeys(final BRLActionColumn column) {
        final Map<InterpolationVariable, Integer> ivs = new HashMap<>();
        final RuleModel rm = new RuleModel();
        column.getDefinition().forEach(rm::addRhsItem);

        final RuleModelVisitor rmv = new RuleModelVisitor(ivs);
        rmv.visit(rm);
        return ivs.size() > 0;
    }

    private ActionCol52 getLinkedTemplateKeyColumn(final BRLActionColumn fragment,
                                                   final String factType,
                                                   final String fieldName) {
        for (BRLActionVariableColumn var : fragment.getChildColumns()) {
            if (factType.equals(var.getFactType()) && fieldName.equals(var.getFactField())) {
                return var;
            }
        }
        return null;
    }

    private ActionCol52 getLinkedDefinitionColumn(final GuidedDecisionTable52 model,
                                                  final BRLActionColumn fragment,
                                                  final String factType,
                                                  final String fieldName) {
        final Map<String, List<String>> rhsTypeFields = rhsTypeFieldsExtractor.extract(model,
                                                                                       fragment.getDefinition());
        if (rhsTypeFields.containsKey(factType)) {
            for (String field : rhsTypeFields.get(factType)) {
                if (field.equals(fieldName)) {
                    return fragment.getChildColumns().get(0);
                }
            }
        }
        return null;
    }
}
