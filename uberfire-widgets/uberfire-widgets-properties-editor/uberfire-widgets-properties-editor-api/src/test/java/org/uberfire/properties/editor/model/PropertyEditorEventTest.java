package org.uberfire.properties.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyEditorEventTest {

    @Test
    public void sortCategoryByPriority() {
        List<PropertyEditorCategory> categories = new ArrayList<PropertyEditorCategory>();
        PropertyEditorCategory category1 = new PropertyEditorCategory( "cat1", 2 );
        categories.add( category1 );
        PropertyEditorCategory category2 = new PropertyEditorCategory( "catHighPriority", 1 );
        categories.add( category2 );
        PropertyEditorEvent event = new PropertyEditorEvent( "event", categories );
        assertEquals( category2, event.getSortedProperties().get( 0 ) );
        assertEquals( category1, event.getSortedProperties().get( 1 ) );
    }

    @Test
    public void sortFieldsByPriority() {
        PropertyEditorFieldInfo highPriority = new PropertyEditorFieldInfo( "highPriority", PropertyEditorType.BOOLEAN ).withPriority( 1 );
        PropertyEditorFieldInfo lowPriority = new PropertyEditorFieldInfo( "lowPriority", PropertyEditorType.BOOLEAN ).withPriority( 10 );
        PropertyEditorCategory category = new PropertyEditorCategory( "cat1", 2 )
                .withField( lowPriority )
                .withField( highPriority );

        PropertyEditorEvent event = new PropertyEditorEvent( "event", category );
        assertEquals( highPriority, event.getSortedProperties().get( 0 ).getFields().get( 0 ) );
        assertEquals( lowPriority, event.getSortedProperties().get( 0 ).getFields().get( 1 ) );

    }
}
