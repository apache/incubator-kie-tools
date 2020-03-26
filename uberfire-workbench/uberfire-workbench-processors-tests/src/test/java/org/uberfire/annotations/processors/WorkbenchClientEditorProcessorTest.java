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

package org.uberfire.annotations.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import elemental2.promise.Promise;
import org.junit.Test;

public class WorkbenchClientEditorProcessorTest extends AbstractProcessorTest {

    Result result = new Result();
    
    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new WorkbenchClientEditorProcessor(code -> result.setActualCode(code));
    }

    @Test
    public void testNoWorkbenchClientEditorAnnotation() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest1");
        assertSuccessfulCompilation(diagnostics);
        assertNull(result.getActualCode());
    }
    
    @Test
    public void testDoNotExtendWidgetOrProvideElement() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest2");
        assertFailedCompilation(diagnostics);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 Diagnostic.NOPOS,
                                 Diagnostic.NOPOS,
                                 "org.uberfire.annotations.processors.WorkbenchClientEditorTest2Activity: The WorkbenchClientEditor must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget.");
        assertNull(result.getActualCode());
    }
    
    @Test
    public void testMissingWorkbenchPartTitle() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest3");
        assertFailedCompilation(diagnostics);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 Diagnostic.NOPOS,
                                 Diagnostic.NOPOS,
                                 "org.uberfire.annotations.processors.WorkbenchClientEditorTest3Activity: The WorkbenchClientEditor must provide a @WorkbenchPartTitle annotated method to return a java.lang.String.");
        assertNull(result.getActualCode());
    }
    
    @Test
    public void testMissingSetContent() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest4");
        assertFailedCompilation(diagnostics);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 Diagnostic.NOPOS,
                                 Diagnostic.NOPOS,
                                 "org.uberfire.annotations.processors.WorkbenchClientEditorTest4Activity: The WorkbenchClientEditor must provide a @SetContent annotated method that has two java.lang.String (path and content) as parameters.");
        assertNull(result.getActualCode());
    }
    
    @Test
    public void testMissingGetContent() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest5");
        assertFailedCompilation(diagnostics);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 Diagnostic.NOPOS,
                                 Diagnostic.NOPOS,
                                 "org.uberfire.annotations.processors.WorkbenchClientEditorTest5Activity: The WorkbenchClientEditor must provide a @GetContent annotated method to return a elemental2.promise.Promise");
        assertNull(result.getActualCode());
    }
    
    
    @Test
    public void testSuccessContent() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest6");
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchClientEditorTest6.expected";
        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }
    
    @Test
    public void testSuccessContentWithGetPreview() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchClientEditorTest7");
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchClientEditorTest7.expected";
        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }
}
