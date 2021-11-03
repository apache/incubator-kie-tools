/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.annotations.processors;

import java.io.FileNotFoundException;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Test;

import static org.junit.Assert.*;

public class PerspectiveProcessorTest extends AbstractProcessorTest {

    final Result result = new Result();

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new PerspectiveProcessor(new GenerationCompleteCallback() {

            @Override
            public void generationComplete(String code) {
                result.setActualCode(code);
            }
        });
    }

    @Test
    public void testNoPerspectiveAnnotation() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/PerspectiveTest1");

        assertSuccessfulCompilation(diagnostics);
        assertNull(result.getActualCode());
    }

    @Test
    public void testIncorrectReturnTypeWithoutArguments() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/PerspectiveTest2");

        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 10,
                                 17,
                                 "Methods annotated with @Perspective must return org.uberfire.workbench.model.PerspectiveDefinition");
        assertNull(result.getActualCode());
    }

    @Test
    public void testCorrectReturnTypeWithArguments() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/PerspectiveTest3");

        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 Diagnostic.NOPOS,
                                 Diagnostic.NOPOS,
                                 "A WorkbenchPerspective class must have either a valid @Perspective method or at least one @WorkbenchPanel field.");
        assertNull(result.getActualCode());
    }

    @Test
    public void testCorrectReturnTypeWithoutArguments() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest4";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest4.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testCorrectReturnTypeWithoutArgumentsIsDefault() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest5.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testCorrectReturnTypeWithAllAnnotationsOnStart() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest6.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testCorrectReturnTypeWithAllAnnotationsOnStartWithPath() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest7.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest8.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest9.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchToolBarAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest10.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchToolBarAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest11";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest11.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchTemplateAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest12";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest12.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        printDiagnostics(diagnostics);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchTemplateAnnotationWithOnlyWorkbenchParts() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest13";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest13.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        printDiagnostics(diagnostics);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testWorkbenchTemplateAnnotationMustHaveWorkbenchPanelsOrParts() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest14";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        assertFailedCompilation(diagnostics);
    }

    @Test
    public void testWorkbenchTemplateAnnotationShouldNotAllowTwoDefaultWorkbenchPanels() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest15";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 15,
                                 8,
                                 "Found more than one @WorkbenchPanel with isDefault=true.");
    }

    @Test
    public void testWorkbenchTemplateAnnotationWithNoDefaultWorkbenchPanel() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest16";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest16.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testAlonePartAnnotationShouldGenerateDefaultPanel() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest17";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest17.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        printDiagnostics(diagnostics);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testAlonePartsAnnotationShouldGenerateDefaultPanel() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest18";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest18.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        printDiagnostics(diagnostics);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testPartsAnnotationShouldReceiveParameters() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest19";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest19.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        printDiagnostics(diagnostics);
        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testPerspectiveWithActivator() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest20";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest20.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testNonTransientPerspective() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest21";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest21.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void twoDefaultPerspectivesShouldGenerateAnError() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest19";
        final String pathCompilationUnit2 = "org/uberfire/annotations/processors/PerspectiveTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest19.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit,
                                                                               pathCompilationUnit2);
        assertFailedCompilation(diagnostics);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 -1,
                                 -1,
                                 "Found too many default WorkbenchPerspectives (expected 1). Found: (HomePerspective, PerspectiveTest7).");
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
    }

    @Test
    public void testWorkbenchPerspectivesOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest22";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 36,
                                 17,
                                 "Found multiple @OnStartup methods. Each class can declare at most one.");
        assertFailedCompilation(diagnostics);
    }

    @Test
    public void combiningPerspectiveMethodAndWorkbenchPanelFieldsShouldGenerateAnError() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest23";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(getProcessorUnderTest(),
                                                                               pathCompilationUnit);
        assertFailedCompilation(diagnostics);
        assertCompilationMessage(diagnostics,
                                 Kind.ERROR,
                                 19,
                                 8,
                                 "This WorkbenchPerspective has both a @Perspective method and a @WorkbenchPanel field. Only one or the other is allowed.");
    }

    @Test
    public void testDynamicPerspective() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest24";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest24.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    @Test
    public void testQualifiersInTheActivity() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest25";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest25.expected";

        result.setExpectedCode(getExpectedSourceCode(pathExpectedResult));

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit);

        assertSuccessfulCompilation(diagnostics);
        assertNotNull(result.getActualCode());
        assertNotNull(result.getExpectedCode());
        assertEquals(result.getExpectedCode(),
                     result.getActualCode());
    }

    private void printDiagnostics(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            System.out.println(diagnostic);
        }
    }
}
