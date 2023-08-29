/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.diagram;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractMetadata implements Metadata {

    private String definitionSetId;
    private String profileId;
    private String title;
    private String shapeSetId;
    private String canvasRootUUID;
    private String thumbData;
    private Path path;

    public AbstractMetadata() {
    }

    public AbstractMetadata(final @MapsTo("definitionSetId") String definitionSetId) {
        this.definitionSetId = definitionSetId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String getDefinitionSetId() {
        return definitionSetId;
    }

    @Override
    public String getProfileId() {
        return profileId;
    }

    @Override
    public void setProfileId(final String profileId) {
        this.profileId = profileId;
    }

    @Override
    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public void setShapeSetId(final String id) {
        this.shapeSetId = id;
    }

    @Override
    public String getCanvasRootUUID() {
        return canvasRootUUID;
    }

    @Override
    public void setCanvasRootUUID(final String uuid) {
        this.canvasRootUUID = uuid;
    }

    @Override
    public String getThumbData() {
        return thumbData;
    }

    @Override
    public void setThumbData(final String data) {
        this.thumbData = data;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void setPath(final Path path) {
        this.path = path;
    }

    public void setDefinitionSetId(final String defSetId) {
        this.definitionSetId = defSetId;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(definitionSetId),
                                         Objects.hashCode(profileId),
                                         Objects.hashCode(shapeSetId),
                                         Objects.hashCode(canvasRootUUID),
                                         Objects.hashCode(thumbData),
                                         Objects.hashCode(path),
                                         Objects.hashCode(title));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractMetadata) {
            AbstractMetadata other = (AbstractMetadata) o;
            return Objects.equals(definitionSetId, other.definitionSetId) &&
                    Objects.equals(profileId, other.profileId) &&
                    Objects.equals(shapeSetId, other.shapeSetId) &&
                    Objects.equals(canvasRootUUID, other.canvasRootUUID) &&
                    Objects.equals(thumbData, other.thumbData) &&
                    Objects.equals(path, other.path) &&
                    Objects.equals(title, other.title);
        }
        return false;
    }
}
