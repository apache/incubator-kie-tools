/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.annotations.processors;

import java.io.FileNotFoundException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExperimentalFeatureProcessorTest extends AbstractProcessorTest {

    private Result provider = new Result();
    private Result reference = new Result();

    private boolean providerGenerated = false;
    private boolean referenceGenerated = false;

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new ExperimentalFeatureProcessor(code -> {
            if (!providerGenerated) {
                provider.setActualCode(code);
                providerGenerated = true;
            } else {
                reference.setActualCode(code);
                referenceGenerated = true;
            }
        });
    }

    @Test
    public void testDefaultConfiguration() throws FileNotFoundException {
        testCodeGeneration("org/uberfire/annotations/processors/ExperimentalFeatureTest1", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest1.expected", null);
    }

    @Test
    public void testExtraConfiguration() throws FileNotFoundException {
        testCodeGeneration("org/uberfire/annotations/processors/ExperimentalFeatureTest2", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest2.expected", null);
    }

    @Test
    public void testExperimentalForWorkbenchScreen() throws FileNotFoundException {
        testCodeGeneration("org/uberfire/annotations/processors/ExperimentalFeatureTest3", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest3Provider.expected", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest3Reference.expected");
    }

    @Test
    public void testExperimentalForWorkbenchPerspective() throws FileNotFoundException {
        testCodeGeneration("org/uberfire/annotations/processors/ExperimentalFeatureTest4", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest4Provider.expected", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest4Reference.expected");
    }

    @Test
    public void testExperimentalForWorkbenchEditor() throws FileNotFoundException {
        testCodeGeneration("org/uberfire/annotations/processors/ExperimentalFeatureTest5", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest5Provider.expected", "org/uberfire/annotations/processors/expected/ExperimentalFeatureTest5Reference.expected");
    }

    private void testCodeGeneration(String sourceCodePath, String expectedProviderCodePath, String expectedReferenceCodePath) throws FileNotFoundException {
        provider.setExpectedCode(getExpectedSourceCode(expectedProviderCodePath));

        if (expectedReferenceCodePath != null) {
            reference.setExpectedCode(getExpectedSourceCode(expectedReferenceCodePath));
        }

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                sourceCodePath);

        assertTrue(providerGenerated);

        assertEquals(referenceGenerated, expectedReferenceCodePath != null);

        assertSuccessfulCompilation(diagnostics);

        checkCode(provider);

        if (referenceGenerated) {
            checkCode(reference);
        }
    }

    private void checkCode(Result result) {
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(), result.getActualCode());
    }
}
