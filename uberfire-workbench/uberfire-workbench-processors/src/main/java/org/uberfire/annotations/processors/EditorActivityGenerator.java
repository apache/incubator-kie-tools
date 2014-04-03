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
package org.uberfire.annotations.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.enterprise.context.ApplicationScoped;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A source code generator for Activities
 */
public class EditorActivityGenerator extends AbstractGenerator {

    @Override
    public StringBuffer generate( final String packageName,
                                  final PackageElement packageElement,
                                  final String className,
                                  final Element element,
                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {

        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage( Kind.NOTE, "Starting code generation for [" + className + "]" );

        final Elements elementUtils = processingEnvironment.getElementUtils();

        //Extract required information
        final TypeElement classElement = (TypeElement) element;

        final String annotationName = ClientAPIModule.getWorkbenchEditorClass();

        String identifier = null;
        Integer priority = 0;
        Collection<String> associatedResources = null;

        for ( final AnnotationMirror am : classElement.getAnnotationMirrors() ) {
            if ( annotationName.equals( am.getAnnotationType().toString() ) ) {
                for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet() ) {
                    if ( "identifier".equals( entry.getKey().getSimpleName().toString() ) ) {
                        identifier = entry.getValue().toString();
                    } else if ( "priority".equals( entry.getKey().getSimpleName().toString() ) ) {
                        priority = (Integer) entry.getValue().getValue();
                    } else if ( "supportedTypes".equals( entry.getKey().getSimpleName().toString() ) ) {
                        associatedResources = GeneratorUtils.extractValue( entry.getValue() );
                    }
                }
                break;
            }
        }

        if ( associatedResources != null && associatedResources.size() > 0 ) {
            for ( final String resourceTypeName : associatedResources ) {
                final TypeElement resourceType = processingEnvironment.getElementUtils().getTypeElement( resourceTypeName );
                if ( resourceType.getAnnotation( ApplicationScoped.class ) == null ) {
                    messager.printMessage(
                            Kind.ERROR,
                            "This type is referenced as a resource from " + className + ", so it must be @ApplicationScoped.",
                            resourceType );
                }
            }
        }

        final String onStartup1ParameterMethodName = GeneratorUtils.getOnStartupPathParameterMethodName( classElement,
                                                                                                         processingEnvironment );
        final String onStartup2ParametersMethodName = GeneratorUtils.getOnStartupPathPlaceRequestParametersMethodName( classElement,
                                                                                                                       processingEnvironment );
        final String onMayCloseMethodName = GeneratorUtils.getOnMayCloseMethodName( classElement,
                                                                                    processingEnvironment );
        final String onCloseMethodName = GeneratorUtils.getOnCloseMethodName( classElement,
                                                                              processingEnvironment );
        final String onShutdownMethodName = GeneratorUtils.getOnShutdownMethodName( classElement,
                                                                                    processingEnvironment );
        final String onOpenMethodName = GeneratorUtils.getOnOpenMethodName( classElement,
                                                                            processingEnvironment );
        final String onLostFocusMethodName = GeneratorUtils.getOnLostFocusMethodName( classElement,
                                                                                      processingEnvironment );
        final String onFocusMethodName = GeneratorUtils.getOnFocusMethodName( classElement,
                                                                              processingEnvironment );
        final String getDefaultPositionMethodName = GeneratorUtils.getDefaultPositionMethodName( classElement,
                                                                                                 processingEnvironment );
        final String getTitleMethodName = GeneratorUtils.getTitleMethodName( classElement,
                                                                             processingEnvironment );
        final String getContextIdMethodName = GeneratorUtils.getContextIdMethodName( classElement,
                                                                                     processingEnvironment );
        final ExecutableElement getTitleWidgetMethod = GeneratorUtils.getTitleWidgetMethodName( classElement,
                                                                                                processingEnvironment );
        final String getTitleWidgetMethodName = getTitleWidgetMethod == null ? null : getTitleWidgetMethod.getSimpleName().toString();
        final ExecutableElement getWidgetMethod = GeneratorUtils.getWidgetMethodName( classElement,
                                                                                      processingEnvironment );
        final String getWidgetMethodName = getWidgetMethod == null ? null : getWidgetMethod.getSimpleName().toString();
        final boolean hasUberView = GeneratorUtils.hasUberViewReference( classElement,
                                                                         processingEnvironment,
                                                                         getWidgetMethod );

        final boolean isWidget = GeneratorUtils.getIsWidget( classElement,
                                                             processingEnvironment );
        final String isDirtyMethodName = GeneratorUtils.getIsDirtyMethodName( classElement,
                                                                              processingEnvironment );
        final String onSaveMethodName = GeneratorUtils.getOnSaveMethodName( classElement,
                                                                            processingEnvironment );
        final String getMenuBarMethodName = GeneratorUtils.getMenuBarMethodName( classElement,
                                                                                 processingEnvironment );
        final String getToolBarMethodName = GeneratorUtils.getToolBarMethodName( classElement,
                                                                                 processingEnvironment );
        final String securityTraitList = GeneratorUtils.getSecurityTraitList( elementUtils, classElement );
        final String rolesList = GeneratorUtils.getRoleList( elementUtils, classElement );

        if ( GeneratorUtils.debugLoggingEnabled() ) {
            messager.printMessage( Kind.NOTE, "Package name: " + packageName );
            messager.printMessage( Kind.NOTE, "Class name: " + className );
            messager.printMessage( Kind.NOTE, "Identifier: " + identifier );
            messager.printMessage( Kind.NOTE, "getContextIdMethodName: " + getContextIdMethodName );
            messager.printMessage( Kind.NOTE, "Priority: " + priority );
            messager.printMessage( Kind.NOTE, "Resource types: " + associatedResources );
            messager.printMessage( Kind.NOTE, "onStartup1ParameterMethodName: " + onStartup1ParameterMethodName );
            messager.printMessage( Kind.NOTE, "onStartup2ParametersMethodName: " + onStartup2ParametersMethodName );
            messager.printMessage( Kind.NOTE, "onMayCloseMethodName: " + onMayCloseMethodName );
            messager.printMessage( Kind.NOTE, "onCloseMethodName: " + onCloseMethodName );
            messager.printMessage( Kind.NOTE, "onShutdownMethodName: " + onShutdownMethodName );
            messager.printMessage( Kind.NOTE, "onOpenMethodName: " + onOpenMethodName );
            messager.printMessage( Kind.NOTE, "onLostFocusMethodName: " + onLostFocusMethodName );
            messager.printMessage( Kind.NOTE, "onFocusMethodName: " + onFocusMethodName );
            messager.printMessage( Kind.NOTE, "getDefaultPositionMethodName: " + getDefaultPositionMethodName );
            messager.printMessage( Kind.NOTE, "getTitleMethodName: " + getTitleMethodName );
            messager.printMessage( Kind.NOTE, "getTitleWidgetMethodName: " + getTitleWidgetMethodName );
            messager.printMessage( Kind.NOTE, "getWidgetMethodName: " + getWidgetMethodName );
            messager.printMessage( Kind.NOTE, "isWidget: " + Boolean.toString( isWidget ) );
            messager.printMessage( Kind.NOTE, "hasUberView: " + Boolean.toString( hasUberView ) );
            messager.printMessage( Kind.NOTE, "isDirtyMethodName: " + isDirtyMethodName );
            messager.printMessage( Kind.NOTE, "onSaveMethodName: " + onSaveMethodName );
            messager.printMessage( Kind.NOTE, "getMenuBarMethodName: " + getMenuBarMethodName );
            messager.printMessage( Kind.NOTE, "getToolBarMethodName: " + getToolBarMethodName );
            messager.printMessage( Kind.NOTE, "securityTraitList: " + securityTraitList );
            messager.printMessage( Kind.NOTE, "rolesList: " + rolesList );
        }

        //Validate getWidgetMethodName and isWidget
        if ( !isWidget && getWidgetMethodName == null ) {
            throw new GenerationException( "The WorkbenchEditor must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget.", packageName + "." + className );
        }
        if ( isWidget && getWidgetMethodName != null ) {
            final String msg = "The WorkbenchEditor both extends com.google.gwt.user.client.ui.IsWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence.";
            messager.printMessage( Kind.WARNING, msg, classElement );
        }

        //Validate onStartup1ParameterMethodName and onStartup2ParametersMethodName
        if ( onStartup1ParameterMethodName != null && onStartup2ParametersMethodName != null ) {
            final String msg = "The WorkbenchEditor has methods for both @OnStartup(Path) and @OnStartup(Path, Place). Method @OnStartup(Path, Place) will take precedence.";
            messager.printMessage( Kind.WARNING, msg, classElement );
        }

        //Validate getTitleMethodName
        if ( getTitleMethodName == null ) {
            throw new GenerationException( "The WorkbenchEditor must provide a @WorkbenchPartTitle annotated method to return a java.lang.String.", packageName + "." + className );
        }

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "identifier",
                  identifier );
        root.put( "getContextIdMethodName",
                  getContextIdMethodName );
        root.put( "priority",
                  priority.toString().replace( ",", "" ) );
        root.put( "associatedResources",
                  GeneratorUtils.formatAssociatedResources( associatedResources ) );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onStartup1ParameterMethodName",
                  onStartup1ParameterMethodName );
        root.put( "onStartup2ParametersMethodName",
                  onStartup2ParametersMethodName );
        root.put( "onMayCloseMethodName",
                  onMayCloseMethodName );
        root.put( "onCloseMethodName",
                  onCloseMethodName );
        root.put( "onShutdownMethodName",
                  onShutdownMethodName );
        root.put( "onOpenMethodName",
                  onOpenMethodName );
        root.put( "onLostFocusMethodName",
                  onLostFocusMethodName );
        root.put( "onFocusMethodName",
                  onFocusMethodName );
        root.put( "getDefaultPositionMethodName",
                  getDefaultPositionMethodName );
        root.put( "getTitleMethodName",
                  getTitleMethodName );
        root.put( "getTitleWidgetMethodName",
                  getTitleWidgetMethodName );
        root.put( "getWidgetMethodName",
                  getWidgetMethodName );
        root.put( "isWidget",
                  isWidget );
        root.put( "hasUberView",
                  hasUberView );
        root.put( "isDirtyMethodName",
                  isDirtyMethodName );
        root.put( "onSaveMethodName",
                  onSaveMethodName );
        root.put( "getMenuBarMethodName",
                  getMenuBarMethodName );
        root.put( "getToolBarMethodName",
                  getToolBarMethodName );
        root.put( "securityTraitList",
                  securityTraitList );
        root.put( "rolesList",
                  rolesList );

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "activityEditor.ftl" );
            template.process( root,
                              bw );
        } catch ( IOException ioe ) {
            throw new GenerationException( ioe );
        } catch ( TemplateException te ) {
            throw new GenerationException( te );
        } finally {
            try {
                bw.close();
                sw.close();
            } catch ( IOException ioe ) {
                throw new GenerationException( ioe );
            }
        }
        messager.printMessage( Kind.NOTE, "Successfully generated code for [" + className + "]" );

        return sw.getBuffer();
    }

}
