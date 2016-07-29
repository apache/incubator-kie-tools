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
import javax.tools.Diagnostic;

import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.GenerationCompleteCallback;
import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * Processor for {@code WorkbenchPreferences} and related annotations
 */
@SupportedAnnotationTypes(WorkbenchPreferencesProcessor.WORKBENCH_PREFERENCES)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class WorkbenchPreferencesProcessor extends AbstractErrorAbsorbingProcessor {

    public static final String WORKBENCH_PREFERENCES = "org.uberfire.ext.preferences.client.mvp.WorkbenchPreferences";

    private final PreferencesActivityGenerator activityGenerator;
    private GenerationCompleteCallback callback = null;

    public WorkbenchPreferencesProcessor() {
        PreferencesActivityGenerator ag = null;
        try {
            ag = new PreferencesActivityGenerator();
        } catch (Throwable t) {
            rememberInitializationError(t);
        }
        activityGenerator = ag;
    }

    //Constructor for tests only, to prevent code being written to file. The generated code will be sent to the call-back
    WorkbenchPreferencesProcessor( final GenerationCompleteCallback callback ) {
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

        for ( Element e : roundEnv.getElementsAnnotatedWith( elementUtils.getTypeElement( WORKBENCH_PREFERENCES ) ) ) {
            if ( e.getKind() == ElementKind.CLASS ) {

                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                messager.printMessage( Diagnostic.Kind.NOTE, "Discovered class [" + classElement.getSimpleName() + "]" );

                final String packageName = packageElement.getQualifiedName().toString();
                final String classNameActivity = classElement.getSimpleName() + "Activity";

                try {
                    //Try generating code for each required class
                    messager.printMessage( Diagnostic.Kind.NOTE, "Generating code for [" + classNameActivity + "]" );
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
                    processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR, msg, classElement );
                }
            }
        }
        return true;
    }
}
