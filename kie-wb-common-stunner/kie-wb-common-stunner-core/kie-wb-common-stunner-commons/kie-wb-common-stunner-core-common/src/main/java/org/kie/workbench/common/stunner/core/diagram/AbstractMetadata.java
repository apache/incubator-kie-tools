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

package org.kie.workbench.common.stunner.core.diagram;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractMetadata implements Metadata {

    private String definitionSetId;
    private String title;
    private String shapeSetId;
    private String canvasRootUUID;
    private String thumbData;
    private Path path;

    public AbstractMetadata() {
    }

    public AbstractMetadata( @MapsTo( "definitionSetId" ) String definitionSetId ) {
        this.definitionSetId = definitionSetId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle( final String title ) {
        this.title = title;
    }

    @Override
    public String getDefinitionSetId() {
        return definitionSetId;
    }

    @Override
    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public void setShapeSetId( final String id ) {
        this.shapeSetId = id;
    }

    @Override
    public String getCanvasRootUUID() {
        return canvasRootUUID;
    }

    @Override
    public void setCanvasRootUUID( final String uuid ) {
        this.canvasRootUUID = uuid;
    }

    @Override
    public String getThumbData() {
        return thumbData;
    }

    @Override
    public void setThumbData( final String data ) {
        this.thumbData = data;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void setPath( final Path path ) {
        this.path = path;
    }

    public void setDefinitionSetId( String defSetId ) {
        this.definitionSetId = defSetId;
    }

}
