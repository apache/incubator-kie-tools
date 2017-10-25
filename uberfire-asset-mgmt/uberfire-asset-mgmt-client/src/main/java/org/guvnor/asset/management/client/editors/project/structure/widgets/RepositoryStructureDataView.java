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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import com.google.gwt.user.client.ui.IsWidget;

public interface RepositoryStructureDataView
        extends IsWidget {

    enum ViewMode {
        CREATE_STRUCTURE,
        EDIT_SINGLE_MODULE_PROJECT,
        EDIT_MULTI_MODULE_PROJECT,
        EDIT_UNMANAGED_REPOSITORY
    }

    interface Presenter {

        void onInitRepositoryStructure();
    }

    void setGroupId(final String id);

    String getGroupId();

    void setArtifactId(final String id);

    String getArtifactId();

    void setVersion(final String version);

    String getVersion();

    void setEditUnmanagedRepositoryText();

    void setEditModuleVisibility(final boolean visible);

    void setEditMultiModuleProjectText();

    void setEditSingleModuleProjectText();

    void setCreateStructureText();

    void clear();
}
