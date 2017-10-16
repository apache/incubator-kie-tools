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

package org.kie.workbench.common.services.datamodel.backend.server.builder.packages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.soup.project.datamodel.commons.oracle.PackageDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ExtensionKind;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.GlobalsParser;
import org.uberfire.commons.data.Pair;

/**
 * Builder for PackageDataModelOracle
 */
public final class PackageDataModelOracleBuilder {

    private final String packageName;

    private PackageDataModelOracleImpl packageOracle = new PackageDataModelOracleImpl();
    private ProjectDataModelOracle projectOracle = new ProjectDataModelOracleImpl();

    private Map<String, String[]> factFieldEnums = new HashMap<>();
    private Map<ExtensionKind<?>, List<?>> extensions = new HashMap<>();

    // Package-level map of Globals (name is key) and their type (value).
    private Map<String, String> packageGlobalTypes = new HashMap<>();

    private MVELEvaluator evaluator;

    public static PackageDataModelOracleBuilder newPackageOracleBuilder(final MVELEvaluator evaluator) {
        return new PackageDataModelOracleBuilder("", evaluator);
    }

    public static PackageDataModelOracleBuilder newPackageOracleBuilder(final MVELEvaluator evaluator, final String packageName) {
        return new PackageDataModelOracleBuilder(packageName, evaluator);
    }

    private PackageDataModelOracleBuilder(final String packageName, final MVELEvaluator evaluator) {
        this.packageName = packageName;
        this.evaluator = evaluator;
    }

    public PackageDataModelOracleBuilder setProjectOracle(final ProjectDataModelOracle projectOracle) {
        this.projectOracle = projectOracle;
        return this;
    }

    public PackageDataModelOracleBuilder addEnum(final String factType,
                                                 final String fieldName,
                                                 final String[] values) {
        final String qualifiedFactField = factType + "#" + fieldName;
        factFieldEnums.put(qualifiedFactField,
                           values);
        return this;
    }

    public PackageDataModelOracleBuilder addEnum(final String enumDefinition,
                                                 final ClassLoader classLoader) {
        parseEnumDefinition(enumDefinition,
                            classLoader,
                            evaluator);
        return this;
    }

    private void parseEnumDefinition(final String enumDefinition,
                                     final ClassLoader classLoader,
                                     final MVELEvaluator evaluator) {
        final DataEnumLoader enumLoader = new DataEnumLoader(enumDefinition,
                                                             classLoader,
                                                             evaluator);
        if (!enumLoader.hasErrors()) {
            factFieldEnums.putAll(enumLoader.getData());
        }
    }

    public PackageDataModelOracleBuilder addGlobals(final String definition) {
        List<Pair<String, String>> globals = GlobalsParser.parseGlobals(definition);
        for (Pair<String, String> g : globals) {
            packageGlobalTypes.put(g.getK1(),
                                   g.getK2());
        }
        return this;
    }

    public PackageDataModelOracle build() {
        //Copy Project DMO into Package DMO
        final ProjectDataModelOracleImpl pd = (ProjectDataModelOracleImpl) projectOracle;
        packageOracle.addProjectModelFields(pd.getProjectModelFields());
        packageOracle.addProjectFieldParametersType(pd.getProjectFieldParametersType());
        packageOracle.addProjectJavaEnumDefinitions(pd.getProjectJavaEnumDefinitions());
        packageOracle.addProjectMethodInformation(pd.getProjectMethodInformation());
        packageOracle.addProjectCollectionTypes(pd.getProjectCollectionTypes());
        packageOracle.addProjectEventTypes(pd.getProjectEventTypes());
        packageOracle.addProjectTypeSources(pd.getProjectTypeSources());
        packageOracle.addProjectSuperTypes(pd.getProjectSuperTypes());
        packageOracle.addProjectTypeAnnotations(pd.getProjectTypeAnnotations());
        packageOracle.addProjectTypeFieldsAnnotations(pd.getProjectTypeFieldsAnnotations());
        packageOracle.addProjectPackageNames(pd.getProjectPackageNames());

        //Add Package DMO specifics
        loadEnums();
        loadPackageElements();
        loadGlobals();
        loadProjectOracle();

        return packageOracle;
    }

    private void loadProjectOracle() {
        packageOracle.setPackageName(packageName);
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<>();
        for (Map.Entry<String, String[]> e : factFieldEnums.entrySet()) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put(qualifiedFactField,
                              e.getValue());
        }
        packageOracle.addPackageWorkbenchEnumDefinitions(loadableEnums);
    }

    private void loadPackageElements() {
        packageOracle.addExtensions(extensions);
    }

    private void loadGlobals() {
        packageOracle.addPackageGlobals(packageGlobalTypes);
    }

    @SuppressWarnings("unchecked")
    public PackageDataModelOracleBuilder addExtension(ExtensionKind<?> kind, List<?> values) {
        @SuppressWarnings("rawtypes")
        List list = extensions.computeIfAbsent(kind, k -> new ArrayList<>());
        list.addAll(values);
        return this;
    }
}
