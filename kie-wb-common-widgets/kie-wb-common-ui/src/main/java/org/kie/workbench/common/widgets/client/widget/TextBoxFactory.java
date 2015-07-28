/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.widgets.client.widget;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common.client.common.NumericBigDecimalTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericBigIntegerTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericByteTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericDoubleTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericFloatTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericIntegerTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericLongTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericShortTextBox;
import org.uberfire.ext.widgets.common.client.common.NumericTextBox;

/**
 * A Factory for Text Boxes relevant to the specified data-type
 */
public class TextBoxFactory {

    /**
     * Get a TextBox relevant to the specified data-type
     * @param dataType
     * @return
     */
    public static TextBox getTextBox( final String dataType ) {

        if ( DataType.TYPE_NUMERIC.equals( dataType ) ) {
            return new NumericTextBox();
        } else if ( DataType.TYPE_NUMERIC_BIGDECIMAL.equals( dataType ) ) {
            return new NumericBigDecimalTextBox();
        } else if ( DataType.TYPE_NUMERIC_BIGINTEGER.equals( dataType ) ) {
            return new NumericBigIntegerTextBox();
        } else if ( DataType.TYPE_NUMERIC_BYTE.equals( dataType ) ) {
            return new NumericByteTextBox();
        } else if ( DataType.TYPE_NUMERIC_DOUBLE.equals( dataType ) ) {
            return new NumericDoubleTextBox();
        } else if ( DataType.TYPE_NUMERIC_FLOAT.equals( dataType ) ) {
            return new NumericFloatTextBox();
        } else if ( DataType.TYPE_NUMERIC_INTEGER.equals( dataType ) ) {
            return new NumericIntegerTextBox();
        } else if ( DataType.TYPE_NUMERIC_LONG.equals( dataType ) ) {
            return new NumericLongTextBox();
        } else if ( DataType.TYPE_NUMERIC_SHORT.equals( dataType ) ) {
            return new NumericShortTextBox();
        } else if ( DataType.TYPE_STRING.equals( dataType ) ) {
            return new LiteralTextBox();
        } else {
            return new TextBox();
        }
    }

}
