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

public class FormDefinitionsProcessorTest extends AbstractProcessorTest {

    private Result generated = new Result();

    @Test
    public void testSimpleInheritance() throws FileNotFoundException {
        testCodeGeneration("org/kie/workbench/common/forms/adf/processors/models/Children.expected", "org/kie/workbench/common/forms/adf/processors/models/Children");
    }

    @Test
    public void testInheritanceWithNestedTypes() throws FileNotFoundException {
        testCodeGeneration("org/kie/workbench/common/forms/adf/processors/models/Parent.expected", "org/kie/workbench/common/forms/adf/processors/models/Parent", "org/kie/workbench/common/forms/adf/processors/models/Children");
    }

    @Test
    public void testBasicMeta() throws FileNotFoundException {
        testCodeGeneration("org/kie/workbench/common/forms/adf/processors/meta/Person.expected", "org/kie/workbench/common/forms/adf/processors/meta/Person", "org/kie/workbench/common/forms/adf/processors/meta/Name", "org/kie/workbench/common/forms/adf/processors/meta/Age", "org/kie/workbench/common/forms/adf/processors/meta/Married");
    }

    @Test
    public void testGenerationErrorMetaFieldWithoutValue() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error1", "org/kie/workbench/common/forms/adf/processors/meta/errors/Error1Field");
    }

    @Test
    public void testGenerationErrorMetaFieldWithoutValueModifiers() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error2Field");
    }

    @Test
    public void testGenerationErrorMetaFieldMultipleValue() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error3Field");
    }

    @Test
    public void testGenerationErrorMetaFieldMultipleReadOnly() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error4Field");
    }

    @Test
    public void testGenerationErrorMetaFieldReadOnlyWrongType() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error5Field");
    }

    @Test
    public void testGenerationErrorMetaFieldReadOnlyWithoutGetter() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error6Field");
    }

    @Test
    public void testGenerationErrorMetaFieldMultipleRequired() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error7Field");
    }

    @Test
    public void testGenerationErrorMetaFieldRequiredWrongType() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error8Field");
    }

    @Test
    public void testGenerationErrorMetaFieldRequiredWithoutGetter() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error9Field");
    }

    @Test
    public void testGenerationErrorMetaFieldMultipleLabel() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error10Field");
    }

    @Test
    public void testGenerationErrorMetaFieldLabelWrongType() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error11Field");
    }

    @Test
    public void testGenerationErrorMetaFieldLabelWithoutGetter() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error12Field");
    }

    @Test
    public void testGenerationErrorMetaFieldMultipleHelp() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error13Field");
    }

    @Test
    public void testGenerationErrorMetaFieldHelpWrongType() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error14Field");
    }

    @Test
    public void testGenerationErrorMetaFieldHelpWithoutGetter() {
        testCodeGenerationFailure("org/kie/workbench/common/forms/adf/processors/meta/errors/Error15Field");
    }

    private void testCodeGeneration(String expectedCodePath, String... sourceCodePath) throws FileNotFoundException {
        generated.setExpectedCode(getExpectedSourceCode(expectedCodePath));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(), sourceCodePath);

        assertSuccessfulCompilation(diagnostics);

        assertNotNull(generated.getActualCode());
        assertNotNull(generated.getExpectedCode());
        assertEquals(generated.getExpectedCode(), generated.getActualCode());
    }

    private void testCodeGenerationFailure(String... sourceCodePath) {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(), sourceCodePath);
        assertFailedCompilation(diagnostics);
    }

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new FormDefinitionsProcessor(code -> generated.setActualCode(code));
    }
}
