/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.patternfly.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class Table {

    private static final int DEFAULT_PAGE_SIZE = 20;
    @Inject
    View view;

    private Value[][] data;
    private int pageSize;
    private String search;
    private BiConsumer<String, Integer> onCellSelectedListener;

    public interface View extends UberElemental<Table> {

        void setTitle(String title);

        void setData(Value[][] pagedData);

        void setColumns(List<String> columns);

        void setPagination(int nRows, int nPages);

        void setSelectable(boolean selectable);

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setOnCellSelectedListener(BiConsumer<String, Integer> onCellSelectedListener) {
        this.onCellSelectedListener = onCellSelectedListener;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setTitle(String title) {
        view.setTitle(title);
    }

    public void buildTable(List<String> columns,
                           String[][] data,
                           int pageSize) {
        this.data = buildData(data);
        this.pageSize = pageSize > 1 ? pageSize : DEFAULT_PAGE_SIZE;
        view.setColumns(columns);
        view.setPagination(data.length, this.pageSize);
        showPage(1);
    }

    private Value[][] buildData(String[][] data) {
        var dataValue = new Value[data.length][];
        for (int i = 0; i < data.length; i++) {
            dataValue[i] = new Value[data[i].length];
            for (int j = 0; data[i] != null && j < data[i].length; j++) {
                dataValue[i][j] = new Value(i, data[i][j]);
            }
        }
        return dataValue;
    }

    public void showPage(int page) {
        if (data == null || data.length == 0 || data[0] == null) {
            return;
        }
        var filteredData = filter();
        var begin = pageSize * (page - 1);
        var end = pageSize * page;

        if (begin < 0) {
            begin = 0;
        }

        if (end > data.length) {
            end = data.length;
        }

        var pagedData = Arrays.copyOfRange(filteredData, begin, end);
        view.setData(pagedData);
    }

    public void onFilterChange(String value) {
        this.search = value;
        showPage(1);
    }

    void onCellSelected(String column, int i) {
        if (onCellSelectedListener != null) {
            onCellSelectedListener.accept(column, i);
        }

    }

    Value[][] filter() {
        // isBlank do not work with current GWT version
        if (search == null || search.trim().isEmpty()) {
            return data;
        }
        var filteredData = new ArrayList<Value[]>();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j].toString().toLowerCase().contains(search.toLowerCase())) {
                    filteredData.add(data[i]);
                    break;
                }
            }
        }
        if (filteredData.isEmpty()) {
            return new Value[0][0];
        }
        var result = new Value[filteredData.size()][];
        filteredData.toArray(result);
        return result;
    }

    public void setSelectable(boolean selectable) {
        view.setSelectable(selectable);
    }

    class Value {

        int index;
        String value;

        public Value(int index, String value) {
            super();
            this.index = index;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

}
