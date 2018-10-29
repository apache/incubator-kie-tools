/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.GenerationContext;
import org.kie.workbench.common.forms.jbpm.server.service.util.JBPMFormsIntegrationBackendConstants;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

@Runtime
@Dependent
public class BPMNRuntimeFormGeneratorService extends AbstractBPMNFormGeneratorService<ClassLoader> {

    private static final Logger logger = LoggerFactory.getLogger(BPMNRuntimeFormGeneratorService.class);

    private MVELEvaluator evaluator;

    @Inject
    public BPMNRuntimeFormGeneratorService(FieldManager fieldManager,
                                           MVELEvaluator evaluator) {
        super(fieldManager);
        this.evaluator = evaluator;
    }

    @Override
    protected FormDefinition createRootFormDefinition(GenerationContext<ClassLoader> context) {
        FormDefinition form = new FormDefinition(context.getFormModel());

        form.setId(context.getFormModel().getFormName());
        form.setName(context.getFormModel().getFormName());

        context.getFormModel().getProperties().forEach(property -> {

            if (!BPMNVariableUtils.isValidInputName(property.getName())) {
                return;
            }

            FieldDefinition field = generateFieldDefinition(property, context);

            if (field != null) {
                form.getFields().add(field);
            }
        });

        return form;
    }

    @Override
    protected List<FieldDefinition> extractModelFields(JavaFormModel formModel, GenerationContext<ClassLoader> context) {

        final String modelType = formModel.getType();

        Class<?> clazz = null;

        try {
            clazz = context.getSource().loadClass(modelType);
            if (clazz == null) {
                clazz = getClass().forName(modelType);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to extract Form Fields for class '" + modelType + "'");
        }

        if (clazz == null) {
            throw new IllegalArgumentException("Unable to extract Form Fields for class '" + modelType + "'");
        }

        ModuleDataModelOracle oracle = getModuleOracle(clazz);
        if (oracle != null) {

            List<FieldDefinition> formFields = new ArrayList<>();

            ModelField[] fields = oracle.getModuleModelFields().get(modelType);

            Arrays.stream(fields).forEach(modelField -> {
                if (modelField.getName().equals("this")) {
                    return;
                }
                if (!FieldAccessorsAndMutators.BOTH.equals(modelField.getAccessorsAndMutators())) {
                    return;
                }
                try {
                    String fieldType = modelField.getClassName();
                    boolean isEnunm = oracle.getModuleJavaEnumDefinitions().get(modelType + "#" + modelField.getName()) != null;
                    boolean isList = DataType.TYPE_COLLECTION.equals(modelField.getType());

                    if (isList) {
                        fieldType = oracle.getModuleFieldParametersType().get(modelType + "#" + modelField.getName());
                    }

                    TypeKind typeKind = isEnunm ? TypeKind.ENUM : FormModelPropertiesUtil.isBaseType(fieldType) ? TypeKind.BASE : TypeKind.OBJECT;

                    TypeInfo info = new TypeInfoImpl(typeKind,
                                                     fieldType,
                                                     isList);

                    ModelProperty modelProperty = new ModelPropertyImpl(modelField.getName(),
                                                                        info);

                    formModel.getProperties().add(modelProperty);

                    FieldDefinition field = generateFieldDefinition(modelProperty, context);

                    if (field != null) {
                        formFields.add(field);
                    }
                } catch (Exception ex) {
                    logger.warn("Error processing model \"" + modelType + "\" impossible generate FieldDefinition for model field \"" + modelField.getName() + "\" (" + modelField.getType() + ")");
                }
            });
            return formFields;
        }

        return null;
    }

    @Override
    protected Supplier<LayoutComponent> getRootFormHeader() {
        return () -> {
            ResourceBundle bundle = ResourceBundle.getBundle(JBPMFormsIntegrationBackendConstants.BUNDLE);

            String warning = bundle.getString(JBPMFormsIntegrationBackendConstants.RUNTIMER_FORM_GENERATION_WARNING_KEY);
            String message = bundle.getString(JBPMFormsIntegrationBackendConstants.RUNTIMER_FORM_GENERATION_MESSAGE_KEY);
            String code = MessageFormat.format(JBPMFormsIntegrationBackendConstants.RUNTIME_FORM_GENERATION_WARNING_TEMPLATE, warning, message);
            return generateHTMLElement(code);
        };
    }

    @Override
    protected boolean supportsEmptyNestedForms() {
        return false;
    }

    @Override
    protected void log(String message, Exception ex) {
        logger.warn(message, ex);
    }

    protected ModuleDataModelOracle getModuleOracle(Class clazz) {
        try {
            final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(evaluator);

            final ClassFactBuilder modelFactBuilder = new ClassFactBuilder(builder,
                                                                           clazz,
                                                                           false,
                                                                           TypeSource.JAVA_PROJECT);

            ModuleDataModelOracle oracle = modelFactBuilder.getDataModelBuilder().build();

            Map<String, FactBuilder> builders = new HashMap<>();

            for (FactBuilder factBuilder : modelFactBuilder.getInternalBuilders().values()) {
                if (factBuilder instanceof ClassFactBuilder) {
                    builders.put(((ClassFactBuilder) factBuilder).getType(),
                                 factBuilder);
                    factBuilder.build((ModuleDataModelOracleImpl) oracle);
                }
            }
            builders.put(modelFactBuilder.getType(),
                         modelFactBuilder);

            modelFactBuilder.build((ModuleDataModelOracleImpl) oracle);

            return oracle;
        } catch (IOException ex) {

        }
        return null;
    }

    protected FieldDefinition generateFieldDefinition(ModelProperty property, GenerationContext<ClassLoader> context) {
        return fieldManager.getDefinitionByModelProperty(property);
    }
}
