/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.dmn.client.editors.types.persistence.ActiveRecord;
import org.kie.workbench.common.dmn.client.editors.types.persistence.RecordEngine;
import org.uberfire.commons.uuid.UUID;

public class DataType extends ActiveRecord<DataType> {

    private String uuid;

    private String parentUUID;

    private String name;

    private String type;

    private List<DataType> subDataTypes;

    private boolean isBasic;

    private boolean isExternal;

    private boolean isDefault;

    public DataType(final String uuid,
                    final String parentUUID,
                    final String name,
                    final String type,
                    final List<DataType> subDataTypes,
                    final boolean isBasic,
                    final boolean isExternal,
                    final boolean isDefault,
                    final RecordEngine<DataType> recordEngine) {
        super(recordEngine);

        this.uuid = uuid;
        this.parentUUID = parentUUID;
        this.name = name;
        this.type = type;
        this.subDataTypes = subDataTypes;
        this.isBasic = isBasic;
        this.isExternal = isExternal;
        this.isDefault = isDefault;
    }

    public DataType(final String name,
                    final String type) {

        this.uuid = UUID.uuid();
        this.name = name;
        this.type = type;
        this.subDataTypes = new ArrayList<>();
        this.isBasic = false;
        this.isExternal = false;
        this.isDefault = true;
    }

    public String getUUID() {
        return uuid;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<DataType> getSubDataTypes() {
        return subDataTypes;
    }

    public boolean isBasic() {
        return isBasic;
    }

    public void setBasic(final boolean isBasic) {
        this.isBasic = isBasic;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(final boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public boolean hasSubDataTypes() {
        return !subDataTypes.isEmpty();
    }

    @Override
    protected DataType getRecord() {
        return this;
    }
}
