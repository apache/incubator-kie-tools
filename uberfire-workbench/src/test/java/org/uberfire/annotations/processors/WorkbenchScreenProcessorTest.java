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
import org.uberfire.annotations.processors.GenerationCompleteCallback;
import org.uberfire.annotations.processors.WorkbenchScreenProcessor;
import org.uberfire.annotations.processors.AbstractProcessorTest.Result;

/**
 * Tests for Screen related class generation
 */
public class WorkbenchScreenProcessorTest extends AbstractProcessorTest {

    @Test
    public void testNoWorkbenchScreenAnnotation() throws FileNotFoundException {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchScreenTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenMissingViewAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchScreenTest2" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchPart must either extend isWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationMissingTitleAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchScreenTest3" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchPart must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenMissingViewAnnotationHasTitleAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchScreenTest4" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchPart must either extend isWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchScreenHasViewAnnotationAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest5.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchScreenExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest6.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchScreenHasViewAnnotationAndExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest7.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchPart both extends com.google.gwt.user.client.ui.isWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchScreenAllAnnotations() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest8.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchScreenWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest9.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchScreenWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchScreenTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchScreenTest10.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchScreenProcessor( new GenerationCompleteCallback() {

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

}
