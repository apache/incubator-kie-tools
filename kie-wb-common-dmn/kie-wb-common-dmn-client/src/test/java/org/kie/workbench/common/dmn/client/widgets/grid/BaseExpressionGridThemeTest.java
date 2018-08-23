/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DescriptionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationParameterColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.DESCRIPTION_COLUMN_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.INPUT_CLAUSE_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.LABEL_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.LITERAL_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.OUTPUT_CLAUSE_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.RELATION_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.ROW_NUMBER_BACKGROUND_FILL_COLOUR;
import static org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme.UNDEFINED_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR;
import static org.mockito.Mockito.mock;

/**
 * Unfortunately this cannot be ran as a Parameterized test
 */
@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridThemeTest {

    private List<Pair<Class<? extends GridColumn>, String>> tests = new ArrayList<Pair<Class<? extends GridColumn>, String>>() {{
        add(new Pair<>(NameColumn.class, LABEL_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(InvocationParameterColumn.class, LABEL_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.NameColumn.class, LABEL_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(RelationColumn.class, RELATION_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(LiteralExpressionColumn.class, LITERAL_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(RowNumberColumn.class, ROW_NUMBER_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(DecisionTableRowNumberColumn.class, ROW_NUMBER_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(InputClauseColumn.class, INPUT_CLAUSE_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(OutputClauseColumn.class, OUTPUT_CLAUSE_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(DescriptionColumn.class, DESCRIPTION_COLUMN_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(ExpressionEditorColumn.class, EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR));
        add(new Pair<>(UndefinedExpressionColumn.class, UNDEFINED_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR));
    }};

    private BaseExpressionGridTheme theme;

    @Before
    public void setUp() throws Exception {
        theme = new BaseExpressionGridTheme();
    }

    @Test
    public void testGetBodyBackgroundFillColour() {
        tests.stream().forEach(test -> {
            final GridColumn column = mock(test.getK1());
            final Rectangle rectangle = theme.getBodyBackground(column);
            assertEquals(test.getK2(), rectangle.getFillColor());
        });
    }
}
