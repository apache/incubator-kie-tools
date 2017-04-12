/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class KSessionSelectorViewImpl implements KSessionSelectorView {

    Document document;

    @DataField("kbaseSelect")
    Select kbaseSelect;

    @Inject
    @DataField("ksessionSelect")
    Select ksessionSelect;

    @Inject
    @DataField("warningLabel")
    Label warningLabel;

    private KSessionSelector presenter;

    public KSessionSelectorViewImpl() {
    }

    @Inject
    public KSessionSelectorViewImpl(final Document document,
                                    final Select kbaseSelect,
                                    final Select ksessionSelect,
                                    final Label warningLabel) {
        this.document = document;
        this.kbaseSelect = kbaseSelect;
        this.ksessionSelect = ksessionSelect;
        this.warningLabel = warningLabel;
    }

    @Override
    public void setPresenter(final KSessionSelector presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSelected(final String kbase,
                            final String ksession) {
        kbaseSelect.setValue(kbase);
        ksessionSelect.setValue(ksession);

        onSelectionChange();
    }

    @Override
    public void addKBase(final String name) {
        kbaseSelect.add(createOption(name));
    }

    @Override
    public void setKSessions(final List<String> ksessions) {
        removeChildren(ksessionSelect);
        for (String ksession : ksessions) {
            ksessionSelect.add(createOption(ksession));
        }
    }

    private Option createOption(final String value) {
        Option option = (Option) document.createElement("option");
        option.setText(value);
        return option;
    }

    private void removeChildren(final Select select) {
        while (select.hasChildNodes()) {
            select.removeChild(select.getLastChild());
        }
    }

    @Override
    public void showWarningSelectedKSessionDoesNotExist() {
        warningLabel.setHidden(false);
    }

    @Override
    public void clear() {
        removeChildren(kbaseSelect);
        removeChildren(ksessionSelect);
    }

    @Override
    public String getSelectedKSessionName() {
        return ksessionSelect.getValue();
    }

    @EventHandler("kbaseSelect")
    public void onKBaseChange(final ChangeEvent event) {
        presenter.onKBaseSelected(kbaseSelect.getValue());
    }

    @EventHandler("ksessionSelect")
    public void onKSessionSelected(final ChangeEvent event) {
        onSelectionChange();
    }

    void onSelectionChange() {
        presenter.onSelectionChange();
    }
}
