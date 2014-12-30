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

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.plugin.client.type.ScreenPluginResourceType;
import org.uberfire.ext.plugin.client.widget.plugin.GeneralPluginEditor;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@WorkbenchEditor(identifier = "Screen PlugIn Editor", supportedTypes = { ScreenPluginResourceType.class }, priority = Integer.MAX_VALUE)
public class ScreenEditorPresenter
        extends RuntimePluginBaseEditor {

    @Inject
    protected GeneralPluginEditor editor;
    @Inject
    private ScreenPluginResourceType resourceType;
    @Inject
    private Caller<PluginServices> pluginServices;
    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;


    @Inject
    public ScreenEditorPresenter( final ScreenEditorView baseView ) {
        super( baseView );
    }

    protected ClientResourceType getResourceType(){
        return resourceType;
    }

    protected PluginType getPluginType(){
        return PluginType.SCREEN;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "Screen PlugIn Editor [" + this.plugin.getName() + "]";
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
                ( ( ScreenEditorView ) baseView ).setFramework( response.getFrameworks() );
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
    public UberView<ScreenEditorPresenter> getWidget() {
        return ( UberView<ScreenEditorPresenter> ) super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getContent().hashCode() );
    }

    public PluginSimpleContent getContent() {
        return new PluginSimpleContent( editor.getContent(), editor.getTemplate(), editor.getCss(), editor.getCodeMap(),
                ( ( ScreenEditorView ) baseView ).getFrameworks(), editor.getContent().getLanguage() );
    }

}