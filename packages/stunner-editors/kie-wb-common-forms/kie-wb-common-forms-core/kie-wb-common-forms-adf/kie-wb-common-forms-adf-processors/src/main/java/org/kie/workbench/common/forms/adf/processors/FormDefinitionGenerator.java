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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Column;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldHelp;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.adf.processors.util.FieldInfo;
import org.kie.workbench.common.forms.adf.processors.util.FormGenerationUtils;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.uberfire.annotations.processors.GeneratorUtils;

import static org.kie.workbench.common.forms.adf.processors.FormDefinitionsProcessor.FORM_DEFINITON_ANNOTATION;

public class FormDefinitionGenerator {

    private static final String FORM_BUILDER_SUFFIX = "FormBuilder";

    private final SourceGenerationContext context;

    private TypeMirror listType;
    private TypeMirror enumType;

    public FormDefinitionGenerator(SourceGenerationContext context) {
        this.context = context;
    }

    public void generate() throws Exception {
        TypeElement annotation = context.getElementUtils().getTypeElement(FORM_DEFINITON_ANNOTATION);

        Set<? extends Element> formDefinitions = context.getRoundEnvironment().getElementsAnnotatedWith(annotation);

        context.getMessager().printMessage(Diagnostic.Kind.NOTE, "FormDefinitions found:  " + formDefinitions.size());

        // Initializing list type to avoid getting the element each time.
        listType = context.getElementUtils().getTypeElement(List.class.getName()).asType();
        enumType = context.getElementUtils().getTypeElement(Enum.class.getName()).asType();

        for (Element element : formDefinitions) {
            if (element.getKind().equals(ElementKind.CLASS)) {
                processFormDefinition((TypeElement) element);
            }
        }
    }

    protected void processFormDefinition(TypeElement formElement) throws Exception {
        final Messager messager = context.getMessager();

        messager.printMessage(Diagnostic.Kind.NOTE, "Discovered FormDefintion class [" + formElement.getSimpleName() + "]");

        boolean checkInheritance = false;

        FormDefinition definition = formElement.getAnnotation(FormDefinition.class);

        String modelClassName = formElement.getQualifiedName().toString();
        String builderClassName = FormGenerationUtils.fixClassName(formElement.getQualifiedName().toString()) + FORM_BUILDER_SUFFIX;

        FormDefinitionData form = new FormDefinitionData(modelClassName, builderClassName);

        form.setStartElement(definition.startElement());
        form.setI18nBundle(StringUtils.isEmpty(definition.i18n().bundle()) ? formElement.asType().toString() : definition.i18n().bundle());

        Column[] columns = definition.layout().value();

        List<String> layoutColumns = new ArrayList<>();
        if (columns.length == 0) {
            layoutColumns.add(ColSpan.SPAN_12.getName());
        } else {
            for (Column column : columns) {
                layoutColumns.add(column.value().getName());
            }
        }

        form.setLayoutColumns(layoutColumns);

        checkInheritance = definition.allowInheritance();

        Map<String, String> defaultFieldSettings = new HashMap<>();

        for (FieldParam param : definition.defaultFieldSettings()) {
            defaultFieldSettings.put(param.name(), param.value());
        }

        List<FormDefinitionFieldData> formElements = new ArrayList<>();

        if (checkInheritance) {
            TypeElement parent = getParent(formElement);
            formElements.addAll(extractParentFormFields(parent, definition.policy(), definition.i18n(), defaultFieldSettings));
        }

        formElements.addAll(extracFormFields(formElement, definition.policy(), definition.i18n(), defaultFieldSettings));

        FormGenerationUtils.sort(definition.startElement(), formElements);

        messager.printMessage(Diagnostic.Kind.NOTE, "Discovered " + formElements.size() + " elements for form [" + formElement.getQualifiedName().toString() + "]");

        form.getElements().addAll(formElements);

        context.getForms().add(form);
    }

    private List<FormDefinitionFieldData> extractParentFormFields(TypeElement element, FieldPolicy policy, I18nSettings i18nSettings, Map<String, String> defaultParams) throws Exception {

        if (element.toString().equals(Object.class.getName())) {
            return new ArrayList<>();
        }

        TypeElement parentElement = getParent(element);

        List<FormDefinitionFieldData> result = extractParentFormFields(parentElement, policy, i18nSettings, defaultParams);

        result.addAll(extracFormFields(element, policy, i18nSettings, defaultParams));

        return result;
    }

