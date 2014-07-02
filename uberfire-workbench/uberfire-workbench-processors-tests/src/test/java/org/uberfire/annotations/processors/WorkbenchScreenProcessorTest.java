/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.annotations.processors;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Test;

/**
 * Tests for Screen related class generation
 */
public class WorkbenchScreenProcessorTest extends AbstractProcessorTest {

    Result result = new Result();

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new WorkbenchScreenProcessor( new GenerationCompleteCallback() {
            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } );
    }

    @Test
    public void testNoWorkbenchScreenAnnotation() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchScreenTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenMissingViewAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchScreenTest2" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchScreenTest2Activity: The WorkbenchScreen must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationMissingTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchScreenTest3" );
        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchScreenTest3Activity: The WorkbenchScreen must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenMissingViewAnnotationHasTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchScreenTest4" );
        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchScreenTest4Activity: The WorkbenchScreen must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest5.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest6.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationAndExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest7.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.WARNING, Diagnostic.NOPOS, Diagnostic.NOPOS, "The WorkbenchScreen both extends com.google.gwt.user.client.ui.IsWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenAllAnnotations() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest8.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenAllAnnotationsPrivate() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest20";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, 19, 22, "Methods annotated with @WorkbenchPartView must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 24, 20, "Methods annotated with @WorkbenchPartTitle must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 29, 18, "Methods annotated with @OnStartup must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 33, 21, "Methods annotated with @OnMayClose must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 38, 18, "Methods annotated with @OnClose must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 42, 18, "Methods annotated with @OnOpen must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 46, 18, "Methods annotated with @OnLostFocus must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 50, 18, "Methods annotated with @OnFocus must be non-private" );

        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest9.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest10.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenOnStart0Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest11";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest11.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenOnStart1Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest12";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest12.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest13";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest13.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.WARNING, 24, 17, "There is also an @OnStartup(PlaceRequest) method in this class. That method takes precedence over this one." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenWorkbenchToolBarAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest14";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest14.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenWorkbenchToolBarAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest15";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest15.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenHasTitleWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest16";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchScreenTest16Activity: The WorkbenchScreen must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenHasTitleAndTitleWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest17";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest17.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenCanInheritAnnotatedMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest18";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest18.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testScreenWithActivator() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest19";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest19.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                result.getExpectedCode() );
    }

    @Test
    public void testScreenWithOwningPerspective() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest21";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest21.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(), result.getExpectedCode() );
    }

    @Test
    public void testScreenWithInvalidOwningPerspectiveRef() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest22";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertCompilationMessage( diagnostics, Kind.ERROR, 13, Diagnostic.NOPOS, "owningPerspective must be a class annotated with @WorkbenchPerspective" );
    }

}
