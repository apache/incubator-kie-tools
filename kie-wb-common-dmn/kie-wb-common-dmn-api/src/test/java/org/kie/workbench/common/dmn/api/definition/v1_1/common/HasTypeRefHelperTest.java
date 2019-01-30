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

package org.kie.workbench.common.dmn.api.definition.v1_1.common;

import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasTypeRefs;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HasTypeRefHelperTest {

    @Test
    public void testGetNotNullHasTypeRefs() {

        final HasTypeRef hasTypeRef = mock(HasTypeRef.class);
        final List<HasTypeRef> expectedHasTypeRefs = asList(mock(HasTypeRef.class), mock(HasTypeRef.class));

        when(hasTypeRef.getHasTypeRefs()).thenReturn(expectedHasTypeRefs);

        final List<HasTypeRef> actualHasTypeRefs = HasTypeRefHelper.getNotNullHasTypeRefs(hasTypeRef);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testGetNotNullHasTypeRefsWhenHasTypeRefIsNull() {

        final List<HasTypeRef> expectedHasTypeRefs = emptyList();
        final List<HasTypeRef> actualHasTypeRefs = HasTypeRefHelper.getNotNullHasTypeRefs(null);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testGetFlatHasTypeRefs() {

        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef5 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef6 = mock(HasTypeRef.class);
        final List<HasTypeRef> typeRefList1 = asList(hasTypeRef3, hasTypeRef4);
        final List<HasTypeRef> typeRefList2 = asList(hasTypeRef5, hasTypeRef6);
        final List<HasTypeRefs> hasTypeRefs = asList(hasTypeRef1, hasTypeRef2);

        when(hasTypeRef1.getHasTypeRefs()).thenReturn(typeRefList1);
        when(hasTypeRef2.getHasTypeRefs()).thenReturn(typeRefList2);

        final List<HasTypeRef> actual = HasTypeRefHelper.getFlatHasTypeRefs(hasTypeRefs);
        final List<HasTypeRef> expected = asList(hasTypeRef3, hasTypeRef4, hasTypeRef5, hasTypeRef6);

        assertEquals(expected, actual);
    }
}
