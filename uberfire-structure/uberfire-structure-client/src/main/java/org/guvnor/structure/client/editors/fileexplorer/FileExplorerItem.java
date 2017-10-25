/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.tree.FSTreeItem;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

class FileExplorerItem {

    private CommonConstants constants = CommonConstants.INSTANCE;

    private final FSTreeItem parent;

    FileExplorerItem(final FSTreeItem treeItem) {
        this.parent = checkNotNull("parent",
                                   treeItem);
    }

    public void addDirectory(final Path child) {
        checkCleanupLoading();

        final FSTreeItem newDirectory = parent.addItem(FSTreeItem.FSType.FOLDER,
                                                       child.getFileName());
        newDirectory.addItem(FSTreeItem.FSType.LOADING,
                             constants.Loading());
        newDirectory.setUserObject(child);
    }

    public void addFile(final Path child) {
        checkCleanupLoading();

        final FSTreeItem newFile = parent.addItem(FSTreeItem.FSType.ITEM,
                                                  child.getFileName());
        newFile.setUserObject(child);
    }

    private void checkCleanupLoading() {
        if (parent.getChild(0) != null && parent.getChild(0).getUserObject() == null) {
            parent.getChild(0).remove();
        }
    }
}
