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

@Portable
public final class DiagramRepresentationImpl implements DiagramRepresentation {

    private final String uuid;
    private final String graphUUID;
    private final String title;
    private final String defSetId;
    private final String shapeSetId;
    private final String vfsPath;
    private final String thumbImageData;

    public DiagramRepresentationImpl( @MapsTo( "uuid" ) String uuid,
                                      @MapsTo( "graphUUID" ) String graphUUID,
                                      @MapsTo( "title" ) String title,
                                      @MapsTo( "defSetId" ) String defSetId,
                                      @MapsTo( "shapeSetId" ) String shapeSetId,
                                      @MapsTo( "vfsPath" ) String vfsPath,
                                      @MapsTo( "thumbImageData" ) String thumbImageData ) {
        this.uuid = uuid;
        this.graphUUID = graphUUID;
        this.title = title;
        this.defSetId = defSetId;
        this.shapeSetId = shapeSetId;
        this.vfsPath = vfsPath;
        this.thumbImageData = thumbImageData;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public String getGraphUUID() {
        return graphUUID;
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
    public String getVFSPath() {
        return vfsPath;
    }

    @Override
    public String getThumbImageData() {
        return thumbImageData;
    }

    @NonPortable
    public static final class DiagramRepresentationBuilder {

        private final Diagram diagram;

        public DiagramRepresentationBuilder( final Diagram diagram ) {
            this.diagram = diagram;
        }

        public DiagramRepresentation build() {
            return new DiagramRepresentationImpl( diagram.getUUID(),
                    diagram.getGraph().getUUID(), diagram.getSettings().getTitle(),
                    diagram.getSettings().getDefinitionSetId(),
                    diagram.getSettings().getShapeSetId(),
                    diagram.getSettings().getVFSPath(),
                    diagram.getSettings().getThumbData() );
        }

    }

}
