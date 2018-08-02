/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

/***
 * Used to store informations of each pom found in a prj
 */
public class PomPlaceHolder {

    private String filePath;
    private String artifactID;
    private String groupID;
    private String version;
    private String packaging;
    private Boolean isValid;

    public PomPlaceHolder() {
        this.isValid = Boolean.FALSE;
    }

    public PomPlaceHolder(String filePath,
                          String artifactID,
                          String groupID,
                          String version,
                          String packaging) {
        this.filePath = filePath;
        this.artifactID = artifactID;
        this.groupID = groupID;
        this.version = version;
        this.packaging = packaging;
        this.isValid = Boolean.TRUE;
    }

    public PomPlaceHolder(String filePath,
                          String artifactID,
                          String groupID,
                          String version,
                          String packaging,
                          byte[] content) {
        this.filePath = filePath;
        this.artifactID = artifactID;
        this.groupID = groupID;
        this.version = version;
        this.packaging = packaging;
        this.isValid = Boolean.TRUE;
    }

    /**
     * If is false you have to check the correctness of the POM
     * @return
     */
    public Boolean isValid() {
        return isValid;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getVersion() {
        return version;
    }

    public String getPackaging() {
        return packaging;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PomPlaceHolder{");
        sb.append("filePath='").append(filePath).append('\'');
        sb.append(", artifactID='").append(artifactID).append('\'');
        sb.append(", groupID='").append(groupID).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", packaging='").append(packaging).append('\'');
        sb.append(", isValid=").append(isValid);
        sb.append('}');
        return sb.toString();
    }
}
