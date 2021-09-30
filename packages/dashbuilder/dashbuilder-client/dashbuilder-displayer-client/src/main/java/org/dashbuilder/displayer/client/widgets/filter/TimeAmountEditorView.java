/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.filter;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.i18n.DateIntervalTypeConstants;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.widgets.common.client.common.NumericLongTextBox;

@Dependent
public class TimeAmountEditorView extends Composite implements TimeAmountEditor.View {

    interface Binder extends UiBinder<Widget, TimeAmountEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    TimeAmountEditor presenter = null;

    @UiField
    NumericLongTextBox input;

    @UiField
    InputGroupAddon minusIcon;

    @UiField
    InputGroupAddon plusIcon;

    @UiField
    ListBox typeList;

    @Override
    public void init(final TimeAmountEditor presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));

        plusIcon.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                presenter.increaseQuantity();
            }
        }, ClickEvent.getType());

        minusIcon.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                presenter.decreaseQuantity();
            }
        }, ClickEvent.getType());

        input.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.changeQuantity(event.getValue());
            }
        });
    }

    @Override
    public void setQuantity(long quantity) {
        input.setValue(Long.toString(quantity));
    }

    @Override
    public long getQuantity() {
        return Long.valueOf(input.getValue());
    }

    @Override
    public void clearIntervalTypeSelector() {
        typeList.clear();
    }

    @Override
    public void addIntervalTypeItem(DateIntervalType type) {
        String typeName = DateIntervalTypeConstants.INSTANCE.getString(type.name());
        typeList.addItem(typeName);
    }

    @Override
    public void setSelectedTypeIndex(int idx) {
        typeList.setSelectedIndex(idx);
    }

    @Override
    public int getSelectedTypeIndex() {
        return typeList.getSelectedIndex();
    }

    // UI events

    @UiHandler(value = "typeList")
    public void onFilterSelected(ChangeEvent changeEvent) {
        presenter.changeIntervalType();
    }
}
