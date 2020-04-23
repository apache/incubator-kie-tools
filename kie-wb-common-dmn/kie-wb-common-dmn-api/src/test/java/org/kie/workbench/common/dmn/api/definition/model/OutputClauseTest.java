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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutputClauseTest {

    private static final String OUTPUT_ID = "OUTPUT-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String NAME = "NAME";
    private static final String TEXT = "TEXT";
    private static final String CLAUSE_ID = "CLAUSE_ID";
    private static final String UNARY_ID = "UNARY_ID";
    private OutputClause outputClause;

    @Before
    public void setup() {
        outputClause = spy(new OutputClause());
    }

    @Test
    public void testGetHasTypeRefs() {
        final OutputClauseLiteralExpression outputClauseLiteralExpression = mock(OutputClauseLiteralExpression.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(outputClauseLiteralExpression).when(outputClause).getDefaultOutputEntry();

        when(outputClauseLiteralExpression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = outputClause.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(outputClause, hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final OutputClause source = new OutputClause(
                new Id(OUTPUT_ID),
                new Description(DESCRIPTION),
                buildOutputClauseUnaryTests(),
                buildOutputClauseLiteralExpression(),
                NAME,
                BuiltInType.BOOLEAN.asQName()
        );

        final OutputClause target = source.copy();

        assertNotNull(target);
        assertNotEquals(OUTPUT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertNotNull(target.getOutputValues());
        assertNotEquals(CLAUSE_ID, target.getOutputValues().getId());
        assertEquals(TEXT, target.getOutputValues().getText().getValue());
        assertNotNull(target.getOutputValues());
        assertNotEquals(UNARY_ID, target.getOutputValues().getId());
        assertEquals(TEXT, target.getOutputValues().getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getOutputValues().getConstraintType());
    }

    private OutputClauseUnaryTests buildOutputClauseUnaryTests() {
        return new OutputClauseUnaryTests(
                new Id(UNARY_ID),
                new Text(TEXT),
                ConstraintType.ENUMERATION
        );
    }

    private OutputClauseLiteralExpression buildOutputClauseLiteralExpression() {
        return new OutputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                new ImportedValues()
        );
    }
}
