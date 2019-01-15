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

package org.drools.workbench.services.verifier.plugin.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issues;
import org.drools.verifier.core.checks.base.JavaCheckRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.DrlInitialize;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.drools.workbench.services.verifier.plugin.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ReceiverTest {

    protected AnalyzerProvider analyzerProvider;

    @Mock
    private Poster poster;

    @Captor
    private ArgumentCaptor<Status> statusArgumentCaptor;

    @Captor
    private ArgumentCaptor<Issues> issuesArgumentCaptor;

    private Receiver receiver;

    @Before
    public void setUp() throws
            Exception {

        analyzerProvider = new AnalyzerProvider();

        receiver = new Receiver(poster,
                                new JavaCheckRunner());
    }

    @Test
    public void testInit() throws
            Exception {

        final GuidedDecisionTable52 table1 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(0,
                                       true)
                                  .end())
                .buildTable();

        receiver.received(new DrlInitialize("testUUID",
                                            table1,
                                            new ModelMetaDataEnhancer(table1).getHeaderMetaData(),
                                            analyzerProvider.getFactTypes(),
                                            "dd-MMM-yyyy"));

        verify(poster).post(issuesArgumentCaptor.capture());
    }
}