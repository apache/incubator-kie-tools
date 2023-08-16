/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.widgets.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * EnumEditor I18N constants
 */
public interface CommonConstants
        extends
        Messages {

    CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String InvalidDateFormatMessage();

    String January();

    String February();

    String March();

    String April();

    String May();

    String June();

    String July();

    String August();

    String September();

    String October();

    String November();

    String December();

    String OK();

    String Edit();

    String Error();

    String SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

    String ShowDetail();

    String Choose();

    String WaitWhileValidating();

    String Wait();

    String File();

    String Other();

    String Save();

    String Validate();

    String ValidationErrors();

    String Restore();

    String Copy();

    String Delete();

    String Rename();

    String Archive();

    String Restoring();

    String Copying();

    String Deleting();

    String Renaming();

    String Move();

    String EditTabTitle();

    String SourceTabTitle();

    String MetadataTabTitle();

    String DataObjectsTabTitle();

    String DiscardUnsavedData();

    String NoChangesSinceLastSave();

    String CantSaveReadOnly();

    String ItemCreatedSuccessfully();

    String ItemSavedSuccessfully();

    String ItemDeletedSuccessfully();

    String ItemRenamedSuccessfully();

    String ItemCopiedSuccessfully();

    String ItemMovedSuccessfully();

    String ItemValidatedSuccessfully();

    String ItemPathSubheading();

    String ItemUndefinedPath();

    String ItemRestored();

    String AddAnOptionalCheckInComment();

    String CheckIn();

    String RefreshingList();

    String UnableToLoadList();

    String Cancel();

    String Name();

    String WholeNumberInteger();

    String TrueOrFalse();

    String Text();

    String Date();

    String DecimalNumber();

    String DeleteItem();

    String NewItem();

    String New();

    String RuleAsset();

    String AElementToDelInCollectionList();

    String AddElementBelow();

    String MoveDownListMove();

    String MoveUpList();

    String NewItemBelow();

    String MoveDown();

    String MoveUp();

    String PleaseSetAName();

    String Loading();

    String Saving();

    String UploadSuccess();

    String InvalidFileName0( final String fileName );

    String NewName();

    String NewNameColon();

    String CheckInComment();

    String CheckInCommentColon();

    String CopyPopupTitle();

    String CopyPopupCreateACopy();

    String CopyPopupCreateACopyNamePrompt();

    String RenamePopupTitle();

    String RenamePopupRenameItem();

    String RenamePopupRenameNamePrompt();

    String SavePopupTitle();

    String DeletePopupTitle();

    String DeletePopupDelete();

    String DeletePopupRenameNamePrompt();

    String NoneSelected();

    String LineNoneLine();

    String UploadSelectAFile();

    String UploadFileTypeNotSupported();

    String UploadFileTypeSupportedExtensions0( String validFileExtensions );

    String UploadGenericError();

    String NoSuchFileTabTitle();

    String NoSuchFileMessage();

    String Overview();

    String Comments();

    String Version();

    String More();

    String ShowAll();

    String Author();

    String CommitMessage();

    String FailedToGenerateSource();

    String About();

    String Language();

    String French();

    String Japanese();

    String Spanish();

    String English();

    String ResetPerspectivesTooltip();

    String PromptResetPerspectives();

    String Workbench_Settings();

    String Advanced();

    String Basic();

    String Language_Selector();

    String All_Perspectives_View_Mode();

    String Reimport();

    String ReimportSuccessful();

    String AppsLauncher();
}
