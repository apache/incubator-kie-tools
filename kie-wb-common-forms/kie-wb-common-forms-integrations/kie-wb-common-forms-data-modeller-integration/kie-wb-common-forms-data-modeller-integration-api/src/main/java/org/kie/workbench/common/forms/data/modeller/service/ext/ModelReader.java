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

package org.kie.workbench.common.forms.data.modeller.service.ext;

import java.util.Collection;

import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;

/**
 * Component to read {@link DataObjectFormModel} from a given module.
 */
public interface ModelReader {

    /**
     * Reads the {@link DataObjectFormModel} for a given type
     * @param typeName a String representing the full qualified name that we want to read the model
     * @return a {@link DataObjectFormModel} for the given type
     */
    DataObjectFormModel readFormModel(String typeName);

    /**
     * Reads all {@link DataObjectFormModel}, including dependencies.
     * @return a {@link Collection<DataObjectFormModel>} containing all available {@link DataObjectFormModel}
     */
    Collection<DataObjectFormModel> readAllFormModels();

    /**
     * Reads only the {@link DataObjectFormModel} contained on the module excluding dependencies
     * @return a {@link Collection<DataObjectFormModel>} containing the module {@link DataObjectFormModel}.
     */
    Collection<DataObjectFormModel> readModuleFormModels();
}
