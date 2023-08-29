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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.databinding.client.BindableListWrapper;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;

@Dependent
public class MultipleSelectorInput<TYPE> implements IsWidget,
                                                    MultipleSelectorInputView.Presenter<TYPE> {

    private MultipleSelectorInputView<TYPE> view;
    private LiveSearchDropDown<TYPE> selector;
    private LiveSearchService<TYPE> searchService;
    private MultipleLiveSearchSelectionHandler<TYPE> selectionHandler;

    @Inject
    public MultipleSelectorInput(MultipleSelectorInputView view,
                                 LiveSearchDropDown<TYPE> selector) {
        this.view = view;
        this.selector = selector;

        view.setPresenter(this);
    }

    public void init(LiveSearchService<TYPE> searchService,
                     MultipleLiveSearchSelectionHandler<TYPE> searchSelectionHandler) {
        this.searchService = searchService;
        this.selectionHandler = searchSelectionHandler;
        selector.init(searchService,
                      selectionHandler);
        selector.setOnChange(() -> setValue(selectionHandler.getSelectedValues(),
                                            true));
    }

    @Override
    public void setValue(List<TYPE> values) {
        setValue(values,
                 false);
    }

    @Override
    public void setValue(List<TYPE> values,
                         boolean fireEvents) {
        if (values != null) {

            if(values instanceof BindableListWrapper) {
                values = ((BindableListWrapper<TYPE>)values).deepUnwrap();
            }

            if (!selectionHandler.getSelectedValues().equals(values)) {
                selector.clearSelection();
                values.stream().forEach(value -> selector.setSelectedItem(value));
            }

            if (fireEvents) {
                ValueChangeEvent.fire(this,
                                      values);
            }
        }
    }

    @Override
    public List<TYPE> getValue() {
        return selectionHandler.getSelectedValues();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<TYPE>> handler) {
        return view.asWidget().addHandler(handler,
                                          ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        asWidget().fireEvent(event);
    }

    @Override
    public IsWidget getSelector() {
        return selector;
    }

    public void setMaxItems(Integer maxItems) {
        selector.setMaxItems(maxItems);
    }

    public void setEnabled(boolean enabled) {
        selector.setEnabled(enabled);
    }

    public void setFilterEnabled(Boolean allowFilter) {
        selector.setSearchEnabled(allowFilter);
    }

    public void setClearSelectionEnabled(Boolean allowClearSelection) {
        selector.setClearSelectionEnabled(allowClearSelection);
    }
}
