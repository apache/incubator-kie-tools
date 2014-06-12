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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A workbench perspective view that builds on stock GWT {@link com.google.gwt.user.client.ui.SplitLayoutPanel}'s.
 * It supports CENTER and WEST positions.
 *
 * @author Heiko Braun
 */
@Dependent
@Named("SplitLayoutPanelView")
public class SplitLayoutPanelView implements WorkbenchPanelView<SplitLayoutPanelPresenter> {

    private SplitLayoutPanel layout;
    private LayoutPanel centerPanel;
    private LayoutPanel westPanel;
    private SplitLayoutPanelPresenter presenter;
    private Pair<PanelDefinition, Widget> activePanel;

    @Inject
    PlaceManager placeManager;

    public SplitLayoutPanelView() {
        layout = new SplitLayoutPanel(2);

        centerPanel = new LayoutPanel();
        westPanel = new LayoutPanel();

        // TODO (hbraun): the actual width should become meta data. i.e. passed through the position object
        layout.addWest(westPanel, 217);
        layout.add(centerPanel);
    }

    // ------------------------- panel management

    @Override
    public void addPanel(PanelDefinition panel, WorkbenchPanelView view, Position position) {

        System.out.println("Adding '"+view.getPresenter().getDefinition().getPanelType()+"'to instance: "+this);

        if(Position.WEST.equals(position))
        {
            Widget widget = view.asWidget();
            widget.addStyleName("split-west"); // HAL specific
            westPanel.add(widget);
        }
        else if(Position.CENTER.equals(position))
        {

            // identify active panel

            if(activePanel !=null) {
                // close active parts of current panel
                for (PartDefinition part : activePanel.getK1().getParts()) {
                    placeManager.closePlace(part.getPlace());
                }
            }

            Widget widget = view.asWidget();
            widget.addStyleName("split-center"); // HAL specific
            centerPanel.clear();
            centerPanel.add(widget);
            activePanel = new Pair<PanelDefinition, Widget>(panel, widget);
        }
        else
        {
            throw new IllegalArgumentException("Unsupported position directive: "+ position);
        }
    }

    @Override
    public void removePanel() {
        System.out.println("removePanel called on "+ this);
    }

    public void selectPanel(PanelDefinition panelDefinition) {
        // noop
    }

    // -------------------------

    @Override
    public Widget asWidget() {
        System.out.println("asWidget called on "+ this);
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
    public void clear() {
        layout.clear();
    }

    // -------------------------  part management

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        //centerPanel.add(view.getWrappedWidget().asWidget());
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

    // -------------------------

    @Override
    public void setFocus( boolean hasFocus ) {
        if(hasFocus)
            System.out.println("panel focus on instance "+ this);
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
}
