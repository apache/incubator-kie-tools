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

import static org.uberfire.annotations.processors.TemplateInformationHelper.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generates a Java source file enerator for Activities
 */
public class PerspectiveActivityGenerator extends AbstractGenerator {

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
        String identifier = ClientAPIModule.getWbPerspectiveScreenIdentifierValueOnClass( classElement );
        boolean isDefault = ClientAPIModule.getWbPerspectiveScreenIsDefaultValueOnClass( classElement );
        boolean isTemplate = ClientAPIModule.getWbPerspectiveScreenIsATemplate( elementUtils, classElement );

        final String onStartup0ParameterMethodName = GeneratorUtils.getOnStartupZeroParameterMethodName( classElement,
                                                                                                         processingEnvironment );
        final String onStartup1ParameterMethodName = GeneratorUtils.getOnStartPlaceRequestParameterMethodName( classElement,
                                                                                                               processingEnvironment );
        final String onCloseMethodName = GeneratorUtils.getOnCloseMethodName( classElement,
                                                                              processingEnvironment );
        final String onShutdownMethodName = GeneratorUtils.getOnShutdownMethodName( classElement,
                                                                                    processingEnvironment );
        final String onOpenMethodName = GeneratorUtils.getOnOpenMethodName( classElement,
                                                                            processingEnvironment );
        final String getPerspectiveMethodName = GeneratorUtils.getPerspectiveMethodName( classElement,
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
            messager.printMessage( Kind.NOTE, "isDefault: " + isDefault );
            messager.printMessage( Kind.NOTE, "isTemplate: " + isTemplate );
            messager.printMessage( Kind.NOTE, "onStartup0ParameterMethodName: " + onStartup0ParameterMethodName );
            messager.printMessage( Kind.NOTE, "onStartup1ParameterMethodName: " + onStartup1ParameterMethodName );
            messager.printMessage( Kind.NOTE, "onCloseMethodName: " + onCloseMethodName );
            messager.printMessage( Kind.NOTE, "onShutdownMethodName: " + onShutdownMethodName );
            messager.printMessage( Kind.NOTE, "onOpenMethodName: " + onOpenMethodName );
            messager.printMessage( Kind.NOTE, "getPerspectiveMethodName: " + getPerspectiveMethodName );
            messager.printMessage( Kind.NOTE, "getMenuBarMethodName: " + getMenuBarMethodName );
            messager.printMessage( Kind.NOTE, "getToolBarMethodName: " + getToolBarMethodName );
            messager.printMessage( Kind.NOTE, "securityTraitList: " + securityTraitList );
            messager.printMessage( Kind.NOTE, "rolesList: " + rolesList );
        }

        //Validate onStartup0ParameterMethodName and onStartup1ParameterMethodName
        if ( onStartup0ParameterMethodName != null && onStartup1ParameterMethodName != null ) {
            final String msg = "The WorkbenchPerspective has methods for both @OnStartup() and @OnStartup(Place). Method @OnStartup(Place) will take precedence.";
            messager.printMessage( Kind.WARNING, msg, classElement );
        }

        //Validate getPerspectiveMethodName
        if ( getPerspectiveMethodName == null && !isTemplate ) {
            throw new GenerationException( "The WorkbenchPerspective must provide a @Perspective annotated method to return a org.uberfire.client.workbench.model.PerspectiveDefinition.", packageName + "." + className );
        }

        //Setup data for template sub-system
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "packageName",
                  packageName );
        root.put( "className",
                  className );
        root.put( "identifier",
                  identifier );
        root.put( "isTemplate", isTemplate );
        root.put( "isDefault",
                  isDefault );
        root.put( "realClassName",
                  classElement.getSimpleName().toString() );
        root.put( "onStartup0ParameterMethodName",
                  onStartup0ParameterMethodName );
        root.put( "onStartup1ParameterMethodName",
                  onStartup1ParameterMethodName );
        root.put( "onCloseMethodName",
                  onCloseMethodName );
        root.put( "onShutdownMethodName",
                  onShutdownMethodName );
        root.put( "onOpenMethodName",
                  onOpenMethodName );
        root.put( "getPerspectiveMethodName",
                  getPerspectiveMethodName );
        root.put( "getMenuBarMethodName",
                  getMenuBarMethodName );
        root.put( "getToolBarMethodName",
                  getToolBarMethodName );
        root.put( "securityTraitList",
                  securityTraitList );
        root.put( "rolesList",
                  rolesList );

        if ( isTemplate ) {
            setupTemplateElements( elementUtils, root, classElement );
        }

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            final Template template = config.getTemplate( "perspective.ftl" );
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

    private static void setupTemplateElements( Elements elementUtils,
                                               Map<String, Object> root,
                                               TypeElement classElement ) throws GenerationException {

        TemplateInformation helper = extractWbTemplatePerspectiveInformation( elementUtils, classElement );

        if ( helper.getDefaultPanel() != null ) {
            root.put( "defaultPanel", helper.getDefaultPanel() );
        }
        root.put( "wbPanels", helper.getTemplateFields() );

    }

}
