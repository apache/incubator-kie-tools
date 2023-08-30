/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.included;

import org.kie.workbench.common.dmn.client.editors.common.persistence.ActiveRecord;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;

import static org.uberfire.commons.UUID.uuid;

public abstract class BaseIncludedModelActiveRecord extends ActiveRecord<BaseIncludedModelActiveRecord> {

    private String uuid;

    private String namespace;

    private String importType;

    private String path;

    private String name;

    public BaseIncludedModelActiveRecord(final RecordEngine<BaseIncludedModelActiveRecord> recordEngine) {
        super(recordEngine);
        setUuid(uuid());
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(final String importType) {
        this.importType = importType;
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

    public String getUUID() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    protected BaseIncludedModelActiveRecord getRecord() {
        return this;
    }
}