    private boolean filter(VariableElement fieldElement, FieldPolicy policy) {
        if (policy.equals(FieldPolicy.ALL)) {
            AnnotationMirror annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldElement, SkipFormField.class.getName());
            if (annotation != null) {
                return false;
            }
        } else {
            AnnotationMirror annotation = GeneratorUtils.getAnnotation(context.getElementUtils(), fieldElement, FormField.class.getName());
            if (annotation == null) {
                return false;
            }
        }
        return true;
    }

    private List<FormDefinitionFieldData> extracFormFields(TypeElement type, FieldPolicy policy, I18nSettings i18nSettings, Map<String, String> defaultParams) throws Exception {

        final Types typeUtils = context.getProcessingEnvironment().getTypeUtils();

        Collection<FieldInfo> fieldInfos = FormGenerationUtils.extractFieldInfos(type, fieldElement -> filter(fieldElement, policy));

        List<FormDefinitionFieldData> fieldSettings = new ArrayList<>();

        for (FieldInfo fieldInfo : fieldInfos) {

            if (fieldInfo.getSetter() != null && fieldInfo.getGetter() != null) {

                String fieldName = fieldInfo.getFieldElement().getSimpleName().toString();

                FormDefinitionFieldData fieldData = new FormDefinitionFieldData(type.getQualifiedName().toString(), fieldName);

                fieldData.setLabel(fieldName);
                fieldData.setBinding(fieldName);
                fieldData.setMethodName("getFormElement_" + fieldName);

                boolean isList = false;

                org.kie.workbench.common.forms.model.TypeKind typeKind = org.kie.workbench.common.forms.model.TypeKind.BASE;

                boolean overrideI18n = false;

                TypeMirror finalType = fieldInfo.getFieldElement().asType();
                TypeElement finalTypeElement = (TypeElement) typeUtils.asElement(finalType);

                String fieldModifier = "";

                if (finalTypeElement.getKind().equals(ElementKind.CLASS)) {
                    FieldDefinition fieldDefinitionAnnotation = finalTypeElement.getAnnotation(FieldDefinition.class);
                    if (fieldDefinitionAnnotation != null) {

                        // Override the using the i18n mechanism
                        if (fieldDefinitionAnnotation.i18nMode().equals(I18nMode.OVERRIDE_I18N_KEY)) {
                            fieldData.setLabel(finalType.toString() + i18nSettings.separator() + fieldDefinitionAnnotation.labelKeySuffix());
                            Collection<FieldInfo> labelInfos = FormGenerationUtils.extractFieldInfos(finalTypeElement, fieldElement -> fieldElement.getAnnotation(FieldLabel.class) != null);

                            if (labelInfos != null && labelInfos.size() == 1) {
                                FieldInfo labelInfo = labelInfos.iterator().next();
                                fieldData.setLabel(finalType.toString() + i18nSettings.separator() + labelInfo.getFieldElement().getSimpleName());
                            }

                            fieldData.setHelpMessage(finalType.toString() + i18nSettings.separator() + fieldDefinitionAnnotation.helpMessageKeySuffix());
                            Collection<FieldInfo> helpMessages = FormGenerationUtils.extractFieldInfos(finalTypeElement, fieldElement -> fieldElement.getAnnotation(FieldHelp.class) != null);

                            if (helpMessages != null && helpMessages.size() == 1) {
                                FieldInfo helpInfo = helpMessages.iterator().next();
                                fieldData.setHelpMessage(finalType.toString() + i18nSettings.separator() + helpInfo.getFieldElement().getSimpleName());
                            }
                        }

                        Collection<FieldInfo> fieldValue = FormGenerationUtils.extractFieldInfos(finalTypeElement, fieldElement -> fieldElement.getAnnotation(FieldValue.class) != null);

                        if (fieldValue == null || fieldValue.size() != 1) {
                            throw new Exception("Problem processing FieldDefinition [" + finalType + "]: it should have one field marked as @FieldValue");
                        }
                        FieldInfo valueInfo = fieldValue.iterator().next();

                        fieldData.setBinding(fieldData.getBinding() + "." + valueInfo.getFieldElement().getSimpleName());

                        fieldModifier = FormGenerationUtils.fixClassName(finalType.toString()) + "_FieldStatusModifier";

                        finalType = valueInfo.getFieldElement().asType();
                        finalTypeElement = (TypeElement) typeUtils.asElement(finalType);

                        overrideI18n = !fieldDefinitionAnnotation.i18nMode().equals(I18nMode.DONT_OVERRIDE);
                    } else {
                        FormDefinition formDefinitionAnnotation = finalTypeElement.getAnnotation(FormDefinition.class);

                        if (formDefinitionAnnotation != null) {
                            fieldData.setLabel(finalType.toString() + i18nSettings.separator() + FieldDefinition.LABEL);
                            Collection<FieldInfo> labelInfos = FormGenerationUtils.extractFieldInfos(finalTypeElement, fieldElement -> fieldElement.getAnnotation(FieldLabel.class) != null);

                            if (labelInfos != null && labelInfos.size() == 1) {
                                FieldInfo labelInfo = labelInfos.iterator().next();
                                fieldData.setLabel(finalType.toString() + i18nSettings.separator() + labelInfo.getFieldElement().getSimpleName());
                                overrideI18n = true;
                            }

                            fieldData.setHelpMessage(finalType.toString() + i18nSettings.separator() + FieldDefinition.HELP_MESSAGE);
                            Collection<FieldInfo> helpMessages = FormGenerationUtils.extractFieldInfos(finalTypeElement, fieldElement -> fieldElement.getAnnotation(FieldHelp.class) != null);

                            if (helpMessages != null && helpMessages.size() == 1) {
                                FieldInfo helpInfo = helpMessages.iterator().next();
                                fieldData.setHelpMessage(finalType.toString() + i18nSettings.separator() + helpInfo.getFieldElement().getSimpleName());
                                overrideI18n = true;
                            }

                            typeKind = org.kie.workbench.common.forms.model.TypeKind.OBJECT;
                        }
                    }
                }

                DeclaredType fieldType = (DeclaredType) finalType;

                if (typeUtils.isAssignable(finalTypeElement.asType(), listType)) {
                    if (fieldType.getTypeArguments().size() != 1) {
                        throw new IllegalArgumentException("Impossible to generate a field for type " + fieldType.toString() + ". Type should have one and only one Type arguments.");
                    }
                    isList = true;
                    finalType = fieldType.getTypeArguments().get(0);
                    finalTypeElement = (TypeElement) typeUtils.asElement(finalType);

                    if (FormModelPropertiesUtil.isBaseType(finalTypeElement.getQualifiedName().toString())) {
                        typeKind = org.kie.workbench.common.forms.model.TypeKind.BASE;
                    } else if (typeUtils.isAssignable(finalTypeElement.asType(), enumType)) {
                        typeKind = org.kie.workbench.common.forms.model.TypeKind.ENUM;
                    } else {
                        typeKind = org.kie.workbench.common.forms.model.TypeKind.OBJECT;
                    }
                } else if (typeUtils.isAssignable(finalTypeElement.asType(), enumType)) {
                    typeKind = org.kie.workbench.common.forms.model.TypeKind.ENUM;
                }

                fieldData.setType(typeKind.toString());
                fieldData.setClassName(finalTypeElement.getQualifiedName().toString());
                fieldData.setList(String.valueOf(isList));
                fieldData.setFieldModifier(fieldModifier);
                fieldData.getParams().putAll(defaultParams);

                FormField settings = fieldInfo.getFieldElement().getAnnotation(FormField.class);

                if (settings != null) {
                    try {
                        fieldData.setPreferredType(settings.type().getName());
                    } catch (MirroredTypeException exception) {
                        fieldData.setPreferredType(exception.getTypeMirror().toString());
                    }

                    fieldData.setAfterElement(settings.afterElement());

                    if (!overrideI18n && !isEmpty(settings.labelKey())) {
                        fieldData.setLabel(settings.labelKey());
                    }

                    if (!overrideI18n && !isEmpty(settings.helpMessageKey())) {
                        fieldData.setHelpMessage(settings.helpMessageKey());
                    }

                    fieldData.setRequired(Boolean.valueOf(settings.required()).toString());
                    fieldData.setReadOnly(Boolean.valueOf(settings.readonly()).toString());

                    for (FieldParam fieldParam : settings.settings()) {
                        fieldData.getParams().put(fieldParam.name(), fieldParam.value());
                    }

                    fieldData.setWrap(Boolean.valueOf(settings.layoutSettings().wrap()).toString());
                    fieldData.setHorizontalSpan(String.valueOf(settings.layoutSettings().horizontalSpan()));
                    fieldData.setVerticalSpan(String.valueOf(settings.layoutSettings().verticalSpan()));
                }

                if (!overrideI18n) {
                    if (!isEmpty(i18nSettings.keyPreffix())) {
                        fieldData.setLabel(i18nSettings.keyPreffix() + i18nSettings.separator() + fieldData.getLabel());
                        if(!isEmpty(fieldData.getHelpMessage())) {
                            fieldData.setHelpMessage(i18nSettings.keyPreffix() + i18nSettings.separator() + fieldData.getHelpMessage());
                        }
                    }
                }

                extractFieldExtraSettings(fieldData, fieldInfo.getFieldElement());

                fieldSettings.add(fieldData);
            }
        }
        return fieldSettings;
    }


    protected void extractFieldExtraSettings(FormDefinitionFieldData fieldData, Element fieldElement) {
        SelectorDataProvider selectorDataProvider = fieldElement.getAnnotation(SelectorDataProvider.class);
        if (selectorDataProvider != null) {
            String provider = selectorDataProvider.type().getCode() + ":" + selectorDataProvider.className();
            fieldData.getParams().put(SelectorDataProvider.class.getName(), provider);
        }
    }

    private TypeElement getParent(TypeElement classElement) {
        return (TypeElement) context.getProcessingEnvironment().getTypeUtils().asElement(classElement.getSuperclass());
    }

    private static final boolean isEmpty(final String s) {
        return StringUtils.isEmpty(s);
    }
}
