/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.annotations.processors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

@SupportedAnnotationTypes(ClientAPIModule.experimentalFeature)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ExperimentalFeatureProcessor extends AbstractErrorAbsorbingProcessor {

    private static final String PROVIDER_SUFFIX = "ExperimentalFeatureDefinitionProvider";
    private static final String EXPERIMENTAL_ACTIVITY_SUFFIX = "ExperimentalFeatureActivityReference";

    private static final String PERSPECTIVE = "PERSPECTIVE";
    private static final String SCREEN = "SCREEN";
    private static final String EDITOR = "EDITOR";

    public static final Map<String, String> ACTIVITY_TYPES_MAPPING;

    static {
        ACTIVITY_TYPES_MAPPING = new HashMap<>();

        ACTIVITY_TYPES_MAPPING.put(ClientAPIModule.getWorkbenchPerspectiveClass(), "PERSPECTIVE");
        ACTIVITY_TYPES_MAPPING.put(ClientAPIModule.getWorkbenchScreenClass(), "SCREEN");
        ACTIVITY_TYPES_MAPPING.put(ClientAPIModule.getWorkbenchEditorClass(), "EDITOR");
    }

    private ExperimentalFeatureDefinitionProviderGenerator providerGenerator;

    private ExperimentalActivityGenerator experimentalActivityGenerator;

    private GenerationCompleteCallback callback = null;

    public ExperimentalFeatureProcessor() {
        ExperimentalFeatureDefinitionProviderGenerator providerGen = null;
        ExperimentalActivityGenerator activityGen = null;
        try {
            providerGen = new ExperimentalFeatureDefinitionProviderGenerator();
            activityGen = new ExperimentalActivityGenerator();
        } catch (Throwable t) {
            rememberInitializationError(t);
        }
        providerGenerator = providerGen;
        experimentalActivityGenerator = activityGen;
    }

    //Constructor for tests only, to prevent code being written to file. The generated code will be sent to the call-back
    ExperimentalFeatureProcessor(final GenerationCompleteCallback callback) {
        this();
        this.callback = callback;
        System.out.println("GenerationCompleteCallback has been provided. Generated source code will not be compiled and hence classes will not be available.");
    }

    @Override
    protected boolean processWithExceptions(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {

        //We don't have any post-processing
        if (roundEnv.processingOver()) {
            return false;
        }

        //If prior processing threw an error exit
        if (roundEnv.errorRaised()) {
            return false;
        }

        final Messager messager = processingEnv.getMessager();
        final Elements elementUtils = processingEnv.getElementUtils();

        for (Element e : roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ClientAPIModule.experimentalFeature))) {
            if (e.getKind() == ElementKind.CLASS) {

                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Discovered experimental feature [" + classElement.getSimpleName() + "]");

                final String packageName = packageElement.getQualifiedName().toString();
                final String className = classElement.getSimpleName() + PROVIDER_SUFFIX;

                try {
                    //Try generating code for each required class
                    messager.printMessage(Diagnostic.Kind.NOTE, "Generating code for [" + className + "]");

                    final StringBuffer code = providerGenerator.generate(packageName,
                                                                         packageElement,
                                                                         className,
                                                                         classElement,
                                                                         processingEnv);

                    //If code is successfully created write files, or send generated code to call-back.
                    //The call-back function is used primarily for testing when we don't necessarily want
                    //the generated code to be stored as a compilable file for javac to process.
                    write(packageName, className, code);

                    if (getActivityType(classElement).isPresent()) {
                        final String activityClassName = classElement.getSimpleName() + EXPERIMENTAL_ACTIVITY_SUFFIX;
                        final StringBuffer activityCode = experimentalActivityGenerator.generate(packageName, packageElement, activityClassName, classElement, processingEnv);

                        write(packageName, activityClassName, activityCode);
                    }
                } catch (GenerationException ge) {
                    final String msg = ge.getMessage();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                             msg,
                                                             classElement);
                }
            }
        }
        return true;
    }

    public static Optional<String> getActivityType(TypeElement classElement) {
        return classElement.getAnnotationMirrors().stream()
                .map(mirror -> ACTIVITY_TYPES_MAPPING.get(mirror.getAnnotationType().toString()))
                .filter(Objects::nonNull)
                .findAny();
    }

    private void write(final String packageName, final String className, final StringBuffer code) throws IOException {
        if (callback == null) {
            writeCode(packageName, className, code);
        } else {
            callback.generationComplete(code.toString());
        }
    }
}
