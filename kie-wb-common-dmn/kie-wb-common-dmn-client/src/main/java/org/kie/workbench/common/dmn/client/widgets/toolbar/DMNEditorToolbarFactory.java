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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;

@DMNEditor
@ApplicationScoped
public class DMNEditorToolbarFactory extends EditorToolbarFactory {

    protected DMNEditorToolbarFactory() {
        this(null, null, null);
    }

    @Inject
    public DMNEditorToolbarFactory(ToolbarCommandFactory commandFactory,
                                   ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> itemInstances,
                                   ManagedInstance<ToolbarView<AbstractToolbar>> viewInstances) {
        super(commandFactory, itemInstances, viewInstances);
    }

    @Override
    public Toolbar<AbstractClientFullSession> build(AbstractClientFullSession session) {
        final DMNEditorToolbar toolbar = new DMNEditorToolbar(commandFactory,
                                                              itemInstances,
                                                              viewInstances.get());
        toolbar.initialize(session);
        return toolbar;
    }
}
