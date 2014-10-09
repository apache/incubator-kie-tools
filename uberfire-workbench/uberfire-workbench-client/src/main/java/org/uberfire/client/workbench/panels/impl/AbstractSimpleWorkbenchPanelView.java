/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.ContextPanel;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Supertype for both the DnD and non-DnD simple workbench panel views.
 */
public abstract class AbstractSimpleWorkbenchPanelView<P extends WorkbenchPanelPresenter>
extends AbstractDockingWorkbenchPanelView<P> {

    @Inject
    protected ListBarWidget listBar;

    @Inject
    protected ContextPanel contextWidget;

    @PostConstruct
    void setup() {
        setupListBarDragAndDrop();

        getPartViewContainer().add( contextWidget );
        getPartViewContainer().add( listBar );
    }

    private void setupListBarDragAndDrop() {
        listBar.setDndManager( dndManager );
        listBar.setup( false, false );
        addOnFocusHandler( listBar );
        addSelectionHandler( listBar );
        listBar.asWidget().getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );
        Layouts.setToFillParent( listBar );
    }

    public void enableDnd() {
        listBar.enableDnd();
    }

    @Override
    public void init( final P presenter ) {
        this.presenter = presenter;
        listBar.setPresenter( presenter );
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        listBar.addPart( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        listBar.changeTitle( part, title, titleDecoration );
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        return listBar.selectPart( part );
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        return listBar.remove( part );
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        listBar.setFocus( hasFocus );
    }

    @Override
    public void onResize() {
        presenter.onResize( getOffsetWidth(), getOffsetHeight() );

        // this will always be true in real life, but during GwtMockito tests it is not
        if ( getWidget() instanceof RequiresResize ) {
            super.onResize();
        }
    }
}
