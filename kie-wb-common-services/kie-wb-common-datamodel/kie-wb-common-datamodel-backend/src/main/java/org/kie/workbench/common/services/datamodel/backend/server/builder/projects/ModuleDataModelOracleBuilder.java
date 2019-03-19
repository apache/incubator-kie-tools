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

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;

/**
 * Builder for DataModelOracle
 */
public final class ModuleDataModelOracleBuilder {

    private ModuleDataModelOracleImpl oracle = new ModuleDataModelOracleImpl();

    private Map<String, FactBuilder> factTypeBuilders = new HashMap<>();
    private Map<String, String[]> factFieldEnums = new HashMap<>();
    private List<String> packageNames = new ArrayList<>();

    private final Map<String, FactBuilder> discoveredFieldFactBuilders = new HashMap<>();

    private List<String> errors = new ArrayList<>();

    private MVELEvaluator evaluator;

    public static ModuleDataModelOracleBuilder newModuleOracleBuilder(MVELEvaluator evaluator) {
        return new ModuleDataModelOracleBuilder(evaluator);
    }

    private ModuleDataModelOracleBuilder(MVELEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    //Used by tests
    public SimpleFactBuilder addFact(final String factType) {
        return addFact(factType,
                       false);
    }

    //Used by tests
    public SimpleFactBuilder addFact(final String factType,
                                     final boolean isEvent) {
        return addFact(factType,
                       isEvent,
                       TypeSource.JAVA_PROJECT);
    }

    //Used by tests
    public SimpleFactBuilder addFact(final String factType,
                                     final boolean isEvent,
                                     final TypeSource typeSource) {
        final SimpleFactBuilder builder = new SimpleFactBuilder(this,
                                                                factType,
                                                                isEvent,
                                                                type -> typeSource);
        factTypeBuilders.put(factType,
                             builder);
        return builder;
    }

    //Used by tests
    public ModuleDataModelOracleBuilder addClass(final Class clazz) throws IOException {
        return addClass(clazz,
                        false);
    }

    //Used by tests
    public ModuleDataModelOracleBuilder addClass(final Class clazz,
                                                 final boolean isEvent) throws IOException {
        return addClass(clazz,
                        isEvent,
                        type -> TypeSource.JAVA_PROJECT);
    }

    public ModuleDataModelOracleBuilder addClass(final Class clazz,
                                                 final boolean isEvent,
                                                 final Function<String, TypeSource> resolver) throws IOException {
        final FactBuilder builder = new ClassFactBuilder(this,
                                                         discoveredFieldFactBuilders,
                                                         clazz,
                                                         isEvent,
                                                         resolver);
        factTypeBuilders.put(clazz.getName(),
                             builder);
        return this;
    }

    public ModuleDataModelOracleBuilder addEnum(final String factType,
                                                final String fieldName,
                                                final String[] values) {
        final String qualifiedFactField = factType + "#" + fieldName;
        factFieldEnums.put(qualifiedFactField,
                           values);
        return this;
    }

    public ModuleDataModelOracleBuilder addEnum(final String enumDefinition,
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
        if (enumLoader.hasErrors()) {
            logEnumErrors(enumLoader);
        } else {
            factFieldEnums.putAll(enumLoader.getData());
        }
    }

    private void logEnumErrors(final DataEnumLoader enumLoader) {
        errors.addAll(enumLoader.getErrors());
    }

    public ModuleDataModelOracle build() {
        loadFactTypes();
        loadEnums();
        loadPackageNames();

        return oracle;
    }

    private void loadPackageNames() {
        oracle.addModulePackageNames(packageNames);
    }

    private void loadFactTypes() {

        for (final FactBuilder factBuilder : new ArrayList<>(this.factTypeBuilders.values())) {
            this.factTypeBuilders.putAll(factBuilder.getInternalBuilders());
        }

        for (final FactBuilder factBuilder : this.factTypeBuilders.values()) {
            factBuilder.build(oracle);
        }
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<>();
        for (Map.Entry<String, String[]> e : factFieldEnums.entrySet()) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put(qualifiedFactField,
                              e.getValue());
        }
        oracle.addModuleJavaEnumDefinitions(loadableEnums);
    }

    public void addPackage(String packageName) {
        this.packageNames.add(packageName);
    }

    public void addPackages(Collection<String> packageNames) {
        this.packageNames.addAll(packageNames);
    }
}
