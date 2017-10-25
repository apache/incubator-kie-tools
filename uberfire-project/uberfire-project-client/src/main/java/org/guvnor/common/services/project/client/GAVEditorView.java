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

package org.guvnor.common.services.project.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.GAV;

public interface GAVEditorView
        extends IsWidget {

    interface Presenter {

        void setGAV(final GAV gav);

        void setArtifactID(final String artifactID);

        void onGroupIdChange(final String groupId);

        void onArtifactIdChange(final String artifactId);

        void onVersionChange(final String version);

        void addGroupIdChangeHandler(final GroupIdChangeHandler changeHandler);

        void addArtifactIdChangeHandler(final ArtifactIdChangeHandler changeHandler);

        void addVersionChangeHandler(final VersionChangeHandler changeHandler);

        void setReadOnly();

        void disableGroupID(final String reason);

        void disableVersion(final String reason);

        void disableArtifactID(final String reason);

        void enableGroupID();

        void enableArtifactID();

        void enableVersion();

        void setValidGroupID(final boolean isValid);

        void setValidArtifactID(final boolean isValid);

        void setValidVersion(final boolean isValid);
    }

    void setPresenter(final Presenter presenter);

    void setGroupId(final String id);

    void setArtifactId(final String id);

    void setReadOnly();

    void setVersion(final String version);

    void disableGroupID(final String reason);

    void disableArtifactID(final String reason);

    void disableVersion(final String reason);

    void enableGroupID();

    void enableArtifactID();

    void enableVersion();

    void setValidGroupID(final boolean isValid);

    void setValidArtifactID(final boolean isValid);

    void setValidVersion(final boolean isValid);
}
