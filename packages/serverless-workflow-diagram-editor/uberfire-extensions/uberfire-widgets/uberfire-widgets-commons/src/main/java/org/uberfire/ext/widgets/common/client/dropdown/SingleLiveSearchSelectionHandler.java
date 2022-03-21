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

import org.uberfire.mvp.Command;

public class SingleLiveSearchSelectionHandler<TYPE> implements LiveSearchSelectionHandler<TYPE> {

    protected List<LiveSearchSelectorItem<TYPE>> visibleItems = new ArrayList<>();

    private LiveSearchSelectorItem<TYPE> selectedItem;

    private Command onChangeNotification;

    public SingleLiveSearchSelectionHandler() {
    }

    @Override
    public String getDropDownMenuHeader() {
        if(selectedItem != null) {
            return selectedItem.getValue();
        }
        return null;
    }

    @Override
    public void registerItem(final LiveSearchSelectorItem<TYPE> item) {
        if(selectedItem != null && selectedItem.getKey().equals(item.getKey())) {
            visibleItems.remove(selectedItem);
            item.select();
            selectedItem = item;
        }

        item.setSelectionCallback(() -> selectItem(item));

        visibleItems.add(item);
    }

    @Override
    public void selectItem(LiveSearchSelectorItem<TYPE> item) {
        if(selectedItem == null) {
            selectedItem = item;
            selectedItem.select();
        } else {
            if(!selectedItem.getKey().equals(item.getKey())) {
                selectedItem.reset();
                selectedItem = item;
                selectedItem.select();
            }
        }

        if(onChangeNotification != null) {
            onChangeNotification.execute();
        }
    }

    @Override
    public void selectKey(TYPE key) {
        if(selectedItem != null && selectedItem.getKey().equals(key)) {
            return;
        }

        visibleItems.stream()
                .filter(item -> item.getKey().equals(key))
                .findFirst()
                .ifPresent(item -> selectItem(item));
    }

    public TYPE getSelectedKey() {
        if(selectedItem != null) {
            return selectedItem.getKey();
        }

        return null;
    }

    public String getSelectedValue() {
        if(selectedItem != null) {
            return selectedItem.getValue();
        }

        return null;
    }

    @Override
    public void setLiveSearchSelectionCallback(Command command) {
        this.onChangeNotification = command;
    }

    @Override
    public void clearSelection() {
        if(selectedItem != null) {
            selectedItem.reset();
            selectedItem = null;

            if (onChangeNotification != null) {
                onChangeNotification.execute();
            }
        }
    }

    @Override
    public boolean isMultipleSelection() {
        return false;
    }
}
