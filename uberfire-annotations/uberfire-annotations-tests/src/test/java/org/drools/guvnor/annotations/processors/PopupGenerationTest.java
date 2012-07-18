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
package org.drools.guvnor.annotations.processors;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;

/**
 * Tests for Pop-up related class generation
 */
public class PopupGenerationTest extends AbstractPopupGenerationTest {

    @Test(expected = java.io.FileNotFoundException.class)
    public void testNoWorkbenchPopupAnnotation() throws FileNotFoundException {
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor(),
                                                                                 "org/drools/guvnor/annotations/processors/PopupTest1" );
        assertSuccessfulCompilation( diagnostics );

        //Check no source code was generated
        getGeneratedSourceCode( "PopupTest1" );
    }

    @Test
    public void testWorkbenchPopupAnnotationMissingViewAnnotation() {
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor(),
                                                                                 "org/drools/guvnor/annotations/processors/PopupTest2" );
        assertFailedCompilation( diagnostics );
        assertCompilationError( diagnostics,
                                "The WorkbenchPart must either extend PopupPanel or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.PopupPanel." );
    }

    @Test
    public void testWorkbenchPopupView() throws FileNotFoundException {
        final String compilationUnit = "org/drools/guvnor/annotations/processors/PopupTest3";
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor(),
                                                                                 compilationUnit );
        assertSuccessfulCompilation( diagnostics );

        final String source = getGeneratedSourceCode( compilationUnit );
        assertNotNull( source );
    }

    @Test
    public void testWorkbenchPopupPopupPanel() throws FileNotFoundException {
        final String compilationUnit = "org/drools/guvnor/annotations/processors/PopupTest4";
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor(),
                                                                                 compilationUnit );
        assertSuccessfulCompilation( diagnostics );

        final String source = getGeneratedSourceCode( compilationUnit );
        assertNotNull( source );
    }

    @Test
    public void testWorkbenchPopupViewAndPopupPanel() throws FileNotFoundException {
        final String compilationUnit = "org/drools/guvnor/annotations/processors/PopupTest5";
        final List<Diagnostic< ? extends JavaFileObject>> diagnostics = compile( new WorkbenchPopupProcessor(),
                                                                                 compilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationWarning( diagnostics,
                                  "The WorkbenchPart both extends com.google.gwt.user.client.ui.PopupPanel and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );

        final String source = getGeneratedSourceCode( compilationUnit );
        assertNotNull( source );
    }

}
