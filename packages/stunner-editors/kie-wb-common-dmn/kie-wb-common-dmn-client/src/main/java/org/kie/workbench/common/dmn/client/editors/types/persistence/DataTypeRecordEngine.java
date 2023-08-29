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

import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

/**
 * A Data Type Record Engine persists a given data type.
 */
public interface DataTypeRecordEngine extends RecordEngine<DataType> {

    /**
     * Create record by using {@link CreationType} strategy. The new record can be created above, below, or even
     * nested to the reference.
     * @param record represents the new {@link DataType}
     * @param reference represents the reference {@link DataType} used by the 'creationType'
     * @param creationType represents the strategy for creating a new type.
     * @return a list of all affected records by the create operation.
     */
    List<DataType> create(final DataType record,
                          final DataType reference,
                          final CreationType creationType);

    /**
     * Destroy record, but keep all references
     * nested to the reference.
     * @param record represents the destroyed {@link DataType}
     * @return a list of all affected records by the destroy operation.
     */
    List<DataType> destroyWithoutDependentTypes(final DataType record);
}
