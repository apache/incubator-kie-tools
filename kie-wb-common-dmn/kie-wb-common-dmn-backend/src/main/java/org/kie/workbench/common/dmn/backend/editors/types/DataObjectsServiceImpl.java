/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectsService;
import org.kie.workbench.common.dmn.api.marshalling.DMNPathsHelper;
import org.uberfire.backend.vfs.Path;

@Service
@Dependent
public class DataObjectsServiceImpl implements DataObjectsService {

    private final DMNPathsHelper pathsHelper;

    @Inject
    public DataObjectsServiceImpl(final DMNPathsHelper pathsHelper) {
        this.pathsHelper = pathsHelper;
    }

    @Override
    public List<DataObject> loadDataObjects(final WorkspaceProject workspaceProject) {

        final List<DataObject> dos = new ArrayList<>();
        final List<Path> javaFiles = pathsHelper.getDataObjectsPaths(workspaceProject);
        if (javaFiles.size() > 0) {
            for (final Path file : javaFiles) {
                final DataObject dataObject = new DataObject(file.getFileName());
                dos.add(dataObject);
            }
        }

        return dos;
    }
}
