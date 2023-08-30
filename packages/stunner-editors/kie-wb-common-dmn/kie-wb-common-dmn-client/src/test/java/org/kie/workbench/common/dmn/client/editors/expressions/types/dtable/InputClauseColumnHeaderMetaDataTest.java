/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseColumnHeaderMetaDataContextMenuTest;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InputClauseColumnHeaderMetaDataTest extends BaseColumnHeaderMetaDataContextMenuTest<InputClauseColumnHeaderMetaData, Text, HasText> {

    private static final String VALUE = "value";

    @Mock
    private HasText hasValue;

    @Override
    protected InputClauseColumnHeaderMetaData getHeaderMetaData() {
        return new InputClauseColumnHeaderMetaData(hasValue,
                                                   () -> hasTypeRef,
                                                   clearValueConsumer,
                                                   setValueConsumer,
                                                   setTypeRefConsumer,
                                                   translationService,
                                                   cellEditorControls,
                                                   editor,
                                                   listSelector,
                                                   listSelectorItemsSupplier,
                                                   listSelectorItemConsumer);
    }

    @Test
    public void testIsEmptyValue_WhenNull() {
        assertThat(headerMetaData.isEmptyValue(null)).isTrue();
    }

    @Test
    public void testIsEmptyValue_WhenEmptyString() {
        assertThat(headerMetaData.isEmptyValue(new Text())).isTrue();
    }

    @Test
    public void testToModelValue() {
        assertThat(headerMetaData.toModelValue(VALUE).getValue()).isEqualTo(VALUE);
    }

    @Test
    public void testToWidgetValue() {
        assertThat(headerMetaData.toWidgetValue(new Text(VALUE))).isEqualTo(VALUE);
    }

    @Test
    public void testGetValueLabel() {
        assertThat(headerMetaData.getValueLabel()).isEqualTo(DMNEditorConstants.DecisionTableEditor_InputClauseColumnHeaderMetaData_ValueLabel);
    }

    @Test
    public void testNormaliseValue() {
        final String value = "   " + VALUE + "   ";
        assertThat(headerMetaData.normaliseValue(value)).isEqualTo(value);
    }

    @Test
    public void testGetValue() {
        when(hasValue.getValue()).thenReturn(new Text(VALUE));

        assertThat(headerMetaData.getValue()).isNotNull();
        assertThat(headerMetaData.getValue().getValue()).isEqualTo(VALUE);
    }

    @Test
    public void testGetPopoverTitle() {
        assertThat(headerMetaData.getPopoverTitle()).isEqualTo(DMNEditorConstants.DecisionTableEditor_EditInputClause);
    }
}
