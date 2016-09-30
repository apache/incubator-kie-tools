package org.uberfire.ext.properties.editor.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith( GwtMockitoTestRunner.class )
public class PropertyEditorWidgetTest {

    PropertyEditorWidget propertyEditor;

    @Before
    public void setUp() throws Exception {
        propertyEditor = new PropertyEditorWidget();
    }

    @Test
    public void handleExpandedCategoriesTest() throws Exception {
        assertTrue( propertyEditor.getExpandedCategories().isEmpty() );

        propertyEditor.addExpandedCategory( "CAT1", "CAT2" );

        assertEquals( 2, propertyEditor.getExpandedCategories().size() );
        assertEquals( "CAT2", propertyEditor.getLastOpenAccordionGroupTitle() );

        propertyEditor.collapseCategory( "CAT2" );

        assertEquals( 1, propertyEditor.getExpandedCategories().size() );
    }
}