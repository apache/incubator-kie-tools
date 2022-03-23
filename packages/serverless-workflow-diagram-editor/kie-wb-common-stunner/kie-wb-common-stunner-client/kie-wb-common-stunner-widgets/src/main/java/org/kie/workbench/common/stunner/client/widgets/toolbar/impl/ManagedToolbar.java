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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.AbstractToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class ManagedToolbar<S extends ClientSession> implements Toolbar<S> {

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<ToolbarCommand> commandInstances;
    private final ManagedInstance<AbstractToolbarItem<S>> itemInstances;
    private final List<Class<? extends ToolbarCommand>> commandTypes;
    private final List<ToolbarCommand> commands;
    private final List<AbstractToolbarItem<S>> items;
    private final ToolbarView<Toolbar<S>> view;

    @Inject
    public ManagedToolbar(final DefinitionUtils definitionUtils,
                          final @Any ManagedInstance<ToolbarCommand> commandInstances,
                          final @Any ManagedInstance<AbstractToolbarItem<S>> itemInstances,
                          final ToolbarView<Toolbar<S>> view) {
        this.definitionUtils = definitionUtils;
        this.commandInstances = commandInstances;
        this.itemInstances = itemInstances;
        this.view = view;
        this.commands = new LinkedList<>();
        this.commandTypes = new LinkedList<>();
        this.items = new LinkedList<>();
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public ManagedToolbar register(final Class<? extends ToolbarCommand> type) {
        commandTypes.add(type);
        return this;
    }

    @Override
    public void load(final S session) {
        final Diagram diagram = session.getCanvasHandler().getDiagram();
        final Annotation qualifier =
                definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        // Initialize the command and view instances.
        commandTypes.stream()
                .map(type -> loadCommand(type,
                                         qualifier))
                .forEach(this::registerCommand);
        // Show the toolbar.
        show(session);
    }

    @Override
    public void enable(final ToolbarCommand<S> command) {
        getItem(command).enable();
    }

    @Override
    public void disable(final ToolbarCommand<S> command) {
        getItem(command).disable();
    }

    public ToolbarCommand getCommand(final int index) {
        return commands.get(index);
    }

    @Override
    public boolean isEnabled(final ToolbarCommand<S> command) {
        return getItem(command).isEnabled();
    }

    @Override
    public void destroy() {
        commandTypes.clear();
        commands.forEach(this::destroyCommand);
        commands.clear();
        items.forEach(this::destroyItem);
        items.clear();
        getView().destroy();
    }

    @Override
    public ToolbarView<? extends Toolbar> getView() {
        return view;
    }

    @SuppressWarnings("unchecked")
    private void show(final S session) {
        for (int i = 0; i < commands.size(); i++) {
            final AbstractToolbarCommand command =
                    (AbstractToolbarCommand) commands.get(i);
            final AbstractToolbarItem<S> item = items.get(i);
            getView().addItem(item.asWidget());
            item.show(this,
                      session,
                      command,
                      command::execute);
        }
        getView().show();
    }

    private void registerCommand(final ToolbarCommand command) {
        commands.add(command);
        final AbstractToolbarItem<S> item = itemInstances.get();
        items.add(item);
    }

    private ToolbarCommand loadCommand(final Class<? extends ToolbarCommand> type,
                                       final Annotation qualifier) {
        return InstanceUtils.lookup(commandInstances,
                                    type,
                                    qualifier);
    }

    private AbstractToolbarItem<S> getItem(final ToolbarCommand command) {
        return items.get(commands.indexOf(command));
    }

    private void destroyCommand(final ToolbarCommand command) {
        command.destroy();
        commandInstances.destroy(command);
    }

    private void destroyItem(final AbstractToolbarItem<S> item) {
        item.destroy();
        itemInstances.destroy(item);
    }
}
