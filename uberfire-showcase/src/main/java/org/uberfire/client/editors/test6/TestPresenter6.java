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

package org.uberfire.client.editors.test6;

import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.ToolBarItem;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "Test6")
public class TestPresenter6 {

    public interface View
        extends
        IsWidget {
    }

    @Inject
    public View view;

    public TestPresenter6() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Test6";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        final ToolBar toolBar = new DefaultToolBar();
        final ToolBarItem button1 = new DefaultToolBarItem( "image/info.png",
                                                            "Tool#1",
                                                            new Command() {

                                                                @Override
                                                                public void execute() {
                                                                    Window.alert( "Go, go Gadget Toolbar..." );
                                                                }

                                                            } );
        toolBar.addItem( button1 );
        final ToolBarItem button2 = new DefaultToolBarItem( "image/info.png",
                                                            "Tool#2",
                                                            new Command() {

                                                                @Override
                                                                public void execute() {
                                                                    Window.alert( "Go, go Gadget Toolbar..." );
                                                                }

                                                            } );
        button2.setEnabled( false );
        toolBar.addItem( button2 );
        return toolBar;
    }

}