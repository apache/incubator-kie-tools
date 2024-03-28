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


package org.kie.workbench.common.stunner.sw.client.shapes;

import java.util.List;

import org.junit.Before;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class HasTranslationGeneralTest {

    private TranslationService translationService;

    protected static final String TEST_STRING = "TEST_STRING";

    @Before
    public void init() {
        translationService = mock(TranslationService.class);
        when(translationService.getTranslation(anyString())).thenReturn(TEST_STRING);
    }

    public void assertTranslations(String expectedResult, String actualResult, String... translationLabels) {
        assertEquals(expectedResult, actualResult);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        verify(translationService, times(translationLabels.length)).getTranslation(argument.capture());

        List<String> values = argument.getAllValues();

        for (String label : translationLabels) {
            assertTrue(label + " is not invoked.\nLabels invoked: " + values.toString(), values.contains(label));
        }
    }

    public String getTranslation(String constant) {
        return translationService.getTranslation(constant);
    }
}
