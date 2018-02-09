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

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.AdvancedAnnotationListEditorView;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.InstanceMock;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AnnotationListItemTest {

    private static final String ANNOTATION_CLASS_NAME = "ANNOTATION_CLASS_NAME";

    private static final int MAX_ITEMS = 4;

    @Mock
    private AnnotationListItemView view;

    @Mock
    private InstanceMock< AnnotationValuePairListItem > itemInstance;

    @Mock
    private AnnotationListItem listItem;

    @Mock
    private Annotation annotation;

    @Mock
    private AnnotationDefinition annotationDefinition;

    private List< AnnotationValuePairDefinition > valuePairs = new ArrayList< AnnotationValuePairDefinition >( );

    private List< AnnotationValuePairListItem > valuePairItems = new ArrayList< AnnotationValuePairListItem >( );

    @Mock
    private AnnotationSource annotationSource;

    @Mock
    private AnnotationValuePairDefinition valuePairDefinition;

    @Mock
    private AdvancedAnnotationListEditorView.CollapseChangeHandler collapseChangeHandler;

    @Mock
    private AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler;

    @Before
    public void setup( ) {
        when( annotation.getClassName( ) ).thenReturn( ANNOTATION_CLASS_NAME );

        //emulates the annotation value pairs and the corresponding AnnotationValuePairListItem
        for ( int i = 0; i < MAX_ITEMS; i++ ) {
            valuePairs.add( mock( AnnotationValuePairDefinition.class ) );
            valuePairItems.add( mock( AnnotationValuePairListItem.class ) );
        }
        when( annotation.getAnnotationDefinition( ) ).thenReturn( annotationDefinition );
        when( annotationDefinition.getValuePairs( ) ).thenReturn( valuePairs );

        listItem = new AnnotationListItem( view, itemInstance ) {

            int i = 0;

            @Override
            protected AnnotationValuePairListItem createListItem( ) {
                if ( i >= valuePairItems.size( ) ) {
                    throw new RuntimeException( "too many invocations" );
                } else {
                    super.createListItem();
                    return valuePairItems.get( i++ );
                }
            }
        };
    }

    @Test
    public void testLoadAnnotation( ) {

        listItem.loadAnnotation( annotation, annotationSource );

        verify( view, times( 1 ) ).setHeadingTitle( ANNOTATION_CLASS_NAME );
        // the corresponding AnnotationValuePairListItems should have been properly initialized and added to the view.
        verify( itemInstance, times( valuePairs.size( ) ) ).get( );
        for ( int i = 0; i < valuePairItems.size( ); i++ ) {
            verify( valuePairItems.get( i ), times( 1 ) ).loadValuePair( annotation, valuePairs.get( i ), annotationSource );
            verify( valuePairItems.get( i ), times( 1 ) ).setClearValuePairHandler(
                    any( AdvancedAnnotationListEditorView.ClearValuePairHandler.class ) );
            verify( valuePairItems.get( i ), times( 1 ) ).setEditValuePairHandler(
                    any( AdvancedAnnotationListEditorView.EditValuePairHandler.class ) );
            verify( view, times( 1 ) ).addItem( valuePairItems.get( i ) );
        }
    }

    @Test
    public void testSetCollapsedTrue( ) {
        listItem.setCollapsed( true );
        verify( view, times( 1 ) ).setCollapsed( true );
    }

    @Test
    public void testSetCollapsedFalse( ) {
        listItem.setCollapsed( false );
        verify( view, times( 1 ) ).setCollapsed( false );
    }

    @Test
    public void testSetReadonlyTrue( ) {
        listItem.loadAnnotation( annotation, annotationSource );
        listItem.setReadonly( true );
        verify( view, times( 1 ) ).setReadonly( true );
        verifyItemsReadonlyStatus( true );
    }

    @Test
    public void testSetReadonlyFalse( ) {
        listItem.loadAnnotation( annotation, annotationSource );
        listItem.setReadonly( false );
        verify( view, times( 1 ) ).setReadonly( false );
        verifyItemsReadonlyStatus( false );
    }

    private void verifyItemsReadonlyStatus( boolean expectedReadonlyStatus ) {
        for ( AnnotationValuePairListItem valuePairListItem : valuePairItems ) {
            verify( valuePairListItem, times( 1 ) ).setReadonly( expectedReadonlyStatus );
        }
    }

    @Test
    public void testOnCollapseChange( ) {
        listItem.setCollapseChangeHandler( collapseChangeHandler );
        listItem.onCollapseChange( );
        verify( collapseChangeHandler, times( 1 ) ).onCollapseChange( );
    }

    @Test
    public void testOnDelete( ) {
        listItem.loadAnnotation( annotation, annotationSource );
        listItem.setDeleteAnnotationHandler( deleteAnnotationHandler );
        listItem.onDelete( );
        verify( deleteAnnotationHandler, times( 1 ) ).onDeleteAnnotation( annotation );
    }

    @Test
    public void testDestroy() {
        listItem.loadAnnotation( annotation, annotationSource );
        listItem.destroy();
        for ( AnnotationValuePairListItem valuePairItem : valuePairItems ) {
            verify( itemInstance, times( 1 ) ).destroy( valuePairItem );
        }
    }
}