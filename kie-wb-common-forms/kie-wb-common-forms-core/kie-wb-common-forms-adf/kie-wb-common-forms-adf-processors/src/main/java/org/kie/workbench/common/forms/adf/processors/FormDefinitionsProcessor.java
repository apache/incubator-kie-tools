/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.beans.Introspector;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldReadOnly;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldRequired;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.adf.processors.util.FormGenerationUtils;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;
import org.uberfire.annotations.processors.GenerationCompleteCallback;
import org.uberfire.annotations.processors.GeneratorUtils;
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

    private TypeMirror listType;

    private GenerationCompleteCallback callback = null;

    private Elements elementUtils;

    private RoundEnvironment roundEnvironment;

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
    protected boolean processWithExceptions(Set<? extends TypeElement> annotations,
                                            RoundEnvironment roundEnvironment) throws Exception {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                 "Searching FormDefinitions on project");

        //If prior processing threw an error exit
        if (roundEnvironment.errorRaised()) {
            return false;
        }

        this.elementUtils = processingEnv.getElementUtils();
        this.roundEnvironment = roundEnvironment;

        context = new SourceGenerationContext();

        processFieldDefinitions();

        processFormDefinitions();

        if (!context.getForms().isEmpty() || !context.getFieldDefinitions().isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                     "Generating sources for [" + context.getForms().size() + "] FormDefinitions & [" + context.getFieldDefinitions().size() + "] FieldDefinitions");
            String packageName = context.getForms().get(0).get("package") + ".formBuilder.provider";
            Map<String, Object> templateContext = new HashMap<>();

            templateContext.put("package",
                                packageName);
            templateContext.put("generatedByClassName",
                                this.getClass().getName());
            templateContext.put("forms",
                                context.getForms());
            templateContext.put("fieldModifiers",
                                context.getFieldDefinitions());
            templateContext.put("fieldDefinitions",
                                context.getFieldDefinitions());

            StringBuffer code = writeTemplate("templates/FormGenerationResourcesProvider.ftl",
                                              templateContext);

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

    protected void processFieldDefinitions() throws Exception {
        TypeElement annotation = elementUtils.getTypeElement(FIELD_DEFINITION_ANNOTATION);

        Set<? extends Element> fieldDefintions = roundEnvironment.getElementsAnnotatedWith(annotation);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                 "FieldDefinitions found:  " + fieldDefintions.size());

        for (Element element : fieldDefintions) {
            if (element.getKind().equals(ElementKind.CLASS)) {
                processFieldDefinition((TypeElement) element);
            }
        }
    }

    private void processFieldDefinition(TypeElement fieldDefinitionElement) throws Exception {
        final Messager messager = processingEnv.getMessager();

        messager.printMessage(Diagnostic.Kind.NOTE,
                              "Discovered FieldDefinition class [" + fieldDefinitionElement.getSimpleName() + "]");

        Collection<FieldInfo> fieldInfos = extractFieldInfos(fieldDefinitionElement,
                                                             null);

        String modelClassName = fieldDefinitionElement.getQualifiedName().toString();

        String fieldModifierName = fixClassName(modelClassName) + "_FieldStatusModifier";

        Map<String, String> fieldDefinition = new HashMap<>();

        fieldDefinition.put("className",
                            modelClassName);
        fieldDefinition.put("fieldModifierName",
                            fieldModifierName);

        Map<String, Object> templateContext = new HashMap<>();

        templateContext.put("modelClassName",
                            modelClassName);
        templateContext.put("fieldModifierName",
                            fieldModifierName);

        FieldDefinition fieldDefinitionAnnotation = fieldDefinitionElement.getAnnotation(FieldDefinition.class);

        for (FieldInfo fieldInfo : fieldInfos) {
            AnnotationMirror annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                                       fieldInfo.fieldElement,
                                                                       FieldValue.class.getName());

            if (annotation != null) {
                if (fieldDefinition.containsKey("value")) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldValue");
                }

                if (fieldInfo.getter == null || fieldInfo.setter == null) {
                    throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldValue should have setter & getter");
                }

                fieldDefinition.put("value",
                                    fieldInfo.fieldElement.getSimpleName().toString());
            } else {
                annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                          fieldInfo.fieldElement,
                                                          FieldReadOnly.class.getName());

                if (annotation != null) {
                    if (templateContext.containsKey("readOnly")) {
                        throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldReadOnly");
                    }

                    if (!fieldInfo.fieldElement.asType().getKind().equals(TypeKind.BOOLEAN) &&
                            !fieldInfo.fieldElement.asType().toString().equals(Boolean.class.getName())) {
                        throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldReadOnly must be boolean or Boolean");
                    }

                    if (fieldInfo.getter == null) {
                        throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldReadOnly should have getter");
                    }

                    templateContext.put("readOnly",
                                        fieldInfo.getter);
                }

                annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                          fieldInfo.fieldElement,
                                                          FieldRequired.class.getName());

                if (annotation != null) {
                    if (templateContext.containsKey("required")) {
                        throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldRequired");
                    }

                    if (!fieldInfo.fieldElement.asType().getKind().equals(TypeKind.BOOLEAN) &&
                            !fieldInfo.fieldElement.asType().toString().equals(Boolean.class.getName())) {
                        throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldRequired must be boolean or Boolean");
                    }

                    if (fieldInfo.getter == null) {
                        throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldRequired should have getter");
                    }

                    templateContext.put("required",
                                        fieldInfo.getter);
                }

                if (fieldDefinitionAnnotation.i18nMode().equals(I18nMode.OVERRIDE)) {

                    annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                              fieldInfo.fieldElement,
                                                              FieldLabel.class.getName());

                    if (annotation != null) {
                        if (templateContext.containsKey("label")) {
                            throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it should have only one @FieldLabel");
                        }

                        if (!fieldInfo.fieldElement.asType().toString().equals(String.class.getName())) {
                            throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldLabel must be a String");
                        }

                        if (fieldInfo.getter == null) {
                            throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldLabel should have getter");
                        }
                        templateContext.put("label",
                                            fieldInfo.getter);
                    }

                    annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                              fieldInfo.fieldElement,
                                                              FieldHelp.class.getName());

                    if (annotation != null) {
                        if (templateContext.containsKey("helpMessage")) {
                            throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: it has more than one field marked as @FieldHelp");
                        }

                        if (!fieldInfo.fieldElement.asType().toString().equals(String.class.getName())) {
                            throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldHelp must be a String");
                        }

                        if (fieldInfo.getter == null) {
                            throw new Exception("Problem processing FieldDefinition [" + modelClassName + "]: field marked as @FieldHelp should have getter");
                        }
                        templateContext.put("helpMessage",
                                            fieldInfo.getter);
                    }
                }
            }
        }

        StringBuffer source = writeTemplate("templates/FieldDefinitionModifier.ftl",
                                            templateContext);

        fieldDefinition.put("sourceCode",
                            source.toString());

        context.getFieldDefinitions().add(fieldDefinition);
    }

    protected void processFormDefinitions() throws Exception {
        TypeElement annotation = elementUtils.getTypeElement(FORM_DEFINITON_ANNOTATION);

        Set<? extends Element> formDefinitions = roundEnvironment.getElementsAnnotatedWith(annotation);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                 "FormDefinitions found:  " + formDefinitions.size());

        // Initializing list type to avoid getting the element each time.
        listType = elementUtils.getTypeElement(java.util.List.class.getName()).asType();

        for (Element element : formDefinitions) {
            if (element.getKind().equals(ElementKind.CLASS)) {
                processFormDefinition((TypeElement) element);
            }
        }
    }

    protected void processFormDefinition(TypeElement formElement) throws Exception {
        final Messager messager = processingEnv.getMessager();

        messager.printMessage(Diagnostic.Kind.NOTE,
                              "Discovered FormDefintion class [" + formElement.getSimpleName() + "]");

        boolean checkInheritance = false;

        FormDefinition definition = formElement.getAnnotation(FormDefinition.class);

        checkInheritance = definition.allowInheritance();

        Map<String, String> defaultFieldSettings = new HashMap<>();

        for (FieldParam param : definition.defaultFieldSettings()) {
            defaultFieldSettings.put(param.name(), param.value());
        }

        List<Map<String, String>> formElements = new ArrayList<>();

        if (checkInheritance) {
            TypeElement parent = getParent(formElement);
            formElements.addAll(extractParentFormFields(parent, definition.policy(), definition.i18n(), defaultFieldSettings));
        }

        formElements.addAll(extracFormFields(formElement, definition.policy(), definition.i18n(), defaultFieldSettings));

        FormGenerationUtils.sort(definition.startElement(),
                                 formElements);

        messager.printMessage(Diagnostic.Kind.NOTE,
                              "Discovered " + formElements.size() + " elements for form [" + formElement.getQualifiedName().toString() + "]");

        String modelClassName = formElement.getQualifiedName().toString();
        String builderClassName = fixClassName(formElement.getQualifiedName().toString()) + "FormBuilder";

        Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("modelClass",
                            modelClassName);
        templateContext.put("builderClassName",
                            builderClassName);
        templateContext.put("startElement",
                            definition.startElement());

        templateContext.put("i18n_bundle",
                            StringUtils.isEmpty(definition.i18n().bundle()) ? formElement.asType().toString() : definition.i18n().bundle());

        Column[] columns = definition.layout().value();

        List<String> layoutColumns = new ArrayList<>();
        if (columns.length == 0) {
            layoutColumns.add(ColSpan.SPAN_12.getName());
        } else {
            for (Column column : columns) {
                layoutColumns.add(column.value().getName());
            }
        }

        templateContext.put("layout_columns",
                            layoutColumns);

        templateContext.put("elements",
                            formElements);

        StringBuffer builder = writeTemplate("templates/FormDefinitionSettingsBuilder.ftl",
                                             templateContext);

        Map<String, String> form = new HashMap<>();

        form.put("package",
                 ((PackageElement) formElement.getEnclosingElement()).getQualifiedName().toString());
        form.put("modelClass",
                 modelClassName);
        form.put("builderClass",
                 builderClassName);
        form.put("builderCode",
                 builder.toString());

        context.getForms().add(form);
    }

    private List<Map<String, String>> extractParentFormFields(TypeElement element, FieldPolicy policy, I18nSettings i18nSettings, Map<String, String> defaultParams) throws Exception {

        if (element.toString().equals(Object.class.getName())) {
            return new ArrayList<>();
        }

        TypeElement parentElement = getParent(element);

        List<Map<String, String>> result = extractParentFormFields(parentElement, policy, i18nSettings, defaultParams);

        result.addAll(extracFormFields(element, policy, i18nSettings, defaultParams));

        return result;
    }

    private List<Map<String, String>> extracFormFields(TypeElement type, FieldPolicy policy, I18nSettings i18nSettings, Map<String, String> defaultParams) throws Exception {

        final Elements elementUtils = processingEnv.getElementUtils();

        Collection<FieldInfo> fieldInfos = extractFieldInfos(type,
                                                             fieldElement -> {
                                                                 if (policy.equals(FieldPolicy.ALL)) {
                                                                     AnnotationMirror annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                                                                                                fieldElement,
                                                                                                                                SkipFormField.class.getName());
                                                                     if (annotation != null) {
                                                                         return false;
                                                                     }
                                                                 } else {
                                                                     AnnotationMirror annotation = GeneratorUtils.getAnnotation(elementUtils,
                                                                                                                                fieldElement,
                                                                                                                                FormField.class.getName());
                                                                     if (annotation == null) {
                                                                         return false;
                                                                     }
                                                                 }
                                                                 return true;
                                                             });

        List<Map<String, String>> elementsSettings = new ArrayList<>();

        for (FieldInfo fieldInfo : fieldInfos) {

            if (fieldInfo.getter != null && fieldInfo.setter != null) {

                String fieldName = fieldInfo.fieldElement.getSimpleName().toString();

                String fieldLabel = fieldName;
                String helpMessage = "";
                String binding = fieldName;

                String methodName = "getFormElement_" + fieldName;

                Map<String, Object> elementContext = new HashMap<>();

                boolean isList = false;

                org.kie.workbench.common.forms.model.TypeKind typeKind = org.kie.workbench.common.forms.model.TypeKind.BASE;

                boolean overrideI18n = false;

                TypeMirror finalType = fieldInfo.fieldElement.asType();

                String fieldModifier = "";

                if (finalType instanceof DeclaredType) {
                    Element finalTypeElement = processingEnv.getTypeUtils().asElement(finalType);

                    if (finalTypeElement.getKind().equals(ElementKind.CLASS)) {
                        FieldDefinition fieldDefinitionAnnotation = finalTypeElement.getAnnotation(FieldDefinition.class);
                        if (fieldDefinitionAnnotation != null) {

                            // Override the using the i18n mechanism
                            if (fieldDefinitionAnnotation.i18nMode().equals(I18nMode.OVERRIDE_I18N_KEY)) {
                                fieldLabel = finalType.toString() + i18nSettings.separator() + fieldDefinitionAnnotation.labelKeySuffix();
                                Collection<FieldInfo> labelInfos = extractFieldInfos((TypeElement) finalTypeElement,
                                                                                     fieldElement -> fieldElement.getAnnotation(FieldLabel.class) != null);

                                if (labelInfos != null && labelInfos.size() == 1) {
                                    FieldInfo labelInfo = labelInfos.iterator().next();
                                    fieldLabel = finalType.toString() + i18nSettings.separator() + labelInfo.fieldElement.getSimpleName();
                                }

                                helpMessage = finalType.toString() + i18nSettings.separator() + fieldDefinitionAnnotation.helpMessageKeySuffix();
                                Collection<FieldInfo> helpMessages = extractFieldInfos((TypeElement) finalTypeElement,
                                                                                       fieldElement -> fieldElement.getAnnotation(FieldHelp.class) != null);

                                if (helpMessages != null && helpMessages.size() == 1) {
                                    FieldInfo helpInfo = helpMessages.iterator().next();
                                    helpMessage = finalType.toString() + i18nSettings.separator() + helpInfo.fieldElement.getSimpleName();
                                }
                            }

                            Collection<FieldInfo> fieldValue = extractFieldInfos((TypeElement) finalTypeElement,
                                                                                 fieldElement -> fieldElement.getAnnotation(FieldValue.class) != null);

                            if (fieldValue == null || fieldValue.size() != 1) {
                                throw new Exception("Problem processing FieldDefinition [" + finalType + "]: it should have one field marked as @FieldValue");
                            }
                            FieldInfo valueInfo = fieldValue.iterator().next();

                            binding += "." + valueInfo.getFieldElement().getSimpleName();

                            fieldModifier = fixClassName(finalType.toString()) + "_FieldStatusModifier";

                            finalType = valueInfo.getFieldElement().asType();

                            overrideI18n = !fieldDefinitionAnnotation.i18nMode().equals(I18nMode.DONT_OVERRIDE);
                        } else {
                            FormDefinition formDefinitionAnnotation = finalTypeElement.getAnnotation(FormDefinition.class);

                            if (formDefinitionAnnotation != null) {
                                fieldLabel = finalType.toString() + i18nSettings.separator() + FieldDefinition.LABEL;
                                Collection<FieldInfo> labelInfos = extractFieldInfos((TypeElement) finalTypeElement,
                                                                                     fieldElement -> fieldElement.getAnnotation(FieldLabel.class) != null);

                                if (labelInfos != null && labelInfos.size() == 1) {
                                    FieldInfo labelInfo = labelInfos.iterator().next();
                                    fieldLabel = finalType.toString() + i18nSettings.separator() + labelInfo.fieldElement.getSimpleName();
                                    overrideI18n = true;
                                }

                                helpMessage = finalType.toString() + i18nSettings.separator() + FieldDefinition.HELP_MESSAGE;
                                Collection<FieldInfo> helpMessages = extractFieldInfos((TypeElement) finalTypeElement,
                                                                                       fieldElement -> fieldElement.getAnnotation(FieldHelp.class) != null);

                                if (helpMessages != null && helpMessages.size() == 1) {
                                    FieldInfo helpInfo = helpMessages.iterator().next();
                                    helpMessage = finalType.toString() + i18nSettings.separator() + helpInfo.fieldElement.getSimpleName();
                                    overrideI18n = true;
                                }

                                typeKind = org.kie.workbench.common.forms.model.TypeKind.OBJECT;
                            }
                        }
                    }

                    DeclaredType fieldType = (DeclaredType) finalType;

                    if (processingEnv.getTypeUtils().isAssignable(fieldType.asElement().asType(),
                                                                  listType)) {
                        if (fieldType.getTypeArguments().size() != 1) {
                            throw new IllegalArgumentException("Impossible to generate a field for type " + fieldType.toString() + ". Type should have one and only one Type arguments.");
                        }
                        isList = true;
                        finalType = fieldType.getTypeArguments().get(0);

                        if (FormModelPropertiesUtil.isBaseType(finalType.toString())) {
                            typeKind = org.kie.workbench.common.forms.model.TypeKind.BASE;
                        } else if (elementUtils.getTypeElement(finalType.toString()).getSuperclass().toString().startsWith("java.lang.Enum")) {
                            typeKind = org.kie.workbench.common.forms.model.TypeKind.ENUM;
                        } else {
                            typeKind = org.kie.workbench.common.forms.model.TypeKind.OBJECT;
                        }
                    } else if (elementUtils.getTypeElement(finalType.toString()).getSuperclass().toString().startsWith("java.lang.Enum")) {
                        typeKind = org.kie.workbench.common.forms.model.TypeKind.ENUM;
                    }
                }

                elementContext.put("formModel",
                                   type.getQualifiedName().toString());
                elementContext.put("methodName",
                                   methodName);
                elementContext.put("fieldName",
                                   fieldName);
                elementContext.put("binding",
                                   binding);
                elementContext.put("type",
                                   typeKind.toString());
                elementContext.put("className",
                                   finalType.toString());
                elementContext.put("isList",
                                   String.valueOf(isList));
                elementContext.put("fieldModifier",
                                   fieldModifier);

                Map<String, String> params = new HashMap<>(defaultParams);
                elementContext.put("params", params);

                String afterElement = "";
                FormField settings = fieldInfo.fieldElement.getAnnotation(FormField.class);

                if (settings != null) {
                    String typeName;
                    try {
                        typeName = settings.type().getName();
                    } catch (MirroredTypeException exception) {
                        typeName = exception.getTypeMirror().toString();
                    }
                    if (StringUtils.isEmpty(typeName)) {
                        typeName = FieldType.class.getName();
                    }

                    afterElement = settings.afterElement();

                    elementContext.put("preferredType",
                                       typeName);
                    if (!overrideI18n && !isEmpty(settings.labelKey())) {
                        fieldLabel = settings.labelKey();
                    }

                    if (!overrideI18n && !isEmpty(settings.helpMessageKey())) {
                        helpMessage = settings.helpMessageKey();
                    }

                    elementContext.put("required",
                                       Boolean.valueOf(settings.required()).toString());
                    elementContext.put("readOnly",
                                       Boolean.valueOf(settings.readonly()).toString());

                    for (FieldParam fieldParam : settings.settings()) {
                        params.put(fieldParam.name(),
                                   fieldParam.value());
                    }

                    elementContext.put("wrap",
                                       Boolean.valueOf(settings.layoutSettings().wrap()).toString());
                    elementContext.put("horizontalSpan",
                                       String.valueOf(settings.layoutSettings().horizontalSpan()));
                    elementContext.put("verticalSpan",
                                       String.valueOf(settings.layoutSettings().verticalSpan()));
                } else {
                    elementContext.put("preferredType",
                                       FieldType.class.getName());
                    elementContext.put("required",
                                       Boolean.FALSE.toString());
                    elementContext.put("readOnly",
                                       Boolean.FALSE.toString());
                    elementContext.put("wrap",
                                       Boolean.FALSE.toString());
                    elementContext.put("horizontalSpan",
                                       "1");
                    elementContext.put("verticalSpan",
                                       "1");
                }

                if (!overrideI18n) {
                    if (!isEmpty(i18nSettings.keyPreffix())) {
                        fieldLabel = i18nSettings.keyPreffix() + i18nSettings.separator() + fieldLabel;
                        helpMessage = i18nSettings.keyPreffix() + i18nSettings.separator() + helpMessage;
                    }
                }

                elementContext.put("labelKey",
                                   fieldLabel);
                elementContext.put("helpMessageKey",
                                   helpMessage);
                elementContext.put("afterElement",
                                   afterElement);

                extractFieldExtraSettings(elementContext,
                                          fieldInfo.fieldElement);

                StringBuffer methodCode = writeTemplate("templates/FieldElement.ftl",
                                                        elementContext);

                Map<String, String> fieldSettings = new HashMap<>();
                fieldSettings.put("elementName",
                                  fieldName);
                fieldSettings.put("afterElement",
                                  afterElement);
                fieldSettings.put("methodName",
                                  methodName);
                fieldSettings.put("method",
                                  methodCode.toString());

                elementsSettings.add(fieldSettings);
            }
        }
        return elementsSettings;
    }

    protected void extractFieldExtraSettings(Map<String, Object> fieldContext,
                                             VariableElement fieldElement) {
        SelectorDataProvider selectorDataProvider = fieldElement.getAnnotation(SelectorDataProvider.class);
        if (selectorDataProvider != null) {
            String providerSettings = selectorDataProvider.type().getCode() + ":" + selectorDataProvider.className();
            ((Map<String, String>) fieldContext.get("params")).put(SelectorDataProvider.class.getName(),
                                                                   providerSettings);
        }
    }

    protected StringBuffer writeTemplate(String templateName,
                                         Map<String, Object> context) throws GenerationException {
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

    private FieldInfo getInfoFromMap(String fieldName,
                                     Map<String, FieldInfo> map) {
        FieldInfo info = map.get(fieldName);

        if (info == null) {
            info = new FieldInfo();
            map.put(fieldName,
                    info);
        }

        return info;
    }

    protected Collection<FieldInfo> extractFieldInfos(TypeElement typeElement,
                                                      VariableElementValidator validator) {
        Map<String, FieldInfo> allFields = new HashMap<>();
        typeElement.getEnclosedElements().forEach(element -> {
            if (element.getKind().equals(ElementKind.FIELD)) {
                VariableElement fieldElement = (VariableElement) element;

                if (validator != null && !validator.isValid(fieldElement)) {
                    return;
                }

                FieldInfo fieldInfo = getInfoFromMap(fieldElement.getSimpleName().toString(),
                                                     allFields);

                if (fieldInfo == null) {
                    fieldInfo = new FieldInfo();
                    allFields.put(fieldElement.getSimpleName().toString(),
                                  fieldInfo);
                }
                fieldInfo.fieldElement = fieldElement;
            } else if (element.getKind().equals(ElementKind.METHOD)) {
                ExecutableElement method = (ExecutableElement) element;

                String methodName = method.getSimpleName().toString();

                if (isGetter(method)) {
                    String fieldName = extractFieldName(methodName,
                                                        3);
                    FieldInfo info = getInfoFromMap(fieldName,
                                                    allFields);
                    info.getter = methodName;
                } else if (isBooleanGetter(method)) {
                    String fieldName = extractFieldName(methodName,
                                                        2);
                    FieldInfo info = getInfoFromMap(fieldName,
                                                    allFields);
                    info.getter = methodName;
                } else if (isSetter(method)) {
                    String fieldName = extractFieldName(methodName,
                                                        3);
                    FieldInfo info = getInfoFromMap(fieldName,
                                                    allFields);
                    info.setter = methodName;
                }
            }
        });

        return allFields.values().stream().filter(fieldInfo -> fieldInfo.fieldElement != null).collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    private interface VariableElementValidator {

        boolean isValid(VariableElement fieldElement);
    }

    private class FieldInfo {

        private VariableElement fieldElement = null;
        private String setter = null;
        private String getter = null;

        public VariableElement getFieldElement() {
            return fieldElement;
        }

        public void setFieldElement(VariableElement fieldElement) {
            this.fieldElement = fieldElement;
        }
    }

    private String extractFieldName(String methodName,
                                    int index) {
        if (methodName.length() <= index) {
            throw new IllegalArgumentException("MethodName ( '" + methodName + "' ) size < " + index);
        }
        return Introspector.decapitalize(methodName.substring(index));
    }

    private boolean isGetter(final ExecutableElement method) {
        String name = method.getSimpleName().toString();
        if (method.getReturnType().getKind().equals(TypeKind.VOID)) {
            return false;
        }

        int parameterCount = method.getParameters().size();
        if (parameterCount != 0) {
            return false;
        }
        return (name.length() > 3 && name.startsWith("get"));
    }

    private boolean isBooleanGetter(final ExecutableElement method) {
        String name = method.getSimpleName().toString();

        if (!method.getReturnType().getKind().equals(TypeKind.BOOLEAN)) {
            return false;
        }

        int parameterCount = method.getParameters().size();
        if (parameterCount != 0) {
            return false;
        }
        return (name.length() > 2 && name.startsWith("is"));
    }

    private boolean isSetter(final ExecutableElement method) {
        String name = method.getSimpleName().toString();
        int parameterCount = method.getParameters().size();
        if (parameterCount != 1) {
            return false;
        }
        return (name.length() > 3 && name.startsWith("set"));
    }

    private String fixClassName(String className) {
        return className.replaceAll("\\.",
                                    "_");
    }

    private TypeElement getParent(TypeElement classElement) {
        return (TypeElement) processingEnv.getTypeUtils().asElement(classElement.getSuperclass());
    }

    private static final boolean isEmpty(final String s) {
        return StringUtils.isEmpty(s);
    }
}
