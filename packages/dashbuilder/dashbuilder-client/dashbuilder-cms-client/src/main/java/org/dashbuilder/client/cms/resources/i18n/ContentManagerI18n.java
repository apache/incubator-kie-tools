/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.cms.resources.i18n;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContentManagerI18n {

    String contentExplorerNew = ContentManagerConstants.INSTANCE.contentExplorerNew();
    String contentExplorerNavigation = ContentManagerConstants.INSTANCE.contentExplorerNavigation();
    String contentExplorerTopMenu = ContentManagerConstants.INSTANCE.contentExplorerTopMenu();
    String contentManagerHome = ContentManagerConstants.INSTANCE.contentManagerHome();
    String contentManagerHomeTitle = ContentManagerConstants.INSTANCE.contentManagerHomeTitle();
    String contentManagerHomeWelcome = ContentManagerConstants.INSTANCE.contentManagerHomeWelcome();
    String contentManagerHomeCreate = ContentManagerConstants.INSTANCE.contentManagerHomeCreate();
    String contentManagerNavigationChanged = ContentManagerConstants.INSTANCE.contentManagerNavigationChanged();
    String perspectiveResourceName = ContentManagerConstants.INSTANCE.perspective();
    String perspectivesResourceName = ContentManagerConstants.INSTANCE.perspectives();
    String noPerspectives = ContentManagerConstants.INSTANCE.noPerspectives();

    public String capitalizeFirst(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public String lowerCaseFirst(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public String getContentExplorerNavigation() {
        return contentExplorerNavigation;
    }

    public void setContentExplorerNavigation(String contentExplorerNavigation) {
        this.contentExplorerNavigation = contentExplorerNavigation;
    }

    public String getContentExplorerTopMenu() {
        return contentExplorerTopMenu;
    }

    public void setContentExplorerTopMenu(String contentExplorerTopMenu) {
        this.contentExplorerTopMenu = contentExplorerTopMenu;
    }

    public String getContentExplorerNew() {
        return contentExplorerNew;
    }

    public void setContentExplorerNew(String contentExplorerNew) {
        this.contentExplorerNew = contentExplorerNew;
    }

    public String getContentManagerHome() {
        return contentManagerHome;
    }

    public void setContentManagerHome(String contentManagerHome) {
        this.contentManagerHome = contentManagerHome;
    }

    public String getContentManagerHomeTitle() {
        return contentManagerHomeTitle;
    }

    public void setContentManagerHomeTitle(String contentManagerHomeTitle) {
        this.contentManagerHomeTitle = contentManagerHomeTitle;
    }

    public String getContentManagerHomeWelcome() {
        return contentManagerHomeWelcome;
    }

    public void setContentManagerHomeWelcome(String contentManagerHomeWelcome) {
        this.contentManagerHomeWelcome = contentManagerHomeWelcome;
    }

    public String getContentManagerHomeCreate() {
        return contentManagerHomeCreate;
    }

    public void setContentManagerHomeCreate(String contentManagerHomeCreate) {
        this.contentManagerHomeCreate = contentManagerHomeCreate;
    }

    public String getContentManagerHomeNewPerspectiveLink() {
        String resType = capitalizeFirst(perspectiveResourceName);
        return ContentManagerConstants.INSTANCE.contentManagerHomeNewPerspective(resType);
    }

    public String getContentManagerHomeNewPerspectiveButton() {
        String resType = capitalizeFirst(perspectiveResourceName);
        return capitalizeFirst(ContentManagerConstants.INSTANCE.contentManagerHomeNewPerspective(resType));
    }

    public String getContentManagerNavigationChanged() {
        return contentManagerNavigationChanged;
    }

    public void setContentManagerNavigationChanged(String contentManagerNavigationChanged) {
        this.contentManagerNavigationChanged = contentManagerNavigationChanged;
    }

    public String getPerspectiveResourceName() {
        return perspectiveResourceName;
    }

    public String getPerspectivesResourceName() {
        return perspectivesResourceName;
    }

    public void setPerspectiveResourceName(String perspectiveResourceName) {
        this.perspectiveResourceName = perspectiveResourceName;
    }

    public void setPerspectivesResourceName(String perspectivesResourceName) {
        this.perspectivesResourceName = perspectivesResourceName;
    }

    public String getNoPerspectives() {
        return noPerspectives;
    }

    public void setNoPerspectives(String noPerspectives) {
        this.noPerspectives = noPerspectives;
    }
}
