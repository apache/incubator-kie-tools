/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ContextUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;

@Dependent
public class ActionsToolboxHelper {

    private final ActionsToolboxFactory commonActionToolbox;
    private final ManagedInstance<FormGenerationToolboxAction> generateFormsActions;

    @Inject
    public ActionsToolboxHelper(final @CommonActionsToolbox ActionsToolboxFactory commonActionToolbox,
                                final @Any ManagedInstance<FormGenerationToolboxAction> generateFormsActions) {
        this.commonActionToolbox = commonActionToolbox;
        this.generateFormsActions = generateFormsActions;
    }

    @SuppressWarnings("all")
    public Collection<ToolboxAction<AbstractCanvasHandler>> getActions(final AbstractCanvasHandler canvasHandler,
                                                                       final Element<?> e) {
        final List<ToolboxAction<AbstractCanvasHandler>> actions = new LinkedList<>();
        actions.addAll(commonActionToolbox.getActions(canvasHandler, e));
        if (ContextUtils.isFormGenerationSupported(e)) {
            actions.add(generateFormsActions.get());
        }
        return actions;
    }
}
