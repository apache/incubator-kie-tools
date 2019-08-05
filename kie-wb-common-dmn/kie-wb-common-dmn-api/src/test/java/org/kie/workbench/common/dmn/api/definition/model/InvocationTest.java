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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)

public class InvocationTest {

    private Invocation invocation;

    @Before
    public void setup() {
        this.invocation = spy(new Invocation());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final Binding binding1 = mock(Binding.class); //added
        final Binding binding2 = mock(Binding.class); //added
        final List<Binding> binding = asList(binding1, binding2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(invocation).getExpression();
        doReturn(binding).when(invocation).getBinding();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(binding1.getHasTypeRefs()).thenReturn(asList(hasTypeRef3));
        when(binding2.getHasTypeRefs()).thenReturn(asList(hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = invocation.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(invocation, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(invocation.getRequiredComponentWidthCount(),
                     invocation.getComponentWidths().size());
        invocation.getComponentWidths().forEach(Assert::assertNull);
    }
}
