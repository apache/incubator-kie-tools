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

package org.dashbuilder.dataset.def;

/**
 * A builder for defining data sets that runs externally to this application VM
 *
 * <pre>
 *    DataSetDef dataSetDef = DataSetDefFactory.newExternalDataSetDef()
 *     .uuid("all_employees")
 *     .url("http://datasets.com/dataset")
 *     .buildDef();
 * </pre>
 */
public interface ExternalDataSetDefBuilder<T extends DataSetDefBuilder> extends DataSetDefBuilder<T> {

    /**
     * Set the external data set url.
     *
     * @param url The url of a external dataset 
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T url(String url);

    /**
     * Set if this is weather a dynamic or passive dataset. 
     * When true, the external provider will let the external dataset make dataset operations.
     * Default value is false.
     *
     * @param dynamic When true this will be consided as a dynamic dataset
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T dynamic(boolean dynamic);
}
