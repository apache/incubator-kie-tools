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

import java.util.Date;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

@Dependent
public class DateParameterEditorView extends Composite implements DateParameterEditor.View {

    interface Binder extends UiBinder<Widget, DateParameterEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    DateParameterEditor presenter;

    @UiField
    DatePicker input;

    @UiField
    Button icon;

    protected boolean show = false;

    public DateParameterEditorView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final DateParameterEditor presenter) {
        this.presenter = presenter;
        input.addValueChangeHandler(e -> {
            presenter.onChange();
        });
        input.addBlurHandler(e -> {
            presenter.onBlur();
        });
        input.addShowHandler(e -> {
            presenter.onFocus();
            show = true;
        });
        input.addHideHandler(e -> {
            show = false;
        });
        icon.addClickHandler(e -> {
            if (!show) {
                input.onShow(null);
            }
        });
    }

    @Override
    public Date getValue() {
        return input.getValue();
    }

    @Override
    public void setValue(Date value) {
        input.setValue(value);
    }

    @Override
    public void setWidth(int width) {
        input.asWidget().getElement().getStyle().setWidth(width, Style.Unit.PX);
    }
}
