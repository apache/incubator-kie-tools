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

package org.uberfire.ext.layout.editor.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class LayoutEditorPluginImpl implements LayoutEditorPlugin {

    @Inject
    private LayoutEditorPresenter layoutEditorPresenter;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private Caller<LayoutServices> layoutServices;

    @Inject
    private SavePopUpPresenter savePopUpPresenter;

    private String pluginName;
    private String emptyTitleText;
    private String emptySubTitleText;

    private PluginType pluginType;

    private Path currentPath;

    private ParameterizedCommand<LayoutEditorModel> loadCallBack;

    @Override
    public void init(String layoutName,
                     LayoutDragComponentGroup layoutDragComponentGroup,
                     String emptyTitleText,
                     String emptySubTitleText) {
        this.pluginName = layoutName;
        this.emptyTitleText = emptyTitleText;
        this.emptySubTitleText = emptySubTitleText;
        layoutEditorPresenter.addDraggableComponentGroup(layoutDragComponentGroup);
        layoutEditorPresenter.setPageStyle(LayoutTemplate.Style.FLUID);
    }

    @Override
    public void init(String layoutName,
                     LayoutDragComponentGroup layoutDragComponentGroup,
                     String emptyTitleText,
                     String emptySubTitleText,
                     LayoutTemplate.Style style) {
        this.pluginName = layoutName;
        this.emptyTitleText = emptyTitleText;
        this.emptySubTitleText = emptySubTitleText;
        layoutEditorPresenter.addDraggableComponentGroup(layoutDragComponentGroup);
        layoutEditorPresenter.setPageStyle(style);
    }

    @Override
    public void clear() {
        layoutEditorPresenter.clear();
    }

    @Override
    public Widget asWidget() {
        final UberElement<LayoutEditorPresenter> view = layoutEditorPresenter.getView();
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    @Override
    public void loadLayout(LayoutTemplate layoutTemplate) {
        layoutEditorPresenter.loadLayout(layoutTemplate,
                                         emptyTitleText,
                                         emptySubTitleText);
    }

    @Override
    public void loadDefaultLayout(String layoutName) {
        layoutEditorPresenter.loadEmptyLayout(layoutName,
                                              emptyTitleText,
                                              emptySubTitleText);
    }

    @Override
    public LayoutTemplate getLayout() {
        return getLayoutEditor();
    }

    private LayoutTemplate getLayoutEditor() {
        LayoutTemplate layout = layoutEditorPresenter.getLayout();
        layout.setName(pluginName);
        return layout;
    }

    @Override
    public void addLayoutProperty(String key,
                                  String value) {
        layoutEditorPresenter.addLayoutProperty(key,
                                                value);
    }

    @Override
    public String getLayoutProperty(String key) {
        return layoutEditorPresenter.getLayoutProperty(key);
    }

    @Override
    public void load(final PluginType pluginType,
                     final Path currentPath,
                     final ParameterizedCommand<LayoutEditorModel> loadCallBack) {

        this.pluginType = pluginType;
        this.currentPath = currentPath;
        this.loadCallBack = loadCallBack;
        pluginServices.call(new RemoteCallback<LayoutEditorModel>() {

            @Override
            public void callback(final LayoutEditorModel model) {

                layoutServices.call(new RemoteCallback<LayoutTemplate>() {

                    @Override
                    public void callback(final LayoutTemplate layoutTemplate) {
                        if (layoutTemplate != null) {
                            layoutEditorPresenter.loadLayout(layoutTemplate,
                                                             emptyTitleText,
                                                             emptySubTitleText);
                            loadCallBack.execute(getLayoutContent(currentPath,
                                                                  model.getLayoutEditorModel()));
                        } else {
                            layoutEditorPresenter
                                    .loadEmptyLayout(pluginName,
                                                     emptyTitleText,
                                                     emptySubTitleText);
                            ;
                        }
                    }
                }).convertLayoutFromString(model.getLayoutEditorModel());
            }
        }).getLayoutEditor(currentPath,
                           pluginType);
    }

    @Override
    public void save(final Path path,
                     final RemoteCallback<Path> saveSuccessCallback) {

        layoutServices.call(new RemoteCallback<String>() {

            @Override
            public void callback(final String model) {
                savePlugin(model,
                           path,
                           saveSuccessCallback);
            }
        }).convertLayoutToString(getLayoutEditor());
    }

    private void savePlugin(final String model,
                            final Path path,
                            final RemoteCallback<Path> saveSuccessCallback) {
        savePopUpPresenter.show(path,
                                new ParameterizedCommand<String>() {

                                    @Override
                                    public void execute(final String commitMessage) {
                                        pluginServices.call(saveSuccessCallback).saveLayout(
                                                getLayoutContent(path,
                                                                 model),
                                                commitMessage);
                                    }
                                });
    }

    private LayoutEditorModel getLayoutContent(Path currentPath,
                                               String model) {
        return new LayoutEditorModel(pluginName,
                                     pluginType,
                                     currentPath,
                                     model);
    }

    @Override
    public void addDraggableComponentGroup(LayoutDragComponentGroup group) {
        layoutEditorPresenter.addDraggableComponentGroup(group);
    }

    @Override
    public void addDraggableComponentToGroup(String groupId,
                                             String componentId,
                                             LayoutDragComponent component) {
        layoutEditorPresenter.addDraggableComponentToGroup(groupId,
                                                           componentId,
                                                           component);
    }

    @Override
    public void removeDraggableComponentGroup(String groupId) {
        layoutEditorPresenter.removeDraggableGroup(groupId);
    }

    @Override
    public void removeDraggableGroupComponent(String groupId,
                                              String componentId) {
        layoutEditorPresenter.removeDraggableComponentFromGroup(groupId,
                                                                componentId);
    }
}
