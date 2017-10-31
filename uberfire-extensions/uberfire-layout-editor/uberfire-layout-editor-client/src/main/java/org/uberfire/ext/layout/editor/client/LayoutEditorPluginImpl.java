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
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;
import org.uberfire.mvp.Command;

@Dependent
public class LayoutEditorPluginImpl implements LayoutEditorPlugin {

    @Inject
    private LayoutEditorPresenter layoutEditorPresenter;

    @Inject
    private Caller<PerspectiveServices> perspectiveServices;

    @Inject
    private SavePopUpPresenter savePopUpPresenter;

    private String pluginName;
    private String emptyTitleText;
    private String emptySubTitleText;

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
        return layoutEditorPresenter.getLayout();
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
    public void load(Path currentPath,
                     Command loadCallBack) {

        perspectiveServices.call((LayoutTemplate layoutTemplate) -> {
            if (layoutTemplate != null) {
                layoutEditorPresenter.loadLayout(layoutTemplate,
                        emptyTitleText,
                        emptySubTitleText);
                loadCallBack.execute();
            } else {
                layoutEditorPresenter
                        .loadEmptyLayout(pluginName,
                                emptyTitleText,
                                emptySubTitleText);
            }

        }).getLayoutTemplate(currentPath);
    }

    @Override
    public void save(final Path path,
                     final RemoteCallback<Path> saveSuccessCallback) {

        savePopUpPresenter.show(path, commitMessage -> {
            LayoutTemplate layoutTemplate = getLayout();
            perspectiveServices.call(saveSuccessCallback)
                    .saveLayoutTemplate(path, layoutTemplate, commitMessage);
        });
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

    @Override
    public boolean hasDraggableGroupComponent(String groupId,
                                              String componentId) {
        return layoutEditorPresenter.hasDraggableComponent(groupId,
                                                           componentId);
    }
}
