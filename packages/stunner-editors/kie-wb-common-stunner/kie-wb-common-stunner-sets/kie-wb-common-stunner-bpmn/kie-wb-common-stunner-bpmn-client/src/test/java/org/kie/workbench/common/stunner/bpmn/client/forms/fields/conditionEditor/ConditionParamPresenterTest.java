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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.BIG_DECIMAL_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.BIG_INTEGER_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.DOUBLE_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.FLOAT_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.INTEGER_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.LONG_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.PARAM_MUST_BE_COMPLETED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionParamPresenter.SHORT_NUMERIC_VALUE_EXPECTED_ERROR;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConditionParamPresenterTest {

    private static final String VALUE = "VALUE";

    private static final String MESSAGE = "MESSAGE";

    @Mock
    private Command onChangeCommand;

    @Mock
    private ConditionParamPresenter.View view;

    @Mock
    private ClientTranslationService translationService;

    private ConditionParamPresenter presenter;

    @Before
    public void setUp() {
        presenter = new ConditionParamPresenter(view, translationService);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view).init(presenter);
    }

    @Test
    public void testGetView() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testGetName() {
        when(view.getName()).thenReturn(VALUE);
        assertEquals(VALUE, presenter.getName());
    }

    @Test
    public void testSetName() {
        presenter.setName(VALUE);
        verify(view).setName(VALUE);
    }

    @Test
    public void testSetHelp() {
        presenter.setHelp(VALUE);
        verify(view).setHelp(VALUE);
    }

    @Test
    public void testGetValue() {
        when(view.getValue()).thenReturn(VALUE);
        assertEquals(VALUE, presenter.getValue());
    }

    @Test
    public void testSetValue() {
        presenter.setValue(VALUE);
        verify(view).setValue(VALUE);
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view).clear();
    }

    @Test
    public void testClearError() {
        presenter.clearError();
        verify(view).clearError();
    }

    @Test
    public void testSetError() {
        presenter.setError(VALUE);
        verify(view).setError(VALUE);
    }

    @Test
    public void testSetReadonlyTrue() {
        testSetReadonly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        testSetReadonly(false);
    }

    private void testSetReadonly(boolean value) {
        presenter.setReadonly(value);
        verify(view).setReadonly(value);
    }

    @Test
    public void testSetOnChangeCommand() {
        presenter.setOnChangeCommand(onChangeCommand);
        presenter.onValueChange();
        verify(onChangeCommand).execute();
    }

    @Test
    public void testValidateParamEmpty() {
        when(translationService.getValue(PARAM_MUST_BE_COMPLETED_ERROR)).thenReturn(MESSAGE);
        when(view.getValue()).thenReturn("");
        assertFalse(presenter.validateParam("any type"));
        verify(view).clearError();
        verify(view).setError(MESSAGE);
    }

    @Test
    public void testValidateShortParamSuccessful() {
        testValidateParamSuccessful(Short.class.getName(), "1234");
    }

    @Test
    public void testValidateShortParamUnSuccessful() {
        when(translationService.getValue(SHORT_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(Short.class.getName(), "abcd", MESSAGE);
    }

    @Test
    public void testValidateIntParamSuccessful() {
        testValidateParamSuccessful(Integer.class.getName(), "12345678");
    }

    @Test
    public void testValidateIntParamUnSuccessful() {
        when(translationService.getValue(INTEGER_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(Integer.class.getName(), "abcd", MESSAGE);
    }

    @Test
    public void testValidateLongParamSuccessful() {
        testValidateParamSuccessful(Long.class.getName(), "12345678910");
    }

    @Test
    public void testValidateLongParamUnSuccessful() {
        when(translationService.getValue(LONG_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(Long.class.getName(), "abcd", MESSAGE);
    }

    @Test
    public void testValidateFloatParamSuccessful() {
        testValidateParamSuccessful(Float.class.getName(), "12345.12");
    }

    @Test
    public void testValidateFloatParamUnSuccessful() {
        when(translationService.getValue(FLOAT_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(Float.class.getName(), "abcd", MESSAGE);
    }

    @Test
    public void testValidateDoubleParamSuccessful() {
        testValidateParamSuccessful(Double.class.getName(), "12345678910.12");
    }

    @Test
    public void testValidateDoubleParamUnSuccessful() {
        when(translationService.getValue(DOUBLE_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(Double.class.getName(), "abcd", MESSAGE);
    }

    @Test
    public void testValidateBigIntegerParamSuccessful() {
        testValidateParamSuccessful(BigInteger.class.getName(), "123456789101112131415");
    }

    @Test
    public void testValidateBigIntegerParamUnSuccessful() {
        when(translationService.getValue(BIG_INTEGER_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(BigInteger.class.getName(), "abcd", MESSAGE);
    }

    @Test
    public void testValidateBigDecimalParamSuccessful() {
        testValidateParamSuccessful(BigDecimal.class.getName(), "123456789101112131415.1234");
    }

    @Test
    public void testValidateBigDecimalParamUnSuccessful() {
        when(translationService.getValue(BIG_DECIMAL_NUMERIC_VALUE_EXPECTED_ERROR)).thenReturn(MESSAGE);
        testValidateParamUnSuccessful(BigDecimal.class.getName(), "abcd", MESSAGE);
    }

    private void testValidateParamSuccessful(String type, String value) {
        when(view.getValue()).thenReturn(value);
        assertTrue(presenter.validateParam(type));
        verify(view).clearError();
        verify(view, never()).setError(anyString());
    }

    private void testValidateParamUnSuccessful(String type, String value, String expectedError) {
        when(view.getValue()).thenReturn(value);
        assertFalse(presenter.validateParam(type));
        verify(view).clearError();
        verify(view).setError(expectedError);
    }
}
