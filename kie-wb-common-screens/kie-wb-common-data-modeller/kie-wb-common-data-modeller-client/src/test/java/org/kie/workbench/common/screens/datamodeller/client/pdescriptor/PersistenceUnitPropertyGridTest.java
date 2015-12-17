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

package org.kie.workbench.common.screens.datamodeller.client.pdescriptor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class PersistenceUnitPropertyGridTest {

    @Mock
    private PersistenceUnitPropertyGridView view;

    private PersistenceUnitPropertyGrid presenter;

    private
    PropertyRowImpl propertyRow1 = new PropertyRowImpl( "property1", "value1" );

    private
    PropertyRowImpl propertyRow2 = new PropertyRowImpl( "property2", "value2" );

    private
    PropertyRowImpl propertyRow3 = new PropertyRowImpl( "property3", "value3" );

    private ListDataProvider dataProvider;

    @Before
    public void setup() {
        presenter = new PersistenceUnitPropertyGrid( view );

        ArgumentCaptor<ListDataProvider> listDataProviderArgumentCaptor = ArgumentCaptor.forClass( ListDataProvider.class );
        verify( view, times( 1 ) ).setPresenter( eq( presenter ) );
        verify( view ).setDataProvider( listDataProviderArgumentCaptor.capture() );
        dataProvider = listDataProviderArgumentCaptor.getValue();
    }

    @Test
    public void testSetProperties() {
        List<PropertyRow> properties = new ArrayList<PropertyRow>(  );

        properties.add( propertyRow1 );
        properties.add( propertyRow2 );
        properties.add( propertyRow3 );

        presenter.setProperties( properties );

        assertEquals( properties, presenter.getProperties() );
        assertEquals( properties, dataProvider.getList() );
    }

    @Test
    public void testOnRemoveProperty() {

        List<PropertyRow> properties = new ArrayList<PropertyRow>(  );

        properties.add( propertyRow1 );
        properties.add( propertyRow2 );
        properties.add( propertyRow3 );

        presenter.setProperties( properties );
        presenter.onRemoveProperty( propertyRow1 );

        assertEquals( 2, presenter.getProperties().size() );
        assertEquals( 2, dataProvider.getList().size() );
        assertFalse( presenter.getProperties().contains( propertyRow1 ) );
        assertFalse( dataProvider.getList().contains( propertyRow1 ) );
    }

    @Test
    public void testOnAddProperty() {

        when ( view.getNewPropertyName() ).thenReturn( "newPropertyName" );
        when ( view.getNewPropertyValue() ).thenReturn( "newPropertyValue" );

        presenter.onAddProperty();

        verify( view , times( 1 ) ).getNewPropertyName();
        verify( view , times( 1 ) ).getNewPropertyValue();

        assertEquals( 1, presenter.getProperties().size() );
        assertEquals( 1, dataProvider.getList().size() );

        assertEquals( "newPropertyName", presenter.getProperties().get( 0 ).getName() );
        assertEquals( "newPropertyValue", presenter.getProperties().get( 0 ).getValue() );

    }

}
