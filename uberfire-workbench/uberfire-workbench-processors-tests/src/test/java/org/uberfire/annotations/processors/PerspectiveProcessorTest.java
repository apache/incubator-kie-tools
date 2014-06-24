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

import java.io.FileNotFoundException;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Pop-up related class generation
 */
public class PerspectiveProcessorTest extends AbstractProcessorTest {

    @Test
    public void testNoPerspectiveAnnotation() throws FileNotFoundException {
        final Result result = new Result();
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                "org/uberfire/annotations/processors/PerspectiveTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testIncorrectReturnTypeWithoutArguments() throws FileNotFoundException {
        final Result result = new Result();
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                "org/uberfire/annotations/processors/PerspectiveTest2" );
        assertCompilationError( diagnostics,
                                "org.uberfire.annotations.processors.PerspectiveTest2Activity: The WorkbenchPerspective must provide a @Perspective annotated method to return a org.uberfire.client.workbench.model.PerspectiveDefinition." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testCorrectReturnTypeWithArguments() throws FileNotFoundException {
        final Result result = new Result();
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                "org/uberfire/annotations/processors/PerspectiveTest3" );
        assertCompilationError( diagnostics,
                                "org.uberfire.annotations.processors.PerspectiveTest3Activity: The WorkbenchPerspective must provide a @Perspective annotated method to return a org.uberfire.client.workbench.model.PerspectiveDefinition." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testCorrectReturnTypeWithoutArguments() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest4";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest4.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testCorrectReturnTypeWithoutArgumentsIsDefault() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest5.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testCorrectReturnTypeWithAllAnnotationsOnStart() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest6.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testCorrectReturnTypeWithAllAnnotationsOnStartWithPath() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest7.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest8.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest9.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchToolBarAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest10.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchToolBarAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest11";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest11.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchTemplateAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest12";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest12.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        printDiagnostics( diagnostics );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchTemplateAnnotationWithOnlyWorkbenchParts() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest13";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest13.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        printDiagnostics( diagnostics );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchTemplateAnnotationMustHaveWorkbenchPanelsOrParts() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest14";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest13.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        printDiagnostics( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchTemplateAnnotationShouldNotAllowTwoDefaultWorkbenchPanels() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest15";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest13.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        assertCompilationError( diagnostics,
                                "The Template WorkbenchPerspective must provide only one @WorkbenchPanel annotated field." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchTemplateAnnotationWithNoDefaultWorkbenchPanel() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest16";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest16.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testAlonePartAnnotationShouldGenerateDefaultPanel() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest17";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest17.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        printDiagnostics( diagnostics );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testAlonePartsAnnotationShouldGenerateDefaultPanel() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest18";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest18.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        printDiagnostics( diagnostics );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testPartsAnnotationShouldReceiveParameters() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest19";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest19.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        printDiagnostics( diagnostics );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void twoDefaultPerspectivesShouldGenerateAWarning() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest19";
        final String pathCompilationUnit2 = "org/uberfire/annotations/processors/PerspectiveTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/PerspectiveTest19.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit, pathCompilationUnit2 );
        printDiagnostics( diagnostics );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics, "There is more than one default WorkbenchPerspective (HomePerspective, PerspectiveTest7) - @WorkbenchPerspective(isDefault = true). One of them will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPerspectivesOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/PerspectiveTest20";

        final Result result = new Result();

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( new PerspectiveProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } ), pathCompilationUnit );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics, "The WorkbenchPerspective has methods for both @OnStartup() and @OnStartup(Place). Method @OnStartup(Place) will take precedence." );
        assertNull( result.getActualCode() );
    }

    private void printDiagnostics( List<Diagnostic<? extends JavaFileObject>> diagnostics ) {
        for ( Diagnostic diagnostic : diagnostics ) {
            System.out.println( diagnostic );
        }
    }

}
