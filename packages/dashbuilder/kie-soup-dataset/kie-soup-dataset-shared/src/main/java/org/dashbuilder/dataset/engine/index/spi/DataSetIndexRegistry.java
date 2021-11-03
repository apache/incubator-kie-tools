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
package org.dashbuilder.dataset.engine.index.spi;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.engine.index.DataSetIndex;

/**
 * Maintains a registry of data set indexes
 */
public interface DataSetIndexRegistry {

    /**
     * Index the given data set.
     */
    DataSetIndex put(DataSet dataSet);

    /**
     * Get the index for the specified data set.
     * @param uuid The data set unique identifier.
     */
    DataSetIndex get(String uuid);

    /**
     * Removes the index for the specified data set.
     * @param uuid The data set unique identifier.
     * @return The removed index or <tt>null</tt> if there was no mapping for <tt>uuid</tt>.
     */
    DataSetIndex remove(String uuid);
}

