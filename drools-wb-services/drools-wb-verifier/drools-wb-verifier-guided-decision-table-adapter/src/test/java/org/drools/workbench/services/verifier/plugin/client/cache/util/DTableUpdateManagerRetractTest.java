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

package org.drools.workbench.services.verifier.plugin.client.cache.util;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.plugin.client.DTableUpdateManager;
import org.drools.workbench.services.verifier.plugin.client.DataBuilderProvider;
import org.drools.workbench.services.verifier.plugin.client.UpdateException;
import org.drools.workbench.services.verifier.plugin.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DTableUpdateManagerRetractTest {

    private DTableUpdateManager updateManager;
    private GuidedDecisionTable52 table52;

    private AnalyzerProvider analyzerProvider;

    @Mock
    private Analyzer analyzer;

    @Before
    public void setUp() throws
            Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withRetract()
                .withData(DataBuilderProvider
                                  .row(1,
                                       "a")
                                  .row(1,
                                       null)
                                  .end())
                .buildTable();

        updateManager = analyzerProvider.getUpdateManager(table52,
                                                          analyzer);
    }

    @Test
    public void testDoNotUpdateActionWhenValueDidNotChange() throws
            Exception,
            UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add(new Coordinate(0,
                                       3));

        updateManager.update(table52,
                             coordinates);

        verify(analyzer,
               never()).update(anySet());
    }

    @Test
    public void testFillNullAction() throws
            Exception,
            UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate(1,
                                               3);
        coordinates.add(coordinate);
        table52.getData()
                .get(1)
                .get(3)
                .setStringValue("a");

        updateManager.update(table52,
                             coordinates);

        verify(analyzer).update(anySet());
    }
}