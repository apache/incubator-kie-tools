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
package org.kie.workbench.common.screens.search.backend.server;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class MockTypeRegister implements Instance<ResourceTypeDefinition> {

    private List<ResourceTypeDefinition> resourceTypeDefinitions = new ArrayList<ResourceTypeDefinition>() {{
        add( new MockResourceTypeDefinition() );
    }};

    @Override
    public Instance<ResourceTypeDefinition> select( final Annotation... annotations ) {
        return null;
    }

    @Override
    public <U extends ResourceTypeDefinition> Instance<U> select( final Class<U> aClass,
                                                                  final Annotation... annotations ) {
        return null;
    }

    @Override
    public boolean isUnsatisfied() {
        return false;
    }

    @Override
    public boolean isAmbiguous() {
        return false;
    }

    @Override
    public void destroy( final ResourceTypeDefinition resourceTypeDefinition ) {
        //Do nothing
    }

    @Override
    public Iterator<ResourceTypeDefinition> iterator() {
        return resourceTypeDefinitions.iterator();
    }

    @Override
    public ResourceTypeDefinition get() {
        return resourceTypeDefinitions.get( 0 );
    }

    private static class MockResourceTypeDefinition implements ResourceTypeDefinition {

        @Override
        public String getShortName() {
            return "mock";
        }

        @Override
        public String getDescription() {
            return "mock";
        }

        @Override
        public String getPrefix() {
            return "mock";
        }

        @Override
        public String getSuffix() {
            return "mock";
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public String getSimpleWildcardPattern() {
            return "*.mock";
        }

        @Override
        public boolean accept( final Path path ) {
            return false;
        }

    }
}
