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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;
import static org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.$;

@Templated
@Dependent
public class DataTypeConstraintModalView implements DataTypeConstraintModal.View {

    private static final String DISABLED_CLASS = "disabled";

    @DataField("header")
    private final HTMLDivElement header;

    @DataField("body")
    private final HTMLDivElement body;

    @DataField("footer")
    private final HTMLDivElement footer;

    @DataField("constraint-component-container")
    private final HTMLDivElement componentContainer;

    @DataField("ok-button")
    private final HTMLButtonElement okButton;

    @DataField("cancel-button")
    private final HTMLButtonElement cancelButton;

    @DataField("clear-all-anchor")
    private final HTMLAnchorElement clearAllAnchor;

    @DataField("type")
    private final HTMLElement type;

    @DataField("select-constraint")
    private final HTMLDivElement selectConstraint;

    @DataField("constraint-warning-message")
    private final HTMLDivElement constraintWarningMessage;

    @DataField("close-constraint-warning-message")
    private final HTMLButtonElement closeConstraintWarningMessage;

    private DataTypeConstraintModal presenter;

    @Inject
    public DataTypeConstraintModalView(final HTMLDivElement header,
                                       final HTMLDivElement body,
                                       final HTMLDivElement footer,
                                       final HTMLDivElement componentContainer,
                                       final HTMLButtonElement okButton,
                                       final HTMLButtonElement cancelButton,
                                       final HTMLAnchorElement clearAllAnchor,
                                       final @Named("span") HTMLElement type,
                                       final HTMLDivElement selectConstraint,
                                       final HTMLDivElement constraintWarningMessage,
                                       final HTMLButtonElement closeConstraintWarningMessage) {
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.componentContainer = componentContainer;
        this.okButton = okButton;
        this.cancelButton = cancelButton;
        this.clearAllAnchor = clearAllAnchor;
        this.type = type;
        this.selectConstraint = selectConstraint;
        this.constraintWarningMessage = constraintWarningMessage;
        this.closeConstraintWarningMessage = closeConstraintWarningMessage;
    }

    @PostConstruct
    public void init() {
        setupSelectPicker();
        setupSelectPickerOnChangeHandler();
        setupEmptyContainer();
    }

    @Override
    public void init(final DataTypeConstraintModal presenter) {
        this.presenter = presenter;
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

    @EventHandler("ok-button")
    public void onOkButtonClick(final ClickEvent e) {
        presenter.save();
    }

    @EventHandler("cancel-button")
    public void onCancelButtonClick(final ClickEvent e) {
        presenter.hide();
    }

    @EventHandler("clear-all-anchor")
    public void onClearAllAnchorClick(final ClickEvent e) {
        presenter.clearAll();
    }

    @EventHandler("close-constraint-warning-message")
    public void onCloseConstraintWarningClick(final ClickEvent e) {
        hide(constraintWarningMessage);
    }

    @Override
    public void showConstraintWarningMessage() {
        show(constraintWarningMessage);
    }

    @Override
    public void setType(final String type) {
        this.type.textContent = type;
    }

    void onSelectChange(final JQuerySelectPickerEvent event) {

        final String constraintType = event.target.value;

        if (!isEmpty(constraintType)) {
            final ConstraintType constraint = ConstraintType.fromString(constraintType);
            loadComponent(constraint);
            presenter.setConstraintType(constraint);
        }
    }

    @Override
    public void setupEmptyContainer() {
        componentContainer.innerHTML = "";
        componentContainer.appendChild(selectConstraint);
    }

    @Override
    public void loadComponent(final ConstraintType constraintType) {
        presenter.setupComponent(constraintType);
        componentContainer.innerHTML = "";
        componentContainer.appendChild(presenter.getCurrentComponent().getElement());
    }

    @Override
    public void onShow() {
        setPickerValue(getSelectPicker(), getConstraintType());
    }

    @Override
    public void enableOkButton() {
        okButton.classList.remove(DISABLED_CLASS);
    }

    @Override
    public void disableOkButton() {
        okButton.classList.add(DISABLED_CLASS);
    }

    private String getConstraintType() {
        if (presenter.getConstraintType() == null) {
            return presenter.inferComponentType(presenter.getConstraintValue()).value();
        } else {
            return presenter.getConstraintType().toString();
        }
    }

    void setupSelectPicker() {
        triggerPickerAction(getSelectPicker(), "refresh");
    }

    void setupSelectPickerOnChangeHandler() {
        setupOnChangeHandler(getSelectPicker());
    }

    void setupOnChangeHandler(final Element element) {
        $(element).on("hidden.bs.select", this::onSelectChange);
    }

    void setPickerValue(final Element element,
                        final String value) {
        $(element).selectpicker("val", value);
    }

    Element getSelectPicker() {
        return body.querySelector(".selectpicker");
    }

    void triggerPickerAction(final Element element,
                             final String method) {
        $(element).selectpicker(method);
    }
}
