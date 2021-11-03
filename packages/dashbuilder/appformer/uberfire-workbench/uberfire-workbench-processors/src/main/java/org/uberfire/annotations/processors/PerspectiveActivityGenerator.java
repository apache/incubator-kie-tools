/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.annotations.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

import static org.uberfire.annotations.processors.TemplateInformationHelper.extractWbTemplatePerspectiveInformation;

/**
 * Generates a Java source file enerator for Activities
 */
public class PerspectiveActivityGenerator extends AbstractGenerator {

    private final Map<String, String> context = new HashMap<String, String>();

    @Override
    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final Element element,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {

        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage(Kind.NOTE,
                              "Starting code generation for [" + className + "]");

        final Elements elementUtils = processingEnvironment.getElementUtils();

        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        String identifier = ClientAPIModule.getWbPerspectiveScreenIdentifierValueOnClass(classElement);
        boolean isDefault = ClientAPIModule.getWbPerspectiveScreenIsDefaultValueOnClass(classElement);
        boolean isTransient = ClientAPIModule.getWbPerspectiveScreenIsTransientValueOnClass(classElement);
        boolean isDynamic = ClientAPIModule.getWbPerspectiveScreenIsDynamicValueOnClass(classElement);

        final String beanActivatorClass = GeneratorUtils.getBeanActivatorClassName(classElement,
                                                                                   processingEnvironment);

        final ExecutableElement onStartupMethod = GeneratorUtils.getOnStartupMethodForNonEditors(classElement,
                                                                                                 processingEnvironment);

        final String onStartup0ParameterMethodName;
        final String onStartup1ParameterMethodName;
        if (onStartupMethod == null) {
            onStartup0ParameterMethodName = null;
            onStartup1ParameterMethodName = null;
        } else if (onStartupMethod.getParameters().isEmpty()) {
            onStartup0ParameterMethodName = onStartupMethod.getSimpleName().toString();
            onStartup1ParameterMethodName = null;
        } else {
            onStartup0ParameterMethodName = null;
            onStartup1ParameterMethodName = onStartupMethod.getSimpleName().toString();
        }

        final String onCloseMethodName = GeneratorUtils.getOnCloseMethodName(classElement,
                                                                             processingEnvironment);
        final String onShutdownMethodName = GeneratorUtils.getOnShutdownMethodName(classElement,
                                                                                   processingEnvironment);
        final String onOpenMethodName = GeneratorUtils.getOnOpenMethodName(classElement,
                                                                           processingEnvironment);
        final String getPerspectiveMethodName = GeneratorUtils.getPerspectiveMethodName(classElement,
                                                                                        processingEnvironment);
        final String getMenuBarMethodName = GeneratorUtils.getMenuBarMethodName(classElement,
                                                                                processingEnvironment);
        final String getToolBarMethodName = GeneratorUtils.getToolBarMethodName(classElement,
                                                                                processingEnvironment);
        final List<String> qualifiers = GeneratorUtils.getAllQualifiersDeclarationFromType(classElement);

        if (GeneratorUtils.debugLoggingEnabled()) {
            messager.printMessage(Kind.NOTE,
                                  "Package name: " + packageName);
            messager.printMessage(Kind.NOTE,
                                  "Class name: " + className);
            messager.printMessage(Kind.NOTE,
                                  "Identifier: " + identifier);
            messager.printMessage(Kind.NOTE,
                                  "isDefault: " + isDefault);
            messager.printMessage(Kind.NOTE,
                                  "isTransient: " + isTransient);
            messager.printMessage(Kind.NOTE,
                                  "onStartup0ParameterMethodName: " + onStartup0ParameterMethodName);
            messager.printMessage(Kind.NOTE,
                                  "onStartup1ParameterMethodName: " + onStartup1ParameterMethodName);
            messager.printMessage(Kind.NOTE,
                                  "onCloseMethodName: " + onCloseMethodName);
            messager.printMessage(Kind.NOTE,
                                  "onShutdownMethodName: " + onShutdownMethodName);
            messager.printMessage(Kind.NOTE,
                                  "onOpenMethodName: " + onOpenMethodName);
            messager.printMessage(Kind.NOTE,
                                  "getPerspectiveMethodName: " + getPerspectiveMethodName);
            messager.printMessage(Kind.NOTE,
                                  "getMenuBarMethodName: " + getMenuBarMethodName);
            messager.printMessage(Kind.NOTE,
                                  "getToolBarMethodName: " + getToolBarMethodName);
            messager.printMessage(Kind.NOTE,
                                  "Qualifiers: " + String.join(", ",
                                                               qualifiers));
        }

        Map<String, Object> root = new HashMap<String, Object>();

        TemplateInformation helper = extractWbTemplatePerspectiveInformation(elementUtils,
                                                                             classElement);

        if (helper.getDefaultPanel() != null) {
            root.put("defaultPanel",
                     helper.getDefaultPanel());
        }
        root.put("wbPanels",
                 helper.getTemplateFields());

        if (getPerspectiveMethodName == null && !helper.thereIsTemplateFields()) {
            throw new GenerationException("A WorkbenchPerspective class must have either a valid @Perspective method or at least one @WorkbenchPanel field.",
                                          packageName + "." + className);
        }

        if (getPerspectiveMethodName != null && helper.thereIsTemplateFields()) {
            throw new GenerationException("This WorkbenchPerspective has both a @Perspective method and a @WorkbenchPanel field. Only one or the other is allowed.");
        }

        if (isDefault) {
            warningIfMoreThanOneDefaultPerspective(processingEnvironment,
                                                   identifier);
        }

        //Setup data for FreeMarker
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("identifier",
                 identifier);
        root.put("isTemplate",
                 helper.thereIsTemplateFields());
        root.put("isDefault",
                 isDefault);
        root.put("isTransient",
                 isTransient);
        root.put("realClassName",
                 classElement.getSimpleName().toString());
        root.put("beanActivatorClass",
                 beanActivatorClass);
        root.put("onStartup0ParameterMethodName",
                 onStartup0ParameterMethodName);
        root.put("onStartup1ParameterMethodName",
                 onStartup1ParameterMethodName);
        root.put("onCloseMethodName",
                 onCloseMethodName);
        root.put("onShutdownMethodName",
                 onShutdownMethodName);
        root.put("onOpenMethodName",
                 onOpenMethodName);
        root.put("getPerspectiveMethodName",
                 getPerspectiveMethodName);
        root.put("getMenuBarMethodName",
                 getMenuBarMethodName);
        root.put("getToolBarMethodName",
                 getToolBarMethodName);
        root.put("isDynamic",
                 isDynamic);
        root.put("qualifiers",
                 qualifiers);

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);
        try {
            final Template template = config.getTemplate("perspective.ftl");
            template.process(root,
                             bw);
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        } catch (TemplateException te) {
            throw new GenerationException(te);
        } finally {
            try {
                bw.close();
                sw.close();
            } catch (IOException ioe) {
                throw new GenerationException(ioe);
            }
        }
        messager.printMessage(Kind.NOTE,
                              "Successfully generated code for [" + className + "]");

        return sw.getBuffer();
    }

    private void warningIfMoreThanOneDefaultPerspective(ProcessingEnvironment processingEnvironment,
                                                        String perspectiveName) {
        String defaultPerspectivesName = "defaultPerspectivesName";
        String defaultPerspectives = context.get(defaultPerspectivesName);
        if (defaultPerspectives != null) {
            defaultPerspectives = defaultPerspectives + ", " + perspectiveName;
            generateMoreThanOneDefaultPerspectiveWarning(processingEnvironment,
                                                         defaultPerspectives);
            context.put(defaultPerspectivesName,
                        defaultPerspectives);
        } else {
            context.put(defaultPerspectivesName,
                        perspectiveName);
        }
    }

    private void generateMoreThanOneDefaultPerspectiveWarning(ProcessingEnvironment processingEnvironment,
                                                              String defaultPerspectives) {
        final String msg = "Found too many default WorkbenchPerspectives (expected 1). Found: (" + defaultPerspectives + ").";
        processingEnvironment.getMessager().printMessage(Kind.ERROR,
                                                         msg);
    }
}
