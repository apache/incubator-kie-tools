/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.properties.editor.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
@WithClassesToStub( Heading.class )
public class PropertyEditorHelperTest {

    @Test( expected = PropertyEditorHelper.NullEventException.class )
    public void validateNullEventTest() {
        PropertyEditorHelper.validade( null );
    }

    @Test( expected = PropertyEditorHelper.NoPropertiesException.class )
    public void validateEventWithNoPropertiesTest() {
        PropertyEditorEvent event = new PropertyEditorEvent( "id", new ArrayList<PropertyEditorCategory>() );
        PropertyEditorHelper.validade( event );
    }

    @Test
    public void validateEventTest() {
        ArrayList<PropertyEditorCategory> properties = new ArrayList<PropertyEditorCategory>();
        properties.add( new PropertyEditorCategory( "Category" ) );
        PropertyEditorEvent event = new PropertyEditorEvent( "id", properties );
        assertTrue( PropertyEditorHelper.validade( event ) );
    }

    @Test
    public void isAMatchOfEmptyFilterTest() {
        PropertyEditorFieldInfo field = new PropertyEditorFieldInfo();
        assertTrue( PropertyEditorHelper.isAMatchOfFilter( "", field ) );
    }

    @Test
    public void isAMatchOfFilterTest() {
        PropertyEditorFieldInfo field = new PropertyEditorFieldInfo( "label", PropertyEditorType.TEXT );
        assertTrue( PropertyEditorHelper.isAMatchOfFilter( "l", field ) );
        assertTrue( PropertyEditorHelper.isAMatchOfFilter( "label", field ) );
        assertTrue( PropertyEditorHelper.isAMatchOfFilter( "LABEL", field ) );
        assertTrue( PropertyEditorHelper.isAMatchOfFilter( "abel", field ) );
        assertFalse( PropertyEditorHelper.isAMatchOfFilter( "LABELL", field ) );
        assertFalse( PropertyEditorHelper.isAMatchOfFilter( "LASBELL", field ) );
        assertFalse( PropertyEditorHelper.isAMatchOfFilter( "p", field ) );
    }

    @Test
    public void createLabelTest() {
        PropertyEditorFieldInfo field = new PropertyEditorFieldInfo( "label", PropertyEditorType.TEXT );
        PropertyEditorItemLabel label = PropertyEditorHelper.createLabel( field );
        verify( label ).setText( "label" );
    }

    @Test
    public void createCategoryWithNoFields() {
        PropertyEditorWidget propertyEditorWidget = GWT.create( PropertyEditorWidget.class );
        PanelGroup propertyMenu = GWT.create( PanelGroup.class );

        PropertyEditorCategory category = new PropertyEditorCategory( "1" );
        PropertyEditorHelper.createCategory( propertyEditorWidget, propertyMenu, category, "" );

        verify( propertyMenu, never() ).add( any( Widget.class ) );
    }

}
