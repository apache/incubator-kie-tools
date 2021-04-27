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

package org.uberfire.ext.editor.commons.client.event;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.workbench.events.UberFireEvent;

public class AbstractConcurrentOperationEvent implements UberFireEvent {

    private ObservablePath path;

    public AbstractConcurrentOperationEvent(final ObservablePath path) {
        this.path = path;
    }

    public ObservablePath getPath() {
        return path;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractConcurrentOperationEvent)) {
            return false;
        }

        final AbstractConcurrentOperationEvent that = (AbstractConcurrentOperationEvent) o;

        return !(getPath() != null ? !getPath().equals(that.getPath()) : that.getPath() != null);
    }

    @Override
    public int hashCode() {
        return getPath() != null ? getPath().hashCode() : 0;
    }
}
