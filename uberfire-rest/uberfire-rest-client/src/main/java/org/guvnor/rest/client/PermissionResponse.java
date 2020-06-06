/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PermissionResponse {
    private String homePage;
    private Integer priority;
    private ResourcePermission project;
    private ResourcePermission spaces;
    private ResourcePermission editor;
    private ResourcePermission pages;
    private WorkbenchPermission workbench;

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public ResourcePermission getProject() {
        return project;
    }

    public ResourcePermission getSpaces() {
        return spaces;
    }

    public ResourcePermission getEditor() {
        return editor;
    }

    public ResourcePermission getPages() {
        return pages;
    }

    public void setProject(ResourcePermission project) {
        this.project = project;
    }

    public void setSpaces(ResourcePermission spaces) {
        this.spaces = spaces;
    }

    public void setEditor(ResourcePermission editor) {
        this.editor = editor;
    }

    public void setPages(ResourcePermission pages) {
        this.pages = pages;
    }

    public WorkbenchPermission getWorkbench() {
        return workbench;
    }

    public void setWorkbench(WorkbenchPermission workbench) {
        this.workbench = workbench;
    }
}
