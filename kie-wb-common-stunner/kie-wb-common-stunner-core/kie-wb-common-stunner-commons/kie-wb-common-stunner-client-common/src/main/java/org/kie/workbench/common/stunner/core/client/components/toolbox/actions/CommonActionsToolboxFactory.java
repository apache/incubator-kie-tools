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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * This factory builds a toolbox with some already defined buttons for common actions, like
 * removing an element.
 */
@ApplicationScoped
@CommonActionsToolbox
public class CommonActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final Supplier<DeleteNodeAction> deleteNodeActions;
    private final Supplier<ActionsToolboxView> views;

    // CDI proxy.
    protected CommonActionsToolboxFactory() {
        this(null,
             (Supplier) null,
             (Supplier) null);
    }

    @Inject
    public CommonActionsToolboxFactory(final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                       final @Any ManagedInstance<DeleteNodeAction> deleteNodeActions,
                                       final @CommonActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this(commandFactory,
             deleteNodeActions::get,
             views::get);
    }

    CommonActionsToolboxFactory(final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                final Supplier<DeleteNodeAction> deleteNodeActions,
                                final Supplier<ActionsToolboxView> views) {
        this.commandFactory = commandFactory;
        this.deleteNodeActions = deleteNodeActions;
        this.views = views;
    }

    @Override
    protected ActionsToolboxView<?> newViewInstance() {
        return views.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ToolboxAction<AbstractCanvasHandler>> getActions(final AbstractCanvasHandler canvasHandler,
                                                                       final Element<?> e) {
        final Set<ToolboxAction<AbstractCanvasHandler>> actions = new LinkedHashSet<>();
        if (isSupported(canvasHandler,
                        e)) {
            actions.add(deleteNodeActions.get());
        }
        return actions;
    }

    private boolean isSupported(final AbstractCanvasHandler canvasHandler,
                                final Element<?> element) {
        return null != element.asNode()
                && !CommandUtils.isError(
                commandFactory.deleteNode((Node) element).allow(canvasHandler)
        );
    }
}
