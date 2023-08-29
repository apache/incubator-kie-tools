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

package org.dashbuilder.dataset.client;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;

/**
 * Used then users do not want to implement all methods from DataSetReadyCallback 
 *
 */
public class DataSetReadyCallbackAdapter implements DataSetReadyCallback {

    @Override
    public void callback(DataSet dataSet) {
        // empty

    }

    @Override
    public void notFound() {
        // empty

    }

    @Override
    public boolean onError(ClientRuntimeError error) {
        // empty
        return false;
    }

}
