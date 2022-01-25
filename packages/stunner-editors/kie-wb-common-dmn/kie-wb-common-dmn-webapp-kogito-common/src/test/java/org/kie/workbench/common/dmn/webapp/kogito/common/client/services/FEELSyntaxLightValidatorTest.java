/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FEELSyntaxLightValidatorTest {

    @Test
    public void testValidationWhenVariableNameIsEmpty() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid(null)).isFalse();
    }

    @Test
    public void testValidationWhenVariableNameStartsWithValidSymbol() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("_variable")).isTrue();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("?variable")).isTrue();
    }

    @Test
    public void testValidationWhenVariableNameStartsWithNumber() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("2variable")).isFalse();
    }

    @Test
    public void testValidationWhenVariableNameStartsWithInvalidSymbol() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("@variable")).isFalse();
    }

    @Test
    public void testValidationWhenVariableNameStartsWithReservedKeyword() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for variable")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for-name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for.name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for/name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for'name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for*name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for+name")).isFalse();
    }

    @Test
    public void testValidationWhenVariableNameIsSimpleCharSequence() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid(" valid variable name")).isTrue();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("valid variable name")).isTrue();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for_name")).isTrue();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("form name")).isTrue();
    }

    @Test
    public void testValidationWhenVariableNameContainsValidSymbols() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("?_873./-'+*valid")).isTrue();
    }

    @Test
    public void testValidationWhenVariableNameContainsStrangeButValidSymbols() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("🐎")).isTrue();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("版")).isTrue();
    }

    @Test
    public void testValidationWhenVariableNameContainsReservedKeyword() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("name for variable")).isTrue();
    }

    @Test
    public void testValidationWhenVariableNameIsReservedKeyword() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("for")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("return")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("if")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("then")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("else")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("some")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("every")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("satisfies")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("instance")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("of")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("in")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("function")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("external")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("or")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("and")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("between")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("not")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("null")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("true")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("false")).isFalse();
    }

    @Test
    public void testValidationWhenVariableNameContainsInvalidCharacters() {
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ! name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable @ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable # name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable $ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable $ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable % name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable & name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ^ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ( name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ) name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable \" name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ° name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable § name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ← name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable → name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ↓ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ¢ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable µ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable { name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable } name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable [ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ] name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable | name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable \\ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable = name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable < name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable > name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ; name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable : name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable , name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ¶ name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable « name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable » name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable ” name")).isFalse();
        assertThat(FEELSyntaxLightValidator.isVariableNameValid("variable “ name")).isFalse();
    }
}
