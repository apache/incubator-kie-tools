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


package org.kie.workbench.common.stunner.client.widgets.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface StunnerWidgetsConstants {

    @TranslationKey(defaultValue = "")
    String DefinitionPaletteGroupWidgetViewImpl_showMore = "DefinitionPaletteGroupWidgetViewImpl.showMore";

    @TranslationKey(defaultValue = "")
    String DefinitionPaletteGroupWidgetViewImpl_showLess = "DefinitionPaletteGroupWidgetViewImpl.showLess";

    @TranslationKey(defaultValue = "")
    String NameEditBoxWidgetViewImpl_save = "NameEditBoxWidgetViewImpl.save";

    @TranslationKey(defaultValue = "")
    String NameEditBoxWidgetViewImpl_close = "NameEditBoxWidgetViewImpl.close";

    @TranslationKey(defaultValue = "")
    String NameEditBoxWidgetViewImp_name = "NameEditBoxWidgetViewImpl.name";

    @TranslationKey(defaultValue = "")
    String SessionPresenterView_Error = "SessionPresenterView.Error";

    @TranslationKey(defaultValue = "")
    String SessionPresenterView_Warning = "SessionPresenterView.Warning";

    @TranslationKey(defaultValue = "")
    String SessionPresenterView_Info = "SessionPresenterView.Info";

    @TranslationKey(defaultValue = "")
    String SessionPresenterView_Notifications = "SessionPresenterView.Notifications";

    @TranslationKey(defaultValue = "Are you sure?")
    String AbstractToolbarCommand_ConfirmMessage = "AbstractToolbarCommand.ConfirmMessage";

    @TranslationKey(defaultValue = "OK")
    String MarshallingResponsePopup_OkAction = "MarshallingResponsePopup.OkAction";

    @TranslationKey(defaultValue = "Cancel")
    String MarshallingResponsePopup_CancelAction = "MarshallingResponsePopup.CancelAction";

    @TranslationKey(defaultValue = "Copy to clipboard")
    String MarshallingResponsePopup_CopyToClipboardActionTitle = "MarshallingResponsePopup.CopyToClipboardActionTitle";

    @TranslationKey(defaultValue = "")
    String MarshallingResponsePopup_LevelTableColumnName = "MarshallingResponsePopup.LevelTableColumnName";

    @TranslationKey(defaultValue = "")
    String MarshallingResponsePopup_MessageTableColumnName = "MarshallingResponsePopup.MessageTableColumnName";

    @TranslationKey(defaultValue = "Error")
    String MarshallingResponsePopup_ErrorMessageLabel = "MarshallingResponsePopup.ErrorMessageLabel";

    @TranslationKey(defaultValue = "Warning")
    String MarshallingResponsePopup_WarningMessageLabel = "MarshallingResponsePopup.WarningMessageLabel";

    @TranslationKey(defaultValue = "Info")
    String MarshallingResponsePopup_InfoMessageLabel = "MarshallingResponsePopup.InfoMessageLabel";

    @TranslationKey(defaultValue = "Info")
    String MarshallingResponsePopup_UnknownMessageLabel = "MarshallingResponsePopup.UnknownMessageLabel";

    @TranslationKey(defaultValue = "Data Object Exists with Same Name and Different Type - Will be Changed to (Object)")
    String MarshallingResponsePopup_DataObjectsSameNameDifferentType = "MarshallingMessage.dataObjectsSameNameDifferentType";

    @TranslationKey(defaultValue = "Data Object-Name")
    String MarshallingResponsePopup_dataObjectWithName = "MarshallingMessage.dataObjectWithName";

    @TranslationKey(defaultValue = "Data Object with Invalid Name Exists. Illegal chars will be replaced by -")
    String MarshallingResponsePopup_dataObjectWithInvalidName = "MarshallingMessage.dataObjectWithInvalidName";

    @TranslationKey(defaultValue = "Info")
    String SessionCardinalityStateHandler_EmptyStateCaption = "SessionCardinalityStateHandler.EmptyStateCaption";

    @TranslationKey(defaultValue = "Info")
    String SessionCardinalityStateHandler_EmptyStateMessage = "SessionCardinalityStateHandler.EmptyStateMessage";

    @TranslationKey(defaultValue = "Download")
    String DownloadDiagram = "DownloadDiagram";

    @TranslationKey(defaultValue = "Error")
    String DiagramParsingError = "DiagramParsingError";

    @TranslationKey(defaultValue = "Explorer")
    String ScreenExplorerTitle = "ScreenExplorerTitle";

    @TranslationKey(defaultValue = "Explorer")
    String Documentation = "Documentation";
}
