/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceUtilTest {

    private static final String KEY = "KEY";

    private static final String STRING_VALUE = "STRING_VALUE";

    @Test
    public void getGetStringValue() {
        Map values = new HashMap();

        String result = ServiceUtil.getStringValue(values,
                                                   KEY);
        assertNull(result);

        Object obj = mock(Object.class);
        when(obj.toString()).thenReturn(STRING_VALUE);
        values.put(KEY,
                   obj);

        result = ServiceUtil.getStringValue(values,
                                            KEY);
        assertEquals(STRING_VALUE,
                     result);
    }
}
