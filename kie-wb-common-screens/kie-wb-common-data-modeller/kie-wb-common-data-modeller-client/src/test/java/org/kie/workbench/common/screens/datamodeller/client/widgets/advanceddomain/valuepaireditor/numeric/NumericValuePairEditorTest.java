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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.NumberType;
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class NumericValuePairEditorTest
                extends NumericValuePairEditorBaseTest {

    protected Map<NumberType, Pair<String, Object>> validValues = new HashMap<NumberType, Pair<String, Object>>(  );

    protected Map<NumberType, Pair<String, Object>> invalidValues = new HashMap<NumberType, Pair<String, Object>>(  );

    @Before
    public void initTest() {

        validValues.put( NumberType.BYTE, new Pair<String, Object>( "1", (byte)1 ) );
        validValues.put( NumberType.SHORT, new Pair<String, Object>( "1", (short)1 ) );
        validValues.put( NumberType.INT, new Pair<String, Object>( "1", (int)1 ) );
        validValues.put( NumberType.LONG, new Pair<String, Object>( "1", (long)1 ) );
        validValues.put( NumberType.FLOAT, new Pair<String, Object>( "1.1", (float)1.1 ) );
        validValues.put( NumberType.DOUBLE, new Pair<String, Object>( "1.1", (double)1.1 ) );

        invalidValues.put( NumberType.BYTE, new Pair<String, Object>( "wrong", null ) );
        invalidValues.put( NumberType.SHORT, new Pair<String, Object>( "wrong", null ) );
        invalidValues.put( NumberType.INT, new Pair<String, Object>( "wrong", null ) );
        invalidValues.put( NumberType.LONG, new Pair<String, Object>( "wrong", null ) );
        invalidValues.put( NumberType.FLOAT, new Pair<String, Object>( "wrong", null ) );
        invalidValues.put( NumberType.DOUBLE, new Pair<String, Object>( "wrong", null ) );

    }

    @Test
    public void testByteLoadEditor() {
        doTestEditorLoad( NumberType.BYTE, "byteParam1" );
    }

    @Test
    public void testByteValidValueChange() {
        doTestValidValueChange( NumberType.BYTE, "byteParam1" );
    }

    @Test
    public void testByteInvalidValueChange() {
        doTestInvalidValueChange( NumberType.BYTE, "byteParam1" );
    }

    @Test
    public void testShortLoadEditor() {
        doTestEditorLoad( NumberType.SHORT, "shortParam1" );
    }

    @Test
    public void testShortValidValueChange() {
        doTestValidValueChange( NumberType.SHORT, "shortParam1" );
    }

    @Test
    public void testShortInvalidValueChange() {
        doTestInvalidValueChange( NumberType.SHORT, "shortParam1" );
    }

    @Test
    public void testIntLoadEditor() {
        doTestEditorLoad( NumberType.INT, "intParam1" );
    }

    @Test
    public void testIntValidValueChange() {
        doTestValidValueChange( NumberType.INT, "intParam1" );
    }

    @Test
    public void testIntInvalidValueChange() {
        doTestInvalidValueChange( NumberType.INT, "intParam1" );
    }

    @Test
    public void testLongLoadEditor() {
        doTestEditorLoad( NumberType.LONG, "longParam1" );
    }

    @Test
    public void testLongValidValueChange() {
        doTestValidValueChange( NumberType.LONG, "longParam1" );
    }

    @Test
    public void testLongInvalidValueChange() {
        doTestInvalidValueChange( NumberType.LONG, "longParam1" );
    }

    @Test
    public void testFloatLoadEditor() {
        doTestEditorLoad( NumberType.FLOAT, "floatParam1" );
    }

    @Test
    public void testFloatValidValueChange() {
        doTestValidValueChange( NumberType.FLOAT, "floatParam1" );
    }

    @Test
    public void testFloatInvalidValueChange() {
        doTestInvalidValueChange( NumberType.FLOAT, "floatParam1" );
    }

    @Test
    public void testDoubleLoadEditor() {
        doTestEditorLoad( NumberType.DOUBLE, "doubleParam1" );
    }

    @Test
    public void testDoubleValidValueChange() {
        doTestValidValueChange( NumberType.DOUBLE, "doubleParam1" );
    }

    @Test
    public void testDoubleInvalidValueChange() {
        doTestInvalidValueChange( NumberType.DOUBLE, "doubleParam1" );
    }

    protected Pair<String, Object> getValidValue( NumberType numberType ) {
        return validValues.get( numberType );
    }

    protected Pair<String, Object> getInvalidValue( NumberType numberType ) {
        return invalidValues.get( numberType );
    }

    protected void doTestEditorLoad( NumberType numberType, String valuePairName ) {
        NumericValuePairEditor numericEditor = createEditor( numberType, valuePairName );
        verify( singleEditorView, times( 1 ) ).setValuePairLabel( valuePairName );
        verify( singleEditorView, times( 1 ) ).showValuePairRequiredIndicator( false );
        assertEquals( numberType, numericEditor.getNumberType() );
    }

    protected void doTestValidValueChange( NumberType numberType, String valuePairName ) {

        NumericValuePairEditor numericEditor = createEditor( numberType, valuePairName );
        when( singleEditorView.getValue() ).thenReturn( getValidValue( numberType ).getK1() );
        numericEditor.onValueChange();

        verify( singleEditorView, times( 1 ) ).clearErrorMessage();

        assertTrue( numericEditor.isValid() );
        assertEquals( getValidValue( numberType ).getK2(), numericEditor.getValue() );
    }

    protected void doTestInvalidValueChange( NumberType numberType, String valuePairName ) {

        NumericValuePairEditor numericEditor = createEditor( numberType, valuePairName );
        when( singleEditorView.getValue() ).thenReturn( getInvalidValue( numberType ).getK1() );
        numericEditor.onValueChange();

        verify( singleEditorView, times( 1 ) ).setErrorMessage( anyString() );

        assertFalse( numericEditor.isValid() );
        assertNull( numericEditor.getValue() );
    }

}