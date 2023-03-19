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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;

public class ItemElementPresenter extends ElementPresenter<ItemElementView> implements ItemElementView.Presenter {

    /**
     * This <code>Map</code> is used to map all the <b>expandable</b> properties names of a given itemId
     */
    protected Map<String, List<String>> itemIdExpandablePropertiesMap = new HashMap<>();

    @Override
    public LIElement getItemContainer(String itemId, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesValues) {
        final ItemElementView itemElementView = viewsProvider.getListEditorElementView();
        itemElementView.init(this);
        itemElementView.setItemId(itemId);
        final UListElement innerItemContainer = itemElementView.getInnerItemContainer();
        final LIElement saveChange = itemElementView.getSaveChange();
        simplePropertiesMap.forEach((propertyName, propertyValue) ->
                                      innerItemContainer.insertBefore(propertyPresenter.getPropertyFields(itemId, propertyName, propertyValue), saveChange));
        itemIdExpandablePropertiesMap.put(itemId, new ArrayList<>());
        expandablePropertiesValues.forEach((nestedPropertyName, nestedPropertiesValues) -> {
            itemIdExpandablePropertiesMap.get(itemId).add(nestedPropertyName);
            addExpandableItemElementView(itemElementView, nestedPropertiesValues, nestedPropertyName);
        });
        elementViewList.add(itemElementView);
        return itemElementView.getItemContainer();
    }

    @Override
    public void onToggleRowExpansion(ItemElementView itemElementView, boolean isShown) {
        CollectionEditorUtils.toggleRowExpansion(itemElementView.getFaAngleRight(), !isShown);
        propertyPresenter.onToggleRowExpansion(itemElementView.getItemId(), isShown);
        if (itemIdExpandablePropertiesMap.containsKey(itemElementView.getItemId())) {
            itemIdExpandablePropertiesMap.get(itemElementView.getItemId())
                    .forEach(expandablePropertyName -> propertyPresenter.onToggleRowExpansion(generateExpandableItemElementID(itemElementView,
                                                                                                                              expandablePropertyName),
                                                                                              isShown));
        }
        updateCommonToggleStatus(isShown);
    }

    @Override
    public void onEditItem(ItemElementView itemElementView) {
        if (!itemElementView.isShown()) {
            onToggleRowExpansion(itemElementView, false);
        }
        propertyPresenter.editProperties(itemElementView.getItemId());
        itemIdExpandablePropertiesMap.get(itemElementView.getItemId()).forEach(expandablePropertyName -> propertyPresenter.editProperties(generateExpandableItemElementID(itemElementView,
                                                                                                                                                                          expandablePropertyName)));
        itemElementView.getSaveChange().getStyle().setDisplay(Style.Display.INLINE);
        collectionEditorPresenter.toggleEditingStatus(true);
    }

    @Override
    public void onStopEditingItem(ItemElementView itemElementView) {
        propertyPresenter.stopEditProperties(itemElementView.getItemId());
        itemIdExpandablePropertiesMap.get(itemElementView.getItemId()).forEach(expandablePropertyName -> propertyPresenter.stopEditProperties(generateExpandableItemElementID(itemElementView,
                                                                                                                                                                              expandablePropertyName)));
        itemElementView.getSaveChange().getStyle().setDisplay(Style.Display.NONE);
        collectionEditorPresenter.toggleEditingStatus(false);
    }

    @Override
    public void updateItem(ItemElementView itemElementView) {
        propertyPresenter.updateProperties(itemElementView.getItemId());
        itemIdExpandablePropertiesMap.get(itemElementView.getItemId()).forEach(expandablePropertyName -> propertyPresenter.updateProperties(generateExpandableItemElementID(itemElementView,
                                                                                                                                                                            expandablePropertyName)));
        itemElementView.getSaveChange().getStyle().setDisplay(Style.Display.NONE);
        collectionEditorPresenter.toggleEditingStatus(false);
    }

    @Override
    public void onDeleteItem(ItemElementView itemElementView) {
        propertyPresenter.deleteProperties(itemElementView.getItemId());
        itemIdExpandablePropertiesMap.get(itemElementView.getItemId()).forEach(expandablePropertyName -> propertyPresenter.deleteProperties(generateExpandableItemElementID(itemElementView,
                                                                                                                                                                            expandablePropertyName)));
        itemElementView.getItemContainer().removeFromParent();
        elementViewList.remove(itemElementView);
        collectionEditorPresenter.toggleEditingStatus(false);
    }

    @Override
    public Map<String, Map<String, String>> getSimpleItemsProperties() {
        return elementViewList.stream()
                .collect(Collectors.toMap(ElementView::getItemId,
                                          itemElementView -> propertyPresenter.getSimpleProperties(itemElementView.getItemId())));
    }

    @Override
    public Map<String, Map<String, Map<String, String>>> getExpandableItemsProperties() {
        Map<String, Map<String, Map<String, String>>> toReturn = new HashMap<>();
        elementViewList.forEach(itemElementView -> {
            final List<String> expandablePropertiesNames = itemIdExpandablePropertiesMap.get(itemElementView.getItemId());
            Map<String, Map<String, String>> expandableProperties = new HashMap<>();
            expandablePropertiesNames.forEach(expandablePropertyName -> {
                final Map<String, String> simpleProperties = propertyPresenter.getSimpleProperties(generateExpandableItemElementID(itemElementView,
                                                                                                                                   expandablePropertyName));
                expandableProperties.put(expandablePropertyName, simpleProperties);
            });
            toReturn.put(itemElementView.getItemId(), expandableProperties);
        });
        return toReturn;
    }

    protected void addExpandableItemElementView(ItemElementView containerItemElementView, Map<String, String> propertiesMap, String expandablePropertyName) {
        final ItemElementView itemElementView = viewsProvider.getListEditorElementView();
        itemElementView.init(this);
        final UListElement innerItemContainer = itemElementView.getInnerItemContainer();
        final String expandableItemID = generateExpandableItemElementID(containerItemElementView, expandablePropertyName);
        itemElementView.setItemSeparatorText(expandablePropertyName);
        itemElementView.getEditItemButton().removeFromParent();
        itemElementView.getDeleteItemButton().removeFromParent();
        itemElementView.getSaveChange().removeFromParent();
        itemElementView.setItemId(expandableItemID);
        propertiesMap.forEach((propertyName, propertyValue) ->
                              innerItemContainer.appendChild(propertyPresenter.getPropertyFields(expandableItemID,
                                                                                                 propertyName,
                                                                                                 propertyValue)));
        containerItemElementView.getInnerItemContainer().insertBefore(itemElementView.getItemContainer(), containerItemElementView.getSaveChange());
    }

    protected String generateExpandableItemElementID(final ItemElementView itemElementView, final String expandablePropertyName) {
        return itemElementView.getItemId() + "." + expandablePropertyName;
    }
}
