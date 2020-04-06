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

import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.vfs.Path;

/**
 * Interface provide to emulate the "Oracle" implementation used inside Business Central
 */
public interface KogitoAsyncPackageDataModelOracle {

    void init(Path resourcePath);

    Path getResourcePath();

    List<String> getPackageNames();

    String[] getFactTypes();

    String[] getAllFactTypes();

    String[] getInternalFactTypes();

    String[] getExternalFactTypes();

    String getFQCNByFactName(String factName);

    ModelField[] getFieldCompletions(String factType);

    String getFieldType(String variableClass, String fieldName);

    String getFieldClassName(String factName, String fieldName);

    String getParametricFieldType(String factType, String fieldName);

    PackageDataModelOracleBaselinePayload getPackageDataModelOracleBaselinePayload();
    
}
