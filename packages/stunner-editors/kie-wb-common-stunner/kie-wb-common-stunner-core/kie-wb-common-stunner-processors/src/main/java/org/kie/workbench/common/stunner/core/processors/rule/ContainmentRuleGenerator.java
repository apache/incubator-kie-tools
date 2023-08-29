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


package org.kie.workbench.common.stunner.core.processors.rule;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.GenerationException;

public class ContainmentRuleGenerator extends AbstractGenerator {

    private final ProcessingContext processingContext = ProcessingContext.getInstance();

    @Override
    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final Element element,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE,
                              "Starting code adf for [" + className + "]");
        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        final String annotationName = MainProcessor.ANNOTATION_RULE_CAN_CONTAIN;
        final String ruleId = MainProcessor.toValidId(className);
        final String ruleDefinitionId = ((TypeElement) element).getQualifiedName().toString();
        List<String> roles = null;
        for (final AnnotationMirror am : classElement.getAnnotationMirrors()) {
            if (annotationName.equals(am.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    AnnotationValue aval = entry.getValue();
                    if ("roles".equals(entry.getKey().getSimpleName().toString())) {
                        roles = (List<String>) aval.getValue();
                    }
                }
                break;
            }
        }

        processingContext.getContainmentRuleElementsProcessed().add(element);

        if (roles == null) {
            return null;
        }

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("ruleId",
                 ruleId);
        root.put("ruleDefinitionId",
                 ruleDefinitionId);
        root.put("roles",
                 roles);
        root.put("rolesCount",
                 roles.size());
        //Generate code
        try (final StringWriter sw = new StringWriter();
             final BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate("ContainmentRule.ftl");
            template.process(root,
                             bw);
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Successfully generated code for [" + className + "]");
            processingContext.addRule(ruleId,
                                      ProcessingRule.TYPE.CONTAINMENT,
                                      sw.getBuffer());
            return null;
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        } catch (TemplateException te) {
            throw new GenerationException(te);
        }
    }
}
