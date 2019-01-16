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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFieldInspector;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
import org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionGenerator;
import org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionParser;
import org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.FunctionsRegistry;
import org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.GenerateConditionException;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FieldMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.GenerateConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParseConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQuery;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

public abstract class BaseConditionEditorServiceImpl implements ConditionEditorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseConditionEditorServiceImpl.class);

    @Override
    public List<FunctionDef> findAvailableFunctions(Path path, String clazz) {
        ClassLoader classLoader = resolveClassLoader(path);
        Class resolvedClazz;
        try {
            resolvedClazz = classLoader.loadClass(clazz);
        } catch (ClassNotFoundException e) {
            resolvedClazz = Object.class;
            LOGGER.warn("Class: " + clazz + " was not properly resolved for module: " + path
                                + " only java.lang.Object functions will be returned instead");
        }
        return findAvailableFunctions(resolvedClazz, classLoader);
    }

    protected List<FunctionDef> findAvailableFunctions(Class<?> clazz, ClassLoader classLoader) {
        List<FunctionDef> result = new ArrayList<>();
        Class<?> paramClass;
        for (FunctionDef functionDef : FunctionsRegistry.getInstance().getFunctions()) {
            try {
                paramClass = classLoader.loadClass(functionDef.getParams().get(0).getType());
                if (paramClass.isAssignableFrom(clazz)) {
                    result.add(functionDef);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Uncommon error, internal function param type was not resolved: "
                                    + functionDef.getParams().get(0).getType());
            }
        }
        return result;
    }

    @Override
    public ParseConditionResult parseCondition(String conditionStr) {
        try {
            ConditionParser parser = new ConditionParser(conditionStr);
            return new ParseConditionResult(parser.parse());
        } catch (ParseException e) {
            return new ParseConditionResult(e.getMessage());
        }
    }

    @Override
    public GenerateConditionResult generateCondition(Condition condition) {
        ConditionGenerator generator = new ConditionGenerator();
        try {
            return new GenerateConditionResult(generator.generateScript(condition));
        } catch (GenerateConditionException e) {
            return new GenerateConditionResult(null, e.getMessage());
        }
    }

    @Override
    public TypeMetadataQueryResult findMetadata(TypeMetadataQuery query) {
        return findMetadata(query, resolveClassLoader(query.getPath()));
    }

    protected TypeMetadataQueryResult findMetadata(TypeMetadataQuery query, ClassLoader classLoader) {
        Set<TypeMetadata> typeMetadatas = new HashSet<>();
        Set<String> missingTypes = new HashSet<>();
        for (String type : query.getTypes()) {
            try {
                TypeMetadata typeMetadata = buildTypeMetadata(type, classLoader);
                typeMetadatas.add(typeMetadata);
            } catch (ClassNotFoundException e) {
                missingTypes.add(type);
            }
        }
        return new TypeMetadataQueryResult(typeMetadatas, missingTypes);
    }

    protected TypeMetadata buildTypeMetadata(String type, ClassLoader classLoader) throws ClassNotFoundException {
        GenerationTools generationTools = new GenerationTools();
        Class<?> clazz = classLoader.loadClass(type);
        ClassFieldInspector fieldInspector = new ClassFieldInspector(clazz);
        List<FieldMetadata> fields = new ArrayList<>();
        for (String fieldName : fieldInspector.getFieldNames()) {
            ClassFieldInspector.FieldInfo fieldInfo = fieldInspector.getFieldTypesFieldInfo().get(fieldName);
            if (fieldInfo.getOrigin() == ModelField.FIELD_ORIGIN.DECLARED || fieldInfo.getOrigin() == ModelField.FIELD_ORIGIN.INHERITED) {
                String accessor = null;
                String mutator = null;
                if (fieldInfo.getAccessorAndMutator() == FieldAccessorsAndMutators.ACCESSOR || fieldInfo.getAccessorAndMutator() == FieldAccessorsAndMutators.BOTH) {
                    accessor = generationTools.toJavaGetter(fieldName, fieldInfo.getReturnType().getName());
                }

                if (fieldInfo.getAccessorAndMutator() == FieldAccessorsAndMutators.MUTATOR || fieldInfo.getAccessorAndMutator() == FieldAccessorsAndMutators.BOTH) {
                    mutator = generationTools.toJavaSetter(fieldName);
                }
                fields.add(new FieldMetadata(fieldName, fieldInfo.getReturnType().getName(), accessor, mutator));
            }
        }
        return new TypeMetadata(type, fields);
    }

    protected abstract ClassLoader resolveClassLoader(Path path);
}
