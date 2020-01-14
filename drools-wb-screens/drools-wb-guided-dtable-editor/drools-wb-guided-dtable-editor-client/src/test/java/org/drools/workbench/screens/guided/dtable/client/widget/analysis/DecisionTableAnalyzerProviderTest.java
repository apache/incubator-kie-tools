/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.verifier.reporting.client.controller.AnalyzerController;
import org.kie.workbench.common.services.verifier.reporting.client.controller.AnalyzerControllerImpl;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTableAnalyzerProviderTest {

    @Mock
    private AnalysisReportScreen analysisReportScreen;

    @Test
    public void defaultAnalyserSetting() throws
            Exception {

        final Map<String, String> preferences = new HashMap<>();
        preferences.put(ApplicationPreferences.DATE_FORMAT,
                        "dd-MMM-yyyy");
        ApplicationPreferences.setUp(preferences);

        assertTrue(constructAnalyzer() instanceof AnalyzerControllerImpl);
    }

    @Test
    public void verificationDisabledWithSetting() throws
            Exception {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED,
                "true");
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MMM-yyyy");
        }};

        ApplicationPreferences.setUp(preferences);

        assertFalse(constructAnalyzer() instanceof AnalyzerControllerImpl);
    }

    @Test
    public void verificationEnabledByWrongSetting() throws
            Exception {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED,
                "nonBooleanValue");
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MMM-yyyy");
        }};

        ApplicationPreferences.setUp(preferences);

        assertTrue(constructAnalyzer() instanceof AnalyzerControllerImpl);
    }

    @Test
    public void verificationEnabledWithSetting() throws
            Exception {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED,
                "false");
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MMM-yyyy");
        }};

        ApplicationPreferences.setUp(preferences);

        assertTrue(constructAnalyzer() instanceof AnalyzerControllerImpl);
    }

    private AnalyzerController constructAnalyzer() {
        return new DecisionTableAnalyzerProvider().newAnalyzer(mock(AnalysisReportScreen.class),
                                                               mock(PlaceRequest.class),
                                                               mock(AsyncPackageDataModelOracleImpl.class),
                                                               mock(GuidedDecisionTable52.class),
                                                               mock(EventBus.class));
    }
}