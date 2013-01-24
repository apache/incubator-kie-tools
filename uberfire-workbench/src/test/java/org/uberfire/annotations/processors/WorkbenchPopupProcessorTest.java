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

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for Pop-up related class generation
 */
@Ignore
public class WorkbenchPopupProcessorTest extends AbstractProcessorTest {

    @Test
    public void testNoWorkbenchPopupAnnotation() throws FileNotFoundException {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchPopupTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPopupAnnotationMissingViewAnnotation() {
        final Result result = new Result();
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 "org/uberfire/annotations/processors/WorkbenchPopupTest2" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "org.uberfire.annotations.processors.WorkbenchPopupTest2Activity: The WorkbenchPopup must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPopupHasViewAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest3";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest3.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchPopupExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest4";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest4.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchPopupHasViewAnnotationAndExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest5";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest5.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchPopup both extends com.google.gwt.user.client.ui.IsWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPopupAllAnnotations() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest6";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest6.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchPopupOnStart0Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest7";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest7.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchPopupOnStart1Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest8";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest8.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchPopupOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest9";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest9.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

                                                                                     @Override
                                                                                     public void generationComplete(final String code) {
                                                                                         result.setActualCode( code );
                                                                                     }
                                                                                 } ),
                                                                                 pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchPopup has methods for both @OnStart() and @OnStart(Place). Method @OnStart(Place) will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPopupHasTitleWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest10";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest10.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

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
    public void testWorkbenchPopupHasTitleAndTitleWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/annotations/processors/WorkbenchPopupTest11";
        final String pathExpectedResult = "org/uberfire/annotations/processors/expected/WorkbenchPopupTest11.expected";

        final Result result = new Result();
        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor( new GenerationCompleteCallback() {

            @Override
            public void generationComplete(final String code) {
                result.setActualCode( code );
            }
        } ),
                                                                                 pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchPopup has a @WorkbenchPartTitle annotated method that returns java.lang.String and @WorkbenchPartTitle annotated method that returns com.google.gwt.user.client.ui.IsWidget. The IsWidget method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }


}
