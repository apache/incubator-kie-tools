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
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class ConditionParamPresenter {

    static final String PARAM_MUST_BE_COMPLETED_ERROR = "ConditionParamPresenter.ParamMustBeCompletedErrorMessage";

    static final String SHORT_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.ShortNumericValueExpectedErrorMessage";

    static final String INTEGER_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.IntegerNumericValueExpectedErrorMessage";

    static final String LONG_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.LongNumericValueExpectedErrorMessage";

    static final String FLOAT_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.FloatNumericValueExpectedErrorMessage";

    static final String DOUBLE_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.DoubleNumericValueExpectedErrorMessage";

    static final String BIG_DECIMAL_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.BigDecimalNumericValueExpectedErrorMessage";

    static final String BIG_INTEGER_NUMERIC_VALUE_EXPECTED_ERROR = "ConditionParamPresenter.BigIntegerNumericValueExpectedErrorMessage";

    public interface View extends UberElement<ConditionParamPresenter> {

        String getName();

        void setName(String name);

        void setHelp(String help);

        String getValue();

        void setValue(String value);

        void clear();

        void clearError();

        void setError(String error);

        void setReadonly(boolean readonly);
    }

    private Command onChangeCommand;

    private final View view;

    private final ClientTranslationService translationService;

    @Inject
    public ConditionParamPresenter(final View view, final ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public String getName() {
        return view.getName();
    }

    public void setName(String name) {
        view.setName(name);
    }

    public void setHelp(String help) {
        view.setHelp(help);
    }

    public String getValue() {
        return view.getValue();
    }

    public void setValue(String value) {
        view.setValue(value);
    }

    public void clear() {
        view.clear();
    }

    public void clearError() {
        view.clearError();
    }

    public void setError(String error) {
        view.setError(error);
    }

    public void setReadonly(boolean readonly) {
        view.setReadonly(readonly);
    }

    public void setOnChangeCommand(Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
    }

    public boolean validateParam(String expectedType) {
        clearError();
        String value = view.getValue();
        if (isEmpty(value)) {
            setError(translationService.getValue(PARAM_MUST_BE_COMPLETED_ERROR));
            return false;
        }
        if (Short.class.getName().equals(expectedType)) {
            return validateNumber(value, Short::parseShort, SHORT_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        if (Integer.class.getName().equals(expectedType)) {
            return validateNumber(value, Integer::parseInt, INTEGER_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        if (Long.class.getName().equals(expectedType)) {
            return validateNumber(value, Long::parseLong, LONG_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        if (Float.class.getName().equals(expectedType)) {
            return validateNumber(value, Float::parseFloat, FLOAT_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        if (Double.class.getName().equals(expectedType)) {
            return validateNumber(value, Double::parseDouble, DOUBLE_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        if (BigDecimal.class.getName().equals(expectedType)) {
            return validateNumber(value, BigDecimal::new, BIG_DECIMAL_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        if (BigInteger.class.getName().equals(expectedType)) {
            return validateNumber(value, BigInteger::new, BIG_INTEGER_NUMERIC_VALUE_EXPECTED_ERROR);
        }
        return true;
    }

    void onValueChange() {
        if (onChangeCommand != null) {
            onChangeCommand.execute();
        }
    }

    private boolean validateNumber(String value, Consumer<String> valueCheck, String errorCode) {
        try {
            valueCheck.accept(value);
            return true;
        } catch (Exception e) {
            setError(translationService.getValue(errorCode));
            return false;
        }
    }
}
