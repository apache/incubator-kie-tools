/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.widget;

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