package org.uberfire.properties.editor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;


public class PropertyUtilsTest {


    @Test
    public void convertMapToCategoryNullCase() {
        assertNull( PropertyUtils.convertMapToCategory( null ) );
        assertNull( PropertyUtils.convertMapToCategory( new HashMap<String, List<String>>() ) );
    }

    @Test
    public void convertMapToCategory() {

        HashMap<String, List<String>> categoryMap = new HashMap<String, List<String>>();
        List<String> categoryFields = new ArrayList<String>();
        categoryFields.add( "field1" );
        categoryFields.add( "field2" );
        categoryMap.put( "Category1", categoryFields );

        PropertyEditorCategory category = PropertyUtils.convertMapToCategory( categoryMap );
        assertNotNull( category );
        assertEquals( category.getFields().size(), 2 );
        assertEquals( category.getFields().get(0).getLabel(), "field1" );
        assertEquals( category.getFields().get( 1 ).getLabel(), "field2" );
    }


}
