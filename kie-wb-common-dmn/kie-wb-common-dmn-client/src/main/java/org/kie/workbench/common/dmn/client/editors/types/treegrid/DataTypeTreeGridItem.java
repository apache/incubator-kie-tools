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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.uberfire.client.mvp.UberElemental;

public class DataTypeTreeGridItem {

    private final View view;

    private final DataTypeSelect typeSelect;

    private DataType dataType;

    private int level;

    @Inject
    public DataTypeTreeGridItem(final View view,
                                final DataTypeSelect typeSelect) {
        this.view = view;
        this.typeSelect = typeSelect;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public DataTypeTreeGridItem setupDataType(final DataType dataType,
                                              final int level) {
        this.dataType = dataType;
        this.level = level;

        setupSelectComponent();
        setupView();

        return this;
    }

    void setupSelectComponent() {
        typeSelect.init(getDataType());
    }

    void setupView() {
        view.setupSelectComponent(typeSelect);
        view.setDataType(getDataType());
    }

    DataType getDataType() {
        return dataType;
    }

    public int getLevel() {
        return level;
    }

    void expandOrCollapseSubTypes() {
        if (view.isCollapsed()) {
            view.expand();
        } else {
            view.collapse();
        }
    }

    void expandSubDataTypes() {
        expandSubDataTypes(getDataType());
    }

    void collapseSubDataTypes() {
        collapseSubDataTypes(getDataType());
    }

    void expandSubDataTypes(final DataType dataType) {
        dataType.getSubDataTypes().forEach(view::expandSubType);
    }

    void collapseSubDataTypes(final DataType dataType) {
        dataType.getSubDataTypes().forEach(view::collapseSubType);
    }

    public interface View extends UberElemental<DataTypeTreeGridItem> {

        void setDataType(final DataType dataType);

        void expand();

        void collapse();

        void setupSelectComponent(final DataTypeSelect typeSelect);

        boolean isCollapsed();

        void expandSubType(final DataType subDataType);

        void collapseSubType(final DataType subDataType);
    }
}
