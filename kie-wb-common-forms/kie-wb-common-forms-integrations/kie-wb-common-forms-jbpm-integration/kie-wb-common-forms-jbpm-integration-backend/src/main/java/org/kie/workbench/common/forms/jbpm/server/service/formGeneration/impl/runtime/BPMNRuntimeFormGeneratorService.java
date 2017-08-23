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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.appformer.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.ModelField;
import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.appformer.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.forms.commons.shared.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.GenerationContext;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.util.ModelPropertiesUtil;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;

@Runtime
@Dependent
public class BPMNRuntimeFormGeneratorService extends AbstractBPMNFormGeneratorService<ClassLoader> {

    @Inject
    public BPMNRuntimeFormGeneratorService(FieldManager fieldManager,
                                           FormLayoutTemplateGenerator layoutTemplateGenerator) {
        super(fieldManager,
              layoutTemplateGenerator);
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

            FieldDefinition field = generateFieldDefinition(property,
                                                            context);
            if (field != null) {
                form.getFields().add(field);
            }
        });

        layoutTemplateGenerator.generateLayoutTemplate(form);

        return form;
    }

    @Override
    protected List<FieldDefinition> extractModelFields(JavaFormModel formModel,
                                                       GenerationContext<ClassLoader> context) {
        Class clazz;

        String modelType = formModel.getType();

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

        ProjectDataModelOracle oracle = getProjectOracle(clazz);
        if (oracle != null) {

            List<FieldDefinition> formFields = new ArrayList<>();

            ModelField[] fields = oracle.getProjectModelFields().get(modelType);

            Arrays.stream(fields).forEach(modelField -> {
                if (modelField.getName().equals("this")) {
                    return;
                }
                String fieldType = modelField.getClassName();
                boolean isEnunm = oracle.getProjectJavaEnumDefinitions().get(modelType + "#" + modelField.getName()) != null;
                boolean isList = DataType.TYPE_COLLECTION.equals(modelField.getType());

                if (isList) {
                    fieldType = oracle.getProjectFieldParametersType().get(modelType + "#" + modelField.getName());
                }

                TypeKind typeKind = isEnunm ? TypeKind.ENUM : ModelPropertiesUtil.isBaseType(fieldType) ? TypeKind.BASE : TypeKind.OBJECT;

                TypeInfo info = new TypeInfoImpl(typeKind,
                                                 fieldType,
                                                 isList);

                ModelProperty modelProperty = new ModelPropertyImpl(modelField.getName(),
                                                                    info);

                formModel.getProperties().add(modelProperty);

                FieldDefinition field = generateFieldDefinition(modelProperty,
                                                                context);
                if (field != null) {
                    formFields.add(field);
                }
            });
            return formFields;
        }

        return null;
    }

    protected ProjectDataModelOracle getProjectOracle(Class clazz) {
        try {
            final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

            final ClassFactBuilder modelFactBuilder = new ClassFactBuilder(builder,
                                                                           clazz,
                                                                           false,
                                                                           TypeSource.JAVA_PROJECT);

            ProjectDataModelOracle oracle = modelFactBuilder.getDataModelBuilder().build();

            Map<String, FactBuilder> builders = new HashMap<>();

            for (FactBuilder factBuilder : modelFactBuilder.getInternalBuilders().values()) {
                if (factBuilder instanceof ClassFactBuilder) {
                    builders.put(((ClassFactBuilder) factBuilder).getType(),
                                 factBuilder);
                    factBuilder.build((ProjectDataModelOracleImpl) oracle);
                }
            }
            builders.put(modelFactBuilder.getType(),
                         modelFactBuilder);

            modelFactBuilder.build((ProjectDataModelOracleImpl) oracle);

            return oracle;
        } catch (IOException ex) {

        }
        return null;
    }

    protected FieldDefinition generateFieldDefinition(ModelProperty property,
                                                      GenerationContext<ClassLoader> context) {
        FieldDefinition field = fieldManager.getDefinitionByDataType(property.getTypeInfo());

        if (field == null) {
            return null;
        }

        String fieldName = property.getName();

        String label = fieldName.substring(0,
                                           1).toUpperCase() + fieldName.substring(1);
        field.setName(fieldName);
        field.setLabel(label);
        field.setStandaloneClassName(property.getTypeInfo().getClassName());
        field.setBinding(fieldName);

        if (field instanceof HasPlaceHolder) {
            ((HasPlaceHolder) field).setPlaceHolder(label);
        }

        processFieldDefinition(field,
                               context);

        return field;
    }
}
