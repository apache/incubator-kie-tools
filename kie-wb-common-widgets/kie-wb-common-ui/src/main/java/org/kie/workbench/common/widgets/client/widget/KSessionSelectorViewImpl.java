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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLLabelElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static java.util.stream.Collectors.toList;

@Templated
public class KSessionSelectorViewImpl implements KSessionSelectorView {

    HTMLDocument document;

    @DataField("kbaseSelectContainer")
    HTMLDivElement kbaseSelectContainer;

    KieSelectElement kbaseSelect;

    @Inject
    @DataField("ksessionSelectContainer")
    HTMLDivElement ksessionSelectContainer;

    KieSelectElement ksessionSelect;

    @Inject
    @DataField("warningLabel")
    HTMLLabelElement warningLabel;

    private KSessionSelector presenter;

    public KSessionSelectorViewImpl() {
    }

    @Inject
    public KSessionSelectorViewImpl(final HTMLDocument document,
                                    final HTMLDivElement kbaseSelectContainer,
                                    final HTMLDivElement ksessionSelectContainer,
                                    final KieSelectElement kbaseSelect,
                                    final KieSelectElement ksessionSelect,
                                    final HTMLLabelElement warningLabel) {
        this.document = document;
        this.kbaseSelectContainer = kbaseSelectContainer;
        this.ksessionSelectContainer = ksessionSelectContainer;
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
    public void addKBases(final String... names) {

        kbaseSelect.setup(kbaseSelectContainer,
                          buildOptions(names),
                          names[0],
                          new Consumer<String>() {
                              @Override
                              public void accept(String s) {
                                  presenter.onKBaseSelected(s);
                              }
                          });
    }

    List<KieSelectOption> buildOptions(final String[] values) {
        return Arrays.stream(values).map(this::newOption).collect(toList());
    }

    KieSelectOption newOption(final String e) {
        return new KieSelectOption(e, e);
    }

    @Override
    public void setKSessions(final List<String> ksessions) {
        String[] names = ksessions.toArray(new String[ksessions.size()]);
        ksessionSelect.setup(ksessionSelectContainer,
                             buildOptions(names),
                             names[0],
                             s -> onSelectionChange());
    }

    @Override
    public void showWarningSelectedKSessionDoesNotExist() {
        warningLabel.style.setProperty("visibility", "visible");
    }

    @Override
    public String getSelectedKSessionName() {
        return ksessionSelect.getValue();
    }

    void onSelectionChange() {
        presenter.onSelectionChange();
    }
}
