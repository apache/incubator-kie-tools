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

package org.kie.workbench.common.screens.datamodeller.service;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DataModelerService {

    Path createModel( final Path context,
                      final String fileName );

    DataModelTO loadModel( final Project project );

    GenerationResult saveModel( final DataModelTO dataModel,
                                final Project project );

    List<PropertyTypeTO> getBasePropertyTypes();

    Map<String, Boolean> evaluateIdentifiers( final String[] identifiers );

    Map<String, AnnotationDefinitionTO> getAnnotationDefinitions();

}
