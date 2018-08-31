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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.Optional;

public abstract class ActiveRecord<T> {

    private RecordEngine<T> recordEngine;

    public ActiveRecord() {
    }

    public ActiveRecord(final RecordEngine<T> recordEngine) {
        this.recordEngine = recordEngine;
    }

    public void update() {
        getRecordEngine().update(getRecord());
    }

    public void destroy() {
        getRecordEngine().destroy(getRecord());
    }

    public void create() {
        getRecordEngine().create(getRecord());
    }

    public RecordEngine<T> getRecordEngine() {
        return Optional.ofNullable(recordEngine).orElseThrow(this::error);
    }

    protected abstract T getRecord();

    private UnsupportedOperationException error() {
        final String errorMessage = "'ActiveRecord' operations are not supported. Please provide a record engine.";
        return new UnsupportedOperationException(errorMessage);
    }
}
