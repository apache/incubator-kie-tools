/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.controller.AnalyzerControllerImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DecisionTableAnalyzerProviderTest {

    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Mock
    private AnalysisReportScreen analysisReportScreen;

    @Test
    public void defaultAnalyserSetting() throws Exception {

        final Map<String, String> preferences = new HashMap<>();
        preferences.put( ApplicationPreferences.DATE_FORMAT,
                         "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        assertTrue( new DecisionTableAnalyzerProvider( analysisReportScreen ).newAnalyzer( mock( PlaceRequest.class ),
                                                                                           mock( AsyncPackageDataModelOracle.class ),
                                                                                           mock( GuidedDecisionTable52.class ),
                                                                                           mock( EventBus.class ) ) instanceof AnalyzerControllerImpl );

    }

    @Test
    public void verificationDisabledWithSetting() throws Exception {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED,
                 "true" );
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MMM-yyyy" );
        }};

        ApplicationPreferences.setUp( preferences );

        assertFalse( new DecisionTableAnalyzerProvider( analysisReportScreen ).newAnalyzer( mock( PlaceRequest.class ),
                                                                                            mock( AsyncPackageDataModelOracle.class ),
                                                                                            mock( GuidedDecisionTable52.class ),
                                                                                            mock( EventBus.class ) ) instanceof AnalyzerControllerImpl );

    }

    @Test
    public void verificationEnabledWithSetting() throws Exception {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put( GuidedDecisionTableEditorService.DTABLE_VERIFICATION_DISABLED,
                 "false" );
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MMM-yyyy" );
        }};

        ApplicationPreferences.setUp( preferences );

        assertTrue( new DecisionTableAnalyzerProvider( analysisReportScreen ).newAnalyzer( mock( PlaceRequest.class ),
                                                                                           mock( AsyncPackageDataModelOracle.class ),
                                                                                           mock( GuidedDecisionTable52.class ),
                                                                                           mock( EventBus.class ) ) instanceof AnalyzerControllerImpl );

    }
}