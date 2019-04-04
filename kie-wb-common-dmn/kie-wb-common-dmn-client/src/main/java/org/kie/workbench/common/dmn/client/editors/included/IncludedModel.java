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

package org.kie.workbench.common.dmn.client.editors.included;

import org.kie.workbench.common.dmn.client.editors.common.persistence.ActiveRecord;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;

public class IncludedModel extends ActiveRecord<IncludedModel> {

    private String uuid;

    private String namespace;

    private String path;

    private String name;

    private Integer dataTypesCount;

    private Integer drgElementsCount;

    public IncludedModel(final RecordEngine<IncludedModel> recordEngine) {
        super(recordEngine);
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDataTypesCount() {
        return dataTypesCount;
    }

    public void setDataTypesCount(final Integer dataTypesCount) {
        this.dataTypesCount = dataTypesCount;
    }

    public Integer getDrgElementsCount() {
        return drgElementsCount;
    }

    public void setDrgElementsCount(final Integer drgElementsCount) {
        this.drgElementsCount = drgElementsCount;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    protected IncludedModel getRecord() {
        return this;
    }
}
