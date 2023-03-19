/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.event.ContextualEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;

/**
 * <p>CDI event fired when a data set definition creation request is requested.</p>
 *
 * @since 0.6.0
 */
public class DataSetDefCreationRequestEvent extends ContextualEvent {

    DataSetProviderType providerType;

    public DataSetDefCreationRequestEvent(Object context, DataSetProviderType providerType) {
        super(context);
        this.providerType = providerType;
    }

    public DataSetProviderType getProviderType() {
        return providerType;
    }

    @Override
    public String toString() {
        return "DataSetDefCreationRequestEvent [Context=" + getContext().toString() + "]";
    }

}
