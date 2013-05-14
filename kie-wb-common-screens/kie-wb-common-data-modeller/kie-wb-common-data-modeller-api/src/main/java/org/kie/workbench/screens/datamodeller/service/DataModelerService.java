/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.screens.datamodeller.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.screens.datamodeller.model.PropertyTypeTO;
import org.uberfire.backend.vfs.Path;

import java.util.List;
import java.util.Map;

@Remote
public interface DataModelerService {

    Path createModel(Path context, String fileName);

    Path resolveProject(Path path);

    DataModelTO loadModel(final Path path);
    
    void saveModel(DataModelTO dataModel, final Path path);

    /**
     * Indicates if given path related to a project is inside project resources path. (src/main/resources or src/test/resources)
     *
     * e.g. MyProject/src/main/resources/myfiles is inside the project main resources path.
     * e.g. MyProject/src/test/java/org/jboss is not.
     *
     * @param resource The path to check.
     *
     * @return resource if the path is inside the resources path, null in any other case.
     *
     */
    Path resolveResourcePackage(final Path resource);

    List<PropertyTypeTO> getBasePropertyTypes();

    Map<String, Boolean> evaluateIdentifiers(String[] identifiers);

    Map<String, AnnotationDefinitionTO> getAnnotationDefinitions();

}
