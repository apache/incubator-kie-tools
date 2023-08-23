/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;

@ApplicationScoped
public class DMNDataTypesSubIndex implements DMNSubIndex {

    private final DataTypeList dataTypeList;

    private final DataTypeShortcuts dataTypeShortcuts;

    private final DataTypeStore dataTypeStore;

    @Inject
    public DMNDataTypesSubIndex(final DataTypeList dataTypeList,
                                final DataTypeShortcuts dataTypeShortcuts,
                                final DataTypeStore dataTypeStore) {
        this.dataTypeList = dataTypeList;
        this.dataTypeShortcuts = dataTypeShortcuts;
        this.dataTypeStore = dataTypeStore;
    }

    @Override
    public List<DMNSearchableElement> getSearchableElements() {
        return dataTypeList
                .getItems()
                .stream()
                .map(this::makeDataTypeListItem)
                .collect(Collectors.toList());
    }

    private DMNSearchableElement makeDataTypeListItem(final DataTypeListItem item) {

        final DMNSearchableElement element = new DMNSearchableElement();

        element.setText(item.getDataType().getName());
        element.setOnFound(() -> highlight(item));

        return element;
    }

    void highlight(final DataTypeListItem item) {
        expandParents(item);
        dataTypeShortcuts.highlight(item.getDragAndDropElement());
    }

    private void expandParents(final DataTypeListItem item) {
        item.expand();
        getParent(item).ifPresent(parent -> getItem(parent).ifPresent(this::expandParents));
    }

    private Optional<DataType> getParent(final DataTypeListItem item) {
        final String parentUUID = item.getDataType().getParentUUID();
        return Optional.ofNullable(dataTypeStore.get(parentUUID));
    }

    private Optional<DataTypeListItem> getItem(final DataType parent) {
        return dataTypeList
                .getItems()
                .stream()
                .filter(item -> Objects.equals(item.getDataType().getUUID(), parent.getUUID()))
                .findAny();
    }

    @Override
    public void onNoResultsFound() {
        dataTypeShortcuts.reset();
    }
}
