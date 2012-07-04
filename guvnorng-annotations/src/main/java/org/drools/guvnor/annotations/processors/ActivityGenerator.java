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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.drools.guvnor.client.annotations.DefaultPosition;
import org.drools.guvnor.client.annotations.OnClose;
import org.drools.guvnor.client.annotations.OnFocus;
import org.drools.guvnor.client.annotations.OnLostFocus;
import org.drools.guvnor.client.annotations.OnMayClose;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchPart;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 */
public class ActivityGenerator extends AbstractGenerator {

    public void generate(final String packageName,
                         final PackageElement packageElement,
                         final String className,
                         final TypeElement classElement,
                         final ProcessingEnvironment processingEnvironment,
                         final Writer w) {
        final WorkbenchPart wbw = classElement.getAnnotation( WorkbenchPart.class );
        final String tokenName = wbw.nameToken();

        System.out.println( "-----> Generating source code for Activity [" + className + "]" );

        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "tokenName",
                  tokenName );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onStartMethodName",
                  getVoidMethodName( classElement,
                                     processingEnvironment,
                                     OnStart.class ) );
        root.put( "mayCloseMethodName",
                  getBooleanMethodName( classElement,
                                        processingEnvironment,
                                        OnMayClose.class ) );
        root.put( "onCloseMethodName",
                  getVoidMethodName( classElement,
                                     processingEnvironment,
                                     OnClose.class ) );
        root.put( "onRevealMethodName",
                  getVoidMethodName( classElement,
                                     processingEnvironment,
                                     OnReveal.class ) );
        root.put( "onLostFocusMethodName",
                  getVoidMethodName( classElement,
                                     processingEnvironment,
                                     OnLostFocus.class ) );
        root.put( "onFocusMethodName",
                  getVoidMethodName( classElement,
                                     processingEnvironment,
                                     OnFocus.class ) );
        root.put( "getDefaultPositionMethodName",
                  getDefaultPositionMethodName( classElement,
                                                processingEnvironment,
                                                DefaultPosition.class ) );
        root.put( "getTitleMethodName",
                  getStringMethodName( classElement,
                                       processingEnvironment,
                                       WorkbenchPartTitle.class ) );
        root.put( "getWidgetMethodName",
                  getIsWidgetMethodName( classElement,
                                         processingEnvironment,
                                         WorkbenchPartView.class ) );
        root.put( "isWidget",
                  false );

        try {
            final Template template = config.getTemplate( "activity.ftl" );
            template.process( root,
                              w );
        } catch ( IOException ioe ) {
            System.out.println( ioe.getMessage() );
        } catch ( TemplateException te ) {
            System.out.println( te.getMessage() );
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getVoidMethodName(final TypeElement classElement,
                                     final ProcessingEnvironment processingEnvironment,
                                     final Class annotation) {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isSameType( actualReturnType,
                                        requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            return e.getSimpleName().toString();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getBooleanMethodName(final TypeElement classElement,
                                        final ProcessingEnvironment processingEnvironment,
                                        final Class annotation) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "java.lang.Boolean" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            return e.getSimpleName().toString();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getStringMethodName(final TypeElement classElement,
                                       final ProcessingEnvironment processingEnvironment,
                                       final Class annotation) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "java.lang.String" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            return e.getSimpleName().toString();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getIsWidgetMethodName(final TypeElement classElement,
                                         final ProcessingEnvironment processingEnvironment,
                                         final Class annotation) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            return e.getSimpleName().toString();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getDefaultPositionMethodName(final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final Class annotation) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.drools.guvnor.client.workbench.Position" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            return e.getSimpleName().toString();
        }
        return null;
    }

}
