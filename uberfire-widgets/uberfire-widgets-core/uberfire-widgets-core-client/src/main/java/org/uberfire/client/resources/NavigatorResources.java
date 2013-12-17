/*
 * Copyright 2011 JBoss Inc
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
package org.uberfire.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * Wizard resources
 */
public interface NavigatorResources
        extends
        ClientBundle {

    public static final NavigatorResources INSTANCE = GWT.create( NavigatorResources.class );

    @Source("css/Navigator.css")
    NavigatorStyle css();

    public interface NavigatorStyle extends CssResource {

        String container();

        String navigator();

        String message();

        String author();

        String date();

        @ClassName("navigator-folder-icon")
        String navigatorFolderIcon();

        @ClassName("navigator-file-icon")
        String navigatoFileIcon();

        @ClassName("navigator-message")
        String navigatorMessage();

        String breadcrumb();

        @ClassName("breadcrumb-header")
        String breadcrumbHeader();

        @ClassName("breadcrumb-2nd-level")
        String breadcrumb2ndLevel();

        @ClassName("repo-name")
        String repoName();

        @ClassName("directory-name")
        String directory();

        @ClassName("tree-nav")
        String treeNav();

        @ClassName("tree")
        String tree();

        @ClassName("tree-folder")
        String treeFolder();

        @ClassName("tree-folder-header")
        String treeFolderHeader();

        @ClassName("tree-folder-name")
        String treeFolderName();

        @ClassName("tree-folder-content")
        String treeFolderContent();

        @ClassName("tree-item")
        String treeItem();

        @ClassName("tree-item-name")
        String treeItemName();

        @ClassName("tree-selected")
        String treeSelected();
    }
}