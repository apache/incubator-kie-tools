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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import junit.framework.TestCase;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.imp.FieldStyleHandlerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.imp.FormFieldImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.mock.FormFieldProviderMock;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.Model;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.User;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.*;

public abstract class AbstractFormEngineTest extends TestCase {

    public static final String VALUE_FIELD = "value";
    public static final String USER_NAME_FIELD = "user_name";
    public static final String USER_LAST_NAME_FIELD = "user_lastName";
    public static final String USER_BIRTHDAY_FIELD = "user_birthday";
    public static final String USER_MARRIED_FIELD = "user_married";
    public static final String USER_ADDRESS_FIELD = "user_address";

    public static final String[] ALL_FIELDS = {
            VALUE_FIELD,
            USER_NAME_FIELD,
            USER_LAST_NAME_FIELD,
            USER_BIRTHDAY_FIELD,
            USER_MARRIED_FIELD,
            USER_ADDRESS_FIELD
    };

    protected int executionCounts;

    @Mock
    protected FieldChangeHandler anonymous;

    @Mock
    protected FieldChangeHandler value;

    @Mock
    protected FieldChangeHandler userName;

    @Mock
    protected FieldChangeHandler userLastName;

    @Mock
    protected FieldChangeHandler userBirthday;

    @Mock
    protected FieldChangeHandler userMarried;

    @Mock
    protected FieldChangeHandler userAddress;

    protected final Map<String, FormFieldContentHelper> helpers = new HashMap<>();

    protected FormFieldProviderMock formFieldProvider;

    protected Model model;

