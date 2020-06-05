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
import org.drools.workbench.services.verifier.plugin.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DTableUpdateManagerIsNullTest {

    private DTableUpdateManager updateManager;
    private GuidedDecisionTable52 table52;

    @Mock
    private Analyzer analyzer;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws
            Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                .withConditionBooleanColumn("a",
                                            "Person",
                                            "name",
                                            "== null")

                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(true,
                                       true)
                                  .row(false,
                                       true)
                                  .row(null,
                                       true)
                                  .end())
                .buildTable();

        updateManager = analyzerProvider.getUpdateManager(table52,
                                                          analyzer);
    }

    @Test
    public void testTrueDidNotChange() throws
            Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0,
                                       2));

        updateManager.update(table52,
                             coordinates);

        verify(analyzer,
               never()).update(anySet());
    }

    @Test
    public void testFalseDidNotChange() throws
            Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(1,
                                       2));

        updateManager.update(table52,
                             coordinates);

        verify(analyzer,
               never()).update(anySet());
    }

    @Test
    public void testNullDidNotChange() throws
            Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(2,
                                       2));

        updateManager.update(table52,
                             coordinates);

        verify(analyzer,
               never()).update(anySet());
    }

    @Test
    public void testSetTrueToFalse() throws
            Exception {
        set(0,
            3,
            false);

        verify(analyzer).update(anySet());
    }

    @Test
    public void testSetTrueToNull() throws
            Exception {
        set(0,
            3,
            (Boolean) null);

        verify(analyzer).update(anySet());
    }

    @Test
    public void testSetFalseToTrue() throws
            Exception {
        set(1,
            3,
            true);

        verify(analyzer).update(anySet());
    }

    @Test
    public void testSetFalseToNull() throws
            Exception {
        set(1,
            2,
            (Boolean) null);

        verify(analyzer,
               never()).update(anySet());
    }

    @Test
    public void testSetNullToTrue() throws
            Exception {
        set(2,
            3,
            true);

        verify(analyzer).update(anySet());
    }

    @Test
    public void testSetNullToFalse() throws
            Exception {
        set(2,
            2,
            false);

        verify(analyzer,
               never()).update(anySet());
    }

    private void set(final int row,
                     final int col,
                     final Boolean value) {
        final ArrayList<Coordinate> coordinates = new ArrayList<>();
        final Coordinate coordinate = new Coordinate(row,
                                                     col);
        coordinates.add(coordinate);
        table52.getData()
                .get(row)
                .get(col)
                .setBooleanValue(
                        value);

        updateManager.update(table52,
                             coordinates);
    }
}