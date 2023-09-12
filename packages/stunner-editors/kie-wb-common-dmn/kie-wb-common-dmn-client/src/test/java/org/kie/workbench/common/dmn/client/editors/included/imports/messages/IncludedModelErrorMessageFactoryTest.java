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

package org.kie.workbench.common.dmn.client.editors.included.imports.messages;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.ERROR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsBlankErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsBlankErrorMessage_StrongMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsNotUniqueErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsNotUniqueErrorMessage_StrongMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelErrorMessageFactoryTest {

    @Mock
    private TranslationService translationService;

    private IncludedModelErrorMessageFactory factory;

    @Before
    public void setup() {
        factory = new IncludedModelErrorMessageFactory(translationService);
    }

    @Test
    public void testGetNameIsNotUniqueFlashMessage() {

        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final Type expectedType = ERROR;
        final String expectedStrongMessage = "StrongMessage";
        final String expectedRegularMessage = "RegularMessage";
        final String expectedElementSelector = "[data-card-uuid=\"1234\"] [data-field=\"title-input\"]";

        when(includedModel.getUUID()).thenReturn("1234");
        when(includedModel.getName()).thenReturn("file");
        when(translationService.format(IncludedModelNameIsNotUniqueErrorMessage_StrongMessage, "file")).thenReturn(expectedStrongMessage);
        when(translationService.format(IncludedModelNameIsNotUniqueErrorMessage_RegularMessage)).thenReturn(expectedRegularMessage);

        final FlashMessage flashMessage = factory.getNameIsNotUniqueFlashMessage(includedModel);

        assertEquals(expectedType, flashMessage.getType());
        assertEquals(expectedStrongMessage, flashMessage.getStrongMessage());
        assertEquals(expectedRegularMessage, flashMessage.getRegularMessage());
        assertEquals(expectedElementSelector, flashMessage.getElementSelector());
    }

    @Test
    public void testGetNameIsBlankFlashMessage() {

        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final Type expectedType = ERROR;
        final String expectedStrongMessage = "StrongMessage";
        final String expectedRegularMessage = "RegularMessage";
        final String expectedElementSelector = "[data-card-uuid=\"1234\"] [data-field=\"title-input\"]";

        when(includedModel.getUUID()).thenReturn("1234");
        when(translationService.format(IncludedModelNameIsBlankErrorMessage_StrongMessage)).thenReturn(expectedStrongMessage);
        when(translationService.format(IncludedModelNameIsBlankErrorMessage_RegularMessage)).thenReturn(expectedRegularMessage);

        final FlashMessage flashMessage = factory.getNameIsBlankFlashMessage(includedModel);

        assertEquals(expectedType, flashMessage.getType());
        assertEquals(expectedStrongMessage, flashMessage.getStrongMessage());
        assertEquals(expectedRegularMessage, flashMessage.getRegularMessage());
        assertEquals(expectedElementSelector, flashMessage.getElementSelector());
    }
}
