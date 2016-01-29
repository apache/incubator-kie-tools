/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.annotations.processors;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

/**
 * Processor for {@code WorkbenchScreen} and related annotations
 */
@SupportedAnnotationTypes("org.uberfire.client.annotations.WorkbenchScreen")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class WorkbenchScreenProcessor extends AbstractErrorAbsorbingProcessor {

    private final ScreenActivityGenerator activityGenerator;
    private GenerationCompleteCallback callback = null;

    public WorkbenchScreenProcessor() {
        ScreenActivityGenerator ag = null;
        try {
            ag = new ScreenActivityGenerator();
        } catch (Throwable t) {
            rememberInitializationError(t);
        }
        activityGenerator = ag;
    }

    //Constructor for tests only, to prevent code being written to file. The generated code will be sent to the call-back
    WorkbenchScreenProcessor( final GenerationCompleteCallback callback ) {
        this();
        this.callback = callback;
        System.out.println( "GenerationCompleteCallback has been provided. Generated source code will not be compiled and hence classes will not be available." );
    }

    @Override
    public boolean processWithExceptions( Set<? extends TypeElement> annotations,
                            RoundEnvironment roundEnv ) throws IOException {
        //We don't have any post-processing
        if ( roundEnv.processingOver() ) {
            return false;
        }

        //If prior processing threw an error exit
        if ( roundEnv.errorRaised() ) {
            return false;
        }

        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();

        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( ClientAPIModule.getWorkbenchScreenClass() ) ) ) {
            if ( e.getKind() == ElementKind.CLASS ) {

                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                messager.printMessage( Kind.NOTE, "Discovered class [" + classElement.getSimpleName() + "]" );

                final String packageName = packageElement.getQualifiedName().toString();
                final String classNameActivity = classElement.getSimpleName() + "Activity";

                try {
                    //Try generating code for each required class
                    messager.printMessage( Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
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
                    processingEnv.getMessager().printMessage( Kind.ERROR, msg, classElement );
                }
            }
        }
        return true;
    }
}
