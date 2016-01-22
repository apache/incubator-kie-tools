/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface CommonConstants
        extends
        Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String SavePopupTitle();

    String CheckInComment();

    String CheckInCommentColon();

    String Save();

    String Tags();

    String Cancel();

    String CopyPopupTitle();

    String NewName();

    String NewNameColon();

    String CopyPopupCreateACopy();

    String InvalidFileName0( String baseFileName );

    String DeletePopupTitle();

    String DeletePopupDelete();

    String DeletePopupRenameNamePrompt();

    String RenamePopupTitle();

    String RenamePopupRenameItem();

    String Restoring();

    String Delete();

    String Rename();

    String Copy();

    String Validate();

    String Restore();

    String Other();

    String Loading();

    String ItemRestored();

    String ItemSavedSuccessfully();

    String CantSaveReadOnly();

    String Saving();

    String DiscardUnsavedData();

    String LatestVersion();

    String Version( int versionIndex );

    String Date();

    String CommitMessage();

    String Author();

    String More();

    String ShowAll();

    String Deleting();

    String ItemDeletedSuccessfully();

    String ItemRenamedSuccessfully();

    String Renaming();

    String Copying();

    String ItemCopiedSuccessfully();

    String ExceptionFileAlreadyExists0( final String uri );

    String Current();

    String Select();

}
