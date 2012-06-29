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

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

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
        for ( Element elem : roundEnv.getElementsAnnotatedWith( WorkbenchWidget.class ) ) {
            WorkbenchWidget wbw = elem.getAnnotation( WorkbenchWidget.class );
            String message = "WorkbenchWidget found in " + elem.getSimpleName()
                             + " with NameToken " + wbw.nameToken();
            processingEnv.getMessager().printMessage( Diagnostic.Kind.NOTE,
                                                      message );
            System.out.println( "-----> " + message );
        }
        return true;
    }

}
