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

package org.uberfire.client.editors.test;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "Test")
public class TestPresenter {

    public interface View
        extends
        IsWidget {
    }

    @Inject
    public View view;

    public TestPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Test";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @WorkbenchMenu
    public List<MenuItem> getMenu() {
        final List<MenuItem> items = new ArrayList<MenuItem>();
        final MenuBar menuBar = new MenuBar( true );
        items.add( new MenuItem( "TestPresenter menu",
                                 menuBar ) );
        for ( int i = 0; i < 3; i++ ) {
            final String caption = "Item:" + i;
            menuBar.addItem( new MenuItem( caption,
                                           new Command() {

                                               @Override
                                               public void execute() {
                                                   Window.alert( "You clicked " + caption );
                                               }

                                           } ) );
        }

        return items;
    }

}