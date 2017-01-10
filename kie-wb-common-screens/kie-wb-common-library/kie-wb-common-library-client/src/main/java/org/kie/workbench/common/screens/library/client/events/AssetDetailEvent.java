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
package org.kie.workbench.common.screens.library.client.events;

import org.guvnor.common.services.project.model.Project;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class AssetDetailEvent {

    private Project project;

    private Path path;

    public AssetDetailEvent() {
    }

    public AssetDetailEvent( final Project project,
                             final Path path ) {
        checkNotNull( "project", project );
        checkNotNull( "path", path );

        this.project = project;
        this.path = path;
    }

    public Project getProject() {
        return project;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AssetDetailEvent ) ) {
            return false;
        }

        final AssetDetailEvent that = (AssetDetailEvent) o;

        if ( getProject() != null ? !getProject().equals( that.getProject() ) : that.getProject() != null ) {
            return false;
        }
        return !( getPath() != null ? !getPath().equals( that.getPath() ) : that.getPath() != null );

    }

    @Override
    public int hashCode() {
        int result = getProject() != null ? getProject().hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( getPath() != null ? getPath().hashCode() : 0 );
        result = ~~result;

        return result;
    }
}
