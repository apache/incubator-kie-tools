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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.model.Source;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.util.ModelReaderUtil;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldLabelEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldPlaceHolderEntry;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.forms.data.modeller.service.impl.ext.util.ModelReaderUtil.LABEL_ANNOTATION;
import static org.kie.workbench.common.forms.data.modeller.service.impl.ext.util.ModelReaderUtil.LABEL_ANNOTATION_VALUE_PARAM;

public class DMOModelResolver {

    private static final Logger logger = LoggerFactory.getLogger(DMOModelResolver.class);

    public static DataObjectFormModel resolveModelForType(final ModuleDataModelOracle oracle, final String modelType) {
        return resolveModelForType(oracle, modelType, DMOModelResolver::allSourceTypes);
    }

    private static DataObjectFormModel resolveModelForType(final ModuleDataModelOracle oracle, final String modelType, final Predicate<TypeSource> sourcePredicate) {
        PortablePreconditions.checkNotNull("oracle", oracle);
        PortablePreconditions.checkNotNull("modelType", modelType);
        PortablePreconditions.checkNotNull("sourcePredicate", sourcePredicate);

        if (FormModelPropertiesUtil.isBaseType(modelType)) {
            throw new IllegalArgumentException("Cannot extract FormModel for type '" + modelType + "'.");
        }

        final String modelName = modelType.substring(modelType.lastIndexOf(".") + 1);

        final DataObjectFormModel formModel = new DataObjectFormModel(modelName, modelType);

        TypeSource typeSource = oracle.getModuleTypeSources().get(modelType);

        if (typeSource == null || !sourcePredicate.test(typeSource)) {
            return null;
        }

        if (typeSource.equals(TypeSource.JAVA_DEPENDENCY)) {
            formModel.setSource(Source.EXTERNAL);
        }

        ModelField[] fields = oracle.getModuleModelFields().get(modelType);

        Map<String, Set<Annotation>> fieldAnnotations = oracle.getModuleTypeFieldsAnnotations().getOrDefault(modelType, Collections.emptyMap());

        Arrays.stream(fields).forEach(modelField -> {
            if (modelField.getName().equals("this")) {
                return;
            }
            if (!FieldAccessorsAndMutators.BOTH.equals(modelField.getAccessorsAndMutators())) {
                return;
            }

            Set<Annotation> annotations = Optional.ofNullable(fieldAnnotations.get(modelField.getName())).orElse(Collections.EMPTY_SET);

            if (!isValidModelProperty(modelField, annotations)) {
                return;
            }

            try {
                String fieldType = modelField.getClassName();
                boolean isEnum = oracle.getModuleJavaEnumDefinitions().get(modelType + "#" + modelField.getName()) != null;
                boolean isList = DataType.TYPE_COLLECTION.equals(modelField.getType());

                if (isList) {
                    fieldType = oracle.getModuleFieldParametersType().get(modelType + "#" + modelField.getName());
                }

                TypeKind typeKind = isEnum ? TypeKind.ENUM : FormModelPropertiesUtil.isBaseType(fieldType) ? TypeKind.BASE : TypeKind.OBJECT;

                TypeInfo info = new TypeInfoImpl(typeKind, fieldType, isList);

                ModelProperty modelProperty = new ModelPropertyImpl(modelField.getName(), info);

                annotations.stream()
                        .filter(annotation -> annotation.getQualifiedTypeName().equalsIgnoreCase(LABEL_ANNOTATION))
                        .findAny()
                        .ifPresent(annotation -> {
                            String label = (String) annotation.getParameters().get(LABEL_ANNOTATION_VALUE_PARAM);
                            modelProperty.getMetaData().addEntry(new FieldLabelEntry(label));
                            modelProperty.getMetaData().addEntry(new FieldPlaceHolderEntry(label));
                        });

                formModel.addProperty(modelProperty);
            } catch (Exception ex) {
                logger.warn("Error processing model '" + modelType + "' impossible generate FieldDefinition for model field '" + modelField.getName() + "' (" + modelField.getType() + ")");
            }
        });
        return formModel;
    }

    private static DataObjectFormModel secureResolveModelForType(final ModuleDataModelOracle oracle, final String typeName, final Predicate<TypeSource> sourcePredicate) {
        try {
            return resolveModelForType(oracle, typeName, sourcePredicate);
        } catch (Exception ex) {
            logger.warn("Impossible to extract model for type '{}': ", typeName, ex);
        }
        return null;
    }

    public static Collection<DataObjectFormModel> resolveAllFormModels(final ModuleDataModelOracle oracle) {
        return resolveModels(oracle, DMOModelResolver::allSourceTypes);
    }

    public static Collection<DataObjectFormModel> resolveModuleFormModels(final ModuleDataModelOracle oracle) {
        return resolveModels(oracle, DMOModelResolver::onlyProjectSourceTypes);
    }

    private static Collection<DataObjectFormModel> resolveModels(final ModuleDataModelOracle oracle, final Predicate<TypeSource> sourcePredicate) {
        return getFactTypes(oracle).stream()
                .map(typeName -> secureResolveModelForType(oracle, typeName, sourcePredicate))
                .filter(Objects::nonNull)
                .sorted(DMOModelResolver::compare)
                .collect(Collectors.toList());
    }

    private static boolean allSourceTypes(final TypeSource source) {
        return true;
    }

    public static Collection<String> getFactTypes(final ModuleDataModelOracle oracle) {

        List<String> packageNames = oracle.getModulePackageNames();

        return oracle.getModuleModelFields().keySet().stream()
                .filter(factType -> isValid(factType, packageNames))
                .sorted(SortHelper.ALPHABETICAL_ORDER_COMPARATOR)
                .collect(Collectors.toList());
    }

    private static boolean isValid(String factType, Collection<String> oraclePackages) {

        int index = factType.lastIndexOf('.');
        if (index < 0) {
            return true;
        }

        String factPackage = factType.substring(0, index);

        return oraclePackages.contains(factPackage);
    }

    private static boolean onlyProjectSourceTypes(final TypeSource source) {
        return TypeSource.JAVA_PROJECT.equals(source);
    }

    private static int compare(DataObjectFormModel o1, DataObjectFormModel o2) {
        if (!o1.getSource().equals(o2.getSource())) {
            if (o1.getSource().equals(Source.INTERNAL)) {
                return -1;
            }
            return 1;
        }
        return o1.getClassName().compareTo(o2.getClassName());
    }

    public static boolean isValidModelProperty(final ModelField property, Set<Annotation> annotations) {
        if (!ModelReaderUtil.isPropertyAllowed(property.getName())) {
            return false;
        }

        return annotations.stream()
                .map(Annotation::getQualifiedTypeName)
                .noneMatch(((Predicate<String>) ModelReaderUtil::isAnnotationAllowed).negate());
    }
}
