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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
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
public class ContextActivityGenerator extends AbstractGenerator {

    @Override
    public StringBuffer generate( final String packageName,
                                  final PackageElement packageElement,
                                  final String className,
                                  final Element element,
                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {

        Messager messager = processingEnvironment.getMessager();

        messager.printMessage( Kind.NOTE, "Starting code generation for [" + className + "]" );

        final Elements elementUtils = processingEnvironment.getElementUtils();

        //Extract required information
        final TypeElement classElement = (TypeElement) element;

        String identifier =  ClientAPIModule.getWbContextIdentifierValueOnClass( classElement );

        final String onOpenMethodName = GeneratorUtils.getOnOpenMethodName( classElement,
                                                                            processingEnvironment );
        final String getOnContextAttachPanelDefinitionMethodName = GeneratorUtils.getOnContextAttachPanelDefinitionMethodName( classElement,
                                                                                                                               processingEnvironment );
        final String getTitleMethodName = GeneratorUtils.getTitleMethodName( classElement,
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
        final String securityTraitList = GeneratorUtils.getSecurityTraitList( elementUtils, classElement );
        final String rolesList = GeneratorUtils.getRoleList( elementUtils, classElement );

        if ( GeneratorUtils.debugLoggingEnabled() ) {
            messager.printMessage( Kind.NOTE, "Package name: " + packageName );
            messager.printMessage( Kind.NOTE, "Class name: " + className );
            messager.printMessage( Kind.NOTE, "Identifier: " + identifier );
            messager.printMessage( Kind.NOTE, "onOpenMethodName: " + onOpenMethodName );
            messager.printMessage( Kind.NOTE, "getOnContextAttachPanelDefinitionMethodName: " + getOnContextAttachPanelDefinitionMethodName );
            messager.printMessage( Kind.NOTE, "getTitleMethodName: " + getTitleMethodName );
            messager.printMessage( Kind.NOTE, "getTitleWidgetMethodName: " + getTitleWidgetMethodName );
            messager.printMessage( Kind.NOTE, "getWidgetMethodName: " + getWidgetMethodName );
            messager.printMessage( Kind.NOTE, "isWidget: " + Boolean.toString( isWidget ) );
            messager.printMessage( Kind.NOTE, "hasUberView: " + Boolean.toString( hasUberView ) );
            messager.printMessage( Kind.NOTE, "securityTraitList: " + securityTraitList );
            messager.printMessage( Kind.NOTE, "rolesList: " + rolesList );
        }

        //Validate getWidgetMethodName and isWidget
        if ( !isWidget && getWidgetMethodName == null ) {
            throw new GenerationException( "The WorkbenchContext must either extend IsWidget or provide a @WorkbenchPartView annotated method to return a com.google.gwt.user.client.ui.IsWidget.", packageName + "." + className );
        }
        if ( isWidget && getWidgetMethodName != null ) {
            final String msg = "The WorkbenchContext both extends com.google.gwt.user.client.ui.IsWidget and provides a @WorkbenchPartView annotated method. The annotated method will take precedence.";
            processingEnvironment.getMessager().printMessage( Kind.WARNING,
                                                              msg );
        }

        //Validate getTitleMethodName and getTitleWidgetMethodName
        if ( getTitleMethodName == null ) {
            throw new GenerationException( "The WorkbenchContext must provide a @WorkbenchPartTitle annotated method to return a java.lang.String.", packageName + "." + className );
        }

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "identifier",
                  identifier );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onOpenMethodName",
                  onOpenMethodName );
        root.put( "getOnContextAttachPanelDefinitionMethodName",
                  getOnContextAttachPanelDefinitionMethodName );
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
        root.put( "securityTraitList",
                  securityTraitList );
        root.put( "rolesList",
                  rolesList );

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "activityContext.ftl" );
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
