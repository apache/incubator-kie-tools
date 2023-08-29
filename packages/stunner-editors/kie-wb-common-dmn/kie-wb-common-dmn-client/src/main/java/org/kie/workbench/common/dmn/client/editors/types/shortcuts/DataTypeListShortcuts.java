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

package org.kie.workbench.common.dmn.client.editors.types.shortcuts;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.uberfire.client.mvp.HasPresenter;

public class DataTypeListShortcuts {

    private DataTypeList dataTypeList;

    private final View view;

    private final DataTypeUtils dataTypeUtils;

    @Inject
    public DataTypeListShortcuts(final View view,
                                 final DataTypeUtils dataTypeUtils) {
        this.view = view;
        this.dataTypeUtils = dataTypeUtils;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void init(final DataTypeList dataTypeList) {
        this.dataTypeList = dataTypeList;
        this.dataTypeList.registerDataTypeListItemUpdateCallback(this::onDataTypeListItemUpdate);
    }

    void onArrowDown() {
        view.getNextDataTypeRow().ifPresent(view::highlight);
    }

    void onArrowUp() {
        view.getPrevDataTypeRow().ifPresent(view::highlight);
    }

    void onTab() {
        view.getFirstDataTypeRow().ifPresent(view::highlight);
    }

    void onArrowLeft() {
        getCurrentDataTypeListItem().ifPresent(DataTypeListItem::collapse);
    }

    void onArrowRight() {
        getCurrentDataTypeListItem().ifPresent(DataTypeListItem::expand);
    }

    void onCtrlE() {
        consumeIfDataTypeIsNotReadOnly(DataTypeListItem::enableEditMode);
    }

    void onEscape() {

        final Optional<DataTypeListItem> currentDataTypeListItem = getCurrentDataTypeListItem();

        if (currentDataTypeListItem.isPresent()) {
            final DataTypeListItem dataTypeListItem = currentDataTypeListItem.get();
            dataTypeListItem.disableEditMode();
            highlight(dataTypeListItem.getDragAndDropElement());
        } else {
            getVisibleDataTypeListItems().forEach(DataTypeListItem::disableEditMode);
            reset();
        }
    }

    void onCtrlBackspace() {
        consumeIfDataTypeIsNotReadOnly(DataTypeListItem::remove);
    }

    void onCtrlS() {
        consumeIfDataTypeIsNotReadOnly(DataTypeListItem::saveAndCloseEditMode);
    }

    void onCtrlB() {
        consumeIfDataTypeIsNotReadOnly(DataTypeListItem::addDataTypeRow);
    }

    void onCtrlU() {
        consumeIfDataTypeIsNotReadOnly(DataTypeListItem::insertFieldAbove);
    }

    void onCtrlD() {
        consumeIfDataTypeIsNotReadOnly(DataTypeListItem::insertFieldBelow);
    }

    DataTypeList getDataTypeList() {
        return dataTypeList;
    }

    private List<DataTypeListItem> getVisibleDataTypeListItems() {
        return view.getVisibleDataTypeListItems();
    }

    private Optional<DataTypeListItem> getCurrentDataTypeListItem() {
        return view.getCurrentDataTypeListItem();
    }

    void focusIn() {
        view.focusIn();
    }

    public void reset() {
        view.reset();
    }

    private void onDataTypeListItemUpdate(final DataTypeListItem dataTypeListItem) {
        view.highlight(dataTypeListItem.getDragAndDropElement());
    }

    private void consumeIfDataTypeIsNotReadOnly(final Consumer<DataTypeListItem> dataTypeListItemConsumer) {
        getCurrentDataTypeListItem().ifPresent(dataTypeListItem -> {
            if (!dataTypeListItem.isReadOnly()) {
                dataTypeListItemConsumer.accept(dataTypeListItem);
            }
        });
    }

    DataType getTopLevelParent(final DataType dataType) {
        return dataTypeUtils.getTopLevelParent(dataType);
    }

    void highlight(final Element element) {
        dataTypeList.highlight(element);
    }

    void highlightLevel(final Element element) {
        dataTypeList.highlightLevel(element);
    }

    void cleanLevelHighlightClass() {
        dataTypeList.cleanLevelHighlightClass();
    }

    void cleanHighlightClass() {
        dataTypeList.cleanHighlightClass();
    }

    public interface View extends HasPresenter<DataTypeListShortcuts> {

        void reset();

        List<DataTypeListItem> getVisibleDataTypeListItems();

        Optional<DataTypeListItem> getCurrentDataTypeListItem();

        Optional<Element> getFirstDataTypeRow();

        Optional<Element> getNextDataTypeRow();

        Optional<Element> getPrevDataTypeRow();

        void highlight(final Element element);

        void focusIn();
    }
}
