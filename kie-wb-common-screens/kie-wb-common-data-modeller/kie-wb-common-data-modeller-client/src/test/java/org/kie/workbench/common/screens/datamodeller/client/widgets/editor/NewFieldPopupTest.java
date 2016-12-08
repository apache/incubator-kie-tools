/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class NewFieldPopupTest {

    @Mock
    private NewFieldPopupView view;

    @Test
    public void initAndShowPopupTest() {

        DataModelerContext context = DataModelerEditorsTestHelper.createTestContext();
        List<Pair<String, String>> expectedTypeOptions = DataModelerUtils.buildFieldTypeOptions( context.getBaseTypes(),
                context.getDataModel().getDataObjects(), context.getDataModel().getJavaEnums(),
                context.getDataModel().getExternalClasses(), context.getDataModel().getDependencyJavaEnums(), false );

        NewFieldPopup newFieldPopup = new NewFieldPopup( view );
        newFieldPopup.init( context );
        newFieldPopup.show();

        //the field type options should have been properly initialized with the types existing in the context.
        verify( view, times( 1 ) ).init( newFieldPopup );
        verify( view, times( 1 ) ).initTypeList( expectedTypeOptions, true );
        verify( view, times( 1 ) ).show();
    }

    @Test
    public void createFieldTest() {
        doCreateFieldTest( false );
    }

    @Test
    public void createFieldAndContinueTest() {
        doCreateFieldTest( true );
    }

    private void doCreateFieldTest( boolean createAndContinue ) {
        DataModelerContext context = DataModelerEditorsTestHelper.createTestContext();

        NewFieldPopup newFieldPopup = new NewFieldPopup( view );
        PopupHandler handler = new PopupHandler();
        newFieldPopup.addPopupHandler( handler );

        //simulate the data entered by user
        when( view.getFieldName() ).thenReturn( "fieldName" );
        when( view.getFieldLabel() ).thenReturn( "fieldLabel" );
        when( view.getIsMultiple() ).thenReturn( true );
        when( view.getSelectedType() ).thenReturn( "java.lang.Integer" );

        newFieldPopup.init( context );
        newFieldPopup.show();

        //simulate the buttons clicking
        if ( createAndContinue ) {
            newFieldPopup.onCreateAndContinue();
        } else {
            newFieldPopup.onCreate();
        }

        verify( view, times( 1 ) ).getFieldName();
        verify( view, times( 1 ) ).getFieldLabel();
        verify( view, times( 1 ) ).getSelectedType();
        verify( view, times( 1 ) ).getIsMultiple();

        assertEquals( "fieldName", handler.getFieldName() );
        assertEquals( "fieldLabel", handler.getFieldLabel() );
        assertEquals( true, handler.isMultiple() );
        assertEquals( "java.lang.Integer", handler.getType() );
        assertEquals( createAndContinue, handler.isCreateAndContinue() );
        assertEquals( false, handler.isCanceled() );
    }

    @Test
    public void multipleFieldChangeTest() {

        DataModelerContext context = DataModelerEditorsTestHelper.createTestContext();

        NewFieldPopup newFieldPopup = new NewFieldPopup( view );
        newFieldPopup.init( context );

        when( view.getSelectedType() ).thenReturn( "boolean" );

        newFieldPopup.onTypeChange();

        //the ability of creating multiple fields should be disabled for java primitives
        verify( view, times( 1 ) ).enableIsMultiple( false );
        verify( view, times( 1 ) ).setIsMultiple( false );

        //and should be enabled as soon as a non java primitive is selected
        when( view.getSelectedType() ).thenReturn( "java.lang.Integer" );

        newFieldPopup.onTypeChange();
        verify( view, times( 1 ) ).enableIsMultiple( true );

    }

    private static class PopupHandler
            implements NewFieldPopupView.NewFieldPopupHandler {

        private String fieldName;

        private String fieldLabel;

        private String type;

        private boolean multiple;

        private boolean canceled = false;

        private boolean createAndContinue = false;

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldLabel() {
            return fieldLabel;
        }

        public String getType() {
            return type;
        }

        public boolean isMultiple() {
            return multiple;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean isCreateAndContinue() {
            return createAndContinue;
        }

        @Override
        public void onCreate( String fieldName, String fieldLabel, String type, boolean multiple ) {
            this.fieldName = fieldName;
            this.fieldLabel = fieldLabel;
            this.type = type;
            this.multiple = multiple;
        }

        @Override
        public void onCreateAndContinue( String fieldName, String fieldLabel, String type, boolean multiple ) {
            this.fieldName = fieldName;
            this.fieldLabel = fieldLabel;
            this.type = type;
            this.multiple = multiple;
            this.createAndContinue = true;
        }

        @Override
        public void onCancel() {
            this.canceled = true;
        }
    }
}