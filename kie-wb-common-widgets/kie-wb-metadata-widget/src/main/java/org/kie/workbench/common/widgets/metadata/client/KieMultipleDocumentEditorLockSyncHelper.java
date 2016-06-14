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

import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper;
import org.uberfire.workbench.model.menu.MenuItem;

/**
 * Specialized {@link HasLockSyncMenuStateHelper.LockSyncMenuStateHelper} that only enables/disables {@link MenuItem}
 * if the lock relates to the "active document" (see {@link KieMultipleDocumentEditor#getActiveDocument()}
 */
public class KieMultipleDocumentEditorLockSyncHelper extends HasLockSyncMenuStateHelper.BasicLockSyncMenuStateHelper {

    private KieMultipleDocumentEditor editor;

    public KieMultipleDocumentEditorLockSyncHelper( final KieMultipleDocumentEditor editor ) {
        this.editor = PortablePreconditions.checkNotNull( "editor",
                                                          editor );
    }

    @Override
    public Operation enable( final Path file,
                             final boolean isLocked,
                             final boolean isLockedByCurrentUser ) {
        final KieDocument activeDocument = editor.getActiveDocument();
        if ( activeDocument == null || !activeDocument.getCurrentPath().equals( file ) ) {
            return Operation.VETO;
        }
        return super.enable( file,
                             isLocked,
                             isLockedByCurrentUser );
    }

}
