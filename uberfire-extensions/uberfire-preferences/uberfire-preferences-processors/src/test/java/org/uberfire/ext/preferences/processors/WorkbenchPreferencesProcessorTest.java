/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.preferences.processors;

import java.io.FileNotFoundException;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.GenerationCompleteCallback;

import static org.junit.Assert.*;

/**
 * Tests for Preferences related class generation
 */
public class WorkbenchPreferencesProcessorTest extends AbstractProcessorTest {

    Result result = new Result();

    @Override
    protected AbstractErrorAbsorbingProcessor getProcessorUnderTest() {
        return new WorkbenchPreferencesProcessor( new GenerationCompleteCallback() {
            @Override
            public void generationComplete( final String code ) {
                result.setActualCode( code );
            }
        } );
    }

    @Test
    public void testNoWorkbenchPreferencesAnnotation() throws FileNotFoundException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest1" );
        assertSuccessfulCompilation( diagnostics );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPreferencesMissingViewAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest2" );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, 22, 8, "org.uberfire.ext.preferences.processors.WorkbenchPreferencesTest2Activity: The WorkbenchPreferences must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget or preferably org.jboss.errai.common.client.api.IsElement." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasViewAnnotationMissingTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest3" );
        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, Diagnostic.NOPOS, Diagnostic.NOPOS, "org.uberfire.ext.preferences.processors.WorkbenchPreferencesTest3Activity: The WorkbenchPreferences must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPreferencesMissingViewAnnotationHasTitleAnnotation() {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest4" );
        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, 23, 8, "org.uberfire.ext.preferences.processors.WorkbenchPreferencesTest4Activity: The WorkbenchPreferences must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget or preferably org.jboss.errai.common.client.api.IsElement." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasViewAnnotationAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest5";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest5.expected";

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
    public void testWorkbenchPreferencesExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest6";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest6.expected";

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
    public void testWorkbenchPreferencesHasViewAnnotationAndExtendsIsWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest7";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest7.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.WARNING, Diagnostic.NOPOS, Diagnostic.NOPOS, "The WorkbenchPreferences both extends com.google.gwt.user.client.ui.IsWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence." );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesAllAnnotations() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest8";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest8.expected";

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
    public void testWorkbenchPreferencesAllAnnotationsPrivate() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest20";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertFailedCompilation( diagnostics );
        assertCompilationMessage( diagnostics, Kind.ERROR, 35, 22, "Methods annotated with @WorkbenchPartView must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 40, 20, "Methods annotated with @WorkbenchPartTitle must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 45, 18, "Methods annotated with @OnStartup must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 49, 21, "Methods annotated with @OnMayClose must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 54, 18, "Methods annotated with @OnClose must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 58, 18, "Methods annotated with @OnOpen must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 62, 18, "Methods annotated with @OnLostFocus must be non-private" );
        assertCompilationMessage( diagnostics, Kind.ERROR, 66, 18, "Methods annotated with @OnFocus must be non-private" );

        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPreferencesWorkbenchMenuAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest9";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest9.expected";

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
    public void testWorkbenchPreferencesWorkbenchMenuAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest10";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest10.expected";

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
    public void testWorkbenchPreferencesOnStart0Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest11";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest11.expected";

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
    public void testWorkbenchPreferencesOnStart1Parameter() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest12";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest12.expected";

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
    public void testWorkbenchPreferencesOnStartMultipleMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest13";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );

        assertCompilationMessage( diagnostics, Kind.ERROR, 40, 17, "Found multiple @OnStartup methods. Each class can declare at most one." );
    }

    @Test
    public void testWorkbenchPreferencesWorkbenchToolBarAnnotationCorrectReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest14";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest14.expected";

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
    public void testWorkbenchPreferencesWorkbenchToolBarAnnotationWrongReturnType() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest15";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest15.expected";

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
    public void testWorkbenchPreferencesHasTitleWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest16";

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(
                getProcessorUnderTest(),
                pathCompilationUnit );
        assertCompilationMessage( diagnostics, Kind.ERROR, 26, 8, "org.uberfire.ext.preferences.processors.WorkbenchPreferencesTest16Activity: The WorkbenchPreferences must provide a @WorkbenchPartTitle annotated method to return a java.lang.String." );
        assertNull( result.getActualCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasTitleAndTitleWidget() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest17";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest17.expected";

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
    public void testWorkbenchPreferencesCanInheritAnnotatedMethods() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest18";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest18.expected";

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
    public void testPreferencesWithActivator() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest19";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest19.expected";

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
    public void testWorkbenchPreferencesHasTitleAndTitleWidgetWithPreferredWidth() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest24";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest24.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasTitleAndTitleWidgetWithNegativePreferredWidth() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest25";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest25.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasTitleAndTitleWidgetWithPreferredHeight() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest26";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest26.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasTitleAndTitleWidgetWithNegativePreferredHeight() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest27";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest27.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasTitleAndTitleWidgetWithPreferredWidthAndHeight() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest28";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest28.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasTitleAndTitleWidgetWithNegativePreferredWidthAndHeight() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest23";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest23.expected";

        result.setExpectedCode( getExpectedSourceCode( pathExpectedResult ) );

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compile( getProcessorUnderTest(),
                                                                                pathCompilationUnit );
        assertSuccessfulCompilation( diagnostics );
        assertNotNull( result.getActualCode() );
        assertNotNull( result.getExpectedCode() );
        assertEquals( result.getActualCode(),
                      result.getExpectedCode() );
    }

    @Test
    public void testWorkbenchPreferencesHasViewAnnotationIsElementAndHasTitleAnnotation() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest29";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest29.expected";

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
    public void testWorkbenchPreferencesUberElement() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest30";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest30.expected";

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
    public void testWorkbenchPreferencesHasTitleAndTitleElement() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest31";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest31.expected";

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
    public void testWorkbenchPreferencesHasTitleAndTitleAndWidgetAsElement() throws FileNotFoundException {
        final String pathCompilationUnit = "org/uberfire/ext/preferences/processors/WorkbenchPreferencesTest32";
        final String pathExpectedResult = "org/uberfire/ext/preferences/processors/expected/WorkbenchPreferencesTest32.expected";

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

}
