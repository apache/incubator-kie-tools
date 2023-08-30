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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.Objects;
import java.util.Optional;

import elemental2.dom.Element;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_INTO_HOVERED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_NESTED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_SIBLING_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandlerShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGING;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.HIDDEN_Y_POSITION;

class DNDDataTypesHandlerContext {

    private final Element currentElement;

    private final Element hoverElement;

    private DNDDataTypesHandler dndDataTypesHandler;

    private Element previousElement;

    private DataType current;

    private DataType hovered;

    private DataType previous;

    DNDDataTypesHandlerContext(final DNDDataTypesHandler dndDataTypesHandler,
                               final Element currentElement,
                               final Element hoverElement) {

        this.dndDataTypesHandler = dndDataTypesHandler;
        this.currentElement = currentElement;
        this.hoverElement = hoverElement;
        this.current = getDataType(currentElement);
    }

    Optional<DataType> getReference() {

        if (reloadHoveredDataType().isPresent() && hoveredDataTypeIsNotReadOnly()) {
            return getHoveredDataType();
        }

        if (reloadPreviousDataType().isPresent()) {
            return getPreviousDataType();
        }

        return getCurrentDataType().flatMap(this::getFirstDataType);
    }

    DNDDataTypesHandlerShiftStrategy getStrategy() {

        if (getHoveredDataType().isPresent() && hoveredDataTypeIsNotReadOnly()) {
            return INSERT_INTO_HOVERED_DATA_TYPE;
        }

        if (!getPreviousDataType().isPresent()) {
            return INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;
        }

        final int currentElementLevel = DNDListDOMHelper.Position.getX(currentElement);
        final int previousElementLevel = DNDListDOMHelper.Position.getX(previousElement);

        if (currentElementLevel == 0) {
            return INSERT_TOP_LEVEL_DATA_TYPE;
        } else if (previousElementLevel < currentElementLevel && previousDataTypeIsNotReadOnly()) {
            return INSERT_NESTED_DATA_TYPE;
        } else {
            return INSERT_SIBLING_DATA_TYPE;
        }
    }

    private Optional<DataType> reloadHoveredDataType() {
        hovered = getDataType(hoverElement);
        return getHoveredDataType();
    }

    private Optional<DataType> reloadPreviousDataType() {
        previousElement = getPreviousElement(currentElement);
        previous = getDataType(previousElement);
        return getPreviousDataType();
    }

    private boolean previousDataTypeIsNotReadOnly() {
        return !getPreviousDataType().map(DataType::isReadOnly).orElse(false);
    }

    private boolean hoveredDataTypeIsNotReadOnly() {
        return !getHoveredDataType().map(DataType::isReadOnly).orElse(false);
    }

    Optional<DataType> getCurrentDataType() {
        return Optional.ofNullable(current);
    }

    private Optional<DataType> getHoveredDataType() {
        return Optional.ofNullable(hovered);
    }

    private Optional<DataType> getPreviousDataType() {
        return Optional.ofNullable(previous);
    }

    private DataType getDataType(final Element element) {
        return element == null ? null : dndDataTypesHandler.getDataTypeStore().get(element.getAttribute(UUID_ATTR));
    }

    private Optional<DataType> getFirstDataType(final DataType current) {

        final NodeList<Node> nodes = dndDataTypesHandler.getDndListComponent().getDragArea().childNodes;

        for (int i = 0; i < nodes.length; i++) {

            final Element element = (Element) nodes.getAt(i);
            final Integer elementY = DNDListDOMHelper.Position.getY(element);
            final Integer elementX = DNDListDOMHelper.Position.getX(element);

            if (elementY > HIDDEN_Y_POSITION && elementX == 0 && !element.classList.contains(DRAGGING)) {
                final DataType dataType = getDataType(element);
                if (dataType != null && !Objects.equals(current.getName(), dataType.getName())) {
                    return Optional.of(dataType);
                }
            }
        }

        return Optional.empty();
    }

    private Element getPreviousElement(final Element reference) {
        return dndDataTypesHandler
                .getDndListComponent()
                .getPreviousElement(reference, element -> DNDListDOMHelper.Position.getX(element) <= DNDListDOMHelper.Position.getX(reference))
                .orElse(null);
    }
}
