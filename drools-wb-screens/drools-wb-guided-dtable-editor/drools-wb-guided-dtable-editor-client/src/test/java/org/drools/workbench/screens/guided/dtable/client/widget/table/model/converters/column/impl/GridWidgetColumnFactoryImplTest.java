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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.rule.client.widget.attribute.RuleAttributeWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridWidgetColumnFactoryImplTest extends BaseConverterTest {

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;

    @Mock
    private GuidedDecisionTableModellerView dtModellerView;

    @Mock
    private GuidedDecisionTableModellerView.Presenter dtModellerPresenter;

    @Mock
    private GridLayer gridLayer;

    private GridWidgetColumnFactory factory;

    private GuidedDecisionTablePresenter.Access access;

    @Before
    public void setup() {
        super.setup();
        factory = new GridWidgetColumnFactoryImpl();
        factory.setConverters(getConverters());

        when(model.getHitPolicy()).thenReturn(GuidedDecisionTable52.HitPolicy.NONE);

        access = new GuidedDecisionTablePresenter.Access();
    }

    @Override
    protected GuidedDecisionTable52 getModel() {
        return model;
    }

    @Override
    protected AsyncPackageDataModelOracle getOracle() {
        return oracle;
    }

    @Override
    protected GuidedDecisionTableView.Presenter getPresenter() {
        when(dtPresenter.getModellerPresenter()).thenReturn(dtModellerPresenter);
        when(dtModellerPresenter.getView()).thenReturn(dtModellerView);
        when(dtModellerView.getGridLayerView()).thenReturn(gridLayer);

        return dtPresenter;
    }

    @Test
    public void columnResizingListenerSetup_RowNumberColumn() {
        final BaseColumn column = new RowNumberCol52();

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertFalse(uiColumn instanceof BaseUiColumn);

        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());
        assertEquals(50.0,
                     column.getWidth(),
                     0.0);
    }

    @Test
    public void columnResizingListenerSetup_DescriptionColumn() {
        final BaseColumn column = new DescriptionCol52();

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertTrue(uiColumn instanceof BaseUiColumn);
        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());
        assertEquals(150.0,
                     column.getWidth(),
                     0.0);

        uiColumn.setWidth(200.0);
        assertEquals(200,
                     column.getWidth());
    }

    @Test
    public void columnResizingListenerSetup_MetadataColumn() {
        final MetadataCol52 column = new MetadataCol52();
        column.setMetadata("metadata");

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertTrue(uiColumn instanceof BaseUiColumn);
        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());

        uiColumn.setWidth(200.0);
        assertEquals(200,
                     column.getWidth());
    }

    @Test
    public void columnResizingListenerSetup_AttributeColumn() {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(RuleAttributeWidget.SALIENCE_ATTR);

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertTrue(uiColumn instanceof BaseUiColumn);
        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());

        uiColumn.setWidth(200.0);
        assertEquals(200,
                     column.getWidth());
    }

    @Test
    public void columnResizingListenerSetup_ConditionColumn() {
        final Pattern52 pattern = mock(Pattern52.class);
        final ConditionCol52 column = new ConditionCol52();
        column.setFactField("MyField");
        column.setHeader("MyColumn");

        when(model.getPattern(eq(column))).thenReturn(pattern);
        when(pattern.getFactType()).thenReturn("MyFact");
        when(oracle.getFieldType("MyFact",
                                 "MyField")).thenReturn(DataType.TYPE_STRING);

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertTrue(uiColumn instanceof BaseUiColumn);
        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());

        uiColumn.setWidth(200.0);
        assertEquals(200,
                     column.getWidth());
    }

    @Test
    public void columnResizingListenerSetup_ActionSetFieldColumn() {
        final Pattern52 pattern = mock(Pattern52.class);
        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setFactField("MyField");
        column.setHeader("MyColumn");
        column.setBoundName("$f");

        when(model.getConditions()).thenReturn(new ArrayList<CompositeColumn<? extends BaseColumn>>() {{
            add(pattern);
        }});
        when(pattern.getFactType()).thenReturn("MyFact");
        when(pattern.getBoundName()).thenReturn("$f");
        when(pattern.isBound()).thenReturn(true);
        when(oracle.getFieldType("MyFact",
                                 "MyField")).thenReturn(DataType.TYPE_STRING);

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertTrue(uiColumn instanceof BaseUiColumn);
        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());

        uiColumn.setWidth(200.0);
        assertEquals(200,
                     column.getWidth());
    }

    @Test
    public void columnResizingListenerSetup_ActionInsertFactColumn() {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setFactType("MyFact");
        column.setFactField("MyField");
        column.setHeader("MyColumn");

        when(oracle.getFieldType("MyFact",
                                 "MyField")).thenReturn(DataType.TYPE_STRING);

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertTrue(uiColumn instanceof BaseUiColumn);
        assertEquals((int) uiColumn.getWidth(),
                     column.getWidth());

        uiColumn.setWidth(200.0);
        assertEquals(200,
                     column.getWidth());
    }

    @Test
    public void testHiddenColumn_ConditionColumn() {
        final Pattern52 pattern = mock(Pattern52.class);
        final ConditionCol52 column = new ConditionCol52();
        column.setFactField("MyField");
        column.setHeader("MyColumn");
        column.setHideColumn(true);

        when(model.getPattern(eq(column))).thenReturn(pattern);
        when(pattern.getFactType()).thenReturn("MyFact");
        when(oracle.getFieldType("MyFact",
                                 "MyField")).thenReturn(DataType.TYPE_STRING);

        final GridColumn<?> uiColumn = factory.convertColumn(column,
                                                             access,
                                                             gridWidget);

        assertEquals(false,
                     uiColumn.isVisible());
    }

    @Test
    public void testNumericColumn_IsContainedInOperator() {
        final BRLConditionVariableColumn column = new BRLConditionVariableColumn("$a",
                                                                                 DataType.TYPE_NUMERIC_INTEGER,
                                                                                 "Applicant",
                                                                                 "age",
                                                                                 "in");
        column.setHeader("age is");

        final GridColumn gridColumn = factory.convertColumn(column, access, gridWidget);

        assertTrue(gridColumn instanceof StringUiColumn);
    }

    @Test
    public void testNumericColumn_EqualOperator() {
        final BRLConditionVariableColumn column = new BRLConditionVariableColumn("$a",
                                                                                 DataType.TYPE_NUMERIC_INTEGER,
                                                                                 "Applicant",
                                                                                 "age",
                                                                                 "==");
        column.setHeader("age equal to");

        when(oracle.getFieldType("Applicant",
                                 "age")).thenReturn(DataType.TYPE_NUMERIC_INTEGER);

        final GridColumn gridColumn = factory.convertColumn(column, access, gridWidget);

        assertTrue(gridColumn instanceof IntegerUiColumn);
    }
}
