/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.events;

import org.guvnor.ala.ui.model.RuntimeKey;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Event for notifying changes in a Runtime.
 * @see RuntimeChange
 */
@Portable
public class RuntimeChangeEvent {

    private RuntimeChange change;

    private RuntimeKey runtimeKey;

    public RuntimeChangeEvent(@MapsTo("change") final RuntimeChange change,
                              @MapsTo("runtimeKey") final RuntimeKey runtimeKey) {
        this.change = change;
        this.runtimeKey = runtimeKey;
    }

    public RuntimeChange getChange() {
        return change;
    }

    public RuntimeKey getRuntimeKey() {
        return runtimeKey;
    }

    public boolean isDelete() {
        return change == RuntimeChange.DELETED;
    }

    public boolean isStart() {
        return change == RuntimeChange.STARTED;
    }

    public boolean isStop() {
        return change == RuntimeChange.STOPPED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuntimeChangeEvent that = (RuntimeChangeEvent) o;

        if (change != that.change) {
            return false;
        }
        return runtimeKey != null ? runtimeKey.equals(that.runtimeKey) : that.runtimeKey == null;
    }

    @Override
    public int hashCode() {
        int result = change != null ? change.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (runtimeKey != null ? runtimeKey.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
