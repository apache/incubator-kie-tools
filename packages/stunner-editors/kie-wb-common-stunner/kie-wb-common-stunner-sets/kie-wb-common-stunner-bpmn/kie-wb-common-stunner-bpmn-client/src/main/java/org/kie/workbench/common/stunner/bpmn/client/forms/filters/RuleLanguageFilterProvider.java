/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

@Dependent
public class RuleLanguageFilterProvider implements StunnerFormElementFilterProvider {

    private final SessionManager sessionManager;
    private final FieldChangeHandlerManager fieldChangeHandlerManager;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    RuleLanguageFilterProvider() {
        this(null, null, null);
    }

    @Inject
    public RuleLanguageFilterProvider(final SessionManager sessionManager,
                                      final FieldChangeHandlerManager fieldChangeHandlerManager,
                                      final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent) {
        this.sessionManager = sessionManager;
        this.fieldChangeHandlerManager = fieldChangeHandlerManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
    }

    @Override
    public Class<?> getDefinitionType() {
        return BusinessRuleTask.class;
    }

    @Override
    public Collection<FormElementFilter> provideFilters(String elementUUID, Object definition) {
        final BusinessRuleTask businessRuleTask = (BusinessRuleTask) definition;
        final RuleLanguage ruleLanguage = businessRuleTask.getExecutionSet().getRuleLanguage();

        final Predicate drlPredicate = t -> Objects.equals(ruleLanguage.getValue(), RuleLanguage.DRL);
        final Predicate dmnPredicate = t -> Objects.equals(ruleLanguage.getValue(), RuleLanguage.DMN);

        final FormElementFilter ruleFlowGroupFilter =
                new FormElementFilter(BusinessRuleTask.EXECUTION_SET + "." +
                                              BusinessRuleTaskExecutionSet.RULE_FLOW_GROUP, drlPredicate);

        final FormElementFilter fileNameFilter =
                new FormElementFilter(BusinessRuleTask.EXECUTION_SET + "."
                        + BusinessRuleTaskExecutionSet.FILE_NAME, dmnPredicate);

        final FormElementFilter namespaceFilter =
                new FormElementFilter(BusinessRuleTask.EXECUTION_SET + "."
                                              + BusinessRuleTaskExecutionSet.NAMESPACE, dmnPredicate);

        final FormElementFilter decisionNameFilter =
                new FormElementFilter(BusinessRuleTask.EXECUTION_SET + "." +
                                              BusinessRuleTaskExecutionSet.DECISON_NAME, dmnPredicate);

        final FormElementFilter dmnModelNameFilter =
                new FormElementFilter(BusinessRuleTask.EXECUTION_SET + "." +
                                              BusinessRuleTaskExecutionSet.DMN_MODEL_NAME, dmnPredicate);

        return Arrays.asList(ruleFlowGroupFilter, fileNameFilter, namespaceFilter, decisionNameFilter, dmnModelNameFilter);
    }

    void onFormFieldChanged(@Observes FormFieldChanged formFieldChanged) {
        final String ruleLanguageFieldName = BusinessRuleTask.EXECUTION_SET + "." + BusinessRuleTaskExecutionSet.RULE_LANGUAGE;
        if (!Objects.equals(formFieldChanged.getName(), ruleLanguageFieldName)) {
            return;
        }

        refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession(), formFieldChanged.getUuid()));
    }
}
