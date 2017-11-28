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
package org.dashbuilder.displayer.client.widgets.sourcecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class JsValidatorTest {

    @Mock
    JsEvaluator jsEvaluator;

    DefaultJsValidator validator;

    @Before
    public void setUp() {
        validator = new DefaultJsValidator(jsEvaluator);
    }

    @Test
    public void testNoError() {
        String error = validator.validate("var a=1; var b = 42+a; alert(b);", null);
        assertNull(error);
    }

    @Test
    public void testNotAllowed_document() {
        String error = validator.validate("document.getElementById(\"\");", null);
        assertNotNull(error);
    }

    @Test
    public void testNotAllowed_window() {
        String error = validator.validate("window.location=\"\"", null);
        assertNotNull(error);
    }

    @Test
    public void testNotAllowed_eval() {
        String error = validator.validate("eval(\"\");", null);
        assertNotNull(error);
        error = validator.validate("eval\n(\"\");", null);
        assertNotNull(error);
        error = validator.validate("eval     (\"\");", null);
        assertNotNull(error);
        error = validator.validate("eval\t(\"\");", null);
        assertNotNull(error);
    }

    @Test
    public void testNotAllowed_innerHtml() {
        String error = validator.validate("this.innerHtml=\"\"", null);
        assertNotNull(error);
    }

    @Test
    public void testVariableReplacement() throws Exception {
        String error = validator.validate("${this}.style.cursor=\"pointer\";", null);
        String js = "function __alert(msg) {};\nvar __var0__ = document.createElement(\"div\");\n__var0__.style.cursor=\"pointer\";";
        verify(jsEvaluator).evaluate(js);
        assertNull(error);

        doThrow(new Exception("error __var0__")).when(jsEvaluator).evaluate(anyString());
        error = validator.validate("${this}.style.cursor=\"pointer\";", null);
        assertEquals(error, "error ${this}");
    }

    @Test
    public void testAllowedVariables() throws Exception {
        List<String> vars = Arrays.asList("${this}");
        String error = validator.validate("${this}.style.cursor=\"pointer\";", vars);
        assertNull(error);

        vars = new ArrayList<>();
        error = validator.validate("${this}.style.cursor=\"pointer\";", vars);
        assertNotNull(error);
    }

    @Test
    public void testOcurrences() throws Exception {
        assertEquals(validator.occurrences("", "{"), 0);
        assertEquals(validator.occurrences("{", "{"), 1);
        assertEquals(validator.occurrences("{", "}"), 0);
        assertEquals(validator.occurrences("{}", "}"), 1);
        assertEquals(validator.occurrences("{}", "{"), 1);
        assertEquals(validator.occurrences("{{}}", "{"), 2);
    }

    @Test
    public void testIsolateLines() throws Exception {
        String js = "if (a) b;\nelse c;";
        String isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a) b;\nc;\n");

        js = "   ";
        isolated = validator.isolateLines(js);
        assertEquals(isolated.length(), 0);

        js = "if (a) {\nb;\n} else c;";
        isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a) {}\nb;\n{} c;\n");

        js = "if (a) {\nb; }\nelse c;";
        isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a) {}\nb; \nc;\n");

        js = "if (a)\n{ b;\n} else c;";
        isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a)\n b;\n{} c;\n");

        js = "if (a) { b; } else c;";
        isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a) { b; } c;\n");

        js = "if (a) { if (b) {}}";
        isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a) { if (b) {}}\n");

        js = "if (a) {\nif (b) {\n}\n}";
        isolated = validator.isolateLines(js);
        assertEquals(isolated, "if (a) {}\nif (b) {}\n");
    }
}