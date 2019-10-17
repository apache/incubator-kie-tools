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

package org.kie.workbench.common.services.verifier.reporting.client.analysis;

import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReport;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisReporterTest {

    @Mock
    private PlaceRequest place;
    @Mock
    private AnalysisReportScreen reportScreen;
    @Captor
    private ArgumentCaptor<AnalysisReport> analysisReportArgumentCaptor;

    @Test
    public void sendReport() {
        new AnalysisReporter(place,
                             reportScreen).sendReport(new HashSet<>());

        verify(reportScreen).showReport(analysisReportArgumentCaptor.capture());
        final AnalysisReport report = analysisReportArgumentCaptor.getValue();

        assertTrue(report.getAnalysisData().isEmpty());
        assertEquals(place, report.getPlace());
    }
}