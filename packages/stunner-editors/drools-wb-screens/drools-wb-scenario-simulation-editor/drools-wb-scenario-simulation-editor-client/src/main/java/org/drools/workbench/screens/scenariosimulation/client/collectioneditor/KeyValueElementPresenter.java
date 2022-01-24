/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;

public class KeyValueElementPresenter extends ElementPresenter<KeyValueElementView> implements KeyValueElementView.Presenter {

    @Override
    public LIElement getKeyValueContainer(String itemId, Map<String, String> keyPropertiesValues, Map<String, String> valuePropertiesValues) {
        final KeyValueElementView keyValueElementView = viewsProvider.getKeyValueElementView();
        keyValueElementView.init(this);
        keyValueElementView.setItemId(itemId);
        final UListElement keyContainer = keyValueElementView.getKeyContainer();
        List<String> keyValueIds = getKeyValueIds(itemId);
        keyPropertiesValues.forEach((propertyName, propertyValue) ->
                                            keyContainer.appendChild(propertyPresenter.getPropertyFields(keyValueIds.get(0), propertyName, propertyValue)));

        final UListElement valueContainer = keyValueElementView.getValueContainer();
        valuePropertiesValues.forEach((propertyName, propertyValue) ->
                                              valueContainer.appendChild(propertyPresenter.getPropertyFields(keyValueIds.get(1), propertyName, propertyValue)));
        elementViewList.add(keyValueElementView);
        return keyValueElementView.getItemContainer();
    }

    @Override
    public void onToggleRowExpansion(KeyValueElementView keyValueElementView, boolean isShown) {
        CollectionEditorUtils.toggleRowExpansion(keyValueElementView.getFaAngleRight(), !isShown);
        CollectionEditorUtils.toggleRowExpansion(keyValueElementView.getKeyLabel(), isShown);
        CollectionEditorUtils.toggleRowExpansion(keyValueElementView.getValueLabel(), isShown);
        List<String> keyValueIds = getKeyValueIds( keyValueElementView.getItemId());
        keyValueIds.forEach(id -> propertyPresenter.onToggleRowExpansion(id, isShown));
        updateCommonToggleStatus(isShown);
    }

    @Override
    public void onEditItem(KeyValueElementView keyValueElementView) {
        if (!keyValueElementView.isShown()) {
            onToggleRowExpansion(keyValueElementView, false);
        }
        List<String> keyValueIds = getKeyValueIds( keyValueElementView.getItemId());
        keyValueIds.forEach(id -> propertyPresenter.editProperties(id));
        keyValueElementView.getSaveChange().getStyle().setDisplay(Style.Display.INLINE);
        toggleEditingStatus(true);
        collectionEditorPresenter.toggleEditingStatus(true);
    }

    @Override
    public void onStopEditingItem(KeyValueElementView keyValueElementView) {
        List<String> keyValueIds = getKeyValueIds( keyValueElementView.getItemId());
        keyValueIds.forEach(id -> propertyPresenter.stopEditProperties(id));
        keyValueElementView.getSaveChange().getStyle().setDisplay(Style.Display.NONE);
        toggleEditingStatus(false);
        collectionEditorPresenter.toggleEditingStatus(false);
    }

    @Override
    public void onDeleteItem(KeyValueElementView keyValueElementView) {
        List<String> keyValueIds = getKeyValueIds( keyValueElementView.getItemId());
        keyValueIds.forEach(id -> propertyPresenter.deleteProperties(id));
        keyValueElementView.getItemContainer().removeFromParent();
        elementViewList.remove(keyValueElementView);
        toggleEditingStatus(false);
        collectionEditorPresenter.toggleEditingStatus(false);
    }

    @Override
    public void updateItem(KeyValueElementView keyValueElementView) {
        List<String> keyValueIds = getKeyValueIds( keyValueElementView.getItemId());
        keyValueIds.forEach(id -> propertyPresenter.updateProperties(id));
        keyValueElementView.getSaveChange().getStyle().setDisplay(Style.Display.NONE);
        toggleEditingStatus(false);
        collectionEditorPresenter.toggleEditingStatus(false);
    }

    @Override
    public Map<Map<String, String>, Map<String, String>> getItemsProperties() {
        return elementViewList.stream()
                .collect(Collectors.toMap(
                        keyValueElementView -> {
                            String itemId = keyValueElementView.getItemId() + "#key";
                            return propertyPresenter.getSimpleProperties(itemId);
                        },
                        keyValueElementView -> {
                            String itemId = keyValueElementView.getItemId() + "#value";
                            return propertyPresenter.getSimpleProperties(itemId);
                        }));
    }

    /**
     * Returns a <code>List</code>> with (itemId)#key at pos. 0 and (itemId)#value at pos. 1
     * @param itemId
     * @return
     */
    private List<String> getKeyValueIds(String itemId) {
        return Arrays.asList(itemId + "#key", itemId + "#value");
    }
}
