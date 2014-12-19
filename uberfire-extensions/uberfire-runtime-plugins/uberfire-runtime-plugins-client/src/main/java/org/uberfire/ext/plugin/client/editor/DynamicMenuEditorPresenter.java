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

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.plugin.client.type.DynamicMenuResourceType;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.DynamicMenuItem;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "Dynamic Menu Editor", supportedTypes = { DynamicMenuResourceType.class }, priority = Integer.MAX_VALUE)
public class DynamicMenuEditorPresenter
        extends RuntimePluginBaseEditor {

    @Inject
    private DynamicMenuResourceType resourceType;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private Event<NotificationEvent> notification;

    private ListDataProvider<DynamicMenuItem> dataProvider = new ListDataProvider<DynamicMenuItem>();

    private DynamicMenu menuItem;

    @Inject
    public DynamicMenuEditorPresenter( final DynamicMenuEditorView baseView ) {
        super( baseView );
    }

    protected ClientResourceType getResourceType() {
        return resourceType;
    }

    protected PluginType getPluginType() {
        return PluginType.DYNAMIC_MENU;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "Dynamic Menu Editor [" + this.plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }


    public void addMenuItem( final DynamicMenuItem menuItem ) {
        DynamicMenuItem existingItem = null;
        for ( final DynamicMenuItem item : dataProvider.getList() ) {
            if ( menuItem.getMenuLabel().equals( item.getMenuLabel() ) ) {
                existingItem = item;
                break;
            }
        }
        if ( existingItem == null ) {
            dataProvider.getList().add( menuItem );
        } else {
            ( ( DynamicMenuEditorView ) baseView ).setSelected( menuItem );
            dataProvider.refresh();
        }

        dataProvider.flush();
    }

    public void removeObject( DynamicMenuItem object ) {
        dataProvider.getList().remove( object );
        dataProvider.flush();
        dataProvider.refresh();
    }

    public void setDataDisplay( final HasData<DynamicMenuItem> display ) {
        dataProvider.addDataDisplay( display );
    }

    @Override
    protected void loadContent() {
        pluginServices.call( new RemoteCallback<DynamicMenu>() {
            @Override
            public void callback( final DynamicMenu response ) {
                setOriginalHash( response.hashCode() );
                menuItem = response;
                for ( final DynamicMenuItem menuItem : response.getMenuItems() ) {
                    dataProvider.getList().add( menuItem );
                }
                baseView.hideBusyIndicator();
            }
        } ).getDynamicMenuContent( versionRecordManager.getCurrentPath() );
    }

    protected void save() {
        pluginServices.call( getSaveSuccessCallback( getContent().hashCode() ) ).save( getContent() );
    }

    @WorkbenchPartView
    public UberView<DynamicMenuEditorPresenter> getWidget() {
        return ( UberView<DynamicMenuEditorPresenter> ) super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getContent().hashCode() );
    }

    public DynamicMenu getContent() {
        return new DynamicMenu( menuItem.getName(), PluginType.DYNAMIC_MENU, versionRecordManager.getCurrentPath(), new ArrayList<DynamicMenuItem>( dataProvider.getList() ) );
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