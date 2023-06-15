/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.external;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;

class DataSetReadyCallbackWrapper implements DataSetReadyCallback {

    private DataSetReadyCallback wrapped;

    public DataSetReadyCallbackWrapper(DataSetReadyCallback wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void callback(DataSet dataSet) {
        wrapped.callback(dataSet);

    }

    @Override
    public void notFound() {
        wrapped.notFound();

    }

    @Override
    public boolean onError(ClientRuntimeError error) {
        return wrapped.onError(error);
    }

}
