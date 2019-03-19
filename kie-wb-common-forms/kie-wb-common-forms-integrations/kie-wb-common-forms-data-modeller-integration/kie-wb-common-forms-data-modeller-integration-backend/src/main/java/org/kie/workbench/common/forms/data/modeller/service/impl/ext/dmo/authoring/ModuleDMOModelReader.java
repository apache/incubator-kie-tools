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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.authoring;

import java.util.Collection;

import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReader;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.util.DMOModelResolver;

public class ModuleDMOModelReader implements ModelReader {

    private ModuleDataModelOracle oracle;

    public ModuleDMOModelReader(ModuleDataModelOracle oracle) {
        this.oracle = oracle;
    }

    @Override
    public DataObjectFormModel readFormModel(String typeName) {
        return DMOModelResolver.resolveModelForType(oracle, typeName);
    }

    @Override
    public Collection<DataObjectFormModel> readAllFormModels() {
        return DMOModelResolver.resolveAllFormModels(oracle);
    }

    @Override
    public Collection<DataObjectFormModel> readModuleFormModels() {
        return DMOModelResolver.resolveModuleFormModels(oracle);
    }
}
