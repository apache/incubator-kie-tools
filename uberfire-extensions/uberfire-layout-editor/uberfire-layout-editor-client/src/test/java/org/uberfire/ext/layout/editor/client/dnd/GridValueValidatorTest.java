package org.uberfire.ext.layout.editor.client.dnd;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GridValueValidatorTest {

    @Test
    public void testIsValid() throws Exception {
        GridValueValidator gridValueValidator = new GridValueValidator();
        assertTrue(gridValueValidator.isValid( "12" ));
        assertTrue(gridValueValidator.isValid( "6 6" ));
        assertTrue(gridValueValidator.isValid( "4 4 4" ));
        assertTrue(gridValueValidator.isValid( "4 2 6" ));
        assertFalse(gridValueValidator.isValid( "" ));
        assertFalse(gridValueValidator.isValid( " " ));
        assertFalse(gridValueValidator.isValid( " 12a" ));
        assertFalse(gridValueValidator.isValid( "invalid" ));
        assertFalse(gridValueValidator.isValid( "4 3 4" ));
        assertFalse(gridValueValidator.isValid( "4 3 2 2" ));
        assertFalse(gridValueValidator.isValid( null ));
    }
}