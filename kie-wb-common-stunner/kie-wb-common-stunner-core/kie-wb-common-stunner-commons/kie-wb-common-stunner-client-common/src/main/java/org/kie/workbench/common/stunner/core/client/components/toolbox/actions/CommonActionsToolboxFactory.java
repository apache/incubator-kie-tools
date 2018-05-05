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

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.uberfire.mvp.Command;

/**
 * This factory builds a toolbox with some already defined buttons for common actions, like
 * removing an element.
 */
@Dependent
@CommonActionsToolbox
public class CommonActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final Supplier<DeleteNodeAction> deleteNodeActions;
    private final Command deleteNodeActionsDestroyer;
    private final Supplier<ActionsToolboxView> views;
    private final Command viewsDestroyer;

    @Inject
    public CommonActionsToolboxFactory(final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                       final @Any ManagedInstance<DeleteNodeAction> deleteNodeActions,
                                       final @Any @CommonActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this(commandFactory,
             deleteNodeActions::get,
             deleteNodeActions::destroyAll,
             views::get,
             views::destroyAll);
    }

    CommonActionsToolboxFactory(final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                final Supplier<DeleteNodeAction> deleteNodeActions,
                                final Command deleteNodeActionsDestroyer,
                                final Supplier<ActionsToolboxView> views,
                                final Command viewsDestroyer) {
        this.commandFactory = commandFactory;
        this.deleteNodeActions = deleteNodeActions;
        this.deleteNodeActionsDestroyer = deleteNodeActionsDestroyer;
        this.views = views;
        this.viewsDestroyer = viewsDestroyer;
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

    @PreDestroy
    public void destroy() {
        deleteNodeActionsDestroyer.execute();
        viewsDestroyer.execute();
    }

    private boolean isSupported(final AbstractCanvasHandler canvasHandler,
                                final Element<?> element) {
        return null != element.asNode()
                && !CommandUtils.isError(
                commandFactory.deleteNode((Node) element).allow(canvasHandler)
        );
    }
}
