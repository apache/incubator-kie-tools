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
package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectProperty;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectsService;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;

@Service
@Dependent
public class DataObjectsServiceImpl implements DataObjectsService {

    private DataModelService dataModelService;
    private ModuleClassLoaderHelper moduleClassLoaderHelper;
    private KieModuleService moduleService;

    protected DataObjectsServiceImpl() {
        this(null, null, null);
    }

    @Inject
    public DataObjectsServiceImpl(final DataModelService dataModelService,
                                  final ModuleClassLoaderHelper moduleClassLoaderHelper,
                                  final KieModuleService moduleService) {
        this.dataModelService = dataModelService;
        this.moduleClassLoaderHelper = moduleClassLoaderHelper;
        this.moduleService = moduleService;
    }

    @Override
    public List<DataObject> loadDataObjects(final WorkspaceProject workspaceProject) {
        final KieModule module = moduleService.resolveModule(workspaceProject.getRootPath());
        final ClassLoader classLoader = moduleClassLoaderHelper.getModuleClassLoader(module);

        final ModuleDataModelOracle dmo = dataModelService.getModuleDataModel(workspaceProject.getRootPath());
        final String[] types = DataModelOracleUtilities.getFactTypes(dmo);
        final Map<String, ModelField[]> typesModelFields = dmo.getModuleModelFields();
        final Map<String, String> parametersType = dmo.getModuleFieldParametersType();

        final List<DataObject> dataObjects = Arrays.stream(types).map(DataObject::new).collect(Collectors.toList());
        dataObjects.forEach(dataObject -> convertProperties(dataObject, dataObjects, typesModelFields, classLoader, parametersType));
        return dataObjects;
    }

    private void convertProperties(final DataObject dataObject,
                                   final List<DataObject> dataObjects,
                                   final Map<String, ModelField[]> typesModelFields,
                                   final ClassLoader classLoader,
                                   final Map<String, String> parametersType) {
        final ModelField[] typeModelFields = typesModelFields.getOrDefault(dataObject.getClassType(), new ModelField[]{});
        dataObject.setProperties(Arrays.stream(typeModelFields)
                                         .filter(typeModelField -> !Objects.equals(typeModelField.getName(), DataType.TYPE_THIS))
                                         .map(typeModelField -> convertProperty(typeModelField, dataObjects, classLoader, parametersType, dataObject))
                                         .collect(Collectors.toList()));
    }

    private DataObjectProperty convertProperty(final ModelField field,
                                               final List<DataObject> dataObjects,
                                               final ClassLoader classLoader,
                                               final Map<String, String> parametersType,
                                               final DataObject dataObject) {
        final DataObjectProperty dataObjectProperty = new DataObjectProperty();

        dataObjectProperty.setList(isList(field.getClassName(), classLoader));
        if (dataObjectProperty.isList()) {
            final String parametersKey = dataObject.getClassType() + "#" + field.getName();
            if (!parametersType.containsKey(parametersKey)) {
                dataObjectProperty.setType(convertDataType(field.getClassName(), dataObjects, classLoader));
            } else {
                final String type = parametersType.get(parametersKey);
                final String listType = convertDataType(type, dataObjects, classLoader);
                dataObjectProperty.setType(listType);
            }
        } else {
            dataObjectProperty.setType(convertDataType(field.getClassName(), dataObjects, classLoader));
        }

        dataObjectProperty.setProperty(field.getName());
        return dataObjectProperty;
    }

    private boolean isList(final String typeName, final ClassLoader classLoader) {
        try {
            final String className = PrimitiveUtilities.getClassNameForPrimitiveType(typeName);
            final Class<?> clazz = classLoader.loadClass(Objects.nonNull(className) ? className : typeName);
            return List.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException cnfe) {
            return false;
        }
    }

    private String convertDataType(final String typeName,
                                   final List<DataObject> dataObjects,
                                   final ClassLoader classLoader) {
        for (DataObject dataObject : dataObjects) {
            if (Objects.equals(typeName, dataObject.getClassType())) {
                return typeName;
            }
        }

        try {
            final String className = PrimitiveUtilities.getClassNameForPrimitiveType(typeName);
            final Class<?> clazz = classLoader.loadClass(Objects.nonNull(className) ? className : typeName);
            final BuiltInType builtInType = determineBuiltInTypeFromClass(clazz);
            if (Objects.nonNull(builtInType)) {
                return builtInType.getName();
            }
        } catch (ClassNotFoundException cnfe) {
            //Swallow as BuiltInType.ANY is the default response
        }
        return BuiltInType.ANY.getName();
    }

    private BuiltInType determineBuiltInTypeFromClass(final Class<?> clazz) {
        final Type type = JavaBackedType.determineTypeFromClass(clazz);
        if (type instanceof org.kie.dmn.feel.lang.types.BuiltInType) {
            org.kie.dmn.feel.lang.types.BuiltInType builtIn = (org.kie.dmn.feel.lang.types.BuiltInType) type;
            switch (builtIn) {
                case UNKNOWN:
                case DURATION:
                case RANGE:
                case FUNCTION:
                case LIST:
                case UNARY_TEST:
                    return BuiltInType.ANY;
                case NUMBER:
                    return BuiltInType.NUMBER;
                case STRING:
                    return BuiltInType.STRING;
                case DATE:
                    return BuiltInType.DATE;
                case TIME:
                    return BuiltInType.TIME;
                case DATE_TIME:
                    return BuiltInType.DATE_TIME;
                case BOOLEAN:
                    return BuiltInType.BOOLEAN;
                case CONTEXT:
                    return BuiltInType.CONTEXT;
            }
        }
        return BuiltInType.ANY;
    }
}
