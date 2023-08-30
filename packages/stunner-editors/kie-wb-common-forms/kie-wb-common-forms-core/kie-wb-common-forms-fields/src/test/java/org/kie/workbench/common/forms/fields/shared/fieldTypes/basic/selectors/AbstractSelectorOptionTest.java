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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public abstract class AbstractSelectorOptionTest<TYPE> {

    public static final String LABEL_A = "Sample label A";
    public static final String LABEL_B = "Sample label B";

    protected TYPE valueA;
    protected TYPE valueB;

    protected SelectorOption option;

    protected AbstractSelectorOptionTest(TYPE sampleValueA, TYPE sampleValueB) {
        valueA = sampleValueA;
        valueB = sampleValueB;
    }

    protected abstract SelectorOption newSelectorOption(TYPE value, String text);

    @Before
    public void init() {
        option = newSelectorOption(valueA, LABEL_A);
    }

    @Test
    public void testGetValue() {
        assertSame(valueA, option.getValue());
    }

    @Test
    public void testGetName() {
        assertSame(LABEL_A, option.getText());
    }

    @Test
    public void testEqualsAndHashcode() {
        testEquals(valueA, LABEL_A, valueA, LABEL_A, true);
        testEquals(null, LABEL_A, null, LABEL_A, true);
        testEquals(valueA, null, valueA, null, true);
        testEquals(null, null, null, null, true);

        testEquals(valueA, LABEL_A, valueA, LABEL_B, false);
        testEquals(valueA, LABEL_A, valueB, LABEL_A, false);
        testEquals(valueA, LABEL_A, valueB, LABEL_B, false);
        testEquals(valueA, LABEL_A, null, LABEL_A, false);
        testEquals(valueA, LABEL_A, valueA, null, false);
        testEquals(valueA, LABEL_A, null, null, false);

        SelectorOption reference = null;
        assertFalse(option.equals(reference));

        reference = option;
        assertTrue(option.equals(reference));
    }

    private void testEquals(TYPE valueA, String textA, TYPE valueB, String textB, boolean shouldBeEqual) {
        SelectorOption anOption = newSelectorOption(valueA, textA);
        SelectorOption other = newSelectorOption(valueB, textB);
        assertTrue(anOption.equals(other) == shouldBeEqual);
        assertEquals(anOption.hashCode() == other.hashCode(), shouldBeEqual);
    }

    @Test
    public void testValidateOption() {

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        SelectorOption emptyOption = newSelectorOption(null, null);

        Set<ConstraintViolation<SelectorOption>> violations = validator.validate(emptyOption);

        Assertions.assertThat(violations)
                .isNotNull()
                .isNotEmpty();

        violations = validator.validate(option);

        Assertions.assertThat(violations)
                .isNotNull()
                .isEmpty();
    }
}
