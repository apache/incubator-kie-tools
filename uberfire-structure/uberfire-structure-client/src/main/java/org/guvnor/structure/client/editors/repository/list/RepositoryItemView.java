/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import com.google.gwt.user.client.ui.IsWidget;

public interface RepositoryItemView
        extends IsWidget {

    void setRepositoryName(final String repositoryName);

    void setRepositoryDescription(final String description);

    void showAvailableProtocols();

    void setDaemonURI(final String uri);

    void addProtocol(final String protocol);

    void setPresenter(final RepositoryItemPresenter presenter);

    void setUriId(final String uriID);

    void addBranch(final String branch);

    void clearBranches();

    void refresh();

    void setSelectedBranch(final String currentBranch);

    String getSelectedBranch();
}
