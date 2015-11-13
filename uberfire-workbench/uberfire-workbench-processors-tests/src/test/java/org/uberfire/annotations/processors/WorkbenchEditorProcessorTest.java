/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Test;

/**
 * Tests for Editor related class generation
 */
public class WorkbenchEditorProcessorTest extends AbstractProcessorTest {

    final Result result = new Result();

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } );
    }

    @Test
    public void testNoWorkbenchEditorAnnotation() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchEditorTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMissingViewAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchEditorTest2" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchEditorTest2Activity: The WorkbenchEditor must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorHasViewAnnotationMissingTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchEditorTest3" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchEditorTest3Activity: The WorkbenchEditor must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMissingViewAnnotationHasTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchEditorTest4" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchEditorTest4Activity: The WorkbenchEditor must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorHasViewAnnotationAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest5.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest6.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorHasViewAnnotationAndExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest7.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.WARNING, Diagnostic.NOPOS, Diagnostic.NOPOS, "The WorkbenchEditor both extends com.google.gwt.user.client.ui.IsWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorAllAnnotations() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest8.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest9.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest10.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorOnStart1Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest11";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest11.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorOnStart2Parameters() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest12";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest12.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorOnStart2ParametersWrongOrder() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest13";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest13.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "Methods annotated with @OnStartup must take one argument of type org.uberfire.backend.vfs.Path and an optional second argument of type org.uberfire.mvp.PlaceRequest" );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest14";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest14.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.WARNING, 27, 17, "There is also an @OnStartup(Path, PlaceRequest) method in this class. That method takes precedence over this one." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorWorkbenchToolBarAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest15";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest15.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorWorkbenchToolBarAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest16";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest16.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMultipleSupportedTypes() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest17";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest17.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testEditorWithActivator() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest18";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest18.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testEditorWithOwningPerspective() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest19";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest19.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(), 
                      result.getActualCode() );
    }

    @Test
    public void testEditorWithInvalidOwningPerspectiveRef() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest20";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertCompilationMessage( diagnostics, Kind.ERROR, 15, Diagnostic.NOPOS, "owningPerspective must be a class annotated with @WorkbenchPerspective" );
    }

    @Test
    public void testWorkbenchEditorMultipleSupportedTypesWithPreferredWidth() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest24";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest24.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );

        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    public void testWorkbenchEditorMultipleSupportedTypesWithNegativePreferredWidth() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest25";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest25.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMultipleSupportedTypesWithPreferredHeight() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest26";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest26.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMultipleSupportedTypesWithNegativePreferredHeight() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest21";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest21.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMultipleSupportedTypesWithPreferredHeightAndWidth() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest22";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest22.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMultipleSupportedTypesWithNegativePreferredHeightAndWidth() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest23";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest23.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(),
                      result.getActualCode() );
    }
    
    @Test
    public void testEditorWithLockingStrategy() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest27";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest27.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getExpectedCode(), 
                      result.getActualCode() );
    }
}
