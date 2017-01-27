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

package org.kie.workbench.common.widgets.client.handlers;

import org.uberfire.backend.vfs.Path;

public class NewResourceSuccessEvent {

    private Path path;

    public NewResourceSuccessEvent( final Path path ) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof NewResourceSuccessEvent ) ) {
            return false;
        }

        final NewResourceSuccessEvent that = (NewResourceSuccessEvent) o;

        return !( getPath() != null ? !getPath().equals( that.getPath() ) : that.getPath() != null );

    }

    @Override
    public int hashCode() {
        return getPath() != null ? getPath().hashCode() : 0;
    }
}
