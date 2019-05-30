/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.SmallSwitchComponentView_No;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.SmallSwitchComponentView_Yes;

@Dependent
@Templated
public class SmallSwitchComponentView implements SmallSwitchComponent.View {

    @DataField("input-checkbox")
    private final HTMLInputElement inputCheckbox;

    @DataField("checkbox-text")
    private final HTMLElement checkboxText;

    private final TranslationService translationService;

    private SmallSwitchComponent presenter;

    private Consumer<Boolean> onValueChanged;

    @Inject
    public SmallSwitchComponentView(final HTMLInputElement inputCheckbox,
                                    final @Named("span") HTMLElement checkboxText,
                                    final TranslationService translationService) {
        this.inputCheckbox = inputCheckbox;
        this.checkboxText = checkboxText;
        this.translationService = translationService;
    }

    @Override
    public void init(final SmallSwitchComponent presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setup() {
        refreshCheckBoxText();
    }

    @EventHandler("input-checkbox")
    public void onInputCheckBoxChange(final ChangeEvent e) {
        refreshCheckBoxText();
        callOnValueChanged();
    }

    void callOnValueChanged() {
        if (!Objects.isNull(onValueChanged)) {
            onValueChanged.accept(isChecked());
        }
    }

    @Override
    public boolean getValue() {
        return isChecked();
    }

    @Override
    public void setValue(final boolean value) {
        inputCheckbox.checked = value;

        refreshCheckBoxText();
    }

    @Override
    public void setOnValueChanged(final Consumer<Boolean> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    void refreshCheckBoxText() {
        checkboxText.textContent = isChecked() ? yes() : no();
    }

    private String no() {
        return translationService.format(SmallSwitchComponentView_No);
    }

    private String yes() {
        return translationService.format(SmallSwitchComponentView_Yes);
    }

    private boolean isChecked() {
        return inputCheckbox.checked;
    }
}
