/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ValidationMessageTranslatorUtilsTest {

    @Mock
    private Instance<ValidationMessageTranslator> messageTranslatorInstance;

    private ValidationMessageTranslatorUtils translatorUtils;

    @Before
    public void setUp() {
        translatorUtils = new ValidationMessageTranslatorUtils( messageTranslatorInstance );
    }

    @Test
    public void translateMessageExists() {
        translatorUtils.setValidationMessageTranslators( Arrays.asList( new UniversalTestTranslator() ) );

        TestMessage messageToTranslate = new TestMessage();

        List<ValidationMessage> result = translatorUtils.translate( Arrays.asList( messageToTranslate ) );
        assertEquals( 1, result.size() );
        assertEquals( "Translated message", result.get( 0 ).getText() );
    }

    public void translateMessageNonAcceptingTranslator() {
        translatorUtils.setValidationMessageTranslators( Arrays.asList( new FalsumTestTranslator() ) );

        TestMessage messageToTranslate = new TestMessage();

        List<ValidationMessage> result = translatorUtils.translate( Arrays.asList( messageToTranslate ) );
        assertEquals( 1, result.size() );
        assertNull( result.get( 0 ).getText() );
    }

    private class UniversalTestTranslator implements ValidationMessageTranslator {

        @Override
        public boolean accept( ValidationMessage message ) {
            return true;
        }

        @Override
        public ValidationMessage translate( ValidationMessage checkMessage ) {
            checkMessage.setText( "Translated message" );
            return checkMessage;
        }
    }

    private class FalsumTestTranslator implements ValidationMessageTranslator {

        @Override
        public boolean accept( ValidationMessage message ) {
            return false;
        }

        @Override
        public ValidationMessage translate( ValidationMessage checkMessage ) {
            return null;
        }
    }

    private class TestMessage extends ValidationMessage {
    }
}
