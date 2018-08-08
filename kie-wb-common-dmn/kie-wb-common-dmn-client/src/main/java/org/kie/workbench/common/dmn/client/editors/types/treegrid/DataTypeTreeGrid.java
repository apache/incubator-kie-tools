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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

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
public class DataTypeTreeGrid {

    private final View view;

    private final ManagedInstance<DataTypeTreeGridItem> treeGridItems;

    @Inject
    public DataTypeTreeGrid(final DataTypeTreeGrid.View view,
                            final ManagedInstance<DataTypeTreeGridItem> treeGridItems) {
        this.view = view;
        this.treeGridItems = treeGridItems;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void setupItems(final DataType dataType) {
        view.setupGridItems(makeTreeGridItems(dataType, 1));
    }

    List<DataTypeTreeGridItem> makeTreeGridItems(final DataType dataType,
                                                 final int level) {

        final DataTypeTreeGridItem gridItem = getGridItem();
        final List<DataType> subDataTypes = dataType.getSubDataTypes();
        final List<DataTypeTreeGridItem> gridItems = new ArrayList<>();

        gridItem.setupDataType(dataType, level);
        gridItems.add(gridItem);

        for (final DataType subDataType : subDataTypes) {
            gridItems.addAll(makeTreeGridItems(subDataType, level + 1));
        }

        return gridItems;
    }

    DataTypeTreeGridItem getGridItem() {
        return treeGridItems.get();
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public interface View extends UberElemental<DataTypeTreeGrid>,
                                  IsElement {

        void setupGridItems(final List<DataTypeTreeGridItem> treeGridItems);
    }
}
