/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionView;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.ItemElementView;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.KeyValueElementView;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.PropertyView;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.ItemEditingBox;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox.KeyValueEditingBox;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.MenuItemView;
import org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopup;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageDecisionElementView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageScenarioListView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.FieldItemView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemView;
import org.jboss.errai.ioc.client.api.ManagedInstance;

@ApplicationScoped
/**
 * Class used as Provider for <i>Views</i> that has to be dynamically created
 */
public class ViewsProvider {

    @Inject
    private ManagedInstance<MenuItemView> menuItemViewInstance;

    @Inject
    private ManagedInstance<FieldItemView> fieldItemViewInstance;

    @Inject
    private ManagedInstance<CoverageDecisionElementView> decisionElementViewInstance;

    @Inject
    private ManagedInstance<ListGroupItemView> listGroupItemViewInstance;

    @Inject
    private ManagedInstance<CollectionView> collectionViewInstance;

    @Inject
    private ManagedInstance<ItemElementView> listElementViewInstance;

    @Inject
    private ManagedInstance<KeyValueElementView> keyValueElementViewInstance;

    @Inject
    private ManagedInstance<PropertyView> propertyViewInstance;

    @Inject
    private ManagedInstance<ItemEditingBox> listEditingBoxInstance;

    @Inject
    private ManagedInstance<KeyValueEditingBox> mapEditingBoxInstance;

    @Inject
    private ManagedInstance<FileUploadPopup> fileUploadPopupInstance;

    @Inject
    private ManagedInstance<CoverageScenarioListView> coverageScenarioListView;

    public MenuItemView getMenuItemView() {
        return menuItemViewInstance.get();
    }

    public FieldItemView getFieldItemView() {
        return fieldItemViewInstance.get();
    }

    public ListGroupItemView getListGroupItemView() {
        return listGroupItemViewInstance.get();
    }

    public CollectionView getCollectionEditorView() {
        return collectionViewInstance.get();
    }

    public ItemElementView getListEditorElementView() {
        return listElementViewInstance.get();
    }

    public KeyValueElementView getKeyValueElementView() {
        return keyValueElementViewInstance.get();
    }

    public PropertyView getPropertyEditorView() {
        return propertyViewInstance.get();
    }

    public ItemEditingBox getItemEditingBox() {
        return listEditingBoxInstance.get();
    }

    public KeyValueEditingBox getKeyValueEditingBox() {
        return mapEditingBoxInstance.get();
    }

    public FileUploadPopup getFileUploadPopup() {
        return fileUploadPopupInstance.get();
    }

    public CoverageDecisionElementView getDecisionElementView() {
        return decisionElementViewInstance.get();
    }

    public CoverageScenarioListView getCoverageScenarioListView() {
        return coverageScenarioListView.get();
    }
}
