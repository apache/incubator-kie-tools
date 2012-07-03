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

import org.drools.guvnor.client.annotations.WorkbenchWidget;

/**
 * 
 */
@SupportedAnnotationTypes("org.drools.guvnor.client.annotations.WorkbenchWidget")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class WorkbenchWidgetProcessor extends AbstractProcessor {

    private final PlaceGenerator    placeGenerator    = new PlaceGenerator();

    private final ActivityGenerator activityGenerator = new ActivityGenerator();

    @Override
    public boolean process(Set< ? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for ( Element e : roundEnv.getElementsAnnotatedWith( WorkbenchWidget.class ) ) {
            if ( e.getKind() == ElementKind.CLASS ) {
                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
                generatePlace( packageElement,
                               classElement );
                generateActivity( packageElement,
                                  classElement );
            }
        }
        return true;
    }

    private void generatePlace(final PackageElement packageElement,
                               final TypeElement classElement) {
        try {

            final String packageName = packageElement.getQualifiedName().toString();
            final String className = classElement.getSimpleName() + "Place";

            JavaFileObject jfo = processingEnv.getFiler().createSourceFile( packageName + "." + className );
            Writer w = jfo.openWriter();
            BufferedWriter bw = new BufferedWriter( w );
            placeGenerator.generate( packageName,
                                     packageElement,
                                     className,
                                     classElement,
                                     processingEnv,
                                     bw );
            bw.close();
            w.close();

        } catch ( IOException ioe ) {
            System.out.println( ioe.getMessage() );
        }
    }

    private void generateActivity(final PackageElement packageElement,
                                  final TypeElement classElement) {
        try {

            final String packageName = packageElement.getQualifiedName().toString();
            final String className = classElement.getSimpleName() + "Activity";

            JavaFileObject jfo = processingEnv.getFiler().createSourceFile( packageName + "." + className );
            Writer w = jfo.openWriter();
            BufferedWriter bw = new BufferedWriter( w );
            activityGenerator.generate( packageName,
                                        packageElement,
                                        className,
                                        classElement,
                                        processingEnv,
                                        bw );
            bw.close();
            w.close();

        } catch ( IOException ioe ) {
            System.out.println( ioe.getMessage() );
        }
    }

}
