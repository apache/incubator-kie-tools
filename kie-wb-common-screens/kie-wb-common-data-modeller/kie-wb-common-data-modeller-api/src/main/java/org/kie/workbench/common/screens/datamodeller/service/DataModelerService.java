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
import java.util.Set;

import org.guvnor.common.services.project.model.*;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.model.TypeInfoResult;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DataModelerService {

    Path createJavaFile( final Path context, final String fileName, final String comment );

    DataModelTO loadModel( final KieProject project );

    EditorModelContent loadContent(final Path path);

    GenerationResult saveModel( final DataModelTO dataModel,
            final KieProject project,
            final boolean overwrite,
            final String commitMessage );

    GenerationResult saveModel( final DataModelTO dataModel,
            final KieProject project );

    GenerationResult saveSource( final String source, final Path path, final DataObjectTO dataObjectTO, final Metadata metadata, final String commitMessage );

    GenerationResult saveSource( final String source, final Path path, final DataObjectTO dataObjectTO, final Metadata metadata, final String commitMessage, final String newPackageName, final String newFileName );

    GenerationResult updateSource( final String source, final Path path, final DataObjectTO dataObjectTO );

    GenerationResult updateDataObject( final DataObjectTO dataObjectTO, final String source, final Path path );

    Path copy( final Path path, final String newName, final String comment, boolean refactor );

    public Path rename( final Path path, final String newName, String comment, final boolean refactor,
            final boolean saveCurrentChanges, final String source, final DataObjectTO dataObjectTO,
            final Metadata metadata );

    void delete( final Path path, final String comment );

    GenerationResult refactorClass( final Path path, final String newPackageName, final String newClassName );

    List<ValidationMessage> validate( String source, final Path path, DataObjectTO dataObjectTO );

    TypeInfoResult loadJavaTypeInfo( final String source);

    List<PropertyTypeTO> getBasePropertyTypes();

    Map<String, AnnotationDefinitionTO> getAnnotationDefinitions();

    Boolean verifiesHash( Path javaFile );

    List<Path> findClassUsages( String className );

    List<Path> findFieldUsages( String className, String fieldName );

    Boolean exists( Path path );

    Set<Package> resolvePackages( final Path path );

}
