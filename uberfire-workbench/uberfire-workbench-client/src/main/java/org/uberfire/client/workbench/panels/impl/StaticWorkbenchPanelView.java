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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("StaticWorkbenchPanelView")
public class StaticWorkbenchPanelView
        extends BaseWorkbenchPanelView<StaticWorkbenchPanelPresenter> {

    @Inject
    PlaceManager placeManager;

    StaticFocusedResizePanel panel = new StaticFocusedResizePanel();

    public StaticWorkbenchPanelView() {

        panel.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( final FocusEvent event ) {
                panelManager.onPanelFocus( presenter.getDefinition() );
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        panel.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( final SelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus();
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );

        initWidget( panel );
    }

    @Override
    public void init( final StaticWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public StaticWorkbenchPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        if ( panel.getPartView() != null ) {
            placeManager.tryClosePlace( getPlaceOfPartView(), new Command() {
                @Override
                public void execute() {
                    panel.setPart( view );
                }
            } );
        } else {
            panel.setPart( view );
        }
    }

    PlaceRequest getPlaceOfPartView() {
        return panel.getPartView().getPresenter().getDefinition().getPlace();
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        scheduleResize();
    }

    @Override
    public void removePart( final PartDefinition part ) {
        panel.clear();
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        panel.setFocus( hasFocus );
    }

    private void scheduleResize() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        } );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );
            presenter.onResize( width, height );
            panel.setPixelSize( width, height );
            resizeSuper();
        }
    }

    void resizeSuper() {
        super.onResize();
    }
}
