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

package org.uberfire.ext.plugin.client.editor;

import com.google.gwt.user.client.ui.*;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.plugin.client.type.SplashPluginResourceType;
import org.uberfire.ext.plugin.client.widget.plugin.GeneralPluginEditor;
import org.uberfire.ext.plugin.model.*;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;


@Dependent
@WorkbenchEditor(identifier = "Splash PlugIn Editor", supportedTypes = { SplashPluginResourceType.class }, priority = Integer.MAX_VALUE)
public class SplashEditorPresenter
        extends RuntimePluginBaseEditor {

    @Inject
    private SplashPluginResourceType resourceType;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    protected GeneralPluginEditor editor;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;


    @Inject
    public SplashEditorPresenter( final SplashEditorView baseView ) {
        super( baseView );
    }

    protected ClientResourceType getResourceType(){
        return resourceType;
    }

    protected PluginType getPluginType(){
        return PluginType.SPLASH;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "SplashScreen PlugIn Editor [" + this.plugin.getName() + "]";
    }


    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }


    @Override
    protected void loadContent() {
        pluginServices.call( new RemoteCallback<PluginContent>() {
            @Override
            public void callback( final PluginContent response ) {
                ( ( SplashEditorView ) baseView ).setFramework( response.getFrameworks() );
                editor.setupContent( response, new ParameterizedCommand<Media>() {
                    @Override
                    public void execute( final Media media ) {
                        pluginServices.call().deleteMedia( media );
                    }
                } );
                baseView.hideBusyIndicator();
            }
        } ).getPluginContent( versionRecordManager.getCurrentPath() );
    }

    protected void save() {
        pluginServices.call( getSaveSuccessCallback( getContent().hashCode() ) ).save( getContent() );
    }

    @WorkbenchPartView
    public UberView<SplashEditorPresenter> getWidget() {
        return ( UberView<SplashEditorPresenter> ) super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getContent().hashCode() );
    }

    public PluginSimpleContent getContent() {
        return new PluginSimpleContent( editor.getContent(), editor.getTemplate(), editor.getCss(), editor.getCodeMap(),
                ( ( SplashEditorView ) baseView ).getFrameworks(), editor.getContent().getLanguage() );
    }

    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return pluginServices;
    }

}