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

package org.kie.workbench.common.stunner.cm.client.forms.fields.conditionEditor;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.VariableSearchService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Specializes
public class CaseManagementVariableSearchService
        extends VariableSearchService {

    @Inject
    public CaseManagementVariableSearchService(Caller<ConditionEditorService> service, ClientTranslationService translationService) {
        super(service, translationService);
    }

    @Override
    protected String getVariables(Node<?, ? extends Edge> node) {
        View view = node.getContent() instanceof View ? (View) node.getContent() : null;
        if (view == null) {
            return null;
        }
        if (view.getDefinition() instanceof AdHocSubprocess) {
            return ((AdHocSubprocess) view.getDefinition()).getProcessData().getProcessVariables().getValue();
        }
        if (view.getDefinition() instanceof CaseManagementDiagram) {
            CaseManagementDiagram cmDiagram = ((CaseManagementDiagram) view.getDefinition());
            StringBuilder variablesBuilder = new StringBuilder();
            String processVariables = cmDiagram.getProcessData().getProcessVariables().getValue();
            if (!isEmpty(processVariables)) {
                variablesBuilder.append(processVariables);
            }
            addCaseFileVariables(variablesBuilder, cmDiagram.getCaseManagementSet());
            return variablesBuilder.length() > 0 ? variablesBuilder.toString() : null;
        }
        return super.getVariables(node);
    }
}
