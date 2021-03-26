/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.feel;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.api.Parameter;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.TypeStackUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FEELDemoEditorTest {

    @Mock
    private FEELDemoEditor.View view;

    private FEELLanguageService feelLanguageService;

    private FEELDemoEditor editor;

    @Before
    public void setup() {
        final TypeStackUtils typeStackUtils = new TypeStackUtils();
        feelLanguageService = spy(new FEELLanguageService(typeStackUtils));
        editor = spy(new FEELDemoEditor(view, feelLanguageService, typeStackUtils));

        doReturn(getFunctionOverrideVariations()).when(editor).getFunctionOverrideVariations();
        doReturn(getFunctionOverrideVariations()).when(feelLanguageService).getFunctionOverrideVariations();
    }

    @Test
    public void testOnTextChangeForANumber() {

        editor.onStartup(null);
        editor.onTextChange("1 +");

        verify(view).setNodes("1: number\n");
        verify(view).setEvaluation("Evaluation result: 1\nEvaluation type: number");
        verify(view).setFunctions("abs(duration): number\n" +
                                          "abs(number): number\n" +
                                          "sum(list): number\n" +
                                          "string(number): string\n");
        verify(view).setSuggestions("abs(duration): Function\n" +
                                            "abs(number): Function\n" +
                                            "sum(list): Function\n" +
                                            "not: Keyword\n" +
                                            "for: Keyword\n" +
                                            "if: Keyword\n" +
                                            "some: Keyword\n" +
                                            "every: Keyword\n" +
                                            "function: Keyword\n");
    }

    @Test
    public void testOnTextChangeForAString() {

        editor.onStartup(null);
        editor.onTextChange("\"abc\" +");

        verify(view).setNodes("\"abc\": string\n");
        verify(view).setEvaluation("Evaluation result: abc\nEvaluation type: string");
        verify(view).setFunctions("abs(duration): number\n" +
                                          "abs(number): number\n" +
                                          "sum(list): number\n" +
                                          "string(number): string\n");
        verify(view).setSuggestions("string(number): Function\n" +
                                            "not: Keyword\n" +
                                            "for: Keyword\n" +
                                            "if: Keyword\n" +
                                            "some: Keyword\n" +
                                            "every: Keyword\n" +
                                            "function: Keyword\n");
    }

    private List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        return asList(
                new FunctionOverrideVariation(BuiltInType.NUMBER, "abs", new Parameter("duration", BuiltInType.DURATION)),
                new FunctionOverrideVariation(BuiltInType.NUMBER, "abs", new Parameter("number", BuiltInType.NUMBER)),
                new FunctionOverrideVariation(BuiltInType.NUMBER, "sum", new Parameter("list", BuiltInType.LIST)),
                new FunctionOverrideVariation(BuiltInType.STRING, "string", new Parameter("number", BuiltInType.NUMBER))
        );
    }
}
