/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaEnum;
import org.kie.workbench.common.services.datamodeller.core.ObjectSource;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.JavaEnumImpl;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectDataModelOracleUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDataModelOracleUtils.class);

    public static void loadExternalDependencies(DataModel dataModel,
                                                ProjectDataModelOracle projectDataModelOracle, ClassLoader classLoader) throws ModelDriverException {

        String[] factTypes = DataModelOracleUtilities.getFactTypes(projectDataModelOracle);
        ObjectSource source;

        if (factTypes != null) {
            for (int i = 0; i < factTypes.length; i++) {
                source = factSource(projectDataModelOracle, factTypes[i]);
                if (source != null && ObjectSource.DEPENDENCY.equals(source)) {
                    addType(dataModel, projectDataModelOracle, factTypes[i], classLoader);
                }
            }
        }
    }

    private static void addType(DataModel dataModel,
                                ProjectDataModelOracle oracleDataModel,
                                String factType,
                                ClassLoader classLoader) throws ModelDriverException {

        ClassMetadata classMetadata = readClassMetadata(factType, classLoader);
        if (classMetadata != null && !classMetadata.isMemberClass() && !classMetadata.isAnonymousClass()
                && !classMetadata.isLocalClass()) {
            if (classMetadata.isEnumClass()) {
                addEnumType(dataModel, factType, classMetadata);
            } else {
                addDataObjectType(dataModel, oracleDataModel, factType, classMetadata);
            }
        }
    }

    private static void addDataObjectType(DataModel dataModel,
                                          ProjectDataModelOracle oracleDataModel, String factType,
                                          ClassMetadata classMetadata) throws ModelDriverException {

        String superClass = DataModelOracleUtilities.getSuperType(oracleDataModel, factType);
        Visibility visibility = DriverUtils.buildVisibility(classMetadata.getModifiers());
        DataObject dataObject;

        logger.debug("Adding dataObjectType: " + factType + ", to dataModel: " + dataModel +
                             ", from oracleDataModel: " + oracleDataModel);

        dataObject = dataModel.addDataObject(factType,
                                             visibility,
                                             Modifier.isAbstract(classMetadata.getModifiers()),
                                             Modifier.isFinal(classMetadata.getModifiers()),
                                             ObjectSource.DEPENDENCY);

        dataObject.setSuperClassName(superClass);

        Map<String, ModelField[]> fields = oracleDataModel.getProjectModelFields();
        if (fields != null) {
            ModelField[] factFields = fields.get(factType);
            ModelField field;

            if (factFields != null && factFields.length > 0) {
                for (int j = 0; j < factFields.length; j++) {
                    field = factFields[j];
                    if (isLoadableField(field)) {

                        if (field.getType().equals("Collection")) {
                            //read the correct bag and item classes.
                            String bag = DataModelOracleUtilities.getFieldClassName(oracleDataModel,
                                                                                    factType,
                                                                                    field.getName());
                            String itemsClass = DataModelOracleUtilities.getParametricFieldType(oracleDataModel,
                                                                                                factType,
                                                                                                field.getName());
                            if (itemsClass == null) {
                                //if we don't know the items class, the property will be managed as a simple property.
                                dataObject.addProperty(field.getName(), bag);
                            } else {
                                dataObject.addProperty(field.getName(), itemsClass, true, bag);
                            }
                        } else {
                            dataObject.addProperty(field.getName(), field.getClassName());
                        }
                    }
                }
            }
        } else {
            logger.debug("No fields found for factTye: " + factType);
        }
    }

    private static void addEnumType(DataModel dataModel, String factType, ClassMetadata classMetadata) {

        String packageName = NamingUtils.extractPackageName(factType);
        String className = NamingUtils.extractClassName(factType);
        Visibility visibility = DriverUtils.buildVisibility(classMetadata.getModifiers());

        JavaEnum javaEnum = new JavaEnumImpl(packageName, className, visibility);
        dataModel.addJavaEnum(javaEnum, ObjectSource.DEPENDENCY);
    }

    private static ClassMetadata readClassMetadata(String factType, ClassLoader classLoader) {
        try {
            Class _class = classLoader.loadClass(factType);
            return new ClassMetadata(_class.getModifiers(),
                                     _class.isMemberClass(), _class.isLocalClass(), _class.isAnonymousClass(), _class.isEnum());
        } catch (ClassNotFoundException e) {
            logger.error("It was not possible to read class metadata for class: " + factType);
        }
        return null;
    }

    private static ObjectSource factSource(ProjectDataModelOracle oracleDataModel,
                                           String factType) {

        TypeSource oracleType = DataModelOracleUtilities.getTypeSource(oracleDataModel,
                                                                       factType);
        if (TypeSource.JAVA_PROJECT.equals(oracleType)) {
            return ObjectSource.INTERNAL;
        } else if (TypeSource.JAVA_DEPENDENCY.equals(oracleType)) {
            return ObjectSource.DEPENDENCY;
        }
        return null;
    }

    /**
     * Indicates if this field should be loaded or not.
     * Some fields like a filed with name "this" shouldn't be loaded.
     */
    private static boolean isLoadableField(ModelField field) {
        return (field.getOrigin().equals(ModelField.FIELD_ORIGIN.DECLARED));
    }

    static class ClassMetadata {

        int modifiers;

        boolean memberClass;

        boolean localClass;

        boolean anonymousClass;

        boolean enumClass;

        public ClassMetadata(int modifiers,
                             boolean memberClass, boolean localClass, boolean anonymousClass, boolean enumClass) {
            this.modifiers = modifiers;
            this.memberClass = memberClass;
            this.localClass = localClass;
            this.anonymousClass = anonymousClass;
            this.enumClass = enumClass;
        }

        public int getModifiers() {
            return modifiers;
        }

        public void setModifiers(int modifiers) {
            this.modifiers = modifiers;
        }

        public boolean isMemberClass() {
            return memberClass;
        }

        public void setMemberClass(boolean memberClass) {
            this.memberClass = memberClass;
        }

        public boolean isLocalClass() {
            return localClass;
        }

        public void setLocalClass(boolean localClass) {
            this.localClass = localClass;
        }

        public boolean isAnonymousClass() {
            return anonymousClass;
        }

        public void setAnonymousClass(boolean anonymousClass) {
            this.anonymousClass = anonymousClass;
        }

        public boolean isEnumClass() {
            return enumClass;
        }

        public void setEnumClass(boolean enumClass) {
            this.enumClass = enumClass;
        }
    }
}