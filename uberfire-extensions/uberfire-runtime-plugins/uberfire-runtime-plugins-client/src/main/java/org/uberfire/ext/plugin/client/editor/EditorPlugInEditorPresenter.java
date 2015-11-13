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

package org.uberfire.ext.plugin.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.plugin.client.type.EditorPluginResourceType;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "Editor PlugIn Editor", supportedTypes = { EditorPluginResourceType.class }, priority = Integer.MAX_VALUE)
public class EditorPlugInEditorPresenter
        extends RuntimePluginBaseEditor {

    @Inject
    private EditorPluginResourceType resourceType;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    public EditorPlugInEditorPresenter( final EditorPlugInEditorView baseView ) {
        super( baseView );
    }

    protected ClientResourceType getResourceType() {
        return resourceType;
    }

    protected PluginType getPluginType() {
        return PluginType.EDITOR;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "Editor PlugIn [" + this.plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose();
    }

    @Override
    EditorPlugInEditorView view() {
        return (EditorPlugInEditorView) baseView;
    }

}