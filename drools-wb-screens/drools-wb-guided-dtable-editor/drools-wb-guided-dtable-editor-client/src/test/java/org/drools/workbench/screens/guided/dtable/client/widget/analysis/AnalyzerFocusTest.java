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
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AnalyzerFocusTest {

    @Test
    public void testOnFocus() throws Exception {
        final VerifierWebWorkerConnection connection = mock( VerifierWebWorkerConnection.class );
        final DTableUpdateManagerImpl updateManager = mock( DTableUpdateManagerImpl.class );
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final DecisionTableAnalyzer decisionTableAnalyzer = new DecisionTableAnalyzer( model,
                                                                                       updateManager,
                                                                                       connection );
        final List<Coordinate> updates = Collections.emptyList();
        decisionTableAnalyzer.analyze( updates );

        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass( List.class );

        verify( updateManager ).update( eq( model ),
                                        argumentCaptor.capture() );

        assertTrue( argumentCaptor.getValue()
                            .isEmpty() );

        decisionTableAnalyzer.activate();

        verify( connection ).activate();
    }
}
