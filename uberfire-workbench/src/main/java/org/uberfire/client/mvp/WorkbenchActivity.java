/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Base Workbench Part Activity life-cycles
 */
public interface WorkbenchActivity
    extends
    Activity {

    public void launch(final AcceptItem acceptItem,
                       final PlaceRequest place,
                       final Command callback);

    public boolean onMayClose();

    public void onClose();

    public Position getDefaultPosition();

    public void onFocus();

    public void onLostFocus();

    public IsWidget getTitleWidget();

    public IsWidget getWidget();

    public MenuBar getMenuBar();

    public ToolBar getToolBar();

}
