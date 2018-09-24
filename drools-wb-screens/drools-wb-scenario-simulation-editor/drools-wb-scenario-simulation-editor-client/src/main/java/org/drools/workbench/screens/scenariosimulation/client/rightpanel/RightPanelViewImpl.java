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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(stylesheet = "/org/drools/workbench/screens/scenariosimulation/client/resources/css/ScenarioSimulationEditorStyles.css")
public class RightPanelViewImpl
        extends Composite
        implements RightPanelView {

    private Presenter presenter;

    @DataField("rightPanelTabs")
    private UListElement rightPanelTabs = Document.get().createULElement();

    @DataField("clearSearchButton")
    ButtonElement clearSearchButton = Document.get().createButtonElement();

    @DataField("searchButton")
    ButtonElement searchButton = Document.get().createButtonElement();

    @DataField("inputSearch")
    InputElement inputSearch = Document.get().createTextInputElement();

    @DataField("nameField")
    InputElement nameField = Document.get().createTextInputElement();

    @DataField("listContainer")
    DivElement listContainer = Document.get().createDivElement();

    @DataField("conditionsButton")
    ButtonElement conditionsButton = Document.get().createButtonElement();

    public RightPanelViewImpl() {

    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        disableEditorTab();
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @EventHandler("clearSearchButton")
    public void onClearSearchButtonClick(ClickEvent event) {
        presenter.onClearSearch();
    }

    @EventHandler("inputSearch")
    public void onInputSearchKeyUp(KeyUpEvent event) {
        presenter.onShowClearButton();
    }

    @EventHandler("inputSearch")
    public void onInputSearchKeyDownEvent(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            presenter.onSearchedEvent(inputSearch.getValue());
        }
    }

    @EventHandler("searchButton")
    public void onSearchButtonClicked(ClickEvent event) {
        presenter.onSearchedEvent(inputSearch.getValue());
    }

    @Override
    public void clearInputSearch() {
        inputSearch.setValue("");
    }

    @Override
    public void clearNameField() {
        nameField.setValue("");
    }

    @Override
    public void hideClearButton() {
        clearSearchButton.setDisabled(true);
        clearSearchButton.setAttribute("style", "display: none;");
    }

    @Override
    public void showClearButton() {
        clearSearchButton.setDisabled(false);
        clearSearchButton.removeAttribute("style");
    }

    @Override
    public DivElement getListContainer() {
        return listContainer;
    }

    @Override
    public void enableEditorTab() {
        setDisabledStatus(false);
    }

    @Override
    public void disableEditorTab() {
        setDisabledStatus(true);
    }

    protected void setDisabledStatus(boolean disabled) {
        clearSearchButton.setDisabled(disabled);
        searchButton.setDisabled(disabled);
        inputSearch.setDisabled(disabled);
        nameField.setDisabled(disabled);
        conditionsButton.setDisabled(disabled);
        setListContainerDisabledStatus(disabled);
    }

    protected void setListContainerDisabledStatus(boolean disabled) {
        if (disabled) {
            listContainer.addClassName("disabled");
        } else {
            listContainer.removeClassName("disabled");
        }
    }

}
