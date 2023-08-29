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

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface KieWorkbenchWidgetsConstants {

    @TranslationKey(defaultValue = "Create new")
    String NewResourceViewPopupTitle = "NewResourceViewImpl.popupTitle";

    @TranslationKey(defaultValue = "Name:")
    String NewResourceViewItemNameSubheading = "NewResourceViewImpl.itemNameSubheading";

    @TranslationKey(defaultValue = "Missing name for new resource. Please enter.")
    String NewResourceViewFileNameIsMandatory = "NewResourceViewImpl.fileNameIsMandatory";

    @TranslationKey(defaultValue = "Resource Name")
    String NewResourceViewResourceName = "NewResourceViewImpl.resourceName";

    @TranslationKey(defaultValue = "Package")
    String NewResourceViewPackageName = "NewResourceViewImpl.packageName";

    @TranslationKey(defaultValue = "Name...")
    String NewResourceViewResourceNamePlaceholder = "NewResourceViewImpl.resourceNamePlaceholder";

    @TranslationKey(defaultValue = "Path in which to create new resource is missing. Please enter.")
    String NewResourceViewMissingPath = "NewResourceViewImpl.MissingPath";

    @TranslationKey(defaultValue = "")
    String ValidationPopup_YesSaveAnyway = "ValidationPopup.YesSaveAnyway";

    @TranslationKey(defaultValue = "")
    String ValidationPopup_YesCopyAnyway = "ValidationPopup.YesCopyAnyway";

    @TranslationKey(defaultValue = "")
    String ValidationPopup_YesDeleteAnyway = "ValidationPopup.YesDeleteAnyway";

    @TranslationKey(defaultValue = "")
    String ValidationPopup_Cancel = "ValidationPopup.Cancel";

    @TranslationKey(defaultValue = "")
    String ValidationPopupViewImpl_ValidationErrors = "ValidationPopupViewImpl.ValidationErrors";

    @TranslationKey(defaultValue = "")
    String AboutPopupView_Version = "AboutPopupView.Version";

    @TranslationKey(defaultValue = "")
    String AboutPopupView_LicenseDescription = "AboutPopupView.LicenseDescription";

    @TranslationKey(defaultValue = "")
    String AboutPopupView_License = "AboutPopupView.License";

    @TranslationKey(defaultValue = "")
    String KieAssetsDropdownView_Select = "KieAssetsDropdownView.Select";

    @TranslationKey(defaultValue = "")
    String SearchBarComponentView_Find = "SearchBarComponentView.Find";
}
