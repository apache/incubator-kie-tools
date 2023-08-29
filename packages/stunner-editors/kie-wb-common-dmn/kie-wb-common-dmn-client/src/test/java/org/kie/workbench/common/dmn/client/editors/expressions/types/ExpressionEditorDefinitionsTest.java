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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExpressionEditorDefinitionsTest {

    @Mock
    private ExpressionEditorDefinition<Expression> definition1;

    @Mock
    private ExpressionEditorDefinition<Expression> definition2;

    @Mock
    private ExpressionEditorDefinition<Expression> definition3;

    private ExpressionEditorDefinitions definitions;

    @Before
    public void setup() {
        this.definitions = new ExpressionEditorDefinitions();
        this.definitions.add(definition1);
        this.definitions.add(definition2);
        this.definitions.add(definition3);

        doReturn(Optional.of(new LiteralExpression())).when(definition1).getModelClass();
        doReturn(ExpressionType.LITERAL_EXPRESSION).when(definition1).getType();

        doReturn(Optional.of(new Context())).when(definition2).getModelClass();
        doReturn(ExpressionType.CONTEXT).when(definition2).getType();

        doReturn(Optional.empty()).when(definition3).getModelClass();
        doReturn(ExpressionType.UNDEFINED).when(definition3).getType();
    }

    @Test
    public void textLookupForUndefinedExpression() {
        assertEquals(definition3,
                     definitions.getExpressionEditorDefinition(Optional.empty()).get());
    }

    @Test
    public void textLookupByExpressionClass() {
        assertEquals(definition1,
                     definitions.getExpressionEditorDefinition(Optional.of(new LiteralExpression())).get());
        assertEquals(definition2,
                     definitions.getExpressionEditorDefinition(Optional.of(new Context())).get());
    }

    @Test
    public void textLookupByExpressionType() {
        assertEquals(definition1,
                     definitions.getExpressionEditorDefinition(ExpressionType.LITERAL_EXPRESSION).get());
        assertEquals(definition2,
                     definitions.getExpressionEditorDefinition(ExpressionType.CONTEXT).get());
    }
}
