/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.List;

import org.kie.workbench.common.screens.datamodeller.client.widgets.common.HasErrorPopup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.BaseEditorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

public interface MainEditorView<T>
        extends BaseEditorView<T>,
        HasErrorPopup {

    void showUsagesPopupForRenaming( String message, List<Path> usedByFiles, Command yesCommand, Command noCommand );

    void showUsagesPopupForChanging( String message, List<Path> usedByFiles, Command yesCommand, Command noCommand );

}
