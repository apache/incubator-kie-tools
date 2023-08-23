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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.List;

import org.kie.workbench.common.dmn.client.editors.common.persistence.ActiveRecord;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

/**
 * Implements base specific operations for a Data Type Active Record.
 */
public abstract class DataTypeActiveRecord extends ActiveRecord<DataType> {

    public DataTypeActiveRecord(final RecordEngine<DataType> recordEngine) {
        super(recordEngine);
    }

    public List<DataType> create(final DataType reference,
                                 final CreationType creationType) {
        return getDataTypeRecordEngine().create(getRecord(), reference, creationType);
    }

    public List<DataType> destroyWithoutDependentTypes() {
        return getDataTypeRecordEngine().destroyWithoutDependentTypes(getRecord());
    }

    private DataTypeRecordEngine getDataTypeRecordEngine() {
        return (DataTypeRecordEngine) getRecordEngine();
    }
}
