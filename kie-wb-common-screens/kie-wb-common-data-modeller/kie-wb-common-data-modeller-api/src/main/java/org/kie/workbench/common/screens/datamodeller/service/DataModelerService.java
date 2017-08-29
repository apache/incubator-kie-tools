/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.TypeInfoResult;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceResponse;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;

@Remote
public interface DataModelerService {

    Path createJavaFile( final Path context, final String fileName, final String comment );

    Path createJavaFile(  final Path context, final String fileName, final String comment, Map<String, Object> options );

    EditorModelContent loadContent(final Path path);

    EditorModelContent loadContent(final Path path, boolean includeTypesInfo );

    DataModel loadModel(final KieProject project);

    GenerationResult saveModel( final DataModel dataModel,
            final KieProject project,
            final boolean overwrite,
            final String commitMessage );

    GenerationResult saveModel( final DataModel dataModel,
            final KieProject project );

    GenerationResult saveSource( final String source, final Path path, final DataObject dataObject, final Metadata metadata, final String commitMessage );

    GenerationResult saveSource( final String source, final Path path, final DataObject dataObject, final Metadata metadata, final String commitMessage, final String newPackageName, final String newFileName );

    GenerationResult updateSource( final String source, final Path path, final DataObject dataObject );

    GenerationResult updateDataObject(final DataObject dataObject, final String source, final Path path);

    Path copy( final Path path, final String newName, final String newPackageName, final Path targetDirectory,
               final String comment, boolean refactor );

    Path rename( final Path path, final String newName, String comment, final boolean refactor,
            final boolean saveCurrentChanges, final String source, final DataObject dataObject,
            final Metadata metadata );

    void delete( final Path path, final String comment );

    GenerationResult refactorClass( final Path path, final String newPackageName, final String newClassName );

    List<ValidationMessage> validate( String source, final Path path, DataObject dataObject );

    TypeInfoResult loadJavaTypeInfo( final String source);

    GenerationResult loadDataObject( final Path projectPath, final String source, final Path sourcePath );

    List<PropertyType> getBasePropertyTypes();

    Map<String, AnnotationDefinition> getAnnotationDefinitions();

    List<String> findPersistableClasses( final Path path );

    Boolean isPersistableClass( final String className, final Path path );

    Boolean exists( Path path );

    AnnotationSourceResponse resolveSourceRequest( AnnotationSourceRequest sourceRequest );

    List<ValidationMessage> validateValuePair( String annotationClassName, ElementType target, String valuePairName,
            String literalValue);

    AnnotationParseResponse resolveParseRequest( AnnotationParseRequest parseRequest, KieProject project );

    AnnotationDefinitionResponse resolveDefinitionRequest( AnnotationDefinitionRequest definitionRequest,
            KieProject kieProject );
}
