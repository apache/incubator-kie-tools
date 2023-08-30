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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BaseSupplementaryFunctionEditorDefinitionTest {

    @Mock
    private BaseSupplementaryFunctionEditorDefinition baseSupplementaryFunctionEditorDefinition;

    @Mock
    private HasExpression hasExpression;

    private Optional<String> nodeUUID;

    private Optional<Context> expression;

    @Before
    public void setup() {
        final List<String> variables = new ArrayList<>();
        variables.add("var1");
        variables.add("var2");

        nodeUUID = Optional.of("uuid");

        expression = Optional.of(new Context());
        doCallRealMethod().when(baseSupplementaryFunctionEditorDefinition).createVariable(Mockito.<String>any());
        doCallRealMethod().when(baseSupplementaryFunctionEditorDefinition).enrich(nodeUUID,
                                                                                  hasExpression,
                                                                                  expression);

        when(baseSupplementaryFunctionEditorDefinition.getVariableNames()).thenReturn(variables);
    }

    @Test
    public void testDefaultVariableType() {
        final InformationItem variable = baseSupplementaryFunctionEditorDefinition.createVariable("variable");
        assertEquals(variable.getTypeRef().getLocalPart(), BuiltInType.STRING.getName());
    }

    @Test
    public void testEnrich() {
        baseSupplementaryFunctionEditorDefinition.enrich(nodeUUID,
                                                         hasExpression,
                                                         expression);

        final List<ContextEntry> entry = expression.get().getContextEntry();
        assertEquals(2, entry.size());
        checkIfIsBuiltInTypeString(entry.get(0));
        checkIfIsBuiltInTypeString(entry.get(1));
    }

    private void checkIfIsBuiltInTypeString(final ContextEntry entry) {
        assertEquals(BuiltInType.STRING.getName(),
                     entry.getVariable().getTypeRef().getLocalPart());
    }
}