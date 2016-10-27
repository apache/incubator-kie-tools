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
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;

@Portable
public final class DiagramRepresentationImpl implements DiagramRepresentation {

    private final String name;
    private final String title;
    private final String defSetId;
    private final String shapeSetId;
    private final Path path;
    private final String thumbImageData;

    DiagramRepresentationImpl( @MapsTo( "name" ) String name,
                               @MapsTo( "title" ) String title,
                               @MapsTo( "defSetId" ) String defSetId,
                               @MapsTo( "shapeSetId" ) String shapeSetId,
                               @MapsTo( "path" ) Path path,
                               @MapsTo( "thumbImageData" ) String thumbImageData ) {
        this.name = name;
        this.title = title;
        this.defSetId = defSetId;
        this.shapeSetId = shapeSetId;
        this.path = path;
        this.thumbImageData = thumbImageData;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDefinitionSetId() {
        return defSetId;
    }

    @Override
    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String getThumbImageData() {
        return thumbImageData;
    }

    @NonPortable
    public static final class DiagramRepresentationBuilder {

        private final Diagram diagram;
        private final DiagramRepresentation representation;
        private String shapeSetId;

        public DiagramRepresentationBuilder( final Diagram diagram ) {
            this.diagram = diagram;
            this.representation = null;
        }

        public DiagramRepresentationBuilder( final DiagramRepresentation representation ) {
            this.representation = representation;
            this.diagram = null;
        }

        public DiagramRepresentationBuilder setShapeSetId( final String shapeSetId ) {
            this.shapeSetId = shapeSetId;
            return this;
        }

        public DiagramRepresentation build() {
            if ( null != diagram ) {
                return new DiagramRepresentationImpl( diagram.getName(),
                        diagram.getMetadata().getTitle(),
                        diagram.getMetadata().getDefinitionSetId(),
                        null != shapeSetId ? shapeSetId : diagram.getMetadata().getShapeSetId(),
                        diagram.getMetadata().getPath(),
                        diagram.getMetadata().getThumbData() );
            }
            return new DiagramRepresentationImpl( representation.getName(),
                    representation.getTitle(),
                    representation.getDefinitionSetId(),
                    null != shapeSetId ? shapeSetId : representation.getShapeSetId(),
                    representation.getPath(),
                    representation.getThumbImageData());
        }

    }

}