/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.cms.layout.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.plugin.model.Plugin;

@Dependent
public class PerspectiveDragConfigModal implements IsWidget {

    public interface View extends UberView<PerspectiveDragConfigModal> {

        void clearItems();

        void addItem(String name, Command onSelect);

        void setCurrentSelection(String name);

        void setHelpText(String text);

        void show();

        void hide();
    }

    View view;
    PerspectivePluginManager perspectivePluginManager;
    PerspectiveTreeProvider perspectiveTreeProvider;
    Plugin selectedItem = null;
    Command onOk = null;
    Command onCancel = null;

    @Inject
    public PerspectiveDragConfigModal(View view,
                                      PerspectivePluginManager perspectivePluginManager,
                                      PerspectiveTreeProvider perspectiveTreeProvider) {
        this.view = view;
        this.perspectivePluginManager = perspectivePluginManager;
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.view.init(this);
        this.view.setHelpText(ContentManagerConstants.INSTANCE.perspectiveDragComponentHelp());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public View getView() {
        return view;
    }

    public void setOnOk(Command onOk) {
        this.onOk = onOk;
    }

    public void setOnCancel(Command onCancel) {
        this.onCancel = onCancel;
    }

    public Plugin getSelectedItem() {
        return selectedItem;
    }

    public void show(String selectedPerspectiveId) {
        view.clearItems();
        addItems(selectedPerspectiveId);
        view.show();
    }

    private void addItems(String selectedItemId) {
        perspectivePluginManager.getPerspectivePlugins(plugins -> {
            for (Plugin plugin : plugins) {
                String perspectiveName = perspectiveTreeProvider.getPerspectiveName(plugin.getName());
                view.addItem(perspectiveName, () -> onItemSelected(plugin));

                if (selectedItemId != null && plugin.getName().equals(selectedItemId)) {
                    view.setCurrentSelection(perspectiveName);
                }
            }
        });
    }

    // View callbacks

    public void onItemSelected(Plugin plugin) {
        selectedItem = plugin;
        view.setCurrentSelection(selectedItem.getName());
    }

    void onOk() {
        if (selectedItem != null) {
            view.hide();
            if (onOk != null) {
                onOk.execute();
            }
        }
    }

    void onCancel() {
        view.hide();
        if (onCancel != null) {
            onCancel.execute();
        }
    }
}
