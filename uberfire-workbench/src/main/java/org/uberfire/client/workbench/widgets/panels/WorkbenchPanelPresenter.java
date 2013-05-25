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
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
public interface WorkbenchPanelPresenter {

    public PanelDefinition getDefinition();

    public void setDefinition( final PanelDefinition definition );

    public void addPart( final WorkbenchPartPresenter.View view );

    public void removePart( final PartDefinition part );

    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position );

    public void removePanel();

    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration );

    public void setFocus( final boolean hasFocus );

    public void selectPart( final PartDefinition part );

    public void onPartFocus( final PartDefinition part );

    public void onPartLostFocus();

    public void onPanelFocus();

    public void onBeforePartClose( final PartDefinition part );

    public void maximize();

    public void minimize();

    public WorkbenchPanelView getPanelView();

    public void onResize( final int width,
                          final int height );

}
