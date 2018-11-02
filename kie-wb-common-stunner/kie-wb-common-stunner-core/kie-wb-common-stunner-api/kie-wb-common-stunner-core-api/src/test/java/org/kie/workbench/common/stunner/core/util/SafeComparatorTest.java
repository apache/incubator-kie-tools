/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.util;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertEquals;
import static org.kie.workbench.common.stunner.core.util.SafeComparator.TO_STRING_COMPARATOR;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SafeComparatorTest {

    @Mock
    private Comparator<Object> comparator;

    @Mock
    private Object param1;

    @Mock
    private Object param2;

    private SafeComparator<Object> safeComparator;

    @Before
    public void setUp() {
        safeComparator = SafeComparator.of(() -> comparator);
    }

    @Test
    public void testWhenFirstParamIsNull() {
        assertEquals(-1, safeComparator.compare(null, param2));
        verify(comparator, never()).compare(any(), any());
    }

    @Test
    public void testWhenSecondParamIsNull() {
        assertEquals(1, safeComparator.compare(param1, null));
        verify(comparator, never()).compare(any(), any());
    }

    @Test
    public void testWhenBothParamsAreNull() {
        assertEquals(0, safeComparator.compare(null, null));
        verify(comparator, never()).compare(any(), any());
    }

    @Test
    public void testWhenBothParamsAreNotNull() {
        int expectedResult = 1234;
        when(comparator.compare(param1, param2)).thenReturn(expectedResult);
        assertEquals(expectedResult, safeComparator.compare(param1, param2));
        verify(comparator).compare(param1, param2);
    }

    @Test
    public void testToStringComparator() {
        when(param1.toString()).thenReturn("value1");
        when(param2.toString()).thenReturn("value2");
        assertEquals(-1, TO_STRING_COMPARATOR.compare(param1, param2));
        assertEquals(0, TO_STRING_COMPARATOR.compare(param1, param1));
        assertEquals(1, TO_STRING_COMPARATOR.compare(param2, param1));
    }
}
