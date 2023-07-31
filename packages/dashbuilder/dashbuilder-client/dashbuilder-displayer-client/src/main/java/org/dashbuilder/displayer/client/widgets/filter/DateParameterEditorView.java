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
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.CSSProperties.WidthUnionType;
import org.dashbuilder.patternfly.date.DatePicker;

// TODO: create new view
@Dependent
public class DateParameterEditorView extends Composite implements DateParameterEditor.View {

    DateParameterEditor presenter;

    @Inject
    DatePicker input;

    protected boolean show = false;

    @Override
    public void init(final DateParameterEditor presenter) {
        this.presenter = presenter;
        input.addValueChangeHandler(
                presenter::onChange);
        input.addBlurHandler(
                presenter::onBlur);
        input.addShowHandler(() -> {
            presenter.onFocus();
            show = true;
        });
        input.addHideHandler(() -> {
            show = false;
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
        input.getElement().style.width = WidthUnionType.of(width + "px");
    }
}
