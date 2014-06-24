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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

/**
 * A source code generator for Activities
 */
public class PerspectiveActivityGenerator extends AbstractGenerator {

    private static final Logger logger = LoggerFactory.getLogger( PerspectiveActivityGenerator.class );

    private Map<String, String> context = new HashMap<String, String>();

    public StringBuffer generate( final String packageName,
                                  final PackageElement packageElement,
                                  final String className,
                                  final Element element,
                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {

        logger.debug( "Starting code generation for [" + className + "]" );

        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        String identifier = ClientAPIModule.getWbPerspectiveScreenIdentifierValueOnClass( classElement );
        boolean isDefault = ClientAPIModule.getWbPerspectiveScreenIsDefaultValueOnClass( classElement );

        boolean isTemplate = ClientAPIModule.getWbPerspectiveScreenIsATemplate( classElement );

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
        final String securityTraitList = GeneratorUtils.getSecurityTraitList( classElement );
        final String rolesList = GeneratorUtils.getRoleList( classElement );

        logger.debug( "Package name: " + packageName );
        logger.debug( "Class name: " + className );
        logger.debug( "Identifier: " + identifier );
        logger.debug( "isDefault: " + isDefault );
        logger.debug( "isTemplate: " + isTemplate );
        logger.debug( "onStartup0ParameterMethodName: " + onStartup0ParameterMethodName );
        logger.debug( "onStartup1ParameterMethodName: " + onStartup1ParameterMethodName );
        logger.debug( "onCloseMethodName: " + onCloseMethodName );
        logger.debug( "onShutdownMethodName: " + onShutdownMethodName );
        logger.debug( "onOpenMethodName: " + onOpenMethodName );
        logger.debug( "getPerspectiveMethodName: " + getPerspectiveMethodName );
        logger.debug( "getMenuBarMethodName: " + getMenuBarMethodName );
        logger.debug( "getToolBarMethodName: " + getToolBarMethodName );
        logger.debug( "securityTraitList: " + securityTraitList );
        logger.debug( "rolesList: " + rolesList );

        //Validate onStartup0ParameterMethodName and onStartup1ParameterMethodName
        if ( onStartup0ParameterMethodName != null && onStartup1ParameterMethodName != null ) {
            final String msg = "Found multiple @OnStartup methods. Each class can declare at most one.";
            throw new GenerationException( msg );
        }

        //Validate getPerspectiveMethodName
        if ( getPerspectiveMethodName == null && !isTemplate ) {
            throw new GenerationException( "The WorkbenchPerspective must provide a @Perspective annotated method to return a org.uberfire.client.workbench.model.PerspectiveDefinition.", packageName + "." + className );
        }

        if ( isDefault ) {
            warningIfMoreThanOneDefaultPerspective( processingEnvironment, identifier );
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
            setupTemplateElements( root, classElement );
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
        logger.debug( "Successfully generated code for [" + className + "]" );

        return sw.getBuffer();
    }

    private void warningIfMoreThanOneDefaultPerspective( ProcessingEnvironment processingEnvironment,
                                                         String perspectiveName ) {
        String defaultPerspectivesName = "defaultPerspectivesName";
        String defaultPerspectives = context.get( defaultPerspectivesName );
        if ( defaultPerspectives != null ) {
            defaultPerspectives = defaultPerspectives + ", " + perspectiveName;
            generateMoreThanOneDefaultPerspectiveWarning( processingEnvironment, defaultPerspectives );
            context.put( defaultPerspectivesName, defaultPerspectives );
        } else {
            context.put( defaultPerspectivesName, perspectiveName );
        }
    }

    private void generateMoreThanOneDefaultPerspectiveWarning( ProcessingEnvironment processingEnvironment,
                                                               String defaultPerspectives ) {
        final String msg = "There is more than one default WorkbenchPerspective ("+defaultPerspectives+") - @WorkbenchPerspective(isDefault = true). One of them will take precedence.";
        processingEnvironment.getMessager().printMessage( Kind.WARNING,
                                                          msg );
        logger.warn( msg );
    }

    private static void setupTemplateElements( Map<String, Object> root,
                                               TypeElement classElement ) throws GenerationException {

        TemplateInformation helper = TemplateInformationHelper.extractWbTemplatePerspectiveInformation( classElement );

        if ( helper.getDefaultPanel() != null ) {
            root.put( "defaultPanel", helper.getDefaultPanel() );
        }
        root.put( "wbPanels", helper.getTemplateFields() );

    }

}
