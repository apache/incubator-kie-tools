/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class ExampleProject {

    private Path root;
    private String name;
    private String description;

    public ExampleProject( final @MapsTo("root") Path root,
                           final @MapsTo("name") String name,
                           final @MapsTo("description") String description ) {
        this.root = root;
        this.name = name;
        this.description = description;
    }

    public Path getRoot() {
        return root;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ExampleProject ) ) {
            return false;
        }

        ExampleProject that = (ExampleProject) o;

        if ( !root.equals( that.root ) ) {
            return false;
        }
        if ( !name.equals( that.name ) ) {
            return false;
        }
        return !( description != null ? !description.equals( that.description ) : that.description != null );

    }

    @Override
    public int hashCode() {
        int result = root.hashCode();
        result = 31 * result + name.hashCode();
        result = ~~result;
        result = 31 * result + ( description != null ? description.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
