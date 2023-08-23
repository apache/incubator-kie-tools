/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public abstract class BaseEditorDefinition<E extends Expression, D extends GridData> implements ExpressionEditorDefinition<E> {

    protected DefinitionUtils definitionUtils;
    protected SessionManager sessionManager;
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected DefaultCanvasCommandFactory canvasCommandFactory;
    protected Event<ExpressionEditorChanged> editorSelectedEvent;
    protected Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    protected Event<DomainObjectSelectionEvent> domainObjectSelectionEvent;
    protected ListSelectorView.Presenter listSelector;
    protected TranslationService translationService;
    protected ReadOnlyProvider readOnlyProvider;

    public BaseEditorDefinition() {
        //CDI proxy
    }

    public BaseEditorDefinition(final DefinitionUtils definitionUtils,
                                final SessionManager sessionManager,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final DefaultCanvasCommandFactory canvasCommandFactory,
                                final Event<ExpressionEditorChanged> editorSelectedEvent,
                                final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                final ListSelectorView.Presenter listSelector,
                                final TranslationService translationService,
                                final ReadOnlyProvider readOnlyProvider) {
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.editorSelectedEvent = editorSelectedEvent;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.domainObjectSelectionEvent = domainObjectSelectionEvent;
        this.listSelector = listSelector;
        this.translationService = translationService;
        this.readOnlyProvider = readOnlyProvider;
    }

    protected abstract D makeGridData(final Supplier<Optional<E>> expression);

    protected DMNGridPanel getGridPanel() {
        return ((DMNSession) sessionManager.getCurrentSession()).getGridPanel();
    }

    protected DMNGridLayer getGridLayer() {
        return ((DMNSession) sessionManager.getCurrentSession()).getGridLayer();
    }

    protected CellEditorControlsView.Presenter getCellEditorControls() {
        return ((DMNSession) sessionManager.getCurrentSession()).getCellEditorControls();
    }
}
