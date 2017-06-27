/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.GeneratorUtils;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;

/**
 * A source code generator for {@link WorkbenchPreference}.
 */
public class WorkbenchPreferenceGeneratedImplGenerator extends AbstractGenerator {

    private GeneratorContext generatorContext;
    private String targetClassName = null;

    public WorkbenchPreferenceGeneratedImplGenerator(final GeneratorContext generatorContext) {
        this.generatorContext = generatorContext;
    }

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

        final TypeElement classElement = (TypeElement) element;

        final WorkbenchPreference annotation = element.getAnnotation(WorkbenchPreference.class);

        String sourcePackage = packageName;
        String sourceClassName = className;
        String targetPackage = packageName;

        String identifier = annotation.identifier();
        String[] parents = annotation.parents();
        String bundleKey = annotation.bundleKey();

        if (GeneratorContext.BEAN.equals(generatorContext)) {
            targetClassName = className + "BeanGeneratedImpl";
        } else if (GeneratorContext.PORTABLE.equals(generatorContext)) {
            targetClassName = className + "PortableGeneratedImpl";
        }

        List<PropertyData> properties = new ArrayList<>();

        TypeElement c = classElement;
        final TypeElement propertyTypeElement = elementUtils.getTypeElement(Property.class.getName());

        for (Element el : c.getEnclosedElements()) {
            final Property propertyAnnotation = el.getAnnotation(Property.class);
            for (AnnotationMirror am : el.getAnnotationMirrors()) {
                if (am.getAnnotationType().equals(propertyTypeElement.asType())) {
                    properties.add(new PropertyData(el,
                                                    propertyAnnotation,
                                                    am,
                                                    elementUtils));
                }
            }
        }

        final List<PropertyData> simpleProperties = properties.stream()
                .filter(p -> !p.isSubPreference())
                .collect(Collectors.toList());

        final List<PropertyData> subPreferences = properties.stream()
                .filter(p -> p.isSubPreference())
                .collect(Collectors.toList());

        final List<PropertyData> nonSharedSubPreferences = subPreferences.stream()
                .filter(p -> !p.isShared())
                .collect(Collectors.toList());

        final List<PropertyData> sharedSubPreferences = subPreferences.stream()
                .filter(p -> p.isShared())
                .collect(Collectors.toList());

        final List<String> constructorParams = properties.stream()
                .map(p -> "@MapsTo(\"" + p.getFieldName() + "\") " + p.getTypeFullName() + " " + p.getFieldName())
                .collect(Collectors.toList());
        final String constructorParamsText = String.join(", ",
                                                         constructorParams);

        final List<String> propertyFields = properties.stream()
                .map(PropertyData::getFieldName)
                .collect(Collectors.toList());
        final String propertyFieldsText = String.join(", ",
                                                      propertyFields);

        final String parentsIdentifiers = String.join(", ",
                                                      parents);

        final String isPersistable = Boolean.toString(!simpleProperties.isEmpty() || !nonSharedSubPreferences.isEmpty());

        if (GeneratorUtils.debugLoggingEnabled()) {
            final List<String> simplePropertiesNames = simpleProperties.stream()
                    .map(PropertyData::getFieldName)
                    .collect(Collectors.toList());
            final String simplePropertiesText = String.join(", ",
                                                            simplePropertiesNames);

            final List<String> subPreferencesNames = subPreferences.stream()
                    .map(PropertyData::getFieldName)
                    .collect(Collectors.toList());
            final String subPreferencesText = String.join(", ",
                                                          subPreferencesNames);

            final List<String> sharedSubPreferencesNames = sharedSubPreferences.stream()
                    .map(PropertyData::getFieldName)
                    .collect(Collectors.toList());
            final String sharedSubPreferencesText = String.join(", ",
                                                                sharedSubPreferencesNames);

            final List<String> nonSharedSubPreferencesNames = nonSharedSubPreferences.stream()
                    .map(PropertyData::getFieldName)
                    .collect(Collectors.toList());
            final String nonSharedSubPreferencesText = String.join(", ",
                                                                   nonSharedSubPreferencesNames);

            messager.printMessage(Kind.NOTE,
                                  "Source package name: " + sourcePackage);
            messager.printMessage(Kind.NOTE,
                                  "Source class name: " + sourceClassName);
            messager.printMessage(Kind.NOTE,
                                  "Target package name: " + targetPackage);
            messager.printMessage(Kind.NOTE,
                                  "Target class name: " + targetClassName);
            messager.printMessage(Kind.NOTE,
                                  "Identifier: " + identifier);
            messager.printMessage(Kind.NOTE,
                                  "Parents: " + parentsIdentifiers);
            messager.printMessage(Kind.NOTE,
                                  "Property fields: " + propertyFieldsText);
            messager.printMessage(Kind.NOTE,
                                  "Simple properties fields: " + simplePropertiesText);
            messager.printMessage(Kind.NOTE,
                                  "Sub-preferences fields: " + subPreferencesText);
            messager.printMessage(Kind.NOTE,
                                  "Shared subPreferences fields: " + sharedSubPreferencesText);
            messager.printMessage(Kind.NOTE,
                                  "Non-shared subPreferences fields: " + nonSharedSubPreferencesText);
            messager.printMessage(Kind.NOTE,
                                  "Constructor parameters: " + constructorParamsText);
            messager.printMessage(Kind.NOTE,
                                  "Is persistable: " + isPersistable);
        }

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("sourcePackage",
                 sourcePackage);
        root.put("sourceClassName",
                 sourceClassName);
        root.put("targetPackage",
                 targetPackage);
        root.put("targetClassName",
                 targetClassName);
        root.put("identifier",
                 identifier);
        root.put("parentsIdentifiers",
                 parentsIdentifiers);
        root.put("bundleKey",
                 bundleKey);
        root.put("properties",
                 properties);
        root.put("simpleProperties",
                 simpleProperties);
        root.put("subPreferences",
                 subPreferences);
        root.put("sharedSubPreferences",
                 sharedSubPreferences);
        root.put("nonSharedSubPreferences",
                 nonSharedSubPreferences);
        root.put("constructorParamsText",
                 constructorParamsText);
        root.put("propertyFieldsText",
                 propertyFieldsText);
        root.put("isPersistable",
                 isPersistable);

        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);
        try {
            Template template = null;
            if (GeneratorContext.BEAN.equals(generatorContext)) {
                template = config.getTemplate("workbenchPreferenceBean.ftl");
            } else if (GeneratorContext.PORTABLE.equals(generatorContext)) {
                template = config.getTemplate("workbenchPreferencePortable.ftl");
            }

            if (template != null) {
                template.process(root,
                                 bw);
            }
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

    public String getTargetClassName() {
        return targetClassName;
    }
}