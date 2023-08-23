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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ConstraintPlaceholderHelperTest {

    @Mock
    private TranslationService translationService;

    private ConstraintPlaceholderHelper placeholderHelper;

    @Before
    public void setup() {
        placeholderHelper = new ConstraintPlaceholderHelper(translationService);
    }

    @Test
    public void testGetPlaceholderSentenceWhenTypeIsBuiltInTypeWithDefinedSentence() {

        final String expectedPlaceholder = "Example expression for a \"number\" data type: (1..10)";
        when(translationService.getTranslation("ConstraintPlaceholderHelper.SentenceNumber")).thenReturn(expectedPlaceholder);

        final String actualPlaceholder = placeholderHelper.getPlaceholderSentence("number");

        assertEquals(expectedPlaceholder, actualPlaceholder);
    }

    @Test
    public void testGetPlaceholderSentenceWhenTypeIsBuiltInTypeWithoutDefinedSentence() {

        final String expectedPlaceholder = "Enter a valid expression";
        when(translationService.format("ConstraintPlaceholderHelper.SentenceDefault")).thenReturn(expectedPlaceholder);

        final String actualPlaceholder = placeholderHelper.getPlaceholderSentence("context");

        assertEquals(expectedPlaceholder, actualPlaceholder);
    }

    @Test
    public void testGetPlaceholderSentenceWhenTypeIsNotBuiltInType() {

        final String expectedPlaceholder = "Enter a valid expression";
        when(translationService.format("ConstraintPlaceholderHelper.SentenceDefault")).thenReturn(expectedPlaceholder);

        final String actualPlaceholder = placeholderHelper.getPlaceholderSentence("Structure");

        assertEquals(expectedPlaceholder, actualPlaceholder);
    }

    @Test
    public void testGetPlaceholderSampleWhenTypeIsBuiltInTypeWithDefinedSample() {

        final String expectedPlaceholder = "123";
        when(translationService.getTranslation("ConstraintPlaceholderHelper.SampleNumber")).thenReturn(expectedPlaceholder);

        final String actualPlaceholder = placeholderHelper.getPlaceholderSample("number");

        assertEquals(expectedPlaceholder, actualPlaceholder);
    }

    @Test
    public void testGetPlaceholderSampleWhenTypeIsBuiltInTypeWithoutDefinedSample() {

        final String expectedPlaceholder = "Insert a value";
        when(translationService.format("ConstraintPlaceholderHelper.SampleDefault")).thenReturn(expectedPlaceholder);

        final String actualPlaceholder = placeholderHelper.getPlaceholderSample("context");

        assertEquals(expectedPlaceholder, actualPlaceholder);
    }

    @Test
    public void testGetPlaceholderSampleWhenTypeIsNotBuiltInType() {

        final String expectedPlaceholder = "Insert a value";
        when(translationService.format("ConstraintPlaceholderHelper.SampleDefault")).thenReturn(expectedPlaceholder);

        final String actualPlaceholder = placeholderHelper.getPlaceholderSample("Structure");

        assertEquals(expectedPlaceholder, actualPlaceholder);
    }
}
