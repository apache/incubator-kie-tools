/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation.storage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.dashbuilder.project.storage.ProjectStorageServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NavTreeStorage {

    public static final String NAV_TREE_FILE_NAME = "navtree.json";
    private NavTreeJSONMarshaller jsonMarshaller;
    private Logger log = LoggerFactory.getLogger(NavTreeStorage.class);
    private ProjectStorageServices projectStorageServices;

    public NavTreeStorage() {}

    @Inject
    public NavTreeStorage(ProjectStorageServices projectStorageServices) {
        this.projectStorageServices = projectStorageServices;
        this.jsonMarshaller = NavTreeJSONMarshaller.get();
    }

    public NavTree loadNavTree() {
        var navigation = projectStorageServices.getNavigation();
        if (navigation.isEmpty()) {
            return null;
        }
        try {
            return jsonMarshaller.fromJson(navigation.get());
        } catch (Exception e) {
            log.error("Error parsing json definition",
                    e);
            return null;
        }
    }

    public void saveNavTree(NavTree navTree) {
        try {
            var json = jsonMarshaller.toJson(navTree).toString();
            projectStorageServices.saveNavigation(json);
        } catch (Exception e) {
            log.error("Can't save the navigation tree.", e);
        }
    }
}
