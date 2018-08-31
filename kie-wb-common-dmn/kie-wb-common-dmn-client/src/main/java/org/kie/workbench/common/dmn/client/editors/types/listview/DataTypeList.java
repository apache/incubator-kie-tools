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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.uberfire.client.mvp.UberElemental;

@ApplicationScoped
public class DataTypeList {

    private final View view;

    private final ManagedInstance<DataTypeListItem> listItems;

    @Inject
    public DataTypeList(final DataTypeList.View view,
                        final ManagedInstance<DataTypeListItem> listItems) {
        this.view = view;
        this.listItems = listItems;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setupItems(final List<DataType> dataTypes) {

        final List<DataTypeListItem> gridItems = new ArrayList<>();

        dataTypes.forEach(dt -> gridItems.addAll(makeTreeGridItems(dt, 1)));

        view.setupListItems(gridItems);

        gridItems.stream().filter(gi -> gi.getLevel() == 1).forEach(DataTypeListItem::collapse);
    }

    void refreshSubItems(final DataTypeListItem listItem,
                         final List<DataType> subDataTypes) {

        final DataType dataType = listItem.getDataType();
        final int level = listItem.getLevel();
        final List<DataTypeListItem> gridItems = new ArrayList<>();

        for (final DataType subDataType : subDataTypes) {
            gridItems.addAll(makeTreeGridItems(subDataType, level + 1));
        }

        view.addSubItems(dataType, gridItems);
    }

    List<DataTypeListItem> makeTreeGridItems(final DataType dataType,
                                             final int level) {

        final DataTypeListItem listItem = makeGridItem();
        final List<DataType> subDataTypes = dataType.getSubDataTypes();
        final List<DataTypeListItem> gridItems = new ArrayList<>();

        listItem.setupDataType(dataType, level);
        gridItems.add(listItem);

        for (final DataType subDataType : subDataTypes) {
            gridItems.addAll(makeTreeGridItems(subDataType, level + 1));
        }

        return gridItems;
    }

    DataTypeListItem makeGridItem() {
        final DataTypeListItem listItem = listItems.get();
        listItem.init(this);
        return listItem;
    }

    public interface View extends UberElemental<DataTypeList>,
                                  IsElement {

        void setupListItems(final List<DataTypeListItem> treeGridItems);

        void addSubItems(final DataType dataType,
                         final List<DataTypeListItem> treeGridItems);
    }
}
