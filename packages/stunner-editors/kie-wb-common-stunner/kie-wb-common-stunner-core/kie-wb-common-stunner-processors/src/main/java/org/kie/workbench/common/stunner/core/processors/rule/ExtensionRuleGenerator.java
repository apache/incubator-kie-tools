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
import java.util.Objects;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingRule;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtensions;
import org.uberfire.annotations.processors.AbstractGenerator;
import org.uberfire.annotations.processors.GenerationException;

public class ExtensionRuleGenerator extends AbstractGenerator {

    private final ProcessingContext processingContext = ProcessingContext.getInstance();

    @Override
    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final Element element,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE,
                              "Starting code generation for [" + className + "]");
        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        final String ruleNamePrefix = MainProcessor.toValidId(className);
        final String ruleDefinitionId = ((TypeElement) element).getQualifiedName().toString();
        final RuleExtensions extensions = classElement.getAnnotation(RuleExtensions.class);
        if (null != extensions) {
            for (final RuleExtension annotation : extensions.value()) {
                processRuleExtension(ruleNamePrefix,
                                     ruleDefinitionId,
                                     annotation);
            }
        }
        final RuleExtension extension = classElement.getAnnotation(RuleExtension.class);
        if (null != extension) {
            processRuleExtension(ruleNamePrefix,
                                 ruleDefinitionId,
                                 extension);
        }
        return null;
    }

    private void processRuleExtension(final String ruleNamePrefix,
                                      final String ruleDefinitionId,
                                      final RuleExtension annotation) throws GenerationException {
        TypeMirror mirror = null;
        try {
            annotation.handler();
        } catch (MirroredTypeException mte) {
            mirror = mte.getTypeMirror();
        }
        if (null == mirror) {
            throw new RuntimeException("No handler class specifyed for @RuleExtension.");
        }
        final String rhc = mirror.toString();
        // Type arguments.
        List<? extends TypeMirror> argumentTypeMirrors = null;
        try {
            annotation.typeArguments();
        } catch (MirroredTypesException mte) {
            argumentTypeMirrors = mte.getTypeMirrors();
        }
        String rawTypeArgs = "null";
        if (null != argumentTypeMirrors) {
            rawTypeArgs = "new Class<?>[] { ";
            for (int i = 0; i < argumentTypeMirrors.size(); i++) {
                TypeMirror argTypeMirror = argumentTypeMirrors.get(i);
                String morphTargetMirrorClassName = argTypeMirror.toString();
                rawTypeArgs += morphTargetMirrorClassName + ".class"
                        + (!Objects.equals(i, argumentTypeMirrors.size() - 1) ? ", " : "");
            }
            rawTypeArgs += " }";
        }
        // Raw arguments.
        String rawArgs = "null";
        final String[] arguments = annotation.arguments();
        if (null != arguments) {
            rawArgs = "new String[] { ";
            for (String arg : arguments) {
                rawArgs += "\"" + arg + "\"";
            }
            rawArgs += " }";
        }
        final String ruleName = ruleNamePrefix + "_" + MainProcessor.toClassMemberId(rhc);
        final StringBuffer value = generateRule(ruleName,
                                                ruleDefinitionId,
                                                rawTypeArgs,
                                                rawArgs,
                                                rhc);

        processingContext.addRule(ruleName,
                                  ProcessingRule.TYPE.CARDINALITY,
                                  value);
    }

    private StringBuffer generateRule(final String ruleName,
                                      final String ruleDefinitionId,
                                      final String rawTypeArgs,
                                      final String rawArgs,
                                      final String rhc) throws GenerationException {
        Map<String, Object> root = new HashMap<>();
        root.put("ruleId",
                 ruleDefinitionId);
        root.put("args",
                 rawArgs);
        root.put("typeArgs",
                 rawTypeArgs);

        root.put("ruleName",
                 ruleName);
        root.put("ruleHandlerClass",
                 rhc);
        //Generate code
        try (final StringWriter sw = new StringWriter();
             final BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate("RuleExtension.ftl");
            template.process(root,
                             bw);
            return sw.getBuffer();
        } catch (IOException | TemplateException ioe) {
            throw new GenerationException(ioe);
        }
    }
}
