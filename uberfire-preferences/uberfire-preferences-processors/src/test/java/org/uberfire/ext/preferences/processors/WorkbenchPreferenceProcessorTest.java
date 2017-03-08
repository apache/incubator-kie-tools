/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.preferences.processors;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;

import static org.junit.Assert.*;

/**
 * Tests for Workbench Preference related classes generation
 */
public class WorkbenchPreferenceProcessorTest extends AbstractProcessorTest {

    List<Result> results = new ArrayList<>();

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new WorkbenchPreferenceProcessor(code -> {
            Result result = new Result();
            result.setActualCode(code);
            results.add(result);
        });
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/MyPreference";
        final String pathExpectedBeanImplResult = "org/uberfire/ext/preferences/processors/expected/MyPreferenceBeanGeneratedImpl.expected";
        final String pathExpectedPortableImplResult = "org/uberfire/ext/preferences/processors/expected/MyPreferencePortableGeneratedImpl.expected";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);

        getBeanGenerationResult().setExpectedCode(getExpectedSourceCode(pathExpectedBeanImplResult));
        getPortableGenerationResult().setExpectedCode(getExpectedSourceCode(pathExpectedPortableImplResult));

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(getBeanGenerationResult().getActualCode());
        assertNotNull(getPortableGenerationResult().getExpectedCode());
        assertEquals(getBeanGenerationResult().getExpectedCode(),
                     getBeanGenerationResult().getActualCode());
        assertEquals(getPortableGenerationResult().getExpectedCode(),
                     getPortableGenerationResult().getActualCode());
    }

    private Result getBeanGenerationResult() {
        return results.get(0);
    }

    private Result getPortableGenerationResult() {
        return results.get(1);
    }
}
