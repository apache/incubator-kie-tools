/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.events;

public class DependencyPathSelectedEvent {

    private final Object context;
    private final String path;

    public DependencyPathSelectedEvent( final Object context,
                                        final String path ) {
        this.context = context;
        this.path = path;
    }

    public Object getContext() {
        return context;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DependencyPathSelectedEvent ) ) {
            return false;
        }

        final DependencyPathSelectedEvent that = (DependencyPathSelectedEvent) o;

        if ( !context.equals( that.context ) ) {
            return false;
        }
        return path.equals( that.path );

    }

    @Override
    public int hashCode() {
        int result = context.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
