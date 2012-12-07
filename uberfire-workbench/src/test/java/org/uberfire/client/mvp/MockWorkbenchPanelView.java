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
package org.uberfire.client.mvp;

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.widgets.panels.RootWorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPartPresenter.View;
import org.uberfire.client.workbench.model.PanelDefinition;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPanelView;

/**
 * A mock Workbench Panel view.
 */
public class MockWorkbenchPanelView
        implements
        WorkbenchPanelView {

    @Override
    public void onResize() {
    }

    @Override
    public void init( WorkbenchPanelPresenter presenter ) {
    }

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public RootWorkbenchPanelPresenter getPresenter() {
        return null;
    }

    @Override
    public void clear() {
    }

    @Override
    public void addPart( IsWidget title,
                         View view ) {
    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position position ) {
    }

    @Override
    public void changeTitle( int indexOfPartToChangeTabContent,
                             IsWidget tabContent ) {
    }

    @Override
    public void selectPart( int index ) {
    }

    @Override
    public void removePart( int index ) {
    }

    @Override
    public void removePanel() {
    }

    @Override
    public void setFocus( boolean hasFocus ) {
    }

}
