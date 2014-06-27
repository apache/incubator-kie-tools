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
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PartManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link LayoutPanel} presenter. Can be used for both perspectives and panels.
 */
@Dependent
@Named("LayoutPanelView")
public class LayoutPanelView implements WorkbenchPanelView<LayoutPanelPresenter> {

    private LayoutPanel layout;

    private LayoutPanelPresenter presenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private PartManager partManager;

    public LayoutPanelView() {

        layout = new LayoutPanel();
        layout.setStyleName("fill-layout");
    }

    // ------------------------

    @Override
    public void addPanel(PanelDefinition panel, WorkbenchPanelView view, Position position) {
        // invoked when this presenter manages a perspective itself (is root)
        layout.add(view);
    }

    @Override
    public void removePanel() {

    }

    // ------------------------

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void init( final LayoutPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public LayoutPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void clear() {
        layout.clear();
    }


    // ------------------------

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        // invoked when this presenter panels within perspectives (not root)
        PartDefinition part = view.getPresenter().getDefinition();
        if(!partManager.hasPart(part))
            partManager.registerPart(part, view.getWrappedWidget().asWidget());
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        partManager.removePart(part);
        layout.clear(); // only supports a single part
        return true;
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        layout.clear(); // TODO (hbraun): Is this necessary?
        layout.add(partManager.selectPart(part));
        return true;
    }

    // ------------------------

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
}
