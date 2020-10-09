/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.vfs.Path;

/**
 * It provides an emulatation to the "Oracle" implementation used inside Business Central
 */
public abstract class KogitoAsyncPackageDataModelOracle {

    protected Path resourcePath;
    protected Map<String, String> parametricFieldMap;
    protected List<String> packageNames;
    protected String[] factTypes;
    protected String[] fqcnNames;
    protected Map<String, String> fqcnNamesMap;
    protected Map<String, Boolean> collectionTypes;
    protected Map<String, ModelField[]> modelFieldsMap;

    public void init(Path resourcePath) {
        this.resourcePath = resourcePath;
        this.parametricFieldMap = retrieveParametricFieldMap();
        this.packageNames = retrievePackageNames();
        this.factTypes = retrieveFactTypes();
        this.fqcnNames = retrieveFqcnNames();
        this.fqcnNamesMap = retrieveFqcnNamesMap();
        this.collectionTypes = retrieveCollectionTypes();
        this.modelFieldsMap = retrieveModelFieldsMap();
    }

    protected abstract Map<String, ModelField[]> retrieveModelFieldsMap();

    protected abstract Map<String, Boolean> retrieveCollectionTypes();

    protected abstract Map<String, String> retrieveFqcnNamesMap();

    protected abstract String[] retrieveFqcnNames();

    protected abstract String[] retrieveFactTypes();

    protected abstract List<String> retrievePackageNames();

    protected abstract Map<String, String> retrieveParametricFieldMap();

    public Path getResourcePath() {
        return resourcePath;
    }

    public List<String> getPackageNames() {
        return packageNames;
    }

    public String[] getFactTypes() {
        return factTypes;
    }

    public String[] getAllFactTypes() {
        return factTypes;
    }

    public String[] getInternalFactTypes() {
        return factTypes;
    }

    public String[] getExternalFactTypes() {
        return new String[0];
    }

    public String getFQCNByFactName(String factName) {
        return fqcnNamesMap.get(factName);
    }

    public ModelField[] getFieldCompletions(String factType) {
        return modelFieldsMap.get(factType);
    }

    public String getFieldType(String variableClass, String fieldName) {
        String toReturn = null;
        ModelField modelField = getModelField(variableClass, fieldName);
        if (modelField != null) {
            toReturn = modelField.getType();
        }
        return toReturn;
    }

    public String getFieldClassName(String factName, String fieldName) {
        String toReturn = null;
        ModelField modelField = getModelField(factName, fieldName);
        if (modelField != null) {
            toReturn = modelField.getClassName();
        }
        return toReturn;
    }

    public String getParametricFieldType(String factType, String fieldName) {
        String key = factType + "." + fieldName;
        return parametricFieldMap.get(key);
    }

    public PackageDataModelOracleBaselinePayload getPackageDataModelOracleBaselinePayload() {
        PackageDataModelOracleBaselinePayload toReturn = new PackageDataModelOracleBaselinePayload();
        toReturn.setModelFields(modelFieldsMap);
        toReturn.setPackageName(packageNames.get(0));
        toReturn.setCollectionTypes(collectionTypes);
        toReturn.setFieldParametersType(parametricFieldMap);
        return toReturn;
    }

    private ModelField getModelField(String factName, String fieldName) {
        ModelField toReturn = null;
        if (modelFieldsMap.containsKey(factName)) {
            final ModelField[] modelFields = modelFieldsMap.get(factName);
            for (ModelField modelField : modelFields) {
                if (Objects.equals(modelField.getName(), fieldName)) {
                    toReturn = modelField;
                    break;
                }
            }
        }
        return toReturn;
    }
    
}
