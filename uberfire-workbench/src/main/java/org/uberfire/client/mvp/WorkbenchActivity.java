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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

/**
 * Base Workbench Part Activity life-cycles
 */
public interface WorkbenchActivity
        extends
        ContextSensitiveActivity {

    public void launch( final AcceptItem acceptItem,
                        final PlaceRequest place,
                        final Command callback );

    public boolean onMayClose();

    public void onClose();

    public Position getDefaultPosition();

    public void onFocus();

    public void onLostFocus();

    public String getTitle();

    public IsWidget getTitleDecoration();

    public IsWidget getWidget();

    public Menus getMenus();

    public ToolBar getToolBar();

    String contextId();
}
