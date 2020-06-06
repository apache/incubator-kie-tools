/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class WorkbenchPermission {

    private Boolean editDataObject;
    private Boolean plannerAvailable;
    private Boolean editGlobalPreferences;
    private Boolean editProfilePreferences;
    private Boolean accessDataTransfer;
    private Boolean jarDownload;
    private Boolean editGuidedDecisionTableColumns;

    public WorkbenchPermission() {
    }

    public WorkbenchPermission(@MapsTo("editDataObject") Boolean editDataObject, @MapsTo("plannerAvailable") Boolean plannerAvailable,
                               @MapsTo("editGlobalPreferences") Boolean editGlobalPreferences, @MapsTo("editProfilePreferences") Boolean editProfilePreferences,
                               @MapsTo("accessDataTransfer") Boolean accessDataTransfer, @MapsTo("jarDownload") Boolean jarDownload,
                               @MapsTo("editGuidedDecisionTableColumns") Boolean editGuidedDecisionTableColumns) {
        this.editDataObject = editDataObject;
        this.plannerAvailable = plannerAvailable;
        this.editGlobalPreferences = editGlobalPreferences;
        this.editProfilePreferences = editProfilePreferences;
        this.accessDataTransfer = accessDataTransfer;
        this.jarDownload = jarDownload;
        this.editGuidedDecisionTableColumns = editGuidedDecisionTableColumns;
    }

    public Boolean getEditDataObject() {
        return editDataObject;
    }

    public Boolean getPlannerAvailable() {
        return plannerAvailable;
    }

    public Boolean getEditGlobalPreferences() {
        return editGlobalPreferences;
    }

    public Boolean getEditProfilePreferences() {
        return editProfilePreferences;
    }

    public Boolean getAccessDataTransfer() {
        return accessDataTransfer;
    }

    public Boolean getJarDownload() {
        return jarDownload;
    }

    public Boolean getEditGuidedDecisionTableColumns() {
        return editGuidedDecisionTableColumns;
    }

    public void setEditDataObject(Boolean editDataObject) {
        this.editDataObject = editDataObject;
    }

    public void setPlannerAvailable(Boolean plannerAvailable) {
        this.plannerAvailable = plannerAvailable;
    }

    public void setEditGlobalPreferences(Boolean editGlobalPreferences) {
        this.editGlobalPreferences = editGlobalPreferences;
    }

    public void setEditProfilePreferences(Boolean editProfilePreferences) {
        this.editProfilePreferences = editProfilePreferences;
    }

    public void setAccessDataTransfer(Boolean accessDataTransfer) {
        this.accessDataTransfer = accessDataTransfer;
    }

    public void setJarDownload(Boolean jarDownload) {
        this.jarDownload = jarDownload;
    }

    public void setEditGuidedDecisionTableColumns(Boolean editGuidedDecisionTableColumns) {
        this.editGuidedDecisionTableColumns = editGuidedDecisionTableColumns;
    }
}
