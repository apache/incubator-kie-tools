/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Legend;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

import javax.enterprise.context.Dependent;

/**
 * <p>View implementation for creating new entities.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class CreateEntityView extends Composite
        implements
        CreateEntity.View {

    interface CreateEntityViewBinder
            extends
            UiBinder<Widget, CreateEntityView> {

    }

    private static CreateEntityViewBinder uiBinder = GWT.create(CreateEntityViewBinder.class);

    @UiField
    Form form;

    @UiField
    Legend formLegend;

    @UiField
    FormGroup formGroup;

    @UiField
    TextBox identifierBox;

    private CreateEntity presenter;

    @Override
    public void init(final CreateEntity presenter) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        identifierBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> valueChangeEvent) {
                presenter.onEntityIdentifierChanged(valueChangeEvent.getValue());
            }
        });
    }

    @Override
    public void show(String legend, String placeholder) {
        this.formLegend.setText(legend != null ? legend : "");
        identifierBox.setPlaceholder(placeholder != null ? placeholder : "");
    }

    @Override
    public void setValidationState(ValidationState state) {
        formGroup.setValidationState(state);
    }

    @Override
    public void clear() {
        form.reset();
    }
}