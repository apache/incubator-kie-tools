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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.uberfire.client.views.pfly.selectpicker.JQueryList;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;
import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;

@Dependent
public class DataTypeListShortcutsView implements DataTypeListShortcuts.View {

    private final ScrollHelper scrollHelper;

    final ListUtils utils = new ListUtils();

    private String currentUUID = "";

    private String previousUUID = "";

    private DataTypeListShortcuts presenter;

    @Inject
    public DataTypeListShortcutsView(final ScrollHelper scrollHelper) {
        this.scrollHelper = scrollHelper;
    }

    public void init(final DataTypeListShortcuts presenter) {
        this.presenter = presenter;
    }

    @Override
    public Optional<Element> getFirstDataTypeRow() {
        return utils.first(getVisibleDataTypeRows());
    }

    @Override
    public Optional<Element> getNextDataTypeRow() {

        final List<Element> visibleDataTypes = getVisibleDataTypeRows();
        final Optional<Element> next = getCurrentDataTypeRow(visibleDataTypes).flatMap(current -> utils.next(visibleDataTypes, current));

        return next.isPresent() ? next : utils.first(visibleDataTypes);
    }

    @Override
    public Optional<Element> getPrevDataTypeRow() {

        final List<Element> visibleDataTypes = getVisibleDataTypeRows();
        final Optional<Element> prev = getCurrentDataTypeRow(visibleDataTypes).flatMap(current -> utils.prev(visibleDataTypes, current));

        return prev.isPresent() ? prev : utils.last(visibleDataTypes);
    }

    @Override
    public Optional<DataTypeListItem> getCurrentDataTypeListItem() {
        return getDataTypeListItem(getCurrentUUID());
    }

    @Override
    public List<DataTypeListItem> getVisibleDataTypeListItems() {
        return getVisibleDataTypeRows()
                .stream()
                .map(this::getUUID)
                .map(this::getDataTypeListItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void highlight(final Element element) {
        setCurrentUUID(getUUID(element));
        addHighlightClass(element);
        scrollTo(element);
        highlightLevel(element);
    }

    @Override
    public void focusIn() {
        if (isEmpty(getCurrentUUID())) {
            getDataTypeListItem(getPreviousUUID()).ifPresent(listItem -> highlight(listItem.getDragAndDropElement()));
        }
    }

    void scrollTo(final Element target) {

        final int padding = 20;
        final HTMLElement container = getDataTypeList().getListItems();

        scrollHelper.scrollTo(target, container, padding);
    }

    void highlightLevel(final Element element) {
        presenter.highlightLevel(element);
    }

    public void reset() {
        cleanCurrentHighlight();
        setCurrentUUID("");
    }

    void cleanCurrentHighlight() {
        presenter.cleanLevelHighlightClass();
        presenter.cleanHighlightClass();
    }

    void addHighlightClass(final Element element) {
        presenter.highlight(element);
    }

    Optional<DataTypeListItem> getDataTypeListItem(final String uuid) {
        return getDataTypeList()
                .getItems()
                .stream()
                .filter(item -> Objects.equals(item.getDataType().getUUID(), uuid))
                .findFirst();
    }

    Optional<Element> getCurrentDataTypeRow(final List<Element> elements) {
        final String uuid = isEmpty(getCurrentUUID()) ? getPreviousUUID() : getCurrentUUID();
        return elements
                .stream()
                .filter(e -> Objects.equals(uuid, getUUID(e)))
                .findFirst();
    }

    private String getUUID(final Element element) {
        return element.getAttribute(UUID_ATTR);
    }

    String getCurrentUUID() {
        return currentUUID;
    }

    String getPreviousUUID() {
        return previousUUID;
    }

    void setCurrentUUID(final String currentUUID) {
        this.previousUUID = this.currentUUID;
        this.currentUUID = currentUUID;
    }

    private DataTypeList getDataTypeList() {
        return presenter.getDataTypeList();
    }

    private List<Element> getVisibleDataTypeRows() {

        final List<Element> elements = new ArrayList<>();
        final JQueryList<Element> filtered = filterVisible();

        for (int i = 0; i < filtered.length; i++) {
            elements.add(filtered.get(i));
        }

        return elements;
    }

    JQueryList<Element> filterVisible() {
        return $("[" + UUID_ATTR + "]").filter(":visible");
    }

    class ListUtils {

        Optional<Element> next(final List<Element> elements,
                               final Element element) {
            final int index = elements.indexOf(element);
            return index > -1 && index < elements.size() - 1 ? of(elements.get(index + 1)) : empty();
        }

        Optional<Element> prev(final List<Element> elements,
                               final Element element) {
            final int index = elements.indexOf(element);
            return index > 0 && index < elements.size() ? of(elements.get(index - 1)) : empty();
        }

        Optional<Element> first(final List<Element> elements) {
            return elements.isEmpty() ? empty() : of(elements.get(0));
        }

        Optional<Element> last(final List<Element> elements) {
            return elements.isEmpty() ? empty() : of(elements.get(elements.size() - 1));
        }
    }
}
