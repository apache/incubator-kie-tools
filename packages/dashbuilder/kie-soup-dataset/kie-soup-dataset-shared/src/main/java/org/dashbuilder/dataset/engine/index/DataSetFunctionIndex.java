/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.engine.index;

import org.dashbuilder.dataset.impl.MemSizeEstimator;

/**
 * An aggregate function value index
 */
public class DataSetFunctionIndex extends DataSetIndexElement {

    Object value = null;

    public DataSetFunctionIndex(Object value, long buildTime) {
        super(buildTime);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getEstimatedSize() {
        long result = super.getEstimatedSize();
        if (value != null) {
            result += MemSizeEstimator.sizeOfDouble;
        }
        return result;
    }
}

