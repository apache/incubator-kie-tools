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
package org.drools.workbench.screens.scenariosimulation.webapp.client.workarounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.kogito.client.fakes.KogitoAsyncPackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class KogitoRuntimeAsyncPackageDataModelOracle implements KogitoAsyncPackageDataModelOracle {

    @Inject
    protected KogitoResourceContentService kogitoResourceContentService;

    private Path resourcePath;
    private Map<String, String> parametricFieldMap;
    private List<String> packageNames;
    private String[] factTypes;
    private String[] fqcnNames;
    private Map<String, String> fqcnNamesMap;
    private Map<String, Boolean> collectionTypes;
    private Map<String, ModelField[]> modelFieldsMap;

    @Override
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

    @Override
    public Path getResourcePath() {
        return resourcePath;
    }

    @Override
    public List<String> getPackageNames() {
        return packageNames;
    }

    @Override
    public String[] getFactTypes() {
        return factTypes;
    }

    @Override
    public String[] getAllFactTypes() {
        return factTypes;
    }

    @Override
    public String[] getInternalFactTypes() {
        return factTypes;
    }

    @Override
    public String[] getExternalFactTypes() {
        return new String[0];
    }

    @Override
    public String getFQCNByFactName(String factName) {
        return fqcnNamesMap.get(factName);
    }

    @Override
    public ModelField[] getFieldCompletions(String factType) {
        return modelFieldsMap.get(factType);
    }

    @Override
    public String getFieldType(String variableClass, String fieldName) {
        String toReturn = null;
        ModelField modelField = getModelField(variableClass, fieldName);
        if (modelField != null) {
            toReturn = modelField.getType();
        }
        return toReturn;
    }

    @Override
    public String getFieldClassName(String factName, String fieldName) {
        String toReturn = null;
        ModelField modelField = getModelField(factName, fieldName);
        if (modelField != null) {
            toReturn = modelField.getClassName();
        }
        return toReturn;
    }

    @Override
    public String getParametricFieldType(String factType, String fieldName) {
        String key = factType + "." + fieldName;
        return parametricFieldMap.get(key);
    }

    @Override
    public PackageDataModelOracleBaselinePayload getPackageDataModelOracleBaselinePayload() {
        PackageDataModelOracleBaselinePayload toReturn = new PackageDataModelOracleBaselinePayload();
        toReturn.setModelFields(modelFieldsMap);
        toReturn.setPackageName(packageNames.get(0));
        toReturn.setCollectionTypes(collectionTypes);
        toReturn.setFieldParametersType(parametricFieldMap);
        return toReturn;
    }

    private Map<String, String> retrieveParametricFieldMap() {
        return new HashMap<>();
    }

    private List<String> retrievePackageNames() {
        return new ArrayList<>();
    }

    private String[] retrieveFactTypes() {
        return new String[0];
    }

    private String[] retrieveFqcnNames() {
        return fqcnNames;
    }

    private Map<String, String> retrieveFqcnNamesMap() {
        return new HashMap<>();
    }

    private Map<String, Boolean> retrieveCollectionTypes() {
        return new HashMap<>();
    }

    private Map<String, ModelField[]> retrieveModelFieldsMap() {
        return new HashMap<>();
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
