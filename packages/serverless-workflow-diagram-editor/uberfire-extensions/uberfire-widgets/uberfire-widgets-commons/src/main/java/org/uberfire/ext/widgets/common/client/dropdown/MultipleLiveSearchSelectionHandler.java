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

package org.uberfire.ext.widgets.common.client.dropdown;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public class MultipleLiveSearchSelectionHandler<TYPE> implements LiveSearchSelectionHandler<TYPE> {

    private int maxDropDownTextItems = 5;

    private Command onChangeNotification;

    private List<LiveSearchSelectorItem<TYPE>> visibleItems = new ArrayList<>();

    private List<LiveSearchSelectorItem<TYPE>> selectedItems = new ArrayList<>();

    public MultipleLiveSearchSelectionHandler() {
    }

    public MultipleLiveSearchSelectionHandler(int maxDropDownTextItems) {
        this.maxDropDownTextItems = maxDropDownTextItems;
    }

    @Override
    public String getDropDownMenuHeader() {
        if(selectedItems.isEmpty()) {
            return null;
        }

        if(selectedItems.size() > maxDropDownTextItems) {
            return CommonConstants.INSTANCE.liveSearchElementsSelected(selectedItems.size());
        }

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i< selectedItems.size(); i++) {
            if(i > 0) {
                if(i == selectedItems.size() -1) {
                    builder.append(" & ");
                } else {
                    builder.append(", ");
                }
            }

            builder.append(selectedItems.get(i).getValue());
        }

        return builder.toString();
    }

    @Override
    public void registerItem(LiveSearchSelectorItem<TYPE> item) {
        item.setSelectionCallback(() -> selectItem(item));

        item.setMultipleSelection(true);

        LiveSearchSelectorItem<TYPE> selectedItem = selectedItems.stream()
                .filter(filterItem -> filterItem.getKey().equals(item.getKey()))
                .findFirst()
                .orElse(null);

        if(selectedItem != null && selectedItem.getKey().equals(item.getKey())) {
            selectedItems.remove(selectedItem);
            visibleItems.remove(selectedItem);

            item.select();
            selectedItems.add(item);
        }

        visibleItems.add(item);
    }

    @Override
    public void selectItem(LiveSearchSelectorItem<TYPE> item) {

        LiveSearchSelectorItem<TYPE> selectedItem = selectedItems.stream()
                .filter(listItem -> listItem.getKey().equals(item.getKey())).findFirst()
                .orElse(null);

        if(selectedItem != null) {
            selectedItem.reset();
            selectedItems.remove(selectedItem);
        } else {
            item.select();
            selectedItems.add(item);
        }

        if(onChangeNotification != null) {
            onChangeNotification.execute();
        }
    }


    @Override
    public void selectKey(TYPE key) {
        visibleItems.stream()
                .filter(selectorItem -> selectorItem.getKey().equals(key))
                .findFirst()
                .ifPresent(this::selectItem);
    }

    @Override
    public void clearSelection() {
        if(!selectedItems.isEmpty()) {
            selectedItems.forEach(LiveSearchSelectorItem::reset);
            selectedItems.clear();

            if (onChangeNotification != null) {
                onChangeNotification.execute();
            }
        }
    }

    public List<TYPE> getSelectedValues() {
        return selectedItems.stream()
                .map(LiveSearchSelectorItem::getKey)
                .collect(Collectors.toList());
    }

    public void setMaxDropDownTextItems(int maxDropDownTextItems) {
        this.maxDropDownTextItems = maxDropDownTextItems;
    }

    @Override
    public void setLiveSearchSelectionCallback(Command command) {
        this.onChangeNotification = command;
    }

    @Override
    public boolean isMultipleSelection() {
        return true;
    }
}
