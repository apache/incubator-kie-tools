/*
 * Copyright 2015 JBoss Inc
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.mockito.Mock;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerTest {

    @GwtMock AnalysisConstants analysisConstants;
    @GwtMock DateTimeFormat dateTimeFormat;

    @Mock AsyncPackageDataModelOracle oracle;

    EventBusMock eventBus;

    @Before
    public void setUp() throws Exception {

        when(oracle.getFieldType("Person", "age")).thenReturn(DataType.TYPE_NUMERIC_INTEGER);
        when(oracle.getFieldType("Person", "approved")).thenReturn(DataType.TYPE_BOOLEAN);
        when(oracle.getFieldType("Person", "name")).thenReturn(DataType.TYPE_STRING);

        eventBus = new EventBusMock();

        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put(ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy");
        ApplicationPreferences.setUp(preferences);
    }

    @Test
    public void testEmpty() throws Exception {
        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(new AsyncPackageDataModelOracleImpl(),
                                                                   getModel(),
                                                                   eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));

        assertTrue(eventBus.getUpdateColumnDataEvent().getColumnData().isEmpty());

    }

    @Test
    public void testRuleHasNoAction() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withIntegerColumn("a", "Person", "age", ">")
                .withData(new Object[][]{{1, "description", 0}})
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
                                                                   table52,
                                                                   eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));
        assertContains("RuleHasNoAction", eventBus.getUpdateColumnDataEvent().getColumnData());

    }

    @Test
    public void testRuleHasNoActionSet() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withIntegerColumn("a", "Person", "age", ">")
                .withActionSetField("a", "age", DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
                .withActionSetField("a", "name", DataType.TYPE_STRING)
                .withData(new Object[][]{{1, "description", 0, null, null, ""}})
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
                                                                   table52,
                                                                   eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));
        assertContains("RuleHasNoAction", eventBus.getUpdateColumnDataEvent().getColumnData());

    }

    @Test
    public void testRuleHasNoRestrictions() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
                .withData(new Object[][]{{1, "description", true}})
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
                                                                   table52,
                                                                   eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));
        assertContains("RuleHasNoRestrictionsAndWillAlwaysFire", eventBus.getUpdateColumnDataEvent().getColumnData());

    }

    @Test
    public void testRuleHasNoRestrictionsSet() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withIntegerColumn("a", "Person", "age", ">")
                .withStringColumn("a", "Person", "name", "==")
                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
                .withData(new Object[][]{{1, "description", null, "", true}})
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
                                                                   table52,
                                                                   eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));
        assertContains("RuleHasNoRestrictionsAndWillAlwaysFire", eventBus.getUpdateColumnDataEvent().getColumnData());

    }

    @Test
    public void testMultipleValuesForOneAction() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withIntegerColumn("a", "Person", "age", ">")
                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
                .withData(new Object[][]{{1, "description", 100, true, false}})
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
                                                                   table52,
                                                                   eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));
        assertContains("MultipleValuesForOneAction", eventBus.getUpdateColumnDataEvent().getColumnData());

    }

    @Test
    public void testRedundancy() throws Exception {
        GuidedDecisionTable52 table52 = new LimitedGuidedDecisionTableBuilder("org.test",
                new ArrayList<Import>(),
                "mytable")
                .withIntegerColumn("a", "Person", "age", "==", 0)
                .withAction("a", "Person", "approved", new DTCellValue52() {
                    {
                        setBooleanValue(true);
                    }
                }).withAction("a", "Person", "approved", new DTCellValue52() {
                    {
                        setBooleanValue(true);
                    }
                })
                .withData(new Object[][]{
                        {1, "description", true, true, false},
                        {2, "description", true, false, true}})
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
                table52,
                eventBus);

        analyzer.onValidate(new ValidateEvent(new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>()));

        List<CellValue<? extends Comparable<?>>> result = eventBus.getUpdateColumnDataEvent().getColumnData();
        assertContains("ThisRowIsRedundantTo(2)", result);
        assertContains("ThisRowIsRedundantTo(1)", result);

    }

    private GuidedDecisionTable52 getModel() {
        GuidedDecisionTable52 guidedDecisionTable52 = new GuidedDecisionTable52();
        guidedDecisionTable52.initAnalysisColumn();
        return guidedDecisionTable52;
    }
}