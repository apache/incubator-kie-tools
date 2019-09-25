/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioImages;
import org.drools.workbench.screens.testscenario.client.utils.ScenarioUtils;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class VerifyFactWidget extends Composite {

    private Grid outer;
    private boolean showResults;
    private String type;
    private AsyncPackageDataModelOracle oracle;
    private Scenario scenario;
    private ExecutionTrace executionTrace;

    public VerifyFactWidget(final VerifyFact vf,
                            final Scenario sc,
                            final AsyncPackageDataModelOracle oracle,
                            final ExecutionTrace executionTrace,
                            final boolean showResults) {
        outer = new Grid(2,
                         1);
        outer.getCellFormatter().setStyleName(0,
                                              0,
                                              "modeller-fact-TypeHeader"); //NON-NLS
        outer.getCellFormatter().setAlignment(0,
                                              0,
                                              HasHorizontalAlignment.ALIGN_CENTER,
                                              HasVerticalAlignment.ALIGN_MIDDLE);
        outer.setStyleName("modeller-fact-pattern-Widget"); //NON-NLS
        this.oracle = oracle;
        this.scenario = sc;
        this.executionTrace = executionTrace;
        HorizontalPanel ab = new HorizontalPanel();
        ClickableLabel label = null;

        final ClickHandler handler = new ClickHandler() {
            public void onClick(ClickEvent w) {

                final ListBox fieldsListBox = new ListBox();
                VerifyFactWidget.this.oracle.getFieldCompletions(type,
                                                                 new Callback<ModelField[]>() {
                                                                     @Override
                                                                     public void callback(final ModelField[] fields) {

                                                                         // Add fields
                                                                         for (int i = 0; i < fields.length; i++) {
                                                                             fieldsListBox.addItem(fields[i].getName());
                                                                         }

                                                                         // Add methods
                                                                         oracle.getMethodInfos(type,
                                                                                               new Callback<List<MethodInfo>>() {
                                                                                                   @Override
                                                                                                   public void callback(List<MethodInfo> result) {
                                                                                                       for (MethodInfo info : result) {
                                                                                                           if (info.getParams().isEmpty() && !"void".equals(info.getReturnClassType())) {
                                                                                                               fieldsListBox.addItem(info.getName());
                                                                                                           }
                                                                                                       }
                                                                                                   }
                                                                                               });
                                                                     }
                                                                 });

                final FormStylePopup pop = new FormStylePopup(TestScenarioAltedImages.INSTANCE.RuleAsset(),
                                                              TestScenarioConstants.INSTANCE.ChooseAFieldToAdd());
                pop.addRow(fieldsListBox);
                pop.add(new ModalFooterOKCancelButtons(new Command() {
                    @Override
                    public void execute() {
                        String f = fieldsListBox.getItemText(fieldsListBox.getSelectedIndex());
                        vf.getFieldValues().add(new VerifyField(f,
                                                                "",
                                                                "=="));
                        FlexTable data = render(vf);
                        outer.setWidget(1,
                                        0,
                                        data);
                        pop.hide();
                    }
                }, new Command() {
                    @Override
                    public void execute() {
                        pop.hide();
                    }
                }
                ));

                pop.show();
            }
        };

        if (!vf.anonymous) {
            type = (String) sc.getVariableTypes().get(vf.getName());
            label = new ClickableLabel(TestScenarioConstants.INSTANCE.scenarioFactTypeHasValues(type,
                                                                                                vf.getName()),
                                       handler);
        } else {
            type = vf.getName();
            label = new ClickableLabel(TestScenarioConstants.INSTANCE.AFactOfType0HasValues(vf.getName()),
                                       handler);
        }
        ab.add(label);
        this.showResults = showResults;

        outer.setWidget(0,
                        0,
                        ab);
        initWidget(outer);

        FlexTable data = render(vf);
        outer.setWidget(1,
                        0,
                        data);
    }

    private FlexTable render(final VerifyFact vf) {
        FlexTable data = new FlexTable();
        for (int i = 0; i < vf.getFieldValues().size(); i++) {
            final VerifyField fld = (VerifyField) vf.getFieldValues().get(i);
            data.setWidget(i,
                           1,
                           new SmallLabel(fld.getFieldName() + ":"));
            data.getFlexCellFormatter().setHorizontalAlignment(i,
                                                               1,
                                                               HasHorizontalAlignment.ALIGN_RIGHT);

            final ListBox opr = new ListBox();
            opr.addItem(TestScenarioConstants.INSTANCE.equalsScenario(),
                        "==");
            opr.addItem(TestScenarioConstants.INSTANCE.doesNotEqualScenario(),
                        "!=");
            if (fld.getOperator().equals("==")) {
                opr.setSelectedIndex(0);
            } else {
                opr.setSelectedIndex(1);
            }
            opr.addChangeHandler(new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    fld.setOperator(opr.getValue(opr.getSelectedIndex()));
                }
            });

            data.setWidget(i,
                           2,
                           opr);
            Widget cellEditor = new VerifyFieldConstraintEditor(type,
                                                                newValue -> fld.setExpected(newValue),
                                                                fld,
                                                                oracle,
                                                                this.scenario,
                                                                this.executionTrace);

            data.setWidget(i,
                           3,
                           cellEditor);

            Button deleteButton = new Button();
            deleteButton.setIcon(IconType.TRASH);
            deleteButton.setTitle(TestScenarioConstants.INSTANCE.RemoveThisFieldExpectation());
            deleteButton.addClickHandler(clickEvent -> {
                if (Window.confirm(TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisFieldExpectation(
                        fld.getFieldName()))) {
                    vf.getFieldValues().remove(fld);
                    FlexTable renderedTableAfterDelete = render(vf);
                    outer.setWidget(1,
                                    0,
                                    renderedTableAfterDelete);
                }
            });
            data.setWidget(i,
                           4,
                           deleteButton);

            if (showResults && fld.getSuccessResult() != null) {
                if (!fld.getSuccessResult().booleanValue()) {
                    data.setWidget(i,
                                   0,
                                   new Image(CommonImages.INSTANCE.warning()));
                    data.setWidget(i,
                                   5,
                                   new HTML(TestScenarioConstants.INSTANCE.ActualResult(fld.getActualResult())));

                    data.getCellFormatter().addStyleName(i,
                                                         5,
                                                         "testErrorValue"); //NON-NLS
                } else {
                    data.setWidget(i,
                                   0,
                                   new Image(TestScenarioImages.INSTANCE.testPassed()));
                }
            }
        }
        ScenarioUtils.addBottomAndRightPaddingToTableCells(data);
        return data;
    }
}
