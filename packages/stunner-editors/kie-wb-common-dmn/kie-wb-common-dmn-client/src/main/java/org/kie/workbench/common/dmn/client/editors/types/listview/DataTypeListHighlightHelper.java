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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper;

import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;

public class DataTypeListHighlightHelper {

    static final String HIGHLIGHT = "key-highlight";

    static final String LEVEL_HIGHLIGHT = "kie-level-highlight";

    static final String LEVEL_BACKGROUND_LINE = "kie-level-background-line";

    private final DataTypeUtils dataTypeUtils;

    private DataTypeList dataTypeList;

    @Inject
    public DataTypeListHighlightHelper(final DataTypeUtils dataTypeUtils) {
        this.dataTypeUtils = dataTypeUtils;
    }

    public void init(final DataTypeList dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public void highlightLevel(final Element element) {
        getDataTypeListItem(getUUID(element)).ifPresent(listItem -> {
            cleanLevelHighlightClass();
            highlightLevel(dataTypeUtils.getTopLevelParent(listItem.getDataType()));
        });
    }

    public void highlight(final Element element) {
        cleanHighlightClass();
        element.classList.add(HIGHLIGHT);
    }

    public void cleanHighlightClass() {
        cleanCSSClass(HIGHLIGHT);
    }

    public void cleanLevelHighlightClass() {
        cleanBackgroundLine();
        cleanCSSClass(LEVEL_HIGHLIGHT);
    }

    Optional<DataTypeListItem> getDataTypeListItem(final String uuid) {
        return getDataTypeList()
                .getItems()
                .stream()
                .filter(item -> Objects.equals(item.getDataType().getUUID(), uuid))
                .findFirst();
    }

    NodeList<Element> querySelectorAll(final String selector) {
        return getDataTypeList().getElement().querySelectorAll(selector);
    }

    void highlightLevel(final DataType dataType) {

        getDataTypeListItem(dataType.getUUID()).ifPresent(listItem -> {

            final HTMLElement element = listItem.getDragAndDropElement();
            final List<DataType> subDataTypes = dataType.getSubDataTypes();
            final boolean topLevel = dataType.isTopLevel();

            highlightLevel(listItem);
            subDataTypes.forEach(this::highlightLevel);

            if (topLevel && !hasBackgroundLine(element)) {
                appendBackgroundLine(dataType, element);
            }
        });
    }

    void appendBackgroundLine(final DataType dataType,
                              final HTMLElement element) {

        final int numberOfDataTypes = numberOfSubDataTypes(dataType) + numberOfDraggingElements();
        final Element backgroundLine = createBackgroundLine();
        final int dataTypeRow = (int) element.offsetHeight;
        final int lineHeight = dataTypeRow * numberOfDataTypes;

        element.appendChild(backgroundLine);

        backgroundLine.setAttribute("style", "height: " + lineHeight + "px");
    }

    private int numberOfDraggingElements() {
        return (int) querySelectorAll("." + DNDListDOMHelper.DRAGGING).length;
    }

    private Element createBackgroundLine() {
        final Element line = createElement("div");
        line.classList.add(LEVEL_BACKGROUND_LINE);
        return line;
    }

    Element createElement(final String tagName) {
        return DomGlobal.document.createElement(tagName);
    }

    boolean hasBackgroundLine(final HTMLElement element) {
        return element.querySelector("." + LEVEL_BACKGROUND_LINE) != null;
    }

    private void cleanBackgroundLine() {
        final NodeList<Element> elementNodeList = querySelectorAll("." + LEVEL_BACKGROUND_LINE);
        for (int i = 0; i < elementNodeList.length; i++) {
            final Element e = elementNodeList.getAt(i);
            e.parentNode.removeChild(e);
        }
    }

    private int numberOfSubDataTypes(final DataType dataType) {
        final boolean isExpanded = !getDataTypeListItem(dataType.getUUID()).map(DataTypeListItem::isCollapsed).orElse(false);
        if (isExpanded) {
            return dataType.getSubDataTypes().stream().map(this::numberOfSubDataTypes).reduce(1, Integer::sum);
        } else {
            return 1;
        }
    }

    private void highlightLevel(final DataTypeListItem listItem) {
        final HTMLElement element = listItem.getDragAndDropElement();
        element.classList.add(LEVEL_HIGHLIGHT);
    }

    private String getUUID(final Element element) {
        return element.getAttribute(UUID_ATTR);
    }

    private void cleanCSSClass(final String cssClass) {
        final NodeList<Element> highlightedElements = querySelectorAll("." + cssClass);
        for (int i = 0; i < highlightedElements.length; i++) {
            highlightedElements.getAt(i).classList.remove(cssClass);
        }
    }

    public DataTypeList getDataTypeList() {
        return Optional.ofNullable(dataTypeList).orElseThrow(() -> {
            final String errorMessage = "DataTypeListHighlightHelper error 'dataTypeList' must be initialized.";
            return new UnsupportedOperationException(errorMessage);
        });
    }
}
