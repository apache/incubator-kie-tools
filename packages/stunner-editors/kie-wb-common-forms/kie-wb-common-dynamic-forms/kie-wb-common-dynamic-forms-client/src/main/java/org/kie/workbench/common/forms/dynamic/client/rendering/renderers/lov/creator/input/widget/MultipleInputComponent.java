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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent.Action.ADD;
import static org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent.Action.REFRESH;
import static org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent.Action.REMOVE;

@Dependent
public class MultipleInputComponent<TYPE> implements MultipleInputComponentView.Presenter<TYPE>,
                                                     IsElement {

    private MultipleInputComponentView<TYPE> view;

    private EditableColumnGeneratorManager columnGeneratorManager;

    private String modelType;

    private AsyncDataProvider<TableEntry<TYPE>> dataProvider;

    protected List<TableEntry<TYPE>> selectedValues = new ArrayList<>();

    protected List<TableEntry<TYPE>> tableValues = new ArrayList<>();

    private int pageSize = 10;

    private boolean readOnly = false;

    private Command valueChangedCommand;


    @Inject
    public MultipleInputComponent(MultipleInputComponentView view,
                                  EditableColumnGeneratorManager columnGeneratorManager) {
        this.view = view;
        this.columnGeneratorManager = columnGeneratorManager;
    }

    @PostConstruct
    public void init() {
        doInit();
    }

    protected void doInit() {
        dataProvider = new AsyncDataProvider<TableEntry<TYPE>>() {
            @Override
            protected void onRangeChanged(HasData<TableEntry<TYPE>> display) {
                if (tableValues != null) {
                    updateRowCount(tableValues.size(),
                                   true);
                    updateRowData(0,
                                  tableValues);
                } else {
                    updateRowCount(0,
                                   true);
                    updateRowData(0,
                                  new ArrayList<>());
                }
            }
        };

        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void init(String modelType) {
        this.modelType = modelType;
        view.render();
        refresh(REFRESH);
    }

    @Override
    public EditableColumnGenerator<TYPE> getColumnGenerator() {
        return columnGeneratorManager.getGenerator(modelType);
    }

    @Override
    public void notifyChange(int index,
                             TYPE value) {
        tableValues.get(index).setValue(value);

        refresh(REFRESH);

        notifyValueChanged();
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void selectValue(TableEntry<TYPE> entry) {
        if (!selectedValues.contains(entry)) {
            selectedValues.add(entry);
        } else {
            selectedValues.remove(entry);
        }

        view.enableRemoveButton(!selectedValues.isEmpty());

        maybeEnablePromote();
        maybeEnableDegrade();
    }

    protected void maybeEnablePromote() {
        boolean enable = !selectedValues.isEmpty() && tableValues.size() > 1;

        if (enable) {
            if (selectedValues.size() == 1) {
                if (tableValues.indexOf(selectedValues.get(0)) == 0) {
                    enable = false;
                }
            }
        }

        view.enablePromoteButton(enable);
    }

    protected void maybeEnableDegrade() {
        boolean enable = !selectedValues.isEmpty() && tableValues.size() > 1;

        if (enable) {
            if (selectedValues.size() == 1) {
                if (tableValues.indexOf(selectedValues.get(0)) == tableValues.size() - 1) {
                    enable = false;
                }
            }
        }

        view.enableDegradeButton(enable);
    }

    @Override
    public void promoteSelectedValues() {
        Set<Integer> indexes = getSelectedIndexes(Comparator.naturalOrder());

        indexes.forEach(index -> {
            if (index > 0) {

                TableEntry<TYPE> previous = tableValues.get(index -1);

                if(!isSelected(previous)) {
                    Collections.swap(tableValues,
                                     index,
                                     index - 1);
                }
            }
        });

        refresh(REFRESH);

        maybeEnablePromote();
        maybeEnableDegrade();

        notifyValueChanged();
    }

    @Override
    public void degradeSelectedValues() {
        Set<Integer> indexes = getSelectedIndexes(Comparator.reverseOrder());

        indexes.forEach(index -> {
            if (index < tableValues.size() - 1) {

                TableEntry<TYPE> nextElement = tableValues.get(index + 1);

                if(!isSelected(nextElement)) {
                    Collections.swap(tableValues,
                                     index,
                                     index + 1);
                }
            }
        });

        refresh(REFRESH);

        maybeEnablePromote();
        maybeEnableDegrade();

        notifyValueChanged();
    }

    @Override
    public void removeSelectedValues() {
        Set<Integer> indexes = getSelectedIndexes(Comparator.reverseOrder());

        indexes.forEach(index -> {
            TableEntry<TYPE> entry = tableValues.get(index);
            tableValues.remove(entry);
        });

        selectedValues.clear();

        view.enableRemoveButton(false);

        refresh(REMOVE);

        maybeEnablePromote();
        maybeEnableDegrade();

        notifyValueChanged();
    }

    private Set<Integer> getSelectedIndexes(final Comparator comparator) {
        return selectedValues.stream()
                .map(entry -> tableValues.indexOf(entry))
                .collect(Collectors.toCollection((Supplier<TreeSet<Integer>>) () -> new TreeSet(comparator)));
    }

    @Override
    public Boolean isSelected(TableEntry<TYPE> object) {
        return selectedValues.contains(object);
    }

    public List<TYPE> getValues() {
        return tableValues.stream()
                .map(TableEntry::getValue)
                .collect(Collectors.toList());
    }

    public void setValues(List<TYPE> values) {
        selectedValues.clear();
        tableValues.clear();
        tableValues = values.stream()
                .map(value -> new TableEntry<>(value))
                .collect(Collectors.toList());

        doInit();
        init(modelType);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        view.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    enum Action {
        REFRESH,
        ADD,
        REMOVE
    }

    @Override
    public void newElement() {
        tableValues.add(new TableEntry());

        refresh(ADD);

        notifyValueChanged();
    }

    protected void notifyValueChanged() {
        if (valueChangedCommand != null) {
            valueChangedCommand.execute();
        }
    }

    protected void refresh(Action action) {
        int currentStart = view.getCurrentPage();
        if (currentStart < 0) {
            currentStart = 0;
        }
        if (REMOVE.equals(action)) {
            if (currentStart > tableValues.size()) {
                currentStart -= view.getPageSize();
            }
        }
        if (ADD.equals(action)) {
            if (tableValues.size() > view.getPageSize() && tableValues.size() % view.getPageSize() == 1) {
                currentStart = tableValues.size() - 1;
            }
        }

        dataProvider.updateRowCount(tableValues.size(),
                                    true);
        dataProvider.updateRowData(currentStart,
                                   tableValues);

        final HasData<TableEntry<TYPE>> next = dataProvider.getDataDisplays().iterator().next();
        next.setVisibleRangeAndClearData(next.getVisibleRange(),
                                         true);
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public AbstractDataProvider<TableEntry<TYPE>> getProvider() {
        return dataProvider;
    }

    public void setValueChangedCommand(Command valueChangedCommand) {
        this.valueChangedCommand = valueChangedCommand;
    }
}
