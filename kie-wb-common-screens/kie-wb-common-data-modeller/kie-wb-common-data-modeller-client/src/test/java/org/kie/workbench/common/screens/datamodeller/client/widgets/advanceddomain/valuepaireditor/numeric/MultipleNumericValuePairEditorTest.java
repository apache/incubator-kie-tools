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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.multiple.MultipleValuePairEditorView;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.NumberType;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class MultipleNumericValuePairEditorTest
        extends NumericValuePairEditorBaseTest {

    @GwtMock
    MultipleValuePairEditorView multipleEditorView;

    List<ValuePairEditor<?>> singleEditors = new ArrayList<ValuePairEditor<?>>();

    Map<NumberType, List<Object>> validValues = new HashMap<NumberType, List<Object>>(  );

    @Before
    public void initTest() {

        List<Object> values = new ArrayList<Object>(  );
        values.add( (byte) 1 );
        values.add( (byte) 2 );
        validValues.put( NumberType.BYTE, values );

        values = new ArrayList<Object>(  );
        values.add( (short) 1 );
        values.add( (short) 2 );
        validValues.put( NumberType.SHORT, values );

        values = new ArrayList<Object>(  );
        values.add( 1 );
        values.add( 2 );
        validValues.put( NumberType.INT, values );

        values = new ArrayList<Object>(  );
        values.add( (long) 1 );
        values.add( (long) 2 );
        validValues.put( NumberType.LONG, values );

        values = new ArrayList<Object>(  );
        values.add( (float) 1.1 );
        values.add( (float) 2.2 );
        validValues.put( NumberType.FLOAT, values );

        values = new ArrayList<Object>(  );
        values.add( (double) 1.1 );
        values.add( (double) 2.2 );
        validValues.put( NumberType.DOUBLE, values );

    }

    @Test
    public void testByteEditorLoad() {
        doTestEditorLoad( NumberType.BYTE, "byteArrayParam1" );
    }

    @Test
    public void testByteAddValuesChange() {
        doTestAddValuesChange( NumberType.BYTE, "byteArrayParam1" );
    }

    @Test
    public void testShortEditorLoad() {
        doTestEditorLoad( NumberType.SHORT, "shortArrayParam1" );
    }

    @Test
    public void testShortAddValuesChange() {
        doTestAddValuesChange( NumberType.SHORT, "shortArrayParam1" );
    }

    @Test
    public void testIntEditorLoad() {
        doTestEditorLoad( NumberType.INT, "intArrayParam1" );
    }

    @Test
    public void testIntAddValuesChange() {
        doTestAddValuesChange( NumberType.INT, "intArrayParam1" );
    }

    @Test
    public void testLongEditorLoad() {
        doTestEditorLoad( NumberType.LONG, "longArrayParam1" );
    }

    @Test
    public void testLongAddValuesChange() {
        doTestAddValuesChange( NumberType.LONG, "longArrayParam1" );
    }

    @Test
    public void testFloatEditorLoad() {
        doTestEditorLoad( NumberType.FLOAT, "floatArrayParam1" );
    }

    @Test
    public void testFloatAddValuesChange() {
        doTestAddValuesChange( NumberType.FLOAT, "floatArrayParam1" );
    }

    @Test
    public void testDoubleEditorLoad() {
        doTestEditorLoad( NumberType.DOUBLE, "doubleArrayParam1" );
    }

    @Test
    public void testDoubleAddValuesChange() {
        doTestAddValuesChange( NumberType.DOUBLE, "doubleArrayParam1" );
    }

    protected void doTestEditorLoad( NumberType numberType, String valuePairName ) {

        singleEditors.clear();
        List<Object> originalValues = validValues.get( numberType );

        MultipleNumericValuePairEditor multiNumericEditor = new MultipleNumericValuePairEditorExtended(
                multipleEditorView,
                numberType,
                valuePairName );

        verify( multipleEditorView, times( 1 ) ).setValuePairLabel( valuePairName );
        verify( multipleEditorView, times( 1 ) ).showValuePairRequiredIndicator( false );

        multiNumericEditor.setValue( originalValues );

        //as many single editors as orignal values should have been created.
        assertEquals( originalValues.size(), singleEditors.size() );

        //and populated with the corresponding value
        for ( Object originalValue : originalValues ) {
            verify( singleEditorView, times( 1 ) ).setValue( originalValue.toString() );
        }

        when( multipleEditorView.getItemEditors() ).thenReturn( singleEditors );

        assertTrue( multiNumericEditor.isValid() );
        assertEquals( originalValues, multiNumericEditor.getValue() );
    }

    protected void doTestAddValuesChange( NumberType numberType, String valuePairName ) {

        singleEditors.clear();

        NumericValuePairEditor addItemEditor = createEditor( numberType, valuePairName );

        MultipleNumericValuePairEditor multiNumericEditor = new MultipleNumericValuePairEditorExtended(
                multipleEditorView,
                numberType,
                valuePairName );

        when( multipleEditorView.getAddItemEditor() ).thenReturn( ( ValuePairEditor ) addItemEditor );

        List<Object> expectedValues = validValues.get( numberType );

        for ( Object expectedValue : expectedValues ) {
            //emulate the user adding values to the array.
            when( singleEditorView.getValue() ).thenReturn( expectedValue.toString() );
            addItemEditor.onValueChange();
            multiNumericEditor.onAddItem();
        }

        when( multipleEditorView.getItemEditors() ).thenReturn( singleEditors );

        assertTrue( multiNumericEditor.isValid() );
        assertEquals( expectedValues, multiNumericEditor.getValue() );
    }

    private class MultipleNumericValuePairEditorExtended extends MultipleNumericValuePairEditor {

        NumberType numberType;

        String valuePairName;

        public MultipleNumericValuePairEditorExtended( MultipleValuePairEditorView view, NumberType numberType,
                String valuePairName ) {
            super( view );
            this.numberType = numberType;
            this.valuePairName = valuePairName;

            super.init( createAnnotationDefinition( numberType ).getValuePair( valuePairName ) );
        }

        @Override
        public ValuePairEditor<?> createValuePairEditor( AnnotationValuePairDefinition valuePairDefinition ) {
            NumericValuePairEditor numericEditor = MultipleNumericValuePairEditorTest.this.createEditor( numberType, valuePairName );
            singleEditors.add( numericEditor );
            return numericEditor;
        }
    }
}
