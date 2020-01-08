/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.adf.processors;

import java.io.FileNotFoundException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FieldRendererProcessorTest extends AbstractProcessorTest {

    private static final String RENDERER_1 = "org/kie/workbench/common/forms/adf/processors/renderers/TextAreaFieldTypeRenderer";
    private static final String RENDERER_2 = "org/kie/workbench/common/forms/adf/processors/renderers/TextBoxFieldTypeRenderer";
    private static final String RENDERER_3 = "org/kie/workbench/common/forms/adf/processors/renderers/TextAreaFieldDefinitionRenderer";
    private static final String RENDERER_4 = "org/kie/workbench/common/forms/adf/processors/renderers/TextBoxFieldDefinitionRenderer";

    private static final String RESULT = "org/kie/workbench/common/forms/adf/processors/renderers/ModuleFieldRendererTypesProvider.expected";

    private Result generated = new Result();

    @Test
    public void testProcessing() throws FileNotFoundException {
        generated.setExpectedCode(getExpectedSourceCode(RESULT));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(), RENDERER_1, RENDERER_2, RENDERER_3, RENDERER_4);

        assertSuccessfulCompilation(diagnostics);

        assertNotNull(generated.getActualCode());
        assertNotNull(generated.getExpectedCode());
        assertEquals(generated.getExpectedCode(), generated.getActualCode());
    }

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new FieldRendererProcessor(code -> generated.setActualCode(code));
    }
}
