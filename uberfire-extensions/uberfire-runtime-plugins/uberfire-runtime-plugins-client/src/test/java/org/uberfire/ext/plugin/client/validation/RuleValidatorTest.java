/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.validation;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RuleValidatorTest {

    private static final String INVALID_VALUE = "invalid*";
    private static final String VALID_VALUE = "valid";

    private static final String ERROR_MESSAGE = "error";

    private FormGroup field;

    private HelpBlock help;

    private RuleValidator validRuleValidator;

    private RuleValidator invalidRuleValidator;

    @Before
    public void setup() {
        field = mock( FormGroup.class );
        help = mock( HelpBlock.class );

        validRuleValidator = new RuleValidator() {
            @Override
            public boolean isValid( final String value ) {
                return true;
            }

            @Override
            public String getValidationError() {
                return null;
            }
        };

        invalidRuleValidator = new RuleValidator() {
            @Override
            public boolean isValid( final String value ) {
                return false;
            }

            @Override
            public String getValidationError() {
                return ERROR_MESSAGE;
            }
        };
    }

    @Test
    public void validRuleValidatorTest() {
        validRuleValidator.validateFieldInline( VALID_VALUE, field, help );

        verify( field ).setValidationState( ValidationState.NONE );
        verify( help ).setText( "" );
    }

    @Test
    public void invalidRuleValidatorTest() {
        invalidRuleValidator.validateFieldInline( INVALID_VALUE, field, help );

        verify( field ).setValidationState( ValidationState.ERROR );
        verify( help ).setText( ERROR_MESSAGE );
    }
}
