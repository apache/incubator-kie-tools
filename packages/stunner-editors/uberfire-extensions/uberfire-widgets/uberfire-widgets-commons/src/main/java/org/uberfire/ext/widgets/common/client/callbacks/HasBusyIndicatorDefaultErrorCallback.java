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

package org.uberfire.ext.widgets.common.client.callbacks;

import java.util.Objects;

import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

/**
 * Default Error handler for all views that support HasBusyIndicator
 */
public class HasBusyIndicatorDefaultErrorCallback extends DefaultErrorCallback {

    protected HasBusyIndicator view;

    public HasBusyIndicatorDefaultErrorCallback(final HasBusyIndicator view) {
        this.view = Objects.requireNonNull(view, "Parameter named 'view' should be not null!");
    }

    @Override
    public boolean error(final Object message,
                         final Throwable throwable) {
        view.hideBusyIndicator();
        return super.error(message,
                           throwable);
    }

    public void hideBusyIndicator() {
        view.hideBusyIndicator();
    }
}
