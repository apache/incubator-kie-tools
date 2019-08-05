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
//@PrepareForTest({HasTypeRefHelper.class})
public class ListTest {

    private org.kie.workbench.common.dmn.api.definition.model.List list;

    @Before
    public void setup() {
        this.list = spy(new org.kie.workbench.common.dmn.api.definition.model.List());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression1 = mock(Expression.class); //added
        final Expression expression2 = mock(Expression.class); //added
        final List<Expression> expression = asList(expression1, expression2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(expression).when(list).getExpression();

        // mockStatic(HasTypeRefHelper.class);
        //when(HasTypeRefHelper.getFlatHasTypeRefs(expression)).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(expression1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(expression2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = list.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(list, hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(list.getRequiredComponentWidthCount(),
                     list.getComponentWidths().size());
        list.getComponentWidths().forEach(Assert::assertNull);
    }
}
