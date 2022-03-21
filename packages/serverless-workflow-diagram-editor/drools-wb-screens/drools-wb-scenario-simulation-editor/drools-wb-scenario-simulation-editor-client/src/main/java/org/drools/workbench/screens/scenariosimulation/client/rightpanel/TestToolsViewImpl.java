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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@ApplicationScoped
@Templated(stylesheet = "/org/drools/workbench/screens/scenariosimulation/client/resources/css/ScenarioSimulationEditorStyles.css")
public class TestToolsViewImpl
        extends Composite
        implements TestToolsView {

    protected Presenter presenter;

    @DataField("testToolsDescriptionElement")
    protected ParagraphElement testToolsDescriptionElement = Document.get().createPElement();

    @DataField("testToolObjectSelectionTitleElement")
    protected LabelElement testToolObjectSelectionTitleElement = Document.get().createLabelElement();

    @DataField("clearSelectionElement")
    protected AnchorElement clearSelectionElement = Document.get().createAnchorElement();

    @DataField("infoSelectDataObjectElement")
    protected SpanElement infoSelectDataObjectElement = Document.get().createSpanElement();

    @DataField("clearSearchButton")
    protected ButtonElement clearSearchButton = Document.get().createPushButtonElement();

    @DataField("searchButton")
    protected ButtonElement searchButton = Document.get().createPushButtonElement();

    @DataField("inputSearch")
    protected InputElement inputSearch = Document.get().createTextInputElement();

    @DataField("dataObjectListContainer")
    protected DivElement dataObjectListContainer = Document.get().createDivElement();

    @DataField("simpleJavaTypeListContainer")
    protected DivElement simpleJavaTypeListContainer = Document.get().createDivElement();

    @DataField("instanceListContainer-separator")
    protected SpanElement instanceListContainerSeparator = Document.get().createSpanElement();

    @DataField("instanceListContainer")
    protected DivElement instanceListContainer = Document.get().createDivElement();

    @DataField("simpleJavaInstanceListContainer")
    protected DivElement simpleJavaInstanceListContainer = Document.get().createDivElement();

    @DataField("addButtonLabel")
    protected DivElement addButtonLabel = Document.get().createDivElement();

    @DataField("addButton")
    protected ButtonElement addButton = Document.get().createPushButtonElement();

    @DataField("kieTestToolsContent")
    protected DivElement kieTestToolsContent = Document.get().createDivElement();

    protected List<DivElement> managedDivElements;

    public TestToolsViewImpl() {
        managedDivElements = Arrays.asList(dataObjectListContainer, simpleJavaTypeListContainer, instanceListContainer, simpleJavaTypeListContainer);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        disableEditorTab();
        testToolsDescriptionElement.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.testToolsDescription());
        testToolObjectSelectionTitleElement.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.testToolObjectSelectionTitle());
        clearSelectionElement.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.testToolClearSelection());
        addButton.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.testToolsAddButton());
        addButtonLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.testToolsAddButtonLabel());
        instanceListContainerSeparator.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.dataObjectInstances());
        infoSelectDataObjectElement.setAttribute("title", ScenarioSimulationEditorConstants.INSTANCE.testToolObjectSelectionTooltip());
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void reset() {
        clearDataObjectList();
        clearSimpleJavaTypeList();
        showInstanceListContainerSeparator(false);
        clearInstanceList();
        clearSimpleJavaInstanceFieldList();
    }

    @EventHandler("clearSearchButton")
    public void onClearSearchButtonClick(ClickEvent event) {
        presenter.onUndoSearch();
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

    @EventHandler("clearSelectionElement")
    public void onClearSelectionElementClicked(ClickEvent event) {
        presenter.clearSelection();
    }

    @EventHandler("searchButton")
    public void onSearchButtonClicked(ClickEvent event) {
        presenter.onSearchedEvent(inputSearch.getValue());
    }

    @EventHandler("addButton")
    public void onAddButtonClicked(ClickEvent event) {
        presenter.onModifyColumn();
        addButton.setDisabled(true);
        presenter.onDisableEditorTab();
    }

    @Override
    public void clearInputSearch() {
        inputSearch.setValue("");
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
    public void clearDataObjectList() {
        dataObjectListContainer.removeAllChildren();
        dataObjectListContainer.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void clearSimpleJavaTypeList() {
        simpleJavaTypeListContainer.removeAllChildren();
        simpleJavaTypeListContainer.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void clearInstanceList() {
        instanceListContainer.removeAllChildren();
        instanceListContainer.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void clearSimpleJavaInstanceFieldList() {
        simpleJavaInstanceListContainer.removeAllChildren();
        simpleJavaInstanceListContainer.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void addDataObjectListGroupItem(DivElement item) {
        dataObjectListContainer.getStyle().setDisplay(Style.Display.BLOCK);
        dataObjectListContainer.appendChild(item);
    }

    @Override
    public void addSimpleJavaTypeListGroupItem(DivElement item) {
        simpleJavaTypeListContainer.getStyle().setDisplay(Style.Display.BLOCK);
        simpleJavaTypeListContainer.appendChild(item);
    }

    @Override
    public void addInstanceListGroupItem(DivElement item) {
        instanceListContainer.getStyle().setDisplay(Style.Display.BLOCK);
        instanceListContainer.appendChild(item);
    }

    @Override
    public void addSimpleJavaInstanceListGroupItem(DivElement item) {
        simpleJavaInstanceListContainer.getStyle().setDisplay(Style.Display.BLOCK);
        simpleJavaInstanceListContainer.appendChild(item);
    }

    @Override
    public void updateInstanceListSeparator(boolean show) {
        if (!show ||
                (instanceListContainer.getChildCount() < 1 && simpleJavaInstanceListContainer.getChildCount() < 1)) {
            instanceListContainerSeparator.getStyle().setDisplay(Style.Display.NONE);
        } else {
            instanceListContainerSeparator.getStyle().setDisplay(Style.Display.BLOCK);
        }
    }

    @Override
    public void showInstanceListContainerSeparator(boolean show) {
        if (show) {
            instanceListContainerSeparator.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            instanceListContainerSeparator.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    @Override
    public void enableEditorTab() {
        setDisabledStatus(false);
    }

    @Override
    public void disableEditorTab() {
        setDisabledStatus(true);
    }

    @Override
    public void enableSearch() {
        clearSearchButton.setDisabled(false);
        searchButton.setDisabled(false);
        inputSearch.setDisabled(false);
    }

    @Override
    public void disableSearch() {
        hideClearButton();
        searchButton.setDisabled(true);
        inputSearch.setDisabled(true);
        inputSearch.setValue("");
    }

    @Override
    public void enableAddButton() {
        addButton.setDisabled(false);
    }

    @Override
    public void disableAddButton() {
        addButton.setDisabled(true);
    }

    protected void setDisabledStatus(boolean disabled) {
        setClearSelectionAnchorDisabledStatus(disabled);
        setInfoSelectDataObjectElementDisabledStatus(disabled);
        setContainersDisabledStatus(disabled);
        if (disabled) {
            kieTestToolsContent.addClassName(ConstantHolder.DISABLED);
            disableSearch();
            disableAddButton();
        } else {
            kieTestToolsContent.removeClassName(ConstantHolder.DISABLED);
        }
    }

    protected void setContainersDisabledStatus(boolean disabled) {
        if (disabled) {
            managedDivElements.forEach(divElement -> divElement.addClassName(ConstantHolder.DISABLED));
        } else {
            managedDivElements.forEach(divElement -> divElement.removeClassName(ConstantHolder.DISABLED));
        }
    }

    protected void setClearSelectionAnchorDisabledStatus(boolean disabled) {
        if (disabled) {
            clearSelectionElement.addClassName(ConstantHolder.DISABLED);
        } else {
            clearSelectionElement.removeClassName(ConstantHolder.DISABLED);
        }
    }

    protected void setInfoSelectDataObjectElementDisabledStatus(boolean disabled) {
        if (disabled) {
            infoSelectDataObjectElement.addClassName(ConstantHolder.DISABLED);
        } else {
            infoSelectDataObjectElement.removeClassName(ConstantHolder.DISABLED);
        }
    }
}
