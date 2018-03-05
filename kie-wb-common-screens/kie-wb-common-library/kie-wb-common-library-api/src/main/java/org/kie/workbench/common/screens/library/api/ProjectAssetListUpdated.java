/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.api;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * <p>
 * Fired when a client of the {@link LibraryService} should reload the asset list
 * (for example when a batch of files is indexed).
 */
@Portable
public class ProjectAssetListUpdated {

    private final WorkspaceProject project;

    public ProjectAssetListUpdated(final @MapsTo("project") WorkspaceProject project) {
        this.project = project;
    }

    public WorkspaceProject getProject() {
        return project;
    }

}
