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

package org.kie.workbench.common.dmn.client.session.presenters.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.DMNCommand;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@DMNEditor
@Dependent
public class SessionPreviewImpl extends org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl {

    @Inject
    public SessionPreviewImpl(final DefinitionManager definitionManager,
                              final ShapeManager shapeManager,
                              final TextPropertyProviderFactory textPropertyProviderFactory,
                              final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                              final DefinitionUtils definitionUtils,
                              final GraphUtils graphUtils,
                              final @Any ManagedInstance<BaseCanvasHandler> canvasHandlerFactories,
                              final @Any ManagedInstance<CanvasCommandFactory> canvasCommandFactories,
                              final @MultipleSelection SelectionControl<AbstractCanvasHandler, ?> selectionControl,
                              final WidgetWrapperView view) {
        super(definitionManager,
              shapeManager,
              textPropertyProviderFactory,
              canvasCommandManager,
              definitionUtils,
              graphUtils,
              canvasHandlerFactories,
              canvasCommandFactories,
              selectionControl,
              view);
    }

    @Override
    protected void handleCanvasCommandExecutedEvent(final CanvasCommandExecutedEvent event) {
        if (event.getCommand() instanceof DMNCommand) {
            return;
        }
        super.handleCanvasCommandExecutedEvent(event);
    }

    @Override
    protected void handleCanvasUndoCommandExecutedEvent(final CanvasUndoCommandExecutedEvent event) {
        if (event.getCommand() instanceof DMNCommand) {
            return;
        }
        super.handleCanvasUndoCommandExecutedEvent(event);
    }
}
