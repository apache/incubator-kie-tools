/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.widgets.panels;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;

/**
 * Panel views
 */
public interface WorkbenchPanelView
        extends
        UberView<WorkbenchPanelPresenter>,
        RequiresResize {

    WorkbenchPanelPresenter getPresenter();

    void clear();

    void addPart( IsWidget titleWidget,
                  WorkbenchPartPresenter.View view );

    void addPanel( PanelDefinition panel,
                   WorkbenchPanelView view,
                   Position position );

    void changeTitle( int indexOfPartToChangeTabContent,
                      IsWidget titleWidget );

    void selectPart( int index );

    void removePart( int index );

    void removePanel();

    void setFocus( boolean hasFocus );

}