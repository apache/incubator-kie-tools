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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@ApplicationScoped
@Templated(stylesheet = "/org/drools/workbench/screens/scenariosimulation/client/resources/css/ScenarioSimulationEditorStyles.css")
public class CheatSheetViewImpl
        extends Composite
        implements CheatSheetView {

    protected Presenter presenter;

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

    public CheatSheetViewImpl() {
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
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

    @Override
    public void reset() {
        dmnCheatSheet.getStyle().setDisplay(Style.Display.NONE);
        ruleCheatSheet.getStyle().setDisplay(Style.Display.NONE);
    }
}
