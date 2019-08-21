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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;

public class VerifyScorecardScoreWidget implements IsWidget {

    private VerifyScorecardScoreWidgetView view = makeView();
    private VerifyScorecardScore verifyScorecardScore;

    public VerifyScorecardScoreWidget(final VerifyScorecardScore verifyScorecardScore,
                                      final boolean showResults) {
        this.verifyScorecardScore = verifyScorecardScore;

        view.setPresenter(this);

        view.setValue(getValue(verifyScorecardScore.getExpected()));

        if (showResults) {
            if (verifyScorecardScore.wasSuccessful()) {
                view.showPassed();
            } else {
                view.showFailed(getValue(verifyScorecardScore.getResult()));
            }
        }
    }

    // This might never use DI for the view, but just in case. Also we need to mock the view for tests.
    protected VerifyScorecardScoreWidgetView makeView() {
        return new VerifyScorecardScoreWidgetViewImpl();
    }

    private String getValue(final Double value) {
        if (value == null) {
            return null;
        } else {
            return Double.toString(value);
        }
    }

    public void onValueChanged(final String value) {
        verifyScorecardScore.setExpected(getValue(value));
    }

    private Double getValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(value);
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
