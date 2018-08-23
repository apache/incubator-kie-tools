/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.kie.workbench.common.screens.projecteditor.client.resources.css.ProjectEditorCss;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

public interface ProjectEditorResources
        extends
        ClientBundle {

    ProjectEditorResources INSTANCE = GWT.create(ProjectEditorResources.class);

    ProjectEditorConstants CONSTANTS = GWT.create(ProjectEditorConstants.class);

    @Source("css/ProjectEditor.css")
    ProjectEditorCss mainCss();

    @Source("images/error.gif")
    ImageResource Error();

    @Source("images/warning.gif")
    ImageResource Warning();

    @Source("images/information.gif")
    ImageResource Information();

    @Source("images/trash.gif")
    ImageResource Trash();

    @Source("images/newProject.gif")
    ImageResource newProjectIcon();

    @Source("images/BPM_FileIcons_package.png")
    ImageResource newFolderIcon();

}
