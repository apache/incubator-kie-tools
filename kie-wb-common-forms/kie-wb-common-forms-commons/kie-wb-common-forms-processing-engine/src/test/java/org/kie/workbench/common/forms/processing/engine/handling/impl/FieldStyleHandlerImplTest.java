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

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.imp.FieldStyleHandlerImpl;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FieldStyleHandlerImplTest extends AbstractFormEngineTest {

    protected FieldStyleHandlerImpl fieldStyleHandler;

    @Before
    public void init() {
        super.init();
        fieldStyleHandler = new FieldStyleHandlerImpl();
    }

    @Test
    public void testValidFieldStyleClear() {
        for ( String formField : ALL_FIELDS ) {
            FormField field = formFieldProvider.findFormField( formField );

            assertNotNull( field );
            if ( field != null ) {
                fieldStyleHandler.clearFieldError( field );
            }
        }

        checkClearedFields( ALL_FIELDS );
    }

    @Test
    public void testValidFieldErrorDisplay() {
        for ( String formField : ALL_FIELDS ) {
            FormField field = formFieldProvider.findFormField( formField );

            assertNotNull( field );
            if ( field != null ) {
                fieldStyleHandler.clearFieldError( field );
                fieldStyleHandler.displayFieldError( field, "There's something terribly wrong here!" );
            }
        }

        checkWrongFields( ALL_FIELDS );
    }

    @Test
    public void testClearFieldWithoutFormGroup() {
        String fieldName = "without_group";

        FormField test = generateFieldWithoutGroup( fieldName, fieldName, true );

        fieldStyleHandler.clearFieldError( test );

        FormFieldContentHelper field = helpers.get( fieldName );
        assertNotNull( field );
        if ( field != null ) {
            verify( field.getFormGroup(), never() ).removeClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
        }
    }

    @Test
    public void testSetErrorFieldWithoutFormGroup() {
        String fieldName = "without_group";

        FormField test = generateFieldWithoutGroup( fieldName, fieldName, true );

        fieldStyleHandler.displayFieldError( test, "There's something terribly wrong here!" );

        FormFieldContentHelper field = helpers.get( fieldName );
        assertNotNull( field );
        if ( field != null ) {
            verify( field.getFormGroup(), never() ).removeClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
            verify( field.getFormGroup(), never() ).addClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
        }
    }

    @Test
    public void testClearFieldWithoutFormHelpGroup() {
        String fieldName = "without_helpBlock";

        FormField test = generateFormFieldWithoutHelpblock( fieldName, fieldName, true );

        fieldStyleHandler.clearFieldError( test );

        FormFieldContentHelper field = helpers.get( fieldName );
        assertNotNull( field );
        if ( field != null ) {
            verify( field.getFormGroup(), atLeastOnce() ).removeClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
        }
    }

    @Test
    public void testSetErrorFieldWithoutHelpBlcok() {
        String fieldName = "without_helpBlock";

        FormField test = generateFormFieldWithoutHelpblock( fieldName, fieldName, true );

        fieldStyleHandler.displayFieldError( test, "There's something terribly wrong here!" );

        FormFieldContentHelper field = helpers.get( fieldName );
        assertNotNull( field );
        if ( field != null ) {
            verify( field.getFormGroup() ).addClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
            verify( field.getHelpBlock(), never() ).setInnerHTML( anyString() );
        }
    }

}