    protected void init() {

        User user = new User();

        user.setName( "John" );
        user.setLastName( "Snow" );
        user.setBirtDay( new Date() );
        user.setMarried( false );
        user.setAddress( "Winterfell" );

        model = new Model();
        model.setUser( user );
        model.setValue( 25 );

        helpers.clear();

        formFieldProvider = new FormFieldProviderMock();

        formFieldProvider.addFormField( generateFormField( VALUE_FIELD, "value", true ) );
        formFieldProvider.addFormField( generateFormField( USER_NAME_FIELD, "user.name", true ) );
        formFieldProvider.addFormField( generateFormField( USER_LAST_NAME_FIELD, "user.lastName", true ) );
        formFieldProvider.addFormField( generateFormField( USER_BIRTHDAY_FIELD, "user.birthday", true ) );
        formFieldProvider.addFormField( generateFormField( USER_MARRIED_FIELD, "user.married", true ) );
        formFieldProvider.addFormField( generateFormField( USER_ADDRESS_FIELD , "user.address", true ) );

        executionCounts = 0;

        Answer answer = new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                executionCounts ++;
                return null;
            }
        };
        doAnswer( answer ).when( anonymous ).onFieldChange( anyString(), anyObject() );
        doAnswer( answer ).when( value ).onFieldChange( anyString(), anyObject() );
        doAnswer( answer ).when( userName ).onFieldChange( anyString(), anyObject() );
        doAnswer( answer ).when( userLastName ).onFieldChange( anyString(), anyObject() );
        doAnswer( answer ).when( userBirthday ).onFieldChange( anyString(), anyObject() );
        doAnswer( answer ).when( userMarried ).onFieldChange( anyString(), anyObject() );
        doAnswer( answer ).when( userAddress ).onFieldChange( anyString(), anyObject() );
    }

    public FormField generateFormField( String fieldName, String binding, boolean validateOnChange ) {

        Element helpBlockElement = mock( Element.class );

        when( helpBlockElement.getTagName() ).thenReturn( DivElement.TAG );

        when( helpBlockElement.getId() ).thenReturn( fieldName + FieldStyleHandlerImpl.HELP_BLOCK_SUFFIX );

        when( helpBlockElement.getNodeType() ).thenReturn( Node.ELEMENT_NODE );

        Element formGroupElement = mock( Element.class );

        when( formGroupElement.getTagName() ).thenReturn( DivElement.TAG );

        when( formGroupElement.getId() ).thenReturn( fieldName + FieldStyleHandlerImpl.FORM_GROUP_SUFFIX );

        when( formGroupElement.getChildCount() ).thenReturn( 1 );

        when( formGroupElement.getChild( 0 ) ).thenReturn( helpBlockElement );

        Element widgetElement = mock( Element.class );

        when( widgetElement.getTagName() ).thenReturn( DivElement.TAG );

        when( widgetElement.getId() ).thenReturn( fieldName );

        when( widgetElement.getParentElement() ).thenReturn( formGroupElement );

        Widget widget = mock( Widget.class );

        when( widget.getElement() ).thenReturn( widgetElement );

        IsWidget isWidget = mock( IsWidget.class );

        when( isWidget.asWidget() ).thenReturn( widget );

        FormField result = new FormFieldImpl( fieldName, binding, validateOnChange, isWidget );

        helpers.put( fieldName, new FormFieldContentHelper( formGroupElement, helpBlockElement ) );

        return result;
    }

    protected FormField generateFieldWithoutGroup( String fieldName, String binding, boolean validateOnChange ) {

        Element widgetElement = mock( Element.class );

        when( widgetElement.getTagName() ).thenReturn( FormElement.TAG );

        when( widgetElement.getId() ).thenReturn( fieldName );

        Widget widget = mock( Widget.class );

        when( widget.getElement() ).thenReturn( widgetElement );

        IsWidget isWidget = mock( IsWidget.class );

        when( isWidget.asWidget() ).thenReturn( widget );

        FormField result = new FormFieldImpl( fieldName, binding, validateOnChange, isWidget );

        helpers.put( fieldName, new FormFieldContentHelper( widgetElement, null ) );

        return result;
    }

    public FormField generateFormFieldWithoutHelpblock( String fieldName, String binding, boolean validateOnChange ) {

        Element helpBlockElement = mock( Element.class );

        when( helpBlockElement.getTagName() ).thenReturn( DivElement.TAG );

        when( helpBlockElement.getNodeType() ).thenReturn( Node.ELEMENT_NODE );

        when( helpBlockElement.getId() ).thenReturn( fieldName );

        when( helpBlockElement.getChildCount() ).thenReturn( 0 );

        Element formGroupElement = mock( Element.class );

        when( formGroupElement.getTagName() ).thenReturn( DivElement.TAG );

        when( formGroupElement.getId() ).thenReturn( fieldName + FieldStyleHandlerImpl.FORM_GROUP_SUFFIX );

        when( formGroupElement.getChildCount() ).thenReturn( 1 );

        when( formGroupElement.getChild( 0 ) ).thenReturn( helpBlockElement );

        Element widgetElement = mock( Element.class );

        when( widgetElement.getTagName() ).thenReturn( DivElement.TAG );

        when( widgetElement.getId() ).thenReturn( fieldName );

        when( widgetElement.getParentElement() ).thenReturn( formGroupElement );

        Widget widget = mock( Widget.class );

        when( widget.getElement() ).thenReturn( widgetElement );

        IsWidget isWidget = mock( IsWidget.class );

        when( isWidget.asWidget() ).thenReturn( widget );

        FormField result = new FormFieldImpl( fieldName, binding, validateOnChange, isWidget );

        helpers.put( fieldName, new FormFieldContentHelper( formGroupElement, helpBlockElement ) );

        return result;
    }

    protected void checkClearedFields( String... cleared ) {
        for ( String fieldName : cleared ) {
            FormFieldContentHelper field = helpers.get( fieldName );
            assertNotNull( field );
            if ( field != null ) {
                verify( field.getFormGroup() ).removeClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
                verify( field.getHelpBlock(), times( 1 ) ).setInnerHTML( "" );
            }
        }
    }

    protected void checkWrongFields( String... wrongFields ) {
        /*
        Checking that the validation given fields has been successfull. The conditions to check:
        - Group Verification: VALIDATION_ERROR_CLASSNAME should be added to at least one time
            (it may be more if there are more validation errors)
        - HelpBlock Verification: helpBlock's innerHTML should be modified at least two times (one to clean it up
            and at least one more to add the validation error message )
        */
        doValidationFailure( atLeast( 1 ), atLeast( 2 ), wrongFields );
    }

    protected void checkValidFields( String... validFields ) {

        /*
        Checking that the validation given fields has been successfull. The conditions to check:
        - Group Verification: group shouldn't contain the VALIDATION_ERROR_CLASSNAME
        - HelpBlock Verification: helpBlock's innerHTML should be modified only one time (to clean it up)
        */
        doValidationFailure( never(), times( 1 ), validFields );
    }

    protected void doValidationFailure( VerificationMode groupVerification,
                                        VerificationMode helpBlockVerificationMode,
                                        String... fields) {
        for ( String fieldName : fields ) {
            FormFieldContentHelper field = helpers.get( fieldName );
            assertNotNull( field );
            if ( field != null ) {
                verify( field.getFormGroup(), groupVerification ).addClassName( FieldStyleHandlerImpl.VALIDATION_ERROR_CLASSNAME );
                verify( field.getHelpBlock(), helpBlockVerificationMode ).setInnerHTML( anyString() );
            }
        }
    }

    protected static class FormFieldContentHelper {
        private Element formGroup;
        private Element helpBlock;

        public FormFieldContentHelper( Element formGroup, Element helpBlock ) {
            this.formGroup = formGroup;
            this.helpBlock = helpBlock;
        }

        public Element getFormGroup() {
            return formGroup;
        }

        public void setFormGroup( Element formGroup ) {
            this.formGroup = formGroup;
        }

        public Element getHelpBlock() {
            return helpBlock;
        }

        public void setHelpBlock( Element helpBlock ) {
            this.helpBlock = helpBlock;
        }
    }

}
