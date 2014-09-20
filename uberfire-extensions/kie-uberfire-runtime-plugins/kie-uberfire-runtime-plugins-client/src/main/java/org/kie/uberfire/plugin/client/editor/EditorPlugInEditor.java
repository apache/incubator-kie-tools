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

package org.kie.uberfire.plugin.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.plugin.client.type.EditorPluginResourceType;
import org.kie.uberfire.plugin.client.widget.plugin.GeneralPluginEditor;
import org.kie.uberfire.plugin.model.Framework;
import org.kie.uberfire.plugin.model.Media;
import org.kie.uberfire.plugin.model.Plugin;
import org.kie.uberfire.plugin.model.PluginContent;
import org.kie.uberfire.plugin.model.PluginSimpleContent;
import org.kie.uberfire.plugin.model.PluginType;
import org.kie.uberfire.plugin.service.PluginServices;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.uberfire.plugin.client.code.CodeList.*;

@Dependent
@WorkbenchEditor(identifier = "Editor PlugIn", supportedTypes = { EditorPluginResourceType.class }, priority = Integer.MAX_VALUE)
public class EditorPlugInEditor
        extends Composite
        implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, EditorPlugInEditor> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel htmlPanel;

    @UiField
    FlowPanel formArea;

    @UiField
    ListBox framework;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private GeneralPluginEditor editor;

    @Inject
    private PlaceManager placeManager;

    private PlaceRequest place;

    private Plugin plugin;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        editor.setup( MAIN, DIVIDER, ON_OPEN, ON_CLOSE, ON_FOCUS, ON_LOST_FOCUS, ON_MAY_CLOSE, ON_STARTUP, ON_SHUTDOWN,
                      ON_CONCURRENT_UPDATE, ON_CONCURRENT_DELETE, ON_CONCURRENT_RENAME, ON_CONCURRENT_COPY, ON_UPDATE, ON_DELETE, ON_RENAME, ON_COPY
                , DIVIDER, TITLE, RESOURCE_TYPE );
        htmlPanel.add( editor );
    }

    @OnStartup
    public void onStartup( final Path path,
                           final PlaceRequest place ) {
        pluginServices.call( new RemoteCallback<PluginContent>() {
            @Override
            public void callback( final PluginContent response ) {
                setFramework( response.getFrameworks() );
                editor.setupContent( response, new ParameterizedCommand<Media>() {
                    @Override
                    public void execute( final Media media ) {
                        pluginServices.call().deleteMedia( media );
                    }
                } );
            }
        } ).getPluginContent( path );
        plugin = new Plugin( place.getParameter( "name", "" ), PluginType.EDITOR, path );
        this.place = place;
    }

    private void setFramework( final Collection<Framework> frameworks ) {
        if ( frameworks != null && !frameworks.isEmpty() ) {
            final Framework framework = frameworks.iterator().next();
            for ( int i = 0; i < this.framework.getItemCount(); i++ ) {
                if ( this.framework.getItemText( i ).equalsIgnoreCase( framework.toString() ) ) {
                    this.framework.setSelectedIndex( i );
                    return;
                }
            }
        }
        this.framework.setSelectedIndex( 0 );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Editor PlugIn [" + plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return new PluginsCommonMenu().build( new Command() {
            @Override
            public void execute() {
                final PluginSimpleContent content = new PluginSimpleContent( editor.getContent(), editor.getTemplate(), editor.getCss(), editor.getCodeMap(), getFrameworks(), editor.getContent().getLanguage() );

                pluginServices.call().save( content );
            }
        }, new Command() {
            @Override
            public void execute() {
                pluginServices.call().delete( plugin );
                placeManager.forceClosePlace( place );
            }
        } );
    }

    private Collection<Framework> getFrameworks() {
        if ( framework.getValue().equalsIgnoreCase( "(Framework)" ) ) {
            return Collections.emptyList();
        }
        return new ArrayList<Framework>() {{
            add( Framework.valueOf( framework.getValue().toUpperCase() ) );
        }};
    }

    @OnMayClose
    public boolean onMayClose() {
        return true;
    }

    @IsDirty
    public boolean isDirty() {
        return false;
    }

    @Override
    public void onResize() {
        htmlPanel.setHeight( getParent().getParent().getOffsetHeight() + "px" );
        editor.onResize();
    }

}