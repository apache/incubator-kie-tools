/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client;

import java.util.List;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;

public interface KieMultipleDocumentEditorWrapperView extends KieEditorWrapperView,
                                                              UberView<KieMultipleDocumentEditorPresenter> {

    /**
     * Shows an informative message to Users that there are no additional documents available that can be opened in the editor .
     */
    void showNoAdditionalDocuments();

    /**
     * Shows a list of paths to documents that can be opened in the editor from which the User can select on to open.
     * The selected document can be opened in the editor by calling {@link KieMultipleDocumentEditorPresenter#onOpenDocumentInEditor(Path)}
     * @param paths Paths to documents that can be opened in the editor.
     */
    void showAdditionalDocuments( final List<Path> paths );

}
