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
 * Tests for Splash Screen related class generation
 */
public class WorkbenchSplashScreenProcessorTest extends AbstractProcessorTest {

    Result result = new Result();

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new WorkbenchSplashScreenProcessor( new GenerationCompleteCallback() {
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
                "org/uberfire/annotations/processors/WorkbenchSplashScreenTest1" );

        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenMissingViewAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchSplashScreenTest2" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchSplashScreenTest2Activity: The WorkbenchSplashScreen must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationMissingTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchSplashScreenTest3" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchSplashScreenTest3Activity: The WorkbenchSplashScreen must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenMissingViewAnnotationHasTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchSplashScreenTest4" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchSplashScreenTest4Activity: The WorkbenchSplashScreen must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testIncorrectReturnTypeWithoutArguments() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/annotations/processors/WorkbenchSplashScreenTest5" );

        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.annotations.processors.WorkbenchSplashScreenTest5Activity: The WorkbenchSplashScreen must provide a @SplashFilter annotated method to return a org.uberfire.workbench.model.SplashScreenFilter." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchSplashScreenTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchSplashScreenTest6.expected";

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
    public void testSplashScreenWithActivator() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchSplashScreenTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchSplashScreenTest7.expected";

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
    public void testWorkbenchSplashScreenOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchSplashScreenTest8";

        final Result result = new Result();

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new WorkbenchSplashScreenProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, -1, -1,
                                "Found multiple @OnStartup methods. Each class can declare at most one." );
    }
}
