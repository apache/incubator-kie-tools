/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.events;

import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;

@Portable
public class UpdateDriverEvent extends BaseDriverEvent {

    private DriverDef originalDriverDef;

    public UpdateDriverEvent(@MapsTo("driverDef") final DriverDef driverDef,
                             @MapsTo("module") final Module module,
                             @MapsTo("sessionId") final String sessionId,
                             @MapsTo("identity") final String identity,
                             @MapsTo("originalDriverDef") final DriverDef originalDriverDef) {
        super(driverDef,
              module,
              sessionId,
              identity);
        this.originalDriverDef = originalDriverDef;
    }

    public UpdateDriverEvent(final DriverDef driverDef,
                             final String sessionId,
                             final String identity,
                             final DriverDef originalDriverDef) {
        this(driverDef,
             null,
             sessionId,
             identity,
             originalDriverDef);
    }

    public UpdateDriverEvent(final DriverDef driverDef,
                             final String sessionId,
                             final String identity) {
        this(driverDef,
             sessionId,
             identity,
             null);
    }

    public UpdateDriverEvent(final DriverDef driverDef,
                             final Module module,
                             final String sessionId,
                             final String identity) {
        this(driverDef,
             module,
             sessionId,
             identity,
             null);
    }

    public DriverDef getOriginalDriverDef() {
        return originalDriverDef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UpdateDriverEvent that = (UpdateDriverEvent) o;

        return originalDriverDef != null ? originalDriverDef.equals(that.originalDriverDef) : that.originalDriverDef == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (originalDriverDef != null ? originalDriverDef.hashCode() : 0);
        result = ~~result;
        return result;
    }
}