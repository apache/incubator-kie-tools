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

package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoveBranchJobRequest extends JobRequest {

    private String spaceName;
    private String projectName;
    private String branchName;
    private String userIdentifier;

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(final String name) {
        this.spaceName = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String name) {
        this.projectName = name;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(final String name) {
        this.branchName = name;
    }

    public void setUserIdentifier(final String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }
}
