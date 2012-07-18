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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
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

/**
 * Base test to generate source code with an Annotation Processor
 */
public abstract class AbstractProcessorTest {

    //Consistent with maven's default
    private static final String TARGET_ROOT     = "target/generated-test-sources/test-annotations";

    private static final String SOURCE_FILETYPE = ".java";

    /**
     * Compile a unit of source code with the specified annotation processor
     * 
     * @param annotationProcessor
     * @param compilationUnit
     * @return
     */
    public List<Diagnostic< ? extends JavaFileObject>> compile(final Processor annotationProcessor,
                                                               final String compilationUnit) {

        final DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<JavaFileObject>();

        final File targetFolder = new File( TARGET_ROOT );

        try {

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            final StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnosticListener,
                                                                                         null,
                                                                                         null );
            //House keeping: Make target folder for generated source
            final List<String> options = new ArrayList<String>();
            if ( !deleteFile( targetFolder ) ) {
                fail( "Unable to delete target folder [" + TARGET_ROOT + "]." );
            }
            if ( !targetFolder.mkdirs() ) {
                fail( "Unable to create target folder [" + TARGET_ROOT + "]." );
            }

            //Set compiler's output folder to our target folder
            options.add( "-s" );
            options.add( targetFolder.getAbsolutePath() );

            //Convert compilation unit to file path and add to items to compile
            final String path = this.getClass().getResource( "/" + compilationUnit + SOURCE_FILETYPE ).getPath();
            final Iterable< ? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects( path );

            //Compile with provide annotation processor
            final CompilationTask task = compiler.getTask( null,
                                                           fileManager,
                                                           diagnosticListener,
                                                           options,
                                                           null,
                                                           compilationUnits );
            task.setProcessors( Arrays.asList( annotationProcessor ) );
            task.call();

            fileManager.close();

        } catch ( IOException ioe ) {
            fail( ioe.getMessage() );
        }

        return diagnosticListener.getDiagnostics();
    }

    /**
     * Retrieve the generated source code for a compilation unit
     * 
     * @param compilationUnit
     * @return
     * @throws FileNotFoundException
     */
    public String getGeneratedSourceCode(final String compilationUnit) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            final FileReader fr = new FileReader( TARGET_ROOT + "/" + compilationUnit + "Activity" + SOURCE_FILETYPE );
            final BufferedReader input = new BufferedReader( fr );
            try {
                String line = null;
                while ( (line = input.readLine()) != null ) {
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
     * Retrieve the expected source code for a compilation unit
     * 
     * @param compilationUnit
     * @return
     * @throws FileNotFoundException
     */
    public String getExpectedSourceCode(final String compilationUnit) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            final String path = this.getClass().getResource( "/" + compilationUnit ).getPath();
            final FileReader fr = new FileReader( path );
            final BufferedReader input = new BufferedReader( fr );
            try {
                String line = null;
                while ( (line = input.readLine()) != null ) {
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

    //Recursive folder delete
    private boolean deleteFile(File file) {
        if ( !file.exists() ) {
            return true;
        }
        if ( file.isDirectory() ) {
            String[] children = file.list();
            for ( int i = 0; i < children.length; i++ ) {
                boolean success = deleteFile( new File( file,
                                                        children[i] ) );
                if ( !success ) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /**
     * Assert that compilation was successful
     * 
     * @param diagnostics
     */
    public void assertSuccessfulCompilation(final List<Diagnostic< ? extends JavaFileObject>> diagnostics) {
        assertFalse( hasErrors( diagnostics ) );
    }

    /**
     * Assert that compilation failed
     * 
     * @param diagnostics
     */
    public void assertFailedCompilation(final List<Diagnostic< ? extends JavaFileObject>> diagnostics) {
        assertTrue( hasErrors( diagnostics ) );
    }

    private boolean hasErrors(final List<Diagnostic< ? extends JavaFileObject>> diagnostics) {
        for ( Diagnostic< ? extends JavaFileObject> diagnostic : diagnostics ) {
            if ( diagnostic.getKind().equals( Kind.ERROR ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assert that the given error message is contained in the compilation
     * diagnostics
     * 
     * @param diagnostics
     * @param message
     */
    public void assertCompilationError(List<Diagnostic< ? extends JavaFileObject>> diagnostics,
                                       final String message) {
        final List<String> messages = getMessages( diagnostics,
                                                   Kind.ERROR );
        assertTrue( messages.contains( "error: " + message ) );
    }

    /**
     * Assert that the given warning message is contained in the compilation
     * diagnostics
     * 
     * @param diagnostics
     * @param message
     */
    public void assertCompilationWarning(List<Diagnostic< ? extends JavaFileObject>> diagnostics,
                                         final String message) {
        final List<String> messages = getMessages( diagnostics,
                                                   Kind.WARNING );
        assertTrue( messages.contains( "warning: " + message ) );
    }

    private List<String> getMessages(final List<Diagnostic< ? extends JavaFileObject>> diagnostics,
                                     final Kind kind) {
        final List<String> messages = new ArrayList<String>();
        for ( Diagnostic< ? extends JavaFileObject> diagnostic : diagnostics ) {
            if ( diagnostic.getKind().equals( kind ) ) {
                System.out.println( diagnostic.getMessage( null ) );
                messages.add( diagnostic.getMessage( null ) );
            }
        }
        return messages;
    }

}
