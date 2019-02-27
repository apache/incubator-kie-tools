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

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
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
    protected ButtonElement clearSearchButton = Document.get().createButtonElement();

    @DataField("searchButton")
    protected ButtonElement searchButton = Document.get().createButtonElement();

    @DataField("inputSearch")
    protected InputElement inputSearch = Document.get().createTextInputElement();

    @DataField("nameField")
    protected InputElement nameField = Document.get().createTextInputElement();

    @DataField("dataObjectListContainer")
    protected DivElement dataObjectListContainer = Document.get().createDivElement();

    @DataField("simpleJavaTypeListContainer")
    protected DivElement simpleJavaTypeListContainer = Document.get().createDivElement();

    @DataField("instanceListContainer")
    protected DivElement instanceListContainer = Document.get().createDivElement();

    @DataField("simpleJavaInstanceListContainer")
    protected DivElement simpleJavaInstanceListContainer = Document.get().createDivElement();

    @DataField("conditionsButton")
    protected ButtonElement conditionsButton = Document.get().createButtonElement();

    @DataField("addButton")
    protected ButtonElement addButton = Document.get().createButtonElement();

    @DataField("kieTestEditorTabContent")
    protected DivElement kieTestEditorTabContent = Document.get().createDivElement();

    @DataField("ruleCheatSheet")
    protected DivElement ruleCheatSheet = Document.get().createDivElement();

    @DataField("ruleCheatSheetParagraph1")
    protected ParagraphElement ruleCheatSheetParagraph1 = Document.get().createPElement();

    @DataField("ruleCheatSheetParagraph2")
    protected ParagraphElement ruleCheatSheetParagraph2 = Document.get().createPElement();

    @DataField("ruleCheatSheetParagraph3")
    protected ParagraphElement ruleCheatSheetParagraph3 = Document.get().createPElement();

    @DataField("ruleCheatSheetParagraph4")
    protected ParagraphElement ruleCheatSheetParagraph4 = Document.get().createPElement();

    @DataField("ruleCheatSheetList1")
    protected LIElement ruleCheatSheetList1 = Document.get().createLIElement();

    @DataField("ruleCheatSheetList2")
    protected LIElement ruleCheatSheetList2 = Document.get().createLIElement();

    @DataField("ruleCheatSheetList3")
    protected LIElement ruleCheatSheetList3 = Document.get().createLIElement();

    @DataField("ruleCheatSheetList4")
    protected LIElement ruleCheatSheetList4 = Document.get().createLIElement();

    @DataField("ruleCheatSheetList5")
    protected LIElement ruleCheatSheetList5 = Document.get().createLIElement();

    @DataField("ruleCheatSheetList6")
    protected LIElement ruleCheatSheetList6 = Document.get().createLIElement();

    @DataField("ruleCheatSheetParagraph5")
    protected ParagraphElement ruleCheatSheetParagraph5 = Document.get().createPElement();

    @DataField("ruleCheatSheetExampleExpressions")
    protected UListElement ruleCheatSheetExampleExpressions = Document.get().createULElement();

    @DataField("dmnCheatSheet")
    protected DivElement dmnCheatSheet = Document.get().createDivElement();

    @DataField("dmnCheatSheetParagraph1")
    protected ParagraphElement dmnCheatSheetParagraph1 = Document.get().createPElement();

    @DataField("dmnCheatSheetParagraph2")
    protected ParagraphElement dmnCheatSheetParagraph2 = Document.get().createPElement();

    @DataField("dmnCheatSheetParagraph3")
    protected ParagraphElement dmnCheatSheetParagraph3 = Document.get().createPElement();

    @DataField("dmnCheatSheetParagraph4")
    protected ParagraphElement dmnCheatSheetParagraph4 = Document.get().createPElement();

    @DataField("dmnCheatSheetParagraph5")
    protected ParagraphElement dmnCheatSheetParagraph5 = Document.get().createPElement();

    @DataField("dmnCheatSheetList1")
    protected LIElement dmnCheatSheetList1 = Document.get().createLIElement();

    @DataField("dmnCheatSheetList2")
    protected LIElement dmnCheatSheetList2 = Document.get().createLIElement();

    @DataField("dmnCheatSheetList3")
    protected LIElement dmnCheatSheetList3 = Document.get().createLIElement();

    @DataField("dmnCheatSheetList4")
    protected LIElement dmnCheatSheetList4 = Document.get().createLIElement();

    @DataField("dmnCheatSheetList5")
    protected LIElement dmnCheatSheetList5 = Document.get().createLIElement();

    @DataField("dmnCheatSheetList6")
    protected LIElement dmnCheatSheetList6 = Document.get().createLIElement();

    @DataField("dmnCheatSheetParagraph6")
    protected ParagraphElement dmnCheatSheetParagraph6 = Document.get().createPElement();

    protected List<DivElement> managedDivElements;

    public RightPanelViewImpl() {
        managedDivElements = Arrays.asList(dataObjectListContainer, simpleJavaTypeListContainer, instanceListContainer, simpleJavaTypeListContainer);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        disableEditorTab();
        addButton.setDisabled(true);
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
    public DivElement getDataObjectListContainer() {
        return dataObjectListContainer;
    }

    @Override
    public DivElement getSimpleJavaTypeListContainer() {
        return simpleJavaTypeListContainer;
    }

    @Override
    public DivElement getInstanceListContainer() {
        return instanceListContainer;
    }

    @Override
    public DivElement getSimpleJavaInstanceListContainer() {
        return simpleJavaInstanceListContainer;
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
    public void enableAddButton() {
        addButton.setDisabled(false);
    }

    protected void setDisabledStatus(boolean disabled) {
        clearSearchButton.setDisabled(disabled);
        searchButton.setDisabled(disabled);
        inputSearch.setDisabled(disabled);
        nameField.setDisabled(disabled);
        conditionsButton.setDisabled(disabled);
        setContainersDisabledStatus(disabled);
        if (disabled) {
            kieTestEditorTabContent.addClassName("disabled");
        } else {
            kieTestEditorTabContent.removeClassName("disabled");
        }
    }

    protected void setContainersDisabledStatus(boolean disabled) {
        if (disabled) {
            managedDivElements.forEach(divElement -> divElement.addClassName("disabled"));
        } else {
            managedDivElements.forEach(divElement -> divElement.removeClassName("disabled"));
        }
    }

    @Override
    public void setRuleCheatSheetContent() {
        ruleCheatSheet.getStyle().setDisplay(Style.Display.BLOCK);
        dmnCheatSheet.getStyle().setDisplay(Style.Display.NONE);

        ruleCheatSheetParagraph1.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet1());
        ruleCheatSheetParagraph2.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet2() + "<b>" + " " + ScenarioSimulationEditorConstants.INSTANCE.testEditor() + " " + "</b>"
                                                      + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet3() + " <b>" + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet4() + " </b>"
                                                      + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet5());
        ruleCheatSheetParagraph3.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet6());
        ruleCheatSheetParagraph4.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet7());
        ruleCheatSheetList1.setInnerHTML("<tt>=</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet8());
        ruleCheatSheetList2.setInnerHTML("<tt>!</tt>, <tt>!=</tt>, <tt>&lt;&gt;</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet9());
        ruleCheatSheetList3.setInnerHTML("<tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&lt;=</tt>, <tt>&gt;=</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet10());
        ruleCheatSheetList4.setInnerHTML("<tt>[" + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet11() + "]</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet12() + " " + "<b>"
                                                 + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet13() + "</b>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet14());
        ruleCheatSheetList5.setInnerHTML("<tt>" + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet15() + "</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet16()
                                                 + " " + "<b>" + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet17() + "</b>" + " " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet18());
        ruleCheatSheetList6.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet19() + " " + "<tt>=</tt>, <tt>[]</tt>, " + ScenarioSimulationEditorConstants.INSTANCE.or() +
                                                 " " + "<tt>;</tt>. " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet20() + " " + "<tt>null</tt>. " + ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet21());
        ruleCheatSheetParagraph5.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.ruleCheatSheet22());
        ruleCheatSheetExampleExpressions.setInnerHTML("<li><tt>&lt; 1</tt></li>" +
                    "<li><tt>&lt; 1; ! [-1, 0]</tt></li>" +
                    "<li><tt>[Jane, Doe]</tt></li>" +
                    "<li><tt>&lt;&gt; [1, -1]; = 0</tt></li>");
    }

    @Override
    public void setDMNCheatSheetContent() {
        ruleCheatSheet.getStyle().setDisplay(Style.Display.NONE);
        dmnCheatSheet.getStyle().setDisplay(Style.Display.BLOCK);

        dmnCheatSheetParagraph1.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet1());
        dmnCheatSheetParagraph2.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet2() + " <b>" + ScenarioSimulationEditorConstants.INSTANCE.testEditor() + "</b> "
                                                     + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet3());
        dmnCheatSheetParagraph3.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet4());
        dmnCheatSheetParagraph4.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet5());
        dmnCheatSheetParagraph5.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet6());
        dmnCheatSheetList1.setInnerHTML("<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet7() + " " + "</i>" + ScenarioSimulationEditorConstants.INSTANCE.and()
                + " " + "<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet8() + "</i>" + " " + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet9() + " "
                + "<tt>\"John Doe\"</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.or() + " " + "<tt>\"\"</tt>");
        dmnCheatSheetList2.setInnerHTML("<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet10() + "</i> (<tt>true</tt>, <tt>false</tt>, "
                + ScenarioSimulationEditorConstants.INSTANCE.and() + " " + "<tt>null</tt>)");
        dmnCheatSheetList3.setInnerHTML("<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet11() + "</i>" + " "
                                                + ScenarioSimulationEditorConstants.INSTANCE.and() + " " + "<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet12() + "</i>"
                                                + ScenarioSimulationEditorConstants.INSTANCE.forExample() + " " + "<tt>date(\"2019-05-13\")</tt>" + " " + ScenarioSimulationEditorConstants.INSTANCE.or() + " "
                                                + "<tt>time(\"14:10:00+02:00\")</tt>");
        dmnCheatSheetList4.setInnerHTML("<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet13() + "</i>");
        dmnCheatSheetList5.setInnerHTML("<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet14() + "</i>" + ScenarioSimulationEditorConstants.INSTANCE.forExample() +
                                                " " + "<tt>{x : 5, y : 3}</tt>");
        dmnCheatSheetList6.setInnerHTML("<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet15() + "</i>"
                                                + " " + ScenarioSimulationEditorConstants.INSTANCE.and() + " " + "<i>" + ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet16() + "</i>"
                                                + ScenarioSimulationEditorConstants.INSTANCE.forExample() + " " + "<tt>[1 .. 10]</tt> or <tt>[2, 3, 4, 5]</tt>");
        dmnCheatSheetParagraph6.setInnerHTML(ScenarioSimulationEditorConstants.INSTANCE.dmnCheatSheet17());
    }
}
