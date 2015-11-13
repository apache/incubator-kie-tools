/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.apps.client.home;

import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Breadcrumbs;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.apps.api.DirectoryBreadCrumb;
import org.uberfire.ext.apps.client.home.components.TilesApp;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class AppsHomeView extends Composite implements AppsHomePresenter.View {

    private AppsHomePresenter presenter;

    @UiField
    FlowPanel mainPanel;

    @UiField
    Breadcrumbs dirs;

    @UiField
    FlowPanel dirContent;

    interface AppsHomeViewBinder
            extends
            UiBinder<Widget, AppsHomeView> {

    }

    private static AppsHomeViewBinder uiBinder = GWT.create( AppsHomeViewBinder.class );

    @AfterInitialization
    public void initialize() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final AppsHomePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupBreadCrumbs( List<DirectoryBreadCrumb> breadCrumbs,
                                  final ParameterizedCommand<String> breadCrumbAction ) {
        dirs.clear();
        for ( final DirectoryBreadCrumb breadCrumb : breadCrumbs ) {
            final AnchorListItem bread = new AnchorListItem( breadCrumb.getName() );
            bread.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    breadCrumbAction.execute( breadCrumb.getUri() );
                }
            } );
            dirs.add( bread );
        }
    }

    @Override
    public void setupAddDir( final ParameterizedCommand<String> clickCommand,
                             Directory currentDirectory ) {
        generateCreateDirThumbNail( clickCommand, currentDirectory );
    }

    @Override
    public void setupChildsDirectories( List<Directory> childsDirectories,
                                        ParameterizedCommand<String> clickCommand,
                                        ParameterizedCommand<String> deleteCommand ) {
        for ( Directory childsDirectory : childsDirectories ) {
            final TilesApp link = TilesApp.directoryTiles( childsDirectory.getName(), childsDirectory.getURI(), TilesApp.TYPE.DIR, clickCommand, deleteCommand );
            dirContent.add( link );
        }
    }

    @Override
    public void clear() {
        dirContent.clear();
    }

    @Override
    public void setupChildComponents( List<String> childComponents,
                                      ParameterizedCommand<String> clickCommand ) {
        for ( String childComponent : childComponents ) {
            final TilesApp link = TilesApp.componentTiles( childComponent, TilesApp.TYPE.COMPONENT, clickCommand );
            dirContent.add( link );
        }

    }

    private void generateCreateDirThumbNail( final ParameterizedCommand<String> clickCommand,
                                             Directory currentDirectory ) {
        final TilesApp link = TilesApp.createDirTiles( TilesApp.TYPE.ADD, clickCommand, currentDirectory );
        dirContent.add( link );
    }

}
