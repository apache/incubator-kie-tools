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
public class UpdateSettingRequest {

    private String homePage;
    private Integer priority;
    private Permission project;
    private Permission spaces;
    private Permission editor;
    private Permission pages;
    private WorkbenchPermission workbench;

    public UpdateSettingRequest() {
    }

    public UpdateSettingRequest(@MapsTo("homePage") String homePage, @MapsTo("priority") Integer priority,
                                @MapsTo("project") Permission project, @MapsTo("spaces") Permission spaces,
                                @MapsTo("editor") Permission editor, @MapsTo("pages") Permission pages,
                                @MapsTo("workbench") WorkbenchPermission workbench) {
        this.priority = priority;
        this.homePage = homePage;
        this.project = project;
        this.spaces = spaces;
        this.editor = editor;
        this.pages = pages;
        this.workbench = workbench;
    }

    public String getHomePage() {
        return homePage;
    }

    public Integer getPriority() {
        return priority;
    }

    public Permission getProject() {
        return project;
    }

    public Permission getSpaces() {
        return spaces;
    }

    public Permission getEditor() {
        return editor;
    }

    public Permission getPages() {
        return pages;
    }

    public WorkbenchPermission getWorkbench() {
        return workbench;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setProject(Permission project) {
        this.project = project;
    }

    public void setSpaces(Permission spaces) {
        this.spaces = spaces;
    }

    public void setEditor(Permission editor) {
        this.editor = editor;
    }

    public void setPages(Permission pages) {
        this.pages = pages;
    }

    public void setWorkbench(WorkbenchPermission workbench) {
        this.workbench = workbench;
    }
}

