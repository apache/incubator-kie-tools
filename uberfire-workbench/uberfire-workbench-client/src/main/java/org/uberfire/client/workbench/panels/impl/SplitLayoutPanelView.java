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

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Corresponding view to {@link SplitLayoutPanelPresenter}. Only supports panels, not parts.
 * Enforces lifecycle callbacks on center parts.
 * <p>
 * Since this panel cannot hold parts, it also does not support drag-and-drop of parts.
 */
@Dependent
@Named("SplitLayoutPanelView")
public class SplitLayoutPanelView implements WorkbenchPanelView<SplitLayoutPanelPresenter> {

    private final SplitLayoutPanel layout;
    private final LayoutPanel centerPanel;
    private final LayoutPanel westPanel;
    private SplitLayoutPanelPresenter presenter;
    private Pair<PanelDefinition, Widget> activePanel;

    @Inject
    PlaceManager placeManager;

    @Inject
    private LayoutSelection layoutSelection;

    public SplitLayoutPanelView() {
        layout = new SplitLayoutPanel(2);

        centerPanel = new LayoutPanel();
        westPanel = new LayoutPanel();

        // TODO (hbraun): the actual width should become meta data. i.e. passed through the position object
        layout.addWest(westPanel, 217);
        layout.add(centerPanel);
    }

    @Override
    public void addPanel(PanelDefinition panel, WorkbenchPanelView view, Position position) {

        if ( CompassPosition.WEST.equals( position ) ) {
            Widget widget = view.asWidget();
            widget.addStyleName( "split-west" ); // HAL specific
            westPanel.add( widget );

        } else if ( CompassPosition.CENTER.equals( position ) ) {
            if ( activePanel != null ) {
                // close active parts of current panel
                for ( PartDefinition part : activePanel.getK1().getParts() ) {
                    placeManager.closePlace( part.getPlace() );
                }
            }

            Widget widget = view.asWidget();
            widget.addStyleName( "split-center" ); // HAL specific
            centerPanel.clear();
            centerPanel.add( widget );
            activePanel = new Pair<PanelDefinition, Widget>( panel, widget );
        } else {
            throw new IllegalArgumentException( "Unsupported position directive: " + position );
        }
    }

    @Override
    public boolean removePanel( WorkbenchPanelView<?> child ) {
        return westPanel.remove( child ) || centerPanel.remove( child );
    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void init( final SplitLayoutPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public SplitLayoutPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        throw new IllegalArgumentException("Presenter doesn't manage parts!");
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        throw new IllegalArgumentException("Presenter doesn't manage parts!");
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        throw new IllegalArgumentException("Presenter doesn't manage parts!");
    }

    @Override
    public Widget getPartDropRegion() {
        return null;
    }

    @Override
    public void setFocus( boolean hasFocus ) {

    }

    @Override
    public void onResize() {
        // noop
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        // noop
    }

    @Override
    public void setElementId( String elementId ) {
        if ( elementId == null ) {
            asWidget().getElement().removeAttribute( "id" );
        } else {
            asWidget().getElement().setAttribute( "id", elementId );
        }
    }

    @Override
    public void maximize() {
        layoutSelection.get().maximize( asWidget() );
    }

    @Override
    public void unmaximize() {
        layoutSelection.get().unmaximize( asWidget() );
    }
}
