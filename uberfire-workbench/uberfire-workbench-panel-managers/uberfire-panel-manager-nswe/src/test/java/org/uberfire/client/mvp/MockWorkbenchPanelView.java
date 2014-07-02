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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

/**
 * A mock Workbench Panel view.
 */
public class MockWorkbenchPanelView
        implements
        WorkbenchPanelView<WorkbenchPanelPresenter> {

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
    public MultiTabWorkbenchPanelPresenter getPresenter() {
        return null;
    }

    @Override
    public void addPart( final View view ) {
    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position position ) {
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
    }

    @Override
    public void selectPart( final PartDefinition part ) {
    }

    @Override
    public void removePart( final PartDefinition part ) {
    }

    @Override
    public void removePanel() {
    }

    @Override
    public void setFocus( boolean hasFocus ) {
    }

}
