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

package org.uberfire.client.workbench.panels;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

/**
 * Panel views
 */
public interface WorkbenchPanelView<P extends WorkbenchPanelPresenter>
        extends UberView<P>,
                RequiresResize {

    P getPresenter();

    void clear();

    void addPart( final WorkbenchPartPresenter.View view );

    void addPanel( final PanelDefinition panel,
                   final WorkbenchPanelView view,
                   final Position position );

    void changeTitle( final PartDefinition part,
                      final String title,
                      final IsWidget titleDecoration );

    void selectPart( final PartDefinition part );

    void removePart( final PartDefinition part );

    void removePanel();

    void setFocus( boolean hasFocus );

}