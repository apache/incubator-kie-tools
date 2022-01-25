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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FunctionNamingServiceTest {

    private static final String FUNCTION = "FUNCTION";

    private static final String PARAM = "PARAM";

    private static final String TRANSLATED_NAME = "TRANSLATED_NAME";

    @Mock
    private ClientTranslationService translationService;

    private FunctionNamingService namingService;

    @Before
    public void setUp() {
        namingService = new FunctionNamingService(translationService);
    }

    @Test
    public void testGetFunctionName() {
        when(translationService.getValue("KieFunctions." + FUNCTION)).thenReturn(TRANSLATED_NAME);
        assertEquals(TRANSLATED_NAME, namingService.getFunctionName(FUNCTION));
    }

    @Test
    public void testGetParamName() {
        when(translationService.getValue("KieFunctions." + FUNCTION + "." + PARAM + ".name")).thenReturn(TRANSLATED_NAME);
        assertEquals(TRANSLATED_NAME, namingService.getParamName(FUNCTION, PARAM));
    }

    @Test
    public void testGetParamHelp() {
        when(translationService.getValue("KieFunctions." + FUNCTION + "." + PARAM + ".help")).thenReturn(TRANSLATED_NAME);
        assertEquals(TRANSLATED_NAME, namingService.getParamHelp(FUNCTION, PARAM));
    }
}
