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
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public abstract class AbstractPopupGenerationTest {

    //Consistent with maven's default
    private static final String TARGET_ROOT      = "target/generated-test-sources/test-annotations";

    private static final String TARGET_PATH_FULL = "target/generated-test-sources/test-annotations/org/drools/guvnor/annotations/processors";

    private static final String SOURCE_FILETYPE  = ".java";

    public void compile(final String compilationUnit) {

        final DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<JavaFileObject>();

        final File targetFolder = new File( TARGET_ROOT );

        try {

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            final StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnosticListener,
                                                                                         null,
                                                                                         null );
            final List<String> options = new ArrayList<String>();
            if ( !deleteFile( targetFolder ) ) {
                fail( "Unable to delete target folder [" + TARGET_ROOT + "]." );
            }
            if ( !targetFolder.mkdirs() ) {
                fail( "Unable to create target folder [" + TARGET_ROOT + "]." );
            }

            options.add( "-s" );
            options.add( targetFolder.getAbsolutePath() );

            final String path = this.getClass().getResource( compilationUnit + SOURCE_FILETYPE ).getPath();

            final Iterable< ? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects( path );

            final CompilationTask task = compiler.getTask( null,
                                                           fileManager,
                                                           diagnosticListener,
                                                           options,
                                                           null,
                                                           compilationUnits );
            task.setProcessors( Arrays.asList( new WorkbenchPopupProcessor() ) );
            task.call();

            fileManager.close();

        } catch ( IOException ioe ) {
            fail( ioe.getMessage() );
        }

        assertCompilationSuccessful( diagnosticListener.getDiagnostics() );
    }

    public String getGeneratedSourceCode(final String compilationUnit) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader( TARGET_PATH_FULL + "/" + compilationUnit + "Activity" + SOURCE_FILETYPE );
            BufferedReader input = new BufferedReader( fr );
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

    private void assertCompilationSuccessful(List<Diagnostic< ? extends JavaFileObject>> diagnostics) {
        assert (diagnostics != null);
        for ( Diagnostic< ? extends JavaFileObject> diagnostic : diagnostics ) {
            assertFalse( diagnostic.getKind().equals( Kind.ERROR ) );
        }

    }

}
