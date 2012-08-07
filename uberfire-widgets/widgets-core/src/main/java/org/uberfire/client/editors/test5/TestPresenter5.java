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

package org.uberfire.client.editors.test5;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.WorkbenchMenuBar;
import org.uberfire.client.workbench.WorkbenchMenuItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchEditor(identifier = "Test5", fileTypes = {"test5"})
public class TestPresenter5 {

    public interface View
        extends
        IsWidget {

        void setContent(final String content);
    }

    @Inject
    public View                view;

    @Inject
    private Caller<VFSService> vfsServices;

    public TestPresenter5() {
    }

    @OnStart
    public void onStart(Path path) {
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback(String response) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Test5";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @WorkbenchMenu
    public WorkbenchMenuBar getMenuBar() {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        final WorkbenchMenuBar subMenuBar = new WorkbenchMenuBar( true );
        menuBar.addItem( new MenuItem( "TestPresenter5 menu",
                                       subMenuBar ) );
        for ( int i = 0; i < 3; i++ ) {
            final String caption = "TestPresenter5:Item:" + i;
            final WorkbenchMenuItem item = new WorkbenchMenuItem( caption,
                                                                  new Command() {

                                                                      @Override
                                                                      public void execute() {
                                                                          Window.alert( "You clicked " + caption );
                                                                      }

                                                                  } );
            item.setHasPermission( i > 0 );
            subMenuBar.addItem( item );
        }

        return menuBar;
    }

}