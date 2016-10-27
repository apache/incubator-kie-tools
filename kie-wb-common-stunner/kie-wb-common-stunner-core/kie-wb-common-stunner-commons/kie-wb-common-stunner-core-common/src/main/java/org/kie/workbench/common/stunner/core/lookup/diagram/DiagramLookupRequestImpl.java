/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.lookup.diagram;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupRequestBuilder;
import org.uberfire.backend.vfs.Path;

@Portable
public final class DiagramLookupRequestImpl extends AbstractLookupRequest implements DiagramLookupRequest {

    public DiagramLookupRequestImpl( @MapsTo( "criteria" ) String criteria,
                                     @MapsTo( "page" ) int page,
                                     @MapsTo( "pageSize" ) int pageSize ) {
        super( criteria, page, pageSize );
    }

    @NonPortable
    public static class Builder extends AbstractLookupRequestBuilder<Builder> {

        private final StringBuilder criteria = new StringBuilder();

        public Builder withName( final String name ) {
            criteria.append( "name=" ).append( name ).append( ";" );
            return this;
        }

        public Builder forPath( final Path path ) {
            criteria.append( "path=" ).append( path.toURI() ).append( ";" );
            return this;
        }

        public DiagramLookupRequest build() {
            return new DiagramLookupRequestImpl( criteria.toString(), page, pageSize );
        }

    }
}
