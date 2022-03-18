/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.callbacks;

import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Default Error handler for all views that support HasBusyIndicator
 */
public class HasBusyIndicatorDefaultErrorCallback extends DefaultErrorCallback {

    protected HasBusyIndicator view;

    public HasBusyIndicatorDefaultErrorCallback(final HasBusyIndicator view) {
        this.view = checkNotNull("view",
                                 view);
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
