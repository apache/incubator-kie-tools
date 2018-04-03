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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.AbstractToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

public abstract class AbstractToolbar<S extends ClientSession> implements Toolbar<S> {

    private final Map<Class<?>, ToolbarCommand<? super S>> commands = new HashMap<>();
    private final Map<ToolbarCommand<? super S>, AbstractToolbarItem<S>> items = new HashMap<>();
    private final ToolbarView<AbstractToolbar> view;

    protected AbstractToolbar(final ToolbarView<AbstractToolbar> view) {
        this.view = view;
        view.init(this);
    }

    protected abstract AbstractToolbarItem<S> newToolbarItem();

    @SuppressWarnings("unchecked")
    public void initialize(final S session) {
        commands.values().stream()
                .forEach(command -> {
                    final AbstractToolbarItem<S> toolbarItem = newToolbarItem();
                    toolbarItem.setUUID(((AbstractToolbarCommand) command).getUuid());
                    getView().addItem(toolbarItem.asWidget());
                    items.put(command,
                              toolbarItem);
                    toolbarItem.show(this,
                                     session,
                                     (AbstractToolbarCommand<S, ?>) command,
                                     command::execute);
                });
        afterDraw();
        show();
    }

    public void addCommand(Class<? extends ToolbarCommand<? super S>> type, final ToolbarCommand<? super S> item) {
        commands.put(type, item);
    }

    @Override
    public void disable(final ToolbarCommand<S> command) {
        final AbstractToolbarItem<S> item = getItem(command);
        if (null != item) {
            item.disable();
        }
    }

    @Override
    public void enable(final ToolbarCommand<S> command) {
        final AbstractToolbarItem<S> item = getItem(command);
        if (null != item) {
            item.enable();
        }
    }

    @Override
    public boolean isEnabled(final ToolbarCommand<S> command) {
        final AbstractToolbarItem<S> item = getItem(command);
        if (null != item) {
            return item.isEnabled();
        }
        return false;
    }

    @Override
    public void clear() {
        commands.clear();
        items.clear();
        getView().clear();
    }

    @Override
    public void destroy() {
        commands.clear();
        items.clear();
        getView().destroy();
    }

    @Override
    public ToolbarView<? extends Toolbar> getView() {
        return view;
    }

    protected <T extends ToolbarCommand<? super S>> T getCommand(final Class<T> type) {
        return (T) commands.get(type);
    }

    @SuppressWarnings("unchecked")
    protected AbstractToolbarItem<S> getItem(final ToolbarCommand<?> command) {
        return items
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(command))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private void afterDraw() {
        commands.values().forEach(ToolbarCommand::refresh);
    }

    private void show() {
        getView().show();
    }
}
