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

package org.drools.workbench.services.verifier.plugin.client.cache.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.plugin.client.DTableUpdateManager;
import org.drools.workbench.services.verifier.plugin.client.DataBuilderProvider;
import org.drools.workbench.services.verifier.plugin.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DTableUpdateManagerAttributesTest {

    @Captor
    private ArgumentCaptor<Set> setArgumentCaptor;
    private DTableUpdateManager updateManager;
    private GuidedDecisionTable52 table52;
    private AnalyzerProvider analyzerProvider;
    @Mock
    private Analyzer analyzer;

    @Before
    public void setUp()
            throws Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                .withAttributeColumn(Attribute.DATE_EFFECTIVE)
                .withAttributeColumn(Attribute.DATE_EXPIRES)
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(new Date(10), new Date(100), 1, true)
                                  .row(new Date(10), new Date(100), 1, true)
                                  .end())
                .buildTable();

        updateManager = analyzerProvider.getUpdateManager(table52,
                                                          analyzer);
    }

    @Test
    public void testSetDateEffectiveToNull() throws
            Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Coordinate coordinate = new Coordinate(0,
                                               3);
        coordinates.add(coordinate);
        table52.getData()
                .get(0)
                .get(3)
                .setDateValue(null);

        updateManager.update(table52,
                             coordinates);

        verify(analyzer).update(setArgumentCaptor.capture());
        Set value = setArgumentCaptor.getValue();
        assertEquals(1, value.size());
        assertEquals(0, value.iterator().next());
    }

    @Test
    public void testSetDateExpiresToNull() throws
            Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Coordinate coordinate = new Coordinate(0,
                                               3);
        coordinates.add(coordinate);
        table52.getData()
                .get(0)
                .get(3)
                .setDateValue(null);

        updateManager.update(table52,
                             coordinates);

        verify(analyzer).update(setArgumentCaptor.capture());
        Set value = setArgumentCaptor.getValue();
        assertEquals(1, value.size());
        assertEquals(0, value.iterator().next());
    }
}