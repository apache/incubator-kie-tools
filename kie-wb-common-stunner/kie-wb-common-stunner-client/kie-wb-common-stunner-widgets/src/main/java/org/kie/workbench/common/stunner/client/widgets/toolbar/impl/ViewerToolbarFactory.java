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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;

@ApplicationScoped
public class ViewerToolbarFactory implements ToolbarFactory<AbstractClientReadOnlySession> {

    protected final ToolbarCommandFactory commandFactory;
    protected final ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> itemInstances;
    protected final ManagedInstance<ToolbarView> viewInstances;

    @Inject
    public ViewerToolbarFactory(final ToolbarCommandFactory commandFactory,
                                final ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> itemInstances,
                                final ManagedInstance<ToolbarView> viewInstances) {
        this.commandFactory = commandFactory;
        this.itemInstances = itemInstances;
        this.viewInstances = viewInstances;
    }

    @Override
    public Toolbar<AbstractClientReadOnlySession> build(final AbstractClientReadOnlySession session) {
        final ViewerToolbar toolbar = new ViewerToolbar(commandFactory,
                                                        itemInstances,
                                                        viewInstances.get());
        toolbar.initialize(session);
        return toolbar;
    }
}
