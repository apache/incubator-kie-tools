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
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.uberfire.annotations.processors.WorkbenchEditorProcessor;

/**
 * Tests for Editor related class generation
 */
public class WorkbenchEditorProcessorTest extends AbstractProcessorTest {

    @Test
    public void testNoWorkbenchEditorAnnotation() throws FileNotFoundException {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchEditorTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMissingViewAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchEditorTest2" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchEditor must either extend isWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorHasViewAnnotationMissingTitleAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchEditorTest3" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchEditor must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorMissingViewAnnotationHasTitleAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchEditorTest4" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchEditor must either extend isWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchEditorHasViewAnnotationAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest5.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest6.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(String code) {
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
    public void testWorkbenchEditorHasViewAnnotationAndExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest7.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchEditor both extends com.google.gwt.user.client.ui.isWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchEditorAllAnnotations() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest8.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest9.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest10.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorOnStart1Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest11";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest11.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorOnStart2Parameters() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest12";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest12.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorOnStart2ParametersWrongOrder() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest13";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest13.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
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
    public void testWorkbenchEditorOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchEditorTest14";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchEditorTest14.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchEditorProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchEditor has methods for both @OnStart(Path) and @OnStart(Path, Place). Method @OnStart(Path, Place) will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

}
