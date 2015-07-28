/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.RadioButton;

public class KSessionFormViewImpl
        extends Composite
        implements KSessionFormView {


    interface KSessionFormViewImplBinder
            extends
            UiBinder<Widget, KSessionFormViewImpl> {

    }

    private static KSessionFormViewImplBinder uiBinder = GWT.create(KSessionFormViewImplBinder.class);

    @UiField
    Label nameTextBox;

    @UiField
    RadioButton realtime;

    @UiField
    RadioButton pseudo;

    private Presenter presenter;

    public KSessionFormViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(String name) {
        nameTextBox.setText(name);
    }

    @Override
    public void selectPseudo() {
        pseudo.setValue(true);
    }

    @Override
    public void selectRealtime() {
        realtime.setValue(true);
    }

    @Override
    public void clear() {
        realtime.setValue(true);
        nameTextBox.setText("");
    }

    @Override
    public void makeReadOnly() {
        realtime.setEnabled(false);
        pseudo.setEnabled(false);
    }

    @UiHandler("realtime")
    public void onRealtimeChange(ValueChangeEvent<Boolean> valueChangeEvent) {
        if (realtime.getValue()) {
            presenter.onRealtimeSelect();
        }
    }

    @UiHandler("pseudo")
    public void onPseudoChange(ValueChangeEvent<Boolean> valueChangeEvent) {
        if (pseudo.getValue()) {
            presenter.onPseudoSelect();
        }
    }

}
