/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer.export.wizard.widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.NodeList;
import org.dashbuilder.client.cms.screen.util.DomFactory;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElemental;

@Dependent
@Templated
public class AssetsTableView implements UberElemental<AssetsTableView.Presenter> {

    @Inject
    DomFactory domFactory;

    @Inject
    @DataField
    HTMLDivElement assetTableWidgetRoot;

    @Inject
    @DataField
    HTMLInputElement searchAssets;

    @Inject
    @DataField
    HTMLTableElement assetsTable;

    @Inject
    @DataField
    HTMLInputElement selectAllAssets;

    @Inject
    @DataField
    HTMLTableRowElement assetsTableHeaderRow;

    Presenter presenter;

    public interface Presenter<T> {

        List<T> getData();

        List<T> getSelectedData();

        String[] getHeaders();

        String[] toRow(T t);

        void setData(List<T> data);

        default String[][] rows() {
            List<T> data = getData();
            String[][] rows = new String[data.size()][];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = toRow(data.get(i));
            }
            return rows;
        }
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        buildHeaders(presenter.getHeaders());
    }

    public void update() {
        assetsTable.tBodies.getAt(0).innerHTML = "";
        selectAllAssets.checked = true;
        Arrays.stream(presenter.rows())
              .map(this::toRow)
              .forEach(this::appendRow);
    }

    public List<?> getSelectedAssets() {
        List<?> data = presenter.getData();
        HTMLCollection<HTMLTableRowElement> rows = assetsTable.tBodies.getAt(0).rows;
        return IntStream.range(0, rows.getLength())
                        .filter(i -> {
                            Element checkBox = rows.getAt(i).querySelector("td > input[type=checkbox]");
                            return ((HTMLInputElement) checkBox).checked;
                        }).mapToObj(data::get)
                        .collect(Collectors.toList());
    }

    @Override
    public HTMLElement getElement() {
        return assetTableWidgetRoot;
    }

    @EventHandler("selectAllAssets")
    void selectAll(ClickEvent event) {
        allInputsForTable().forEach(chk -> chk.checked = selectAllAssets.checked);
    }

    @EventHandler("searchAssets")
    void onFilter(KeyUpEvent keyDown) {
        filterTable();
    }

    public void clearFilter() {
        searchAssets.value = "";
        filterTable();
    }

    void appendRow(HTMLTableRowElement row) {
        assetsTable.tBodies.getAt(0).appendChild(row);
    }

    HTMLTableRowElement toRow(String[] cells) {
        HTMLTableRowElement row = domFactory.tableRow();

        // first cell is always a check box
        row.appendChild(createRowSelectorCell());

        Arrays.stream(cells)
              .map(this::createCell)
              .forEach(row::appendChild);
        return row;
    }

    void filterTable() {
        String query = searchAssets.value.trim().toLowerCase();
        HTMLCollection<HTMLTableRowElement> rows = assetsTable.tBodies.getAt(0).rows;
        IntStream.range(0, rows.getLength()).mapToObj(rows::getAt).forEach(row -> {
            row.hidden = false;
            if (!query.isEmpty()) {
                row.hidden = IntStream.range(0, row.cells.getLength())
                                      .mapToObj(row.cells::getAt)
                                      .noneMatch(c -> c.textContent.toLowerCase().contains(query));
            }

        });
    }

    private HTMLInputElement createCheckBox() {
        HTMLInputElement checkbox = domFactory.input();
        checkbox.type = "checkbox";
        checkbox.checked = true;
        return checkbox;
    }

    private HTMLTableCellElement createCell(String content) {
        HTMLTableCellElement cell = domFactory.tableCell();
        cell.innerHTML = content;
        return cell;
    }

    private Element createHeaderCell(String content) {
        Element cell = domFactory.element("th");
        cell.innerHTML = content;
        return cell;
    }

    private HTMLTableCellElement createRowSelectorCell() {
        HTMLTableCellElement tableCell = domFactory.tableCell();
        HTMLInputElement rowChk = createCheckBox();
        rowChk.onclick = e -> {
            selectAllAssets.checked = allInputsForTable().allMatch(input -> rowChk.checked);
            return null;
        };
        tableCell.appendChild(rowChk);
        return tableCell;
    }

    private Stream<HTMLInputElement> allInputsForTable() {
        // asArray throws cast exception
        NodeList<Element> items = assetsTable.querySelectorAll("tbody > tr > td:first-of-type > input[type=checkbox]");
        return IntStream.range(0, items.getLength()).mapToObj(i -> (HTMLInputElement) items.getAt(i));
    }

    private void buildHeaders(String[] headers) {
        Arrays.stream(headers)
              .map(this::createHeaderCell)
              .forEach(assetsTableHeaderRow::appendChild);
    }

}