package org.kie.workbench.common.screens.server.management.client.util;

import java.math.BigDecimal;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class NumericTextBoxTest {

    @Test
    public void validInputIncludingEmpty() throws Exception {
        NumericTextBox numericTextBox = new NumericTextBox( true );

        assertTrue( numericTextBox.isValidValue( "5", true ) );
        assertTrue( numericTextBox.isValidValue( "", true ) );
        assertTrue( numericTextBox.isValidValue( "23232", true ) );
        assertTrue( numericTextBox.isValidValue( new BigDecimal( Integer.MAX_VALUE + 1 ).toString(), true ) );

        assertFalse( numericTextBox.isValidValue( " ", true ) );
        assertFalse( numericTextBox.isValidValue( "a", true ) );
        assertFalse( numericTextBox.isValidValue( null, true ) );
        assertFalse( numericTextBox.isValidValue( null, false ) );
    }

    @Test
    public void validInputNonEmpty() throws Exception {
        NumericTextBox numericTextBox = new NumericTextBox();
        assertTrue( numericTextBox.isValidValue( "5", true ) );
        assertFalse( numericTextBox.isValidValue( "", true ) );
    }

    @Test
    public void onLostFocusValue() throws Exception {
        NumericTextBox numericTextBox = new NumericTextBox();
        assertTrue( numericTextBox.isValidValue( "-", false ) );
    }
}