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

package org.kie.workbench.common.dmn.client.editors.types.imported;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLabelElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeList;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem;

@Dependent
@Templated
public class ImportDataObjectModalView implements ImportDataObjectModal.View {

    static final String OPENED_CONTAINER_CSS_CLASS = "opened";

    @DataField("header")
    private final HTMLDivElement header;

    @DataField("body")
    private final HTMLDivElement body;

    @DataField("footer")
    private final HTMLDivElement footer;

    @DataField("note-text")
    private final HTMLElement noteText;

    @DataField("note-label")
    private final HTMLLabelElement noteLabel;

    @DataField("items-container")
    private final HTMLDivElement itemsContainer;

    @DataField("clear-selection")
    private final HTMLAnchorElement clearSelection;

    @DataField("button-cancel")
    private final HTMLButtonElement buttonCancel;

    @DataField("button-import")
    private final HTMLButtonElement buttonImport;

    @DataField("warning-container")
    private final HTMLDivElement warningContainer;

    private final TreeList treeList;

    private ImportDataObjectModal presenter;

    private final ManagedInstance<TreeListItem> items;

    @Inject
    public ImportDataObjectModalView(final HTMLDivElement header,
                                     final HTMLDivElement body,
                                     final HTMLDivElement footer,
                                     final TreeList treeList,
                                     @Named("span") final HTMLElement noteText,
                                     final HTMLLabelElement noteLabel,
                                     final HTMLDivElement itemsContainer,
                                     final HTMLAnchorElement clearSelection,
                                     final ManagedInstance<TreeListItem> items,
                                     final HTMLButtonElement buttonImport,
                                     final HTMLButtonElement buttonCancel,
                                     final HTMLDivElement warningContainer) {
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.treeList = treeList;
        this.noteText = noteText;
        this.noteLabel = noteLabel;
        this.itemsContainer = itemsContainer;
        this.clearSelection = clearSelection;
        this.items = items;
        this.buttonImport = buttonImport;
        this.buttonCancel = buttonCancel;
        this.warningContainer = warningContainer;
    }

    @PostConstruct
    public void setup() {
        treeList.setOnSelectionChanged(getOnSelectionChanged());
    }

    Consumer<List<TreeListItem>> getOnSelectionChanged() {
        return this::onSelectionChanged;
    }

    void onSelectionChanged(final List<TreeListItem> treeListItems) {
        final List<DataObject> selectedItems = treeListItems.stream()
                .map(item -> item.getDataSource())
                .collect(Collectors.toList());
        presenter.onDataObjectSelectionChanged(selectedItems);
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }

    @Override
    public void init(final ImportDataObjectModal presenter) {
        this.presenter = presenter;
    }

    @EventHandler("button-cancel")
    void onButtonCancelClicked(final ClickEvent e) {
        presenter.hide();
    }

    @EventHandler("button-import")
    void onButtonImportClicked(final ClickEvent e) {

        final List<DataObject> selectedItems = getSelectedItems();

        presenter.hide(selectedItems);
    }

    List<DataObject> getSelectedItems() {
        return treeList.getSelectedItems().stream()
                .map(item -> item.getDataSource())
                .collect(Collectors.toList());
    }

    @EventHandler("clear-selection")
    void onClearSelectionClicked(final ClickEvent e) {

        treeList.clearSelection();
        refresh();
    }

    @Override
    public void addItems(final List<DataObject> dataObjects) {

        final List<TreeListItem> dataObjectItems = new ArrayList<>();
        for (final DataObject data : dataObjects) {
            dataObjectItems.add(createTreeListItem(data));
        }
        treeList.populate(dataObjectItems);
        itemsContainer.appendChild(treeList.getElement());
    }

    TreeListItem createTreeListItem(final DataObject data) {
        final TreeListItem item = items.get();
        item.setDataSource(data);
        item.setDescription(data.getClassType());
        return item;
    }

    void refresh() {

        removeTreeList();
        treeList.refresh();
        itemsContainer.appendChild(treeList.getElement());
    }

    @Override
    public void clear() {

        removeTreeList();
        treeList.clear();
    }

    @Override
    public void showDataTypeWithSameNameWarning() {
        warningContainer.classList.add(OPENED_CONTAINER_CSS_CLASS);
    }

    @Override
    public void hideDataTypeWithSameNameWarning() {
        warningContainer.classList.remove(OPENED_CONTAINER_CSS_CLASS);
    }

    void removeTreeList() {

        if (itemsContainer.contains(treeList.getElement())) {
            itemsContainer.removeChild(treeList.getElement());
        }
    }
}
