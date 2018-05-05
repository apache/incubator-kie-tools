/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@Dependent
@DMNCommonActionsToolbox
public class DMNCommonActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final ActionsToolboxFactory commonActionsToolboxFactory;
    private final ManagedInstance<DMNEditDecisionToolboxAction> editDecisionToolboxActions;
    private final ManagedInstance<DMNEditBusinessKnowledgeModelToolboxAction> editBusinessKnowledgeModelToolboxActions;
    private final ManagedInstance<ActionsToolboxView> views;

    @Inject
    public DMNCommonActionsToolboxFactory(final @CommonActionsToolbox ActionsToolboxFactory commonActionsToolboxFactory,
                                          final @Any ManagedInstance<DMNEditDecisionToolboxAction> editDecisionToolboxActions,
                                          final @Any ManagedInstance<DMNEditBusinessKnowledgeModelToolboxAction> editBusinessKnowledgeModelToolboxActions,
                                          final @Any @CommonActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this.commonActionsToolboxFactory = commonActionsToolboxFactory;
        this.editDecisionToolboxActions = editDecisionToolboxActions;
        this.editBusinessKnowledgeModelToolboxActions = editBusinessKnowledgeModelToolboxActions;
        this.views = views;
    }

    @Override
    protected ActionsToolboxView<?> newViewInstance() {
        return views.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ToolboxAction<AbstractCanvasHandler>> getActions(final AbstractCanvasHandler canvasHandler,
                                                                       final Element<?> element) {
        // Obtain default common toolbox actions.
        final List<ToolboxAction<AbstractCanvasHandler>> actions =
                new ArrayList<>(commonActionsToolboxFactory.getActions(canvasHandler,
                                                                       element));

        // Add specific additional toolbox actions for different DMN node-types.
        if (isDecision(element)) {
            actions.add(editDecisionToolboxActions.get());
        } else if (isBusinessKnowledgeModel(element)) {
            actions.add(editBusinessKnowledgeModelToolboxActions.get());
        }
        return actions;
    }

    @PreDestroy
    public void destroy() {
        editDecisionToolboxActions.destroyAll();
        editBusinessKnowledgeModelToolboxActions.destroyAll();
        views.destroyAll();
    }

    private boolean isDecision(final Element<?> element) {
        return null != element.asNode()
                && element.getContent() instanceof Definition
                && ((Definition) element.getContent()).getDefinition() instanceof Decision;
    }

    private boolean isBusinessKnowledgeModel(final Element<?> element) {
        return null != element.asNode()
                && element.getContent() instanceof Definition
                && ((Definition) element.getContent()).getDefinition() instanceof BusinessKnowledgeModel;
    }
}
