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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.Optional;

import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;

public class DNDDataTypesHandler {

    private final DataTypeStore dataTypeStore;

    private final DataTypeManager dataTypeManager;

    private final ItemDefinitionStore itemDefinitionStore;

    private DataTypeList dataTypeList;

    @Inject
    public DNDDataTypesHandler(final DataTypeStore dataTypeStore,
                               final DataTypeManager dataTypeManager,
                               final ItemDefinitionStore itemDefinitionStore) {
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
        this.itemDefinitionStore = itemDefinitionStore;
    }

    public void init(final DataTypeList dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public void onDropDataType(final Element currentElement,
                               final Element hoverElement) {
        try {

            final DNDDataTypesHandlerContext dndContext = makeDndContext(currentElement, hoverElement);
            final Optional<DataType> current = dndContext.getCurrentDataType();
            final Optional<DataType> reference = dndContext.getReference();

            if (current.isPresent() && reference.isPresent()) {
                shiftCurrentByReference(current.get(), reference.get(), dndContext.getStrategy());
            }
        } catch (final Exception e) {
            logError("Drag-n-Drop error (" + e.getMessage() + "). Check '" + DNDDataTypesHandler.class.getSimpleName() + "'.");
        }
    }

    void shiftCurrentByReference(final DataType current,
                                 final DataType reference,
                                 final DNDDataTypesHandlerShiftStrategy shiftStrategy) {

        final String referenceHash = getDataTypeList().calculateHash(reference);
        final DataType clone = cloneDataType(current);
        final Optional<DataTypeListItem> currentItem = getDataTypeList().findItem(current);
        final boolean isCurrentItemCollapsed = currentItem.map(DataTypeListItem::isCollapsed).orElse(false);

        // destroy current data type
        currentItem.ifPresent(item -> {
            if (isTopLevelShiftOperation(current, shiftStrategy)) {
                item.destroyWithoutDependentTypes();
            } else {
                item.destroyWithDependentTypes();
            }
        });

        // create new data type by using shift strategy
        getDataTypeList().findItemByDataTypeHash(referenceHash).ifPresent(ref -> {
            shiftStrategy.getConsumer().accept(ref, clone);
        });

        // keep the state of the new data type item consistent
        getDataTypeList().findItem(clone).ifPresent(item -> {
            if (isCurrentItemCollapsed) {
                item.collapse();
            } else {
                item.expand();
            }
        });
    }

    public void deleteKeepingReferences(final DataType existing) {

        final Optional<DataTypeListItem> currentItem = getDataTypeList().findItem(existing);
        currentItem.ifPresent(item -> {
            item.destroyWithoutDependentTypes();
        });
    }

    boolean isTopLevelShiftOperation(final DataType dataType,
                                     final DNDDataTypesHandlerShiftStrategy shiftStrategy) {
        final boolean isCurrentTopLevel = dataType.isTopLevel();
        final boolean isTopLevelShiftStrategy = shiftStrategy == INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP || shiftStrategy == INSERT_TOP_LEVEL_DATA_TYPE;
        return isCurrentTopLevel && isTopLevelShiftStrategy;
    }

    DataType cloneDataType(final DataType current) {

        final String currentUUID = current.getUUID();
        final ItemDefinition itemDefinition = itemDefinitionStore.get(currentUUID);

        return dataTypeManager.from(itemDefinition).get();
    }

    DNDDataTypesHandlerContext makeDndContext(final Element currentElement,
                                              final Element hoverElement) {
        return new DNDDataTypesHandlerContext(this, currentElement, hoverElement);
    }

    private DataTypeList getDataTypeList() {
        return Optional
                .ofNullable(dataTypeList)
                .orElseThrow(() -> {
                    final String errorMessage = "'DNDDataTypesHandler' must be initialized with a 'DataTypeList' instance.";
                    return new UnsupportedOperationException(errorMessage);
                });
    }

    DataTypeStore getDataTypeStore() {
        return dataTypeStore;
    }

    DNDListComponent getDndListComponent() {
        return Optional
                .ofNullable(getDataTypeList().getDNDListComponent())
                .orElseThrow(() -> {
                    final String errorMessage = "'DNDDataTypesHandler' must be initialized with a 'DNDListComponent' instance.";
                    return new UnsupportedOperationException(errorMessage);
                });
    }

    void logError(final String message) {
        DomGlobal.console.error(message);
    }
}
