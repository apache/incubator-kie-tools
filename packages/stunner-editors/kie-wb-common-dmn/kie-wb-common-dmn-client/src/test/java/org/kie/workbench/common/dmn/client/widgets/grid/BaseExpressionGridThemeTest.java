/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionKindRowColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationParameterColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.uberfire.commons.Pair;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Unfortunately this cannot be ran as a Parameterized test
 */
@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridThemeTest {

    private List<Pair<Class<? extends GridColumn>, String>> tests = new ArrayList<Pair<Class<? extends GridColumn>, String>>() {{
        add(new Pair<>(NameColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(InvocationParameterColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.NameColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(RelationColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(LiteralExpressionColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(RowNumberColumn.class, KIEColours.HEADER_BACKGROUND_WHITE));
        add(new Pair<>(DecisionTableRowNumberColumn.class, KIEColours.HEADER_BACKGROUND_WHITE));
        add(new Pair<>(InputClauseColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(OutputClauseColumn.class, KIEColours.HEADER_BACKGROUND_DARK_BLUE));
        add(new Pair<>(RuleAnnotationClauseColumn.class, KIEColours.HEADER_BACKGROUND_WHITE));
        add(new Pair<>(ExpressionEditorColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(UndefinedExpressionColumn.class, KIEColours.HEADER_BACKGROUND_LIGHT_BLUE));
        add(new Pair<>(FunctionKindRowColumn.class, KIEColours.HEADER_BACKGROUND_WHITE));
    }};

    private BaseExpressionGridTheme theme;

    @Before
    public void setUp() throws Exception {
        theme = new BaseExpressionGridTheme();
    }

    @Test
    public void testGetHeaderBackgroundFillColour() {
        tests.stream().forEach(test -> {
            final GridColumn column = mock(test.getK1());
            final Rectangle rectangle = theme.getHeaderBackground(column);
            assertEquals("Column '" + column.getClass().getSimpleName() + "' has wrong colour.",
                         test.getK2(),
                         rectangle.getFillColor());
        });
    }
}
