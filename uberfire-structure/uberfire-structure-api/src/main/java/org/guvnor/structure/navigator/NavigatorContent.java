/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.navigator;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class NavigatorContent {

    private String repoName;
    private Path root;
    private List<Path> breadcrumbs = new ArrayList<Path>();
    private List<DataContent> content = new ArrayList<DataContent>();

    public NavigatorContent() {
    }

    public NavigatorContent(final String repoName,
                            final Path root,
                            final List<Path> breadcrumbs,
                            final List<DataContent> content) {
        this.repoName = repoName;
        this.root = root;
        this.breadcrumbs = breadcrumbs;
        this.content = content;
    }

    public List<Path> getBreadcrumbs() {
        return breadcrumbs;
    }

    public List<DataContent> getContent() {
        return content;
    }

    public Path getRoot() {
        return root;
    }

    public String getRepoName() {
        return repoName;
    }
}
