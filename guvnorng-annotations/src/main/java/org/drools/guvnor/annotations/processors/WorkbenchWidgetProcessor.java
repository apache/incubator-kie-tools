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
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.drools.guvnor.client.annotations.WorkbenchWidget;

/**
 * 
 */
@SupportedAnnotationTypes("org.drools.guvnor.client.annotations.WorkbenchWidget")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class WorkbenchWidgetProcessor extends AbstractProcessor {

    public WorkbenchWidgetProcessor() {
        System.out.println( "-----> WorkbenchWidgetProcessor.constructor" );
    }

    @Override
    public boolean process(Set< ? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for ( Element e : roundEnv.getElementsAnnotatedWith( WorkbenchWidget.class ) ) {
            System.out.println( "-----> e.kind = " + e.getKind() );
            if ( e.getKind() == ElementKind.CLASS ) {
                TypeElement classElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                try {
                    final String packageName = packageElement.getQualifiedName().toString();
                    final String className = classElement.getSimpleName() + "Smurf";
                    System.out.println( "-----> Generating source code for [" + className + "]" );

                    JavaFileObject jfo = processingEnv.getFiler().createSourceFile( packageName + "." + className );
                    Writer w = jfo.openWriter();
                    BufferedWriter bw = new BufferedWriter( w );
                    bw.append( "package " ).append( packageElement.getQualifiedName() ).append( ";" );
                    bw.newLine();
                    bw.newLine();
                    bw.append( "import org.drools.guvnor.client.editors.test.generated.TestActivity;" );
                    bw.newLine();
                    bw.append( "import javax.enterprise.context.Dependent;" );
                    bw.newLine();
                    bw.append( "import org.drools.guvnor.client.mvp.NameToken;" );
                    bw.newLine();
                    bw.newLine();
                    bw.append( "@Dependent" );
                    bw.newLine();
                    bw.append( "@NameToken(\"Smurf\")" );
                    bw.newLine();
                    bw.append( "public class " ).append( className ).append( " extends TestActivity {" );
                    bw.newLine();
                    bw.append( "}" );
                    bw.newLine();
                    bw.close();
                    w.close();

                    System.out.println( "-----> Source code generated..." );

                } catch ( IOException ioe ) {
                    System.out.println( ioe.getMessage() );
                }
            }

            WorkbenchWidget wbw = e.getAnnotation( WorkbenchWidget.class );
            String message = "WorkbenchWidget found in " + e.getSimpleName()
                             + " with NameToken " + wbw.nameToken();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.NOTE,
                                                      message );
            System.out.println( "-----> " + message );
        }
        return true;
    }

}
