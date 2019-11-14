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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.VerifierWebWorkerConnection;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerTest {

    private List<ActionCol52> actionColumns;
    private List<ConditionCol52> conditionColumns;
    private List<Pattern52> patterns;
    private Pattern52 pattern;

    private VerifierWebWorkerConnection connection;
    private DTableUpdateManager updateManager;
    private GuidedDecisionTable52 model;
    private DecisionTableAnalyzer decisionTableAnalyzer;

    @Before
    public void setUp() throws Exception {
        actionColumns = new ArrayList<>();
        conditionColumns = new ArrayList<>();
        patterns = new ArrayList<>();
        pattern = new Pattern52();

        connection = mock(VerifierWebWorkerConnection.class);
        updateManager = mock(DTableUpdateManager.class);
        model = new GuidedDecisionTable52();
        decisionTableAnalyzer = new DecisionTableAnalyzer(model,
                                                          updateManager,
                                                          connection);
    }

    @Test
    public void testOnFocus() throws Exception {
        final List<Coordinate> updates = Collections.emptyList();
        decisionTableAnalyzer.analyze(updates);

        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(updateManager).update(eq(model),
                                     argumentCaptor.capture());

        assertTrue(argumentCaptor.getValue()
                           .isEmpty());

        decisionTableAnalyzer.activate();

        verify(connection).activate();
    }

    @Test
    public void testInsertConditionColumn() throws Exception {

        ConditionCol52 a = new ConditionCol52();
        ConditionCol52 b = new ConditionCol52();
        conditionColumns.add(a);
        conditionColumns.add(b);
        pattern.setChildColumns(conditionColumns);
        patterns.add(pattern);

        model.getConditions().addAll(patterns);

        decisionTableAnalyzer.insertColumn(a);
        verify(updateManager).newColumn(model, 2);
    }

    @Test
    public void testInsertActionColumn() throws Exception {

        ActionCol52 a = new ActionCol52();
        a.setHeader("A");
        ActionCol52 b = new ActionCol52();
        b.setHeader("B");
        actionColumns.add(a);
        actionColumns.add(b);
        model.setActionCols(actionColumns);

        decisionTableAnalyzer.insertColumn(b);

        verify(updateManager).newColumn(model, 3);
    }

    @Test
    public void testInsertBRLConditionColumn() throws Exception {
        BRLConditionColumn brlConditionOne = new BRLConditionColumn();
        BRLConditionVariableColumn a = new BRLConditionVariableColumn();
        a.setHeader("A");
        BRLConditionVariableColumn b = new BRLConditionVariableColumn();
        b.setHeader("B");

        BRLConditionColumn brlConditionTwo = new BRLConditionColumn();
        BRLConditionVariableColumn c = new BRLConditionVariableColumn();
        BRLConditionVariableColumn d = new BRLConditionVariableColumn();

        brlConditionOne.setChildColumns(Arrays.asList(a, b));
        brlConditionTwo.setChildColumns(Arrays.asList(c, d));

        model.getConditions().addAll(Arrays.asList(brlConditionOne, brlConditionTwo));

        decisionTableAnalyzer.insertColumn(brlConditionTwo);
        verify(updateManager).newColumn(model, 4);
    }

    @Test
    public void testInsertBRLActionColumn() throws Exception {
        BRLActionColumn brlActionOne = new BRLActionColumn();
        BRLActionVariableColumn a = new BRLActionVariableColumn();
        BRLActionVariableColumn b = new BRLActionVariableColumn();

        BRLActionColumn brlActionTwo = new BRLActionColumn();
        BRLActionVariableColumn c = new BRLActionVariableColumn();
        BRLActionVariableColumn d = new BRLActionVariableColumn();

        brlActionOne.setChildColumns(Arrays.asList(a, b));
        brlActionTwo.setChildColumns(Arrays.asList(c, d));

        model.setActionCols(Arrays.asList(brlActionOne, brlActionTwo));

        decisionTableAnalyzer.insertColumn(brlActionOne);
        verify(updateManager).newColumn(model, 2);
    }

    @Test
    public void testInsertBRLVariableColumn() throws Exception {
        ConditionCol52 a = new ConditionCol52();
        a.setHeader("A");
        ConditionCol52 b = new ConditionCol52();
        b.setHeader("B");
        pattern.setChildColumns(Arrays.asList(a, b));

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        BRLConditionVariableColumn c = new BRLConditionVariableColumn();
        c.setHeader("C");
        BRLConditionVariableColumn d = new BRLConditionVariableColumn();
        d.setHeader("D");
        brlCondition.setChildColumns(Arrays.asList(c, d));

        model.getConditions().addAll(Arrays.asList(pattern, brlCondition));

        decisionTableAnalyzer.insertColumn(d);
        verify(updateManager).newColumn(model, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertNonExistingColumn() throws Exception {
        ConditionCol52 a = new ConditionCol52();
        a.setHeader("A");
        ConditionCol52 b = new ConditionCol52();
        b.setHeader("B");
        pattern.setChildColumns(Arrays.asList(a));

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        BRLConditionVariableColumn c = new BRLConditionVariableColumn();
        BRLConditionVariableColumn d = new BRLConditionVariableColumn();
        brlCondition.setChildColumns(Arrays.asList(c, d));

        model.getConditions().addAll(Arrays.asList(pattern, brlCondition));

        decisionTableAnalyzer.insertColumn(b);
    }
}
