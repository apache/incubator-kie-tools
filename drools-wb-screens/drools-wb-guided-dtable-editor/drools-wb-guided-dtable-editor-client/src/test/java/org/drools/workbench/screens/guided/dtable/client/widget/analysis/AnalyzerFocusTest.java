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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class AnalyzerFocusTest {

    private AnalyzerProvider analyzerProvider;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Before
    public void setUp() throws Exception {
        Map<String, String> preferences = new HashMap<>();
        preferences.put( ApplicationPreferences.DATE_FORMAT,
                         "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        analyzerProvider = new AnalyzerProvider();
    }

    @Test
    public void testOnFocus() throws Exception {
        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( new GuidedDecisionTable52() );
        analyzer.analyze( Collections.emptyList() );

        analyzerProvider.clearAnalysisReport();

        analyzer.start();

        assertNotNull( analyzerProvider.getAnalysisReport() );
    }
}
