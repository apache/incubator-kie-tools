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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.runtime;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReader;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.util.DMOModelResolver;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeDMOModelReader implements ModelReader {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeDMOModelReader.class);

    private ClassLoader classLoader;
    private MVELEvaluator evaluator;

    public RuntimeDMOModelReader(ClassLoader classLoader, MVELEvaluator evaluator) {
        this.classLoader = classLoader;
        this.evaluator = evaluator;
    }

    @Override
    public DataObjectFormModel readFormModel(String typeName) {
        return DMOModelResolver.resolveModelForType(getModuleOracle(typeName), typeName);
    }

    @Override
    public Collection<DataObjectFormModel> readModuleFormModels() {
        throw new RuntimeException("Unsupported operation: RuntimeDMOModelReader doesn't support listing models");
    }

    @Override
    public Collection<DataObjectFormModel> readAllFormModels() {
        throw new RuntimeException("Unsupported operation: RuntimeDMOModelReader doesn't support listing all models");
    }

    private ModuleDataModelOracle getModuleOracle(String typeName) {
        Class<?> clazz = null;

        try {
            clazz = classLoader.loadClass(typeName);
            if (clazz == null) {
                clazz = getClass().forName(typeName);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Unable to extract model for class '{}'.", typeName);
            throw new IllegalStateException("Unable to extract model for class '" + typeName + "'");
        }

        if (clazz == null) {
            logger.error("Unable to extract model for class '{}'.", typeName);
            throw new IllegalStateException("Unable to extract model for class '" + typeName + "'");
        }

        try {
            final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(evaluator);


            final ClassFactBuilder modelFactBuilder = new ClassFactBuilder(builder, clazz, false, type -> TypeSource.JAVA_PROJECT);

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
            logger.warn("Couldn't inspect model {} due to: {}", typeName, ex);
        }
        return null;
    }
}
