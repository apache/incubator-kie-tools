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

import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * WorkbenchActivity and its subinterfaces define the interface between UberFire framework behaviour and application-defined behaviour.
 * 
 * In the model-view-presenter (MVP) sense, an Activity is essentially an application-provided Presenter: it has a view (its widget)
 * and it defines a set of operations that can affect that view.
 * 
 */
public interface WorkbenchActivity extends ContextSensitiveActivity {

    boolean onMayClose();

    Position getDefaultPosition();

    void onFocus();

    void onLostFocus();

    String getTitle();

    IsWidget getTitleDecoration();

    IsWidget getWidget();

    Menus getMenus();

    ToolBar getToolBar();

    String contextId();
}
