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

import java.util.Collection;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldHelp;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldReadOnly;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldRequired;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.forms.adf.processors.util.FieldInfo;
import org.kie.workbench.common.forms.adf.processors.util.FormGenerationUtils;
import org.uberfire.annotations.processors.GeneratorUtils;

import static org.kie.workbench.common.forms.adf.processors.FormDefinitionsProcessor.FIELD_DEFINITION_ANNOTATION;

public class FieldDefinitionModifierGenerator {

    private SourceGenerationContext context;

    public FieldDefinitionModifierGenerator(SourceGenerationContext context) {
        this.context = context;
    }

    public void generate() throws Exception {
        TypeElement annotation = context.getElementUtils().getTypeElement(FIELD_DEFINITION_ANNOTATION);

        Set<? extends Element> fieldDefintions = context.getRoundEnvironment().getElementsAnnotatedWith(annotation);

        context.getMessager().printMessage(Diagnostic.Kind.NOTE, "FieldDefinitions found:  " + fieldDefintions.size());

        for (Element element : fieldDefintions) {
            if (element.getKind().equals(ElementKind.CLASS)) {
                processFieldDefinition((TypeElement) element);
            }
        }
    }

    private void processFieldDefinition(TypeElement fieldDefinitionElement) throws Exception {
        final Messager messager = context.getMessager();

        messager.printMessage(Diagnostic.Kind.NOTE, "Discovered FieldDefinition class [" + fieldDefinitionElement.getSimpleName() + "]");

        Collection<FieldInfo> fieldInfos = FormGenerationUtils.extractFieldInfos(fieldDefinitionElement, null);

        String modelClassName = fieldDefinitionElement.getQualifiedName().toString();

        String fieldModifierName = FormGenerationUtils.fixClassName(modelClassName) + "_FieldStatusModifier";

        FieldDefinitionModifierData fieldDefinition = new FieldDefinitionModifierData(modelClassName, fieldModifierName);

        FieldDefinition fieldDefinitionAnnotation = fieldDefinitionElement.getAnnotation(FieldDefinition.class);

        for (FieldInfo fieldInfo : fieldInfos) {
            processField(fieldDefinition, fieldDefinitionAnnotation, fieldInfo);
        }

        context.getFieldDefinitions().add(fieldDefinition);
    }

    private void processField(FieldDefinitionModifierData fieldDefinition, FieldDefinition fieldDefinitionAnnotation, FieldInfo fieldInfo) throws Exception {
        String modelClassName = fieldDefinition.getModelClassName();

        AnnotationMirror annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldInfo.getFieldElement(), FieldValue.class.getName());

        if (annotation != null) {
            if (fieldDefinition.getValue() != null) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldValue");
            }

            if (fieldInfo.getGetter() == null || fieldInfo.getSetter() == null) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldValue should have setter & getter");
            }

            fieldDefinition.setValue(fieldInfo.getFieldElement().getSimpleName().toString());
        }

        annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldInfo.getFieldElement(), FieldReadOnly.class.getName());

        if (annotation != null) {
            if (fieldDefinition.getReadOnlyGetter() != null) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldReadOnly");
            }

            TypeMirror readOnlyType = fieldInfo.getFieldElement().asType();

            if (!readOnlyType.getKind().equals(TypeKind.BOOLEAN) && !readOnlyType.toString().equals(Boolean.class.getName())) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldReadOnly must be boolean or Boolean");
            }

            if (fieldInfo.getGetter() == null) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldReadOnly should have getter");
            }

            fieldDefinition.setReadOnlyGetter(fieldInfo.getGetter());
        }

        annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldInfo.getFieldElement(), FieldRequired.class.getName());

        if (annotation != null) {
            if (fieldDefinition.getRequiredGetter() != null) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldRequired");
            }

            TypeMirror requiredType = fieldInfo.getFieldElement().asType();

            if (!requiredType.getKind().equals(TypeKind.BOOLEAN) && !requiredType.toString().equals(Boolean.class.getName())) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldRequired must be boolean or Boolean");
            }

            if (fieldInfo.getGetter() == null) {
                throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldRequired should have getter");
            }

            fieldDefinition.setRequiredGetter(fieldInfo.getGetter());
        }

        if (fieldDefinitionAnnotation.i18nMode().equals(I18nMode.OVERRIDE)) {

            annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldInfo.getFieldElement(), FieldLabel.class.getName());

            if (annotation != null) {
                if (fieldDefinition.getLabelGetter() != null) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldLabel");
                }

                if (!fieldInfo.getFieldElement().asType().toString().equals(String.class.getName())) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldLabel must be a String");
                }

                if (fieldInfo.getGetter() == null) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldLabel should have getter");
                }
                fieldDefinition.setLabelGetter(fieldInfo.getGetter());
            }

            annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldInfo.getFieldElement(), FieldHelp.class.getName());

            if (annotation != null) {
                if (fieldDefinition.getHelpMessageGetter() != null) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it has more than one field marked as @FieldHelp");
                }

                if (!fieldInfo.getFieldElement().asType().toString().equals(String.class.getName())) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldHelp must be a String");
                }

                if (fieldInfo.getGetter() == null) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldHelp should have getter");
                }
                fieldDefinition.setHelpMessageGetter(fieldInfo.getGetter());
            }
        }
    }
}
