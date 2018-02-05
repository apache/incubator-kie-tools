/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.rest.client;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectResponse implements Serializable {

    private String name;
    private String spaceName;
    private String groupId;
    private String version;
    private String description;
    private List<PublicURI> publicURIs;

    public ProjectResponse() {
    }

    public String getName() {
        return name;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProjectResponse{" +
                "name='" + name + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", publicURIs= [" + publicURIs.stream()
                .map(c -> c.toString())
                .collect(Collectors.joining(",")) +
                "]}";
    }

    public void setPublicURIs(List<PublicURI> publicURIs) {
        this.publicURIs = publicURIs;
    }

    public List<PublicURI> getPublicURIs() {
        return publicURIs;
    }
}