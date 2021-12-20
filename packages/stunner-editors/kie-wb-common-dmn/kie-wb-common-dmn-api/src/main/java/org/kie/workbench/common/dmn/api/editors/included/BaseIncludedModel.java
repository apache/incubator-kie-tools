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
package org.kie.workbench.common.dmn.api.editors.included;

public abstract class BaseIncludedModel implements IncludedModel {

    private String modelName;

    private String modelPackage;

    private String path;

    private String importType;

    public BaseIncludedModel(final String modelName,
                             final String modelPackage,
                             final String path,
                             final String importType) {
        this.modelName = modelName;
        this.modelPackage = modelPackage;
        this.path = path;
        this.importType = importType;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public String getModelPackage() {
        return modelPackage;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getImportType() {
        return importType;
    }
}
