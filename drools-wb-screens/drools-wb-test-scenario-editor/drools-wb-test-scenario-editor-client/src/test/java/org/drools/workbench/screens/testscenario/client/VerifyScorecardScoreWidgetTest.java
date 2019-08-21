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

import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VerifyScorecardScoreWidgetTest {

    @Mock
    VerifyScorecardScoreWidgetView view;

    @Test
    public void setPresenter() {
        final VerifyScorecardScoreWidget presenter = makeWidget(new VerifyScorecardScore(10.0),
                                                                false);

        verify(view).setPresenter(presenter);
    }

    @Test
    public void setValue() {
        makeWidget(new VerifyScorecardScore(10.0),
                   false);

        verify(view).setValue("10.0");
    }

    @Test
    public void setDoNotShowResult() {
        makeWidget(new VerifyScorecardScore(10.0),
                   false);

        verify(view, never()).showFailed(anyString());
        verify(view, never()).showPassed();
    }

    @Test
    public void testShowPassed() {
        final VerifyScorecardScore verifyScorecardScore = new VerifyScorecardScore();
        verifyScorecardScore.setExpected(10.0);
        verifyScorecardScore.setResult(10.0);

        makeWidget(verifyScorecardScore,
                   true);

        verify(view, never()).showFailed(anyString());
        verify(view).showPassed();
    }

    @Test
    public void testShowFailed() {
        final VerifyScorecardScore verifyScorecardScore = new VerifyScorecardScore();
        verifyScorecardScore.setExpected(10.0);
        verifyScorecardScore.setResult(20.0);

        makeWidget(verifyScorecardScore,
                   true);

        verify(view).showFailed("20.0");
        verify(view, never()).showPassed();
    }

    private VerifyScorecardScoreWidget makeWidget(final VerifyScorecardScore verifyScorecardScore,
                                                  final boolean showResults) {
        return new VerifyScorecardScoreWidget(verifyScorecardScore,
                                              showResults) {
            @Override
            protected VerifyScorecardScoreWidgetView makeView() {
                return view;
            }
        };
    }
}