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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import org.drools.guvnor.annotations.processors.exceptions.GenerationException;
import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor for {@code WorkbenchEditor} and related annotations
 */
@SupportedAnnotationTypes("org.drools.guvnor.client.annotations.WorkbenchEditor")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class WorkbenchEditorProcessor extends AbstractProcessor {

    private static final Logger           logger            = LoggerFactory.getLogger( WorkbenchEditorProcessor.class );

    private final EditorActivityGenerator activityGenerator = new EditorActivityGenerator();

    private GenerationCompleteCallback    callback          = null;

    //Constructor for tests only, to prevent code being written to file. The generated code will be sent to the call-back
    WorkbenchEditorProcessor(final GenerationCompleteCallback callback) {
        this.callback = callback;
        logger.info( "GenerationCompleteCallback has been provided. Generated source code will not be compiled and hence classes will not be available." );
    }

    @Override
    public boolean process(Set< ? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        //We don't have any post-processing
        if ( roundEnv.processingOver() ) {
            return false;
        }

        //If prior processing threw an error exit
        if ( roundEnv.errorRaised() ) {
            return false;
        }

        //Scan for all classes with the WorkbenchEditor annotation
        for ( Element e : roundEnv.getElementsAnnotatedWith( WorkbenchEditor.class ) ) {
            if ( e.getKind() == ElementKind.CLASS ) {

                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                logger.info( "Discovered class [" + classElement.getSimpleName() + "]" );

                final String packageName = packageElement.getQualifiedName().toString();
                final String classNameActivity = classElement.getSimpleName() + "Activity";

                try {
                    //Try generating code for each required class
                    logger.info( "Generating code for [" + classNameActivity + "]" );
                    final StringBuffer activityCode = activityGenerator.generate( packageName,
                                                                                  packageElement,
                                                                                  classNameActivity,
                                                                                  classElement,
                                                                                  processingEnv );

                    //If code is successfully created write files, or send generated code to call-back.
                    //The call-back function is used primarily for testing when we don't necessarily want
                    //the generated code to be stored as a compilable file for javac to process.
                    if ( callback == null ) {
                        writeCode( packageName,
                                   classNameActivity,
                                   activityCode );
                    } else {
                        callback.generationComplete( activityCode.toString() );
                    }
                } catch ( GenerationException ge ) {
                    final String msg = ge.getMessage();
                    processingEnv.getMessager().printMessage( Kind.ERROR,
                                                              msg );
                    logger.error( msg );
                }
            }
        }
        return true;
    }

    //Write generated code to javac's Filer
    private void writeCode(final String packageName,
                           final String className,
                           final StringBuffer code) {
        try {
            JavaFileObject jfo = processingEnv.getFiler().createSourceFile( packageName + "." + className );
            Writer w = jfo.openWriter();
            BufferedWriter bw = new BufferedWriter( w );
            bw.append( code );
            bw.close();
            w.close();
        } catch ( IOException ioe ) {
            logger.error( ioe.getMessage(),
                          ioe );
        }
    }

}
