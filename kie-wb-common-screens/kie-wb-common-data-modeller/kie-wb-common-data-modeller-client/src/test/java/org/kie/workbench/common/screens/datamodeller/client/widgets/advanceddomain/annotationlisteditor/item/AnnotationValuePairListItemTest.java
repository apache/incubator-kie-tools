/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.AdvancedAnnotationListEditorView;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AnnotationValuePairListItemTest {

    private static final String VALUE_PAIR_NAME = "VALUE_PAIR_NAME";

    private static final String VALUE_PAIR_SOURCE_CODE = "VALUE_PAIR_SOURCE_CODE";

    @Mock
    private AnnotationValuePairListItemView view;

    @Mock
    private Annotation annotation;

    @Mock
    private AnnotationValuePairDefinition valuePairDefinition;

    @Mock
    private AnnotationSource annotationSource;

    @Mock
    private AdvancedAnnotationListEditorView.EditValuePairHandler editValuePairHandler;

    @Mock
    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private AnnotationValuePairListItem listItem;

    @Before
    public void setup( ) {
        when( valuePairDefinition.getName( ) ).thenReturn( VALUE_PAIR_NAME );
        listItem = new AnnotationValuePairListItem( view );
    }

    @Test
    public void testLoadValuePairWithValueSet( ) {
        when( annotation.getValue( VALUE_PAIR_NAME ) ).thenReturn( new Object( ) );
        when( annotationSource.getValuePairSource( VALUE_PAIR_NAME ) ).thenReturn( VALUE_PAIR_SOURCE_CODE );

        listItem.loadValuePair( annotation, valuePairDefinition, annotationSource );

        verify( view, times( 1 ) ).setValuePairName( VALUE_PAIR_NAME );
        verify( view, times( 1 ) ).setValuePairStringValue( VALUE_PAIR_SOURCE_CODE );
    }

    @Test
    public void testLoadValuePairWithValueNotSet( ) {
        when( annotation.getValue( VALUE_PAIR_NAME ) ).thenReturn( null );
        when( annotationSource.getValuePairSource( VALUE_PAIR_NAME ) ).thenReturn( VALUE_PAIR_SOURCE_CODE );

        listItem.loadValuePair( annotation, valuePairDefinition, annotationSource );

        verify( view, times( 1 ) ).setValuePairName( VALUE_PAIR_NAME );
        verify( view, times( 1 ) ).setValuePairStringValue(
                Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_not_set( ) );
    }

    @Test
    public void testOnEdit( ) {
        listItem.setEditValuePairHandler( editValuePairHandler );
        listItem.loadValuePair( annotation, valuePairDefinition, annotationSource );
        listItem.onEdit( );
        // the edit handler should have been properly invoked.
        verify( editValuePairHandler, times( 1 ) ).onEditValuePair( annotation, VALUE_PAIR_NAME );
    }

    @Test
    public void testOnClear( ) {
        listItem.setClearValuePairHandler( clearValuePairHandler );
        listItem.loadValuePair( annotation, valuePairDefinition, annotationSource );
        listItem.onClear( );
        // the clear handler should have been properly invoked.
        verify( clearValuePairHandler, times( 1 ) ).onClearValuePair( annotation, VALUE_PAIR_NAME );
    }

    @Test
    public void testSetReadonlyTrue( ) {
        listItem.setReadonly( true );
        verify( view, times( 1 ) ).setReadonly( true );
    }

    @Test
    public void testSetReadonlyFalse( ) {
        listItem.setReadonly( false );
        verify( view, times( 1 ) ).setReadonly( false );
    }
}