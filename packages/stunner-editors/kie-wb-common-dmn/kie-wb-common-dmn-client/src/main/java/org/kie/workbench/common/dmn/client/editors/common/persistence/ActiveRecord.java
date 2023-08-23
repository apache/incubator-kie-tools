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
import java.util.Optional;

/**
 * Implements base operations for an Active Record (update, destroy and create).
 */
public abstract class ActiveRecord<T> {

    private RecordEngine<T> recordEngine;

    public ActiveRecord(final RecordEngine<T> recordEngine) {
        this.recordEngine = recordEngine;
    }

    public List<T> update() {
        return getRecordEngine().update(getRecord());
    }

    public List<T> destroy() {
        return getRecordEngine().destroy(getRecord());
    }

    public List<T> create() {
        return getRecordEngine().create(getRecord());
    }

    public boolean isValid() {
        return getRecordEngine().isValid(getRecord());
    }

    public RecordEngine<T> getRecordEngine() {
        return getOptionalRecordEngine().orElseThrow(this::error);
    }

    public boolean isRecordEnginePresent() {
        return getOptionalRecordEngine().isPresent();
    }

    private Optional<RecordEngine<T>> getOptionalRecordEngine() {
        return Optional.ofNullable(recordEngine);
    }

    protected abstract T getRecord();

    private UnsupportedOperationException error() {
        final String errorMessage = "'ActiveRecord' operations are not supported. Please provide a record engine.";
        return new UnsupportedOperationException(errorMessage);
    }
}
