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

package org.kie.workbench.common.dmn.client.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;

public abstract class DMNServiceClient {

    protected final DMNClientServicesProxy clientServicesProxy;

    public DMNServiceClient(final DMNClientServicesProxy clientServicesProxy) {
        this.clientServicesProxy = clientServicesProxy;
    }

    public <T> ServiceCallback<List<T>> callback(final Consumer<List<T>> consumer) {
        return new ServiceCallback<List<T>>() {
            @Override
            public void onSuccess(final List<T> item) {
                consumer.accept(item);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                getClientServicesProxy().logWarning(error);
                consumer.accept(new ArrayList<>());
            }
        };
    }

    DMNClientServicesProxy getClientServicesProxy() {
        return clientServicesProxy;
    }
}
