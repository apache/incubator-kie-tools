/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class KSessionSelectorViewImpl
        extends Composite
        implements KSessionSelectorView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionSelectorViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ListBox kbases;

    @UiField
    ListBox ksessions;

    @UiField
    Label warning;

    public KSessionSelectorViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSelected(String kbase, String ksession) {
        kbases.setSelectedValue(kbase);
        ksessions.setSelectedValue(ksession);
    }

    @Override
    public void addKBase(String name) {
        kbases.addItem(name);
    }

    @Override
    public void setKSessions(List<String> ksessions) {
        this.ksessions.clear();
        for (String ksession : ksessions) {
            this.ksessions.addItem(ksession);
        }
    }

    @Override
    public void showWarningSelectedKSessionDoesNotExist() {
        warning.setVisible(true);
    }

    @Override
    public String getSelectedKBase() {
        return kbases.getItemText(kbases.getSelectedIndex());
    }

    @UiHandler("kbases")
    public void onKBaseSelected(ChangeEvent event) {
        presenter.onKBaseSelected(getSelectedKBase());
    }

    @UiHandler("ksessions")
    public void handleChange(ChangeEvent event) {
        presenter.onKSessionSelected(ksessions.getItemText(ksessions.getSelectedIndex()));
    }

}
