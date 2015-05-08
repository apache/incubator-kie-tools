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

package org.uberfire.client.screens.todo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.util.Layouts;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "ReadmeScreen", preferredWidth = 400)
public class ReadmeScreen
        extends Composite
        implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, ReadmeScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @Inject
    private Caller<VFSService> vfsServices;

    @UiField
    protected TextArea markdown;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        Layouts.setToFillParent( markdown );

        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path o ) {
                vfsServices.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        if ( response == null ) {
                            markdown.setText( "<p>-- empty --</p>" );
                        } else {
                            markdown.setText( response );
                        }
                    }
                } ).readAllString( o );
            }
        } ).get( "default://uf-playground/README.md" );
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "README";
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return MenuFactory
                .newTopLevelMenu( "Validate" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        Window.alert( "valid!" );
                    }
                } )
                .endMenu()
                .build();
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

}