/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.annotations.processors;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

public class ExperimentalFeatureDefinitionProviderGenerator extends AbstractGenerator {

    @Override
    public StringBuffer generate(String packageName, PackageElement packageElement, String className, Element element, ProcessingEnvironment processingEnvironment) throws GenerationException {

        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage(Kind.NOTE, "Starting code generation for [" + className + "]");

        Map<String, Object> root = new HashMap<String, Object>();

        //Extract required information
        final TypeElement typeElement = (TypeElement) element;

        String featureId = typeElement.getQualifiedName().toString();
        String group = ClientAPIModule.getExperimentalFeatureGroup(typeElement);
        Boolean global = ClientAPIModule.isExperimentalFeatureGlobal(typeElement);
        String nameKey = getNameKey(typeElement);
        String descriptionKey = getDescriptionKey(typeElement);

        if (GeneratorUtils.debugLoggingEnabled()) {
            messager.printMessage(Kind.NOTE,
                                  "Package name: " + packageName);
            messager.printMessage(Kind.NOTE,
                                  "Class name: " + className);
            messager.printMessage(Kind.NOTE,
                                  "Feature Id: " + featureId);
            messager.printMessage(Kind.NOTE,
                                  "Scope: " + (global ? "global" : "user"));
            messager.printMessage(Kind.NOTE,
                                  "Group: " + className);
            messager.printMessage(Kind.NOTE,
                                  "Name key: " + nameKey);
            messager.printMessage(Kind.NOTE,
                                  "Description key: " + descriptionKey);
        }

        root.put("packageName", packageName);
        root.put("className", className);
        root.put("featureId", featureId);
        root.put("global", String.valueOf(global));
        root.put("group", group);
        root.put("nameKey", nameKey);
        root.put("descriptionKey", descriptionKey);

        //Generate code
        try (StringWriter sw = new StringWriter();
             BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate("experimentalFeatureDefinitionProvider.ftl");
            template.process(root, bw);
            messager.printMessage(Kind.NOTE,
                                  "Successfully generated code for [" + className + "]");

            return sw.getBuffer();
        } catch (Exception te) {
            throw new GenerationException(te);
        }
    }

    private String getNameKey(TypeElement typeElement) {
        String nameKey = ClientAPIModule.getExperimentalFeatureNameKey(typeElement);

        if (nameKey.isEmpty()) {
            nameKey = typeElement.getQualifiedName().toString();
        }

        return nameKey;
    }

    private String getDescriptionKey(TypeElement typeElement) {
        return ClientAPIModule.getExperimentalFeatureDescriptionKey(typeElement);
    }
}
