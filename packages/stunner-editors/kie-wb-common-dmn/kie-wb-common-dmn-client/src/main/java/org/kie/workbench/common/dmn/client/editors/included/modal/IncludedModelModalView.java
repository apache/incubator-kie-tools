/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.included.modal;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class IncludedModelModalView implements IncludedModelModal.View {

    @DataField("header")
    private final HTMLDivElement header;

    @DataField("body")
    private final HTMLDivElement body;

    @DataField("footer")
    private final HTMLDivElement footer;

    @DataField("dropdown")
    private final HTMLDivElement dropdown;

    @DataField("model-name")
    private final HTMLInputElement modelNameInput;

    @DataField("include")
    private final HTMLButtonElement includeButton;

    @DataField("cancel")
    private final HTMLButtonElement cancelButton;

    private IncludedModelModal presenter;

    @Inject
    public IncludedModelModalView(final HTMLDivElement header,
                                  final HTMLDivElement body,
                                  final HTMLDivElement footer,
                                  final HTMLDivElement dropdown,
                                  final HTMLInputElement modelNameInput,
                                  final HTMLButtonElement includeButton,
                                  final HTMLButtonElement cancelButton) {
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.dropdown = dropdown;
        this.modelNameInput = modelNameInput;
        this.includeButton = includeButton;
        this.cancelButton = cancelButton;
    }

    @Override
    public void init(final IncludedModelModal presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initialize() {
        modelNameInput.value = "";
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }

    @Override
    public String getModelNameInput() {
        return modelNameInput.value;
    }

    @Override
    public void setupAssetsDropdown(final HTMLElement dropdownElement) {
        dropdown.appendChild(dropdownElement);
    }

    @Override
    public void disableIncludeButton() {
        includeButton.disabled = true;
    }

    @Override
    public void enableIncludeButton() {
        includeButton.disabled = false;
    }

    @EventHandler("model-name")
    public void onModelNameInputChanged(final KeyUpEvent e) {
        presenter.onValueChanged();
    }

    @EventHandler("include")
    public void onIncludeButtonClick(final ClickEvent e) {
        presenter.include();
    }

    @EventHandler("cancel")
    public void onCancelButtonClick(final ClickEvent e) {
        presenter.hide();
    }
}
