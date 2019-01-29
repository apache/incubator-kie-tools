/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.adf.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.GenerationCompleteCallback;
import org.uberfire.annotations.processors.exceptions.GenerationException;

import static org.kie.workbench.common.forms.adf.processors.FormDefinitionsProcessor.FIELD_DEFINITION_ANNOTATION;
import static org.kie.workbench.common.forms.adf.processors.FormDefinitionsProcessor.FORM_DEFINITON_ANNOTATION;

@SupportedAnnotationTypes({
        FORM_DEFINITON_ANNOTATION,
        FIELD_DEFINITION_ANNOTATION
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FormDefinitionsProcessor extends AbstractErrorAbsorbingProcessor {

    public static final String FORM_DEFINITON_ANNOTATION = "org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition";
    public static final String FIELD_DEFINITION_ANNOTATION = "org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition";

    private GenerationCompleteCallback callback = null;

    private SourceGenerationContext context;

    public FormDefinitionsProcessor() {

    }

    // Constructor for tests only, to prevent code being written to file. The generated code will be sent to the callback
    FormDefinitionsProcessor(GenerationCompleteCallback callback) {
        this();
        this.callback = callback;
        System.out.println("GenerationCompleteCallback has been provided. Generated source code will not be compiled and hence classes will not be available.");
    }

    @Override
    protected boolean processWithExceptions(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) throws Exception {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                 "Searching FormDefinitions on project");

        //We don't have any post-processing
        if (roundEnvironment.processingOver()) {
            return false;
        }

        //If prior processing threw an error exit
        if (roundEnvironment.errorRaised()) {
            return false;
        }

        context = new SourceGenerationContext(processingEnv, roundEnvironment);

        new FieldDefinitionModifierGenerator(context).generate();
        new FormDefinitionGenerator(context).generate();

        if (!context.getForms().isEmpty() || !context.getFieldDefinitions().isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating sources for [" + context.getForms().size() + "] FormDefinitions & [" + context.getFieldDefinitions().size() + "] FieldDefinitions");

            String packageName = context.getForms().get(0).getModelClass();

            packageName = packageName.substring(0, packageName.lastIndexOf(".")) + ".formBuilder.provider";

            Map<String, Object> templateContext = new HashMap<>();

            templateContext.put("package", packageName);
            templateContext.put("generatedByClassName", this.getClass().getName());
            templateContext.put("forms", context.getForms());
            templateContext.put("fieldModifiers", context.getFieldDefinitions());
            templateContext.put("fieldDefinitions", context.getFieldDefinitions());

            StringBuffer code = writeTemplate("templates/FormGenerationResourcesProvider.ftl", templateContext);

            // If code is successfully created write files, or send generated code to callback.
            // The callback function is used primarily for testing when we don't necessarily want
            // the generated code to be stored as a compilable file for javac to process.
            if (callback == null) {
                writeCode(packageName,
                          "ModuleFormGenerationResourcesProvider",
                          code);
            } else {
                callback.generationComplete(code.toString());
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                     "Succesfully Generated sources for [" + context.getForms().size() + "] FormDefinitions & [" + context.getFieldDefinitions().size() + "] FieldDefinitions");
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                     "No FormDefinitions found on module");
        }

        return true;
    }

    protected StringBuffer writeTemplate(String templateName, Map<String, Object> context) throws GenerationException {
        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);

        // The code used to contain 'new InputStreamReader(this.getClass().getResourceAsStream(templateName))' which for
        // some reason was causing issues during concurrent invocation of this method (e.g. in parallel Maven build).
        // The stream returned by 'getResourceAsStream(templateName)' was sometimes already closed (!) and as the
        // Template class tried to read from the stream it resulted in IOException. Changing the code to
        // 'getResource(templateName).openStream()' seems to be a sensible workaround
        try (InputStream templateIs = this.getClass().getResource(templateName).openStream()) {
            Configuration config = new Configuration();

            Template template = new Template("",
                                             new InputStreamReader(templateIs),
                                             config);

            template.process(context,
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
        return sw.getBuffer();
    }
}
