/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class ToolbarFactory {

    private final ToolbarCommandFactory commandFactory;
    private final Instance<AbstractToolbarItem<ClientSession>> toolbarItems;
    private final Instance<ToolbarView> view;

    private ToolbarImpl toolbar;

    protected ToolbarFactory() {
        this( null, null, null );
    }

    @Inject
    public ToolbarFactory( final ToolbarCommandFactory commandFactory,
                           final Instance<AbstractToolbarItem<ClientSession>> toolbarItems,
                           final Instance<ToolbarView> view ) {
        this.toolbarItems = toolbarItems;
        this.view = view;
        this.commandFactory = commandFactory;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withClearSelectionCommand() {
        getCurrent().addCommand( commandFactory.newClearSelectionCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withSwitchGridCommand() {
        getCurrent().addCommand( commandFactory.newSwitchGridCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withVisitGraphCommand() {
        getCurrent().addCommand( commandFactory.newVisitGraphCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withClearCommand() {
        getCurrent().addCommand( commandFactory.newClearCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withDeleteSelectedElementsCommand() {
        getCurrent().addCommand( commandFactory.newDeleteSelectedElementsCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withUndoCommand() {
        getCurrent().addCommand( commandFactory.newUndoCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ToolbarFactory withValidateCommand() {
        getCurrent().addCommand( commandFactory.newValidateCommand() );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public <S extends ClientSession> Toolbar<S> build() {
        final Toolbar<S> result = ( Toolbar<S> ) getCurrent();
        this.toolbar = null;
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private Toolbar getCurrent() {
        if ( null == toolbar ) {
            toolbar = new ToolbarImpl( toolbarItems, view.get() );
        }
        return toolbar;
    }

}
