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
import org.uberfire.annotations.processors.WorkbenchPopupProcessor;

/**
 * Tests for Pop-up related class generation
 */
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
                                "The WorkbenchPart must either extend PopupPanel or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.PopupPanel." );
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
    public void testWorkbenchPopupExtendsPopupPanel() throws FileNotFoundException {
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
    public void testWorkbenchPopupHasViewAnnotationAndExtendsPopupPanel() throws FileNotFoundException {
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
                                  "The WorkbenchPart both extends com.google.gwt.user.client.ui.PopupPanel and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
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

}
