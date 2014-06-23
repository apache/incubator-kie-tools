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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import static org.junit.Assert.*;

/**
 * Base miscfeatures to generate source code with an Annotation Processor
 */
public abstract class AbstractProcessorTest {

    private static final String SOURCE_FILETYPE = ".java";

    /**
     * Container for miscfeatures results.
     */
    class Result {

        private String expectedCode;
        private String actualCode;

        String getExpectedCode() {
            return expectedCode;
        }

        void setExpectedCode( final String expectedCode ) {
            this.expectedCode = expectedCode;
        }

        String getActualCode() {
            return actualCode;
        }

        void setActualCode( final String actualCode ) {
            this.actualCode = actualCode;
        }
    }

    /**
     * Compile a unit of source code with the specified annotation processor
     * @param annotationProcessor
     * @param compilationUnits
     * @return
     */
    public List<Diagnostic<? extends JavaFileObject>> compile( final Processor annotationProcessor,
                                                               final String... compilationUnits ) {

        final DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<JavaFileObject>();

        try {

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            final StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnosticListener,
                                                                                         null,
                                                                                         null );

            String[] convertedCompilationUnits = convertCompilationUnitToFilePaths( compilationUnits );
            final Iterable<? extends JavaFileObject> compilationUnitsJavaObjects =
                    fileManager.getJavaFileObjects( convertedCompilationUnits );

            //Compile with provide annotation processor
            final CompilationTask task = compiler.getTask( null,
                                                           fileManager,
                                                           diagnosticListener,
                                                           null,
                                                           null,
                                                           compilationUnitsJavaObjects );
            task.setProcessors( Arrays.asList( annotationProcessor ) );
            task.call();
            fileManager.close();

        } catch ( IOException ioe ) {
            fail( ioe.getMessage() );
        }

        return diagnosticListener.getDiagnostics();
    }

    private String[] convertCompilationUnitToFilePaths( String[] compilationUnits ) {
        List<String> convertedCompilationUnits = new ArrayList<String>();
        for ( String compilationUnit : compilationUnits ) {
            convertedCompilationUnits.add( this.getClass().getResource( "/" + compilationUnit + SOURCE_FILETYPE ).getPath() );
        }
        return convertedCompilationUnits.toArray( new String[ convertedCompilationUnits.size() ] );
    }

    /**
     * Retrieve the expected source code for a compilation unit
     * @param compilationUnit
     * @return
     * @throws FileNotFoundException
     */
    public String getExpectedSourceCode( final String compilationUnit ) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            final String path = this.getClass().getResource( "/" + compilationUnit ).getPath();
            final FileReader fr = new FileReader( path );
            final BufferedReader input = new BufferedReader( fr );
            try {
                String line = null;
                while ( ( line = input.readLine() ) != null ) {
                    sb.append( line );
                    sb.append( System.getProperty( "line.separator" ) );
                }
            } finally {
                input.close();
            }
        } catch ( FileNotFoundException fnfe ) {
            throw fnfe;
        } catch ( IOException ioe ) {
            fail( ioe.getMessage() );
        }
        return sb.toString();

    }

    /**
     * Assert that compilation was successful
     * @param diagnostics
     */
    public void assertSuccessfulCompilation( final List<Diagnostic<? extends JavaFileObject>> diagnostics ) {
        assertFalse( hasErrors( diagnostics ) );
    }

    /**
     * Assert that compilation failed
     * @param diagnostics
     */
    public void assertFailedCompilation( final List<Diagnostic<? extends JavaFileObject>> diagnostics ) {
        assertTrue( hasErrors( diagnostics ) );
    }

    private boolean hasErrors( final List<Diagnostic<? extends JavaFileObject>> diagnostics ) {
        for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics ) {
            if ( diagnostic.getKind().equals( Kind.ERROR ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assert that the given error message is contained in the compilation
     * diagnostics
     * @param diagnostics
     * @param message
     */
    public void assertCompilationError( List<Diagnostic<? extends JavaFileObject>> diagnostics,
                                        final String message ) {
        final List<String> messages = getMessages( diagnostics,
                                                   Kind.ERROR );
        assertTrue( messages.contains( "error: " + message ) || messages.contains( message ) );
    }

    /**
     * Assert that the given warning message is contained in the compilation
     * diagnostics
     * @param diagnostics
     * @param message
     */
    public void assertCompilationWarning( List<Diagnostic<? extends JavaFileObject>> diagnostics,
                                          final String message ) {
        final List<String> messages = getMessages( diagnostics,
                                                   Kind.WARNING );
        assertTrue( messages.contains( "warning: " + message ) || messages.contains( message ) );
    }

    private List<String> getMessages( final List<Diagnostic<? extends JavaFileObject>> diagnostics,
                                      final Kind kind ) {
        final List<String> messages = new ArrayList<String>();
        for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics ) {
            if ( diagnostic.getKind().equals( kind ) ) {
                System.out.println( diagnostic.getMessage( null ) );
                messages.add( diagnostic.getMessage( null ) );
            }
        }
        return messages;
    }

}
