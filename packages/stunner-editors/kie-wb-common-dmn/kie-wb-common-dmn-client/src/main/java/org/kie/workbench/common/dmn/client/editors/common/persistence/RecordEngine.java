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

package org.kie.workbench.common.dmn.client.editors.common.persistence;

import java.util.List;

/**
 * A Record Engine persists a given record.
 */
public interface RecordEngine<T> {

    /**
     * Updates a record
     * @param record
     * @return returning a list of all affected records by the update operation.
     */
    List<T> update(final T record);

    /**
     * Destroys a record
     * @param record
     * @return returning a list of all affected records by the destroy operation.
     */
    List<T> destroy(final T record);

    /**
     * Create a record.
     * @param record
     * @return returning a list of all affected records by the create operation.
     */
    List<T> create(final T record);

    /**
     * Check if a record is valid.
     * @param record
     */
    boolean isValid(final T record);
}
