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

package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HasVisibility;
import org.guvnor.common.services.project.model.Module;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.ParameterizedCommand;

public interface View extends HasBusyIndicator,
                              HasVisibility,
                              UberView<BaseViewPresenter> {

    void setContent(final Module activeModule,
                    final FolderListing folderListing,
                    final Map<FolderItem, List<FolderItem>> siblings);

    void setItems(final FolderListing folderListing);

    void showHiddenFiles(final boolean show);

    Explorer getExplorer();

    void deleteItem(final ParameterizedCommand<String> command);

    void renameItem(final Path path,
                    final Validator validator,
                    final CommandWithFileNameAndCommitMessage command);

    void copyItem(final Path path,
                  final Validator validator,
                  final CommandWithFileNameAndCommitMessage command);

    void renderItems(FolderListing filteredContent);

    void setNavType(Explorer.NavType tree);

    void hideTagFilter();

    void showTagFilter();

    void hideHeaderNavigator();

    void showHeaderNavigator();
}