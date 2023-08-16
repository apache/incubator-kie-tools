/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.adf.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.GenerationCompleteCallback;

@SupportedAnnotationTypes(FieldRendererProcessor.FIELD_RENDERER_ANNOTATION)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FieldRendererProcessor extends AbstractErrorAbsorbingProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(FieldRendererProcessor.class.getName());

    static final String FIELD_RENDERER_ANNOTATION = "org.kie.workbench.common.forms.adf.rendering.Renderer";

    private String packageName;
    private List<RendererMeta> fieldTypeRenderers = new ArrayList<>();
    private List<RendererMeta> fieldDefinitionRenderers = new ArrayList<>();

    private GenerationCompleteCallback callback = null;

    public FieldRendererProcessor() {

    }

    // Constructor for tests only, to prevent code being written to file. The generated code will be sent to the callback
    FieldRendererProcessor(GenerationCompleteCallback callback) {
        this();
        this.callback = callback;
        LOGGER.info("GenerationCompleteCallback has been provided. Generated source code will not be compiled and hence classes will not be available.");
    }

    @Override
    protected boolean processWithExceptions(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) throws Exception {

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Searching FieldRenderers on project");

        if (roundEnvironment.processingOver()) {
            return false;
        }

        //If prior processing threw an error exit
        if (roundEnvironment.errorRaised()) {
            return false;
        }

        packageName = null;
        fieldTypeRenderers.clear();
        fieldDefinitionRenderers.clear();

        TypeElement annotation = processingEnv.getElementUtils().getTypeElement(FIELD_RENDERER_ANNOTATION);

        Set<? extends Element> renderers = roundEnvironment.getElementsAnnotatedWith(annotation);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, renderers.size() + " FieldRenderers found on project");

        renderers.forEach(element -> process((TypeElement) element));

        if (!fieldTypeRenderers.isEmpty() || !fieldDefinitionRenderers.isEmpty()) {
            fieldTypeRenderers.sort(Comparator.comparing(RendererMeta::getKey));
            fieldDefinitionRenderers.sort(Comparator.comparing(RendererMeta::getKey));

            Map<String, Object> templateContext = new HashMap<>();

            templateContext.put("package", packageName);
            templateContext.put("generatedByClassName", this.getClass().getName());
            templateContext.put("fieldTypeRenderers", fieldTypeRenderers);
            templateContext.put("fieldDefinitionRenderers", fieldDefinitionRenderers);

            StringBuffer code = TemplateWriter.writeTemplate("FieldRendererTypesProvider.ftl", templateContext);

            // If code is successfully created write files, or send generated code to callback.
            // The callback function is used primarily for testing when we don't necessarily want
            // the generated code to be stored as a compilable file for javac to process.
            if (callback == null) {
                writeCode(packageName, "ModuleFieldRendererTypesProvider", code);
            } else {
                callback.generationComplete(code.toString());
            }

            LOGGER.info("Succesfully Generated sources for [{}] FieldType FieldRenderers and [{}}] FieldDefinition FieldRenderers.", fieldTypeRenderers.size(), fieldDefinitionRenderers.size());
        }

        return true;
    }

    private void process(TypeElement rendererElement) {
        Renderer annotation = rendererElement.getAnnotation(Renderer.class);

        if (annotation != null) {
            String rendererType = rendererElement.getQualifiedName().toString();

            if (packageName == null) {
                packageName = rendererType.substring(0, rendererType.lastIndexOf('.'));
            }

            String fieldDefinitionType = readAnnotationValue(() -> annotation.fieldDefinition().getName());

            if (!fieldDefinitionType.equals(FieldDefinition.class.getName())) {
                fieldDefinitionRenderers.add(new RendererMeta(fieldDefinitionType, rendererType));
            } else {
                fieldTypeRenderers.add(new RendererMeta(readAnnotationValue(() -> annotation.type().getName()), rendererType));
            }
        }
    }

    private String readAnnotationValue(Supplier<String> realValueSupplier) {
        try {
            return realValueSupplier.get();
        } catch (MirroredTypeException exception) {
            return exception.getTypeMirror().toString();
        }
    }

    public static class RendererMeta {

        private String key;
        private String className;

        RendererMeta(String key, String className) {
            this.key = key;
            this.className = className;
        }

        public String getKey() {
            return key;
        }

        public String getClassName() {
            return className;
        }
    }
}
