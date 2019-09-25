/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.client.callbacks.Callback;

public class EditableTextBox
        implements IsWidget {

    private TextBox view;
    private Callback<String> changed;

    public EditableTextBox(final Callback<String> changed,
                           final TextBox view,
                           final String fieldName,
                           final String initialValue) {
        this.changed = changed;
        this.view = view;
        view.addValueChangeHandler(event -> this.onValueChange(event.getValue()));

        view.setText(initialValue);
        view.setTitle(fieldName);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onValueChange(final String value) {
        changed.callback(value);
    }
}
