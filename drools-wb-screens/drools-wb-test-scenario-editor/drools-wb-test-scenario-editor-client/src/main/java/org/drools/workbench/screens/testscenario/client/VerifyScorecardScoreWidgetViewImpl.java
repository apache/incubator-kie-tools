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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioImages;
import org.kie.workbench.common.widgets.client.resources.CommonImages;

public class VerifyScorecardScoreWidgetViewImpl
        extends Composite
        implements VerifyScorecardScoreWidgetView {

    private Grid outer = new Grid(2,
                                  1);
    private VerifyScorecardScoreWidget presenter;
    private TextBox textBox = new TextBox();
    private FlexTable data = new FlexTable();

    public VerifyScorecardScoreWidgetViewImpl() {
        outer.getCellFormatter().setStyleName(0,
                                              0,
                                              "modeller-fact-TypeHeader"); //NON-NLS
        outer.getCellFormatter().setAlignment(0,
                                              0,
                                              HasHorizontalAlignment.ALIGN_CENTER,
                                              HasVerticalAlignment.ALIGN_MIDDLE);
        outer.setStyleName("modeller-fact-pattern-Widget"); //NON-NLS
        HorizontalPanel ab = new HorizontalPanel();

        outer.setWidget(0,
                        0,
                        ab);
        initWidget(outer);
        data.setWidget(0,
                       1,
                       (new Label(TestScenarioConstants.INSTANCE.ScoreCardScore())));
        textBox.addChangeHandler(changeEvent -> {
            presenter.onValueChanged(textBox.getValue());
        });
        data.setWidget(0,
                       2,
                       textBox);
        outer.setWidget(1,
                        0,
                        data);
    }

    @Override
    public void setValue(final String value) {
        textBox.setValue(value);
    }

    @Override
    public void showFailed(final String value) {
        data.setWidget(0,
                       0,
                       new Image(CommonImages.INSTANCE.warning()));
        data.setWidget(0,
                       5,
                       new HTML(TestScenarioConstants.INSTANCE.ActualResult(value)));

        data.getCellFormatter().addStyleName(0,
                                             5,
                                             "testErrorValue"); //NON-NLS
    }

    @Override
    public void showPassed() {
        data.setWidget(0,
                       0,
                       new Image(TestScenarioImages.INSTANCE.testPassed()));
    }

    @Override
    public void setPresenter(VerifyScorecardScoreWidget presenter) {
        this.presenter = presenter;
    }
}
