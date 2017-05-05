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

package org.uberfire.ext.apps.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DirectoryBreadcrumb {

    private String name;
    private String uri;

    private DirectoryBreadcrumb(@MapsTo("name") String name,
                                @MapsTo("uri") String uri) {

        this.name = name;
        this.uri = uri;
    }

    public static List<DirectoryBreadcrumb> getBreadcrumbs(Directory directory) {
        List<DirectoryBreadcrumb> breadcrumbs = new ArrayList<DirectoryBreadcrumb>();
        breadcrumbs.add(new DirectoryBreadcrumb(directory.getName(),
                                                directory.getURI()));
        Directory tempParent = directory.getParent();
        while (tempParent != null) {
            breadcrumbs.add(new DirectoryBreadcrumb(tempParent.getName(),
                                                    tempParent.getURI()));
            tempParent = tempParent.getParent();
        }
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
