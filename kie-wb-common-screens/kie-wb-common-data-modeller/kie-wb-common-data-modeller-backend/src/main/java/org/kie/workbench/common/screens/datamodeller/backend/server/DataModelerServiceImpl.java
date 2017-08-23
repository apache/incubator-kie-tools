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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.drools.core.base.ClassTypeResolver;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.utils.ProjectResourcePaths;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.kie.workbench.common.screens.datamodeller.backend.server.file.DataModelerCopyHelper;
import org.kie.workbench.common.screens.datamodeller.backend.server.file.DataModelerRenameHelper;
import org.kie.workbench.common.screens.datamodeller.backend.server.handler.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.backend.server.helper.DataModelerRenameWorkaroundHelper;
import org.kie.workbench.common.screens.datamodeller.backend.server.helper.DataModelerSaveHelper;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectRenamedEvent;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.TypeInfoResult;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.ServiceException;
import org.kie.workbench.common.services.backend.project.ProjectClassLoaderHelper;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.FilterHolder;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaRoasterModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.ProjectDataModelOracleUtils;
import org.kie.workbench.common.services.datamodeller.driver.impl.UpdateInfo;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.datamodeller.driver.model.ModelDriverResult;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.SegmentedPath;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;

@Service
@ApplicationScoped
public class DataModelerServiceImpl
        extends KieService<EditorModelContent>
        implements DataModelerService {

    private static final Logger logger = LoggerFactory.getLogger(DataModelerServiceImpl.class);

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private User identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private DataModelerServiceHelper serviceHelper;

    @Inject
    private ProjectClassLoaderHelper classLoaderHelper;

    @Inject
    private Event<DataObjectCreatedEvent> dataObjectCreatedEvent;

    @Inject
    private Event<DataObjectDeletedEvent> dataObjectDeletedEvent;

    @Inject
    private Event<DataObjectRenamedEvent> dataObjectRenamedEvent;

    @Inject
    private RefactoringQueryService queryService;

    @Inject
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @Inject
    private DeleteService deleteService;

    @Inject
    private CopyService copyService;

    @Inject
    private RenameService renameService;

    @Inject
    private DataModelerCopyHelper copyHelper;

    @Inject
    private DataModelerRenameHelper renameHelper;

    @Inject
    private Instance<DataModelerSaveHelper> saveHelperInstance;

    @Inject
    private Instance<DataModelerRenameWorkaroundHelper> renameHelperInstance;

    @Inject
    private GenericValidator genericValidator;

    @Inject
    @Any
    private Instance<DomainHandler> domainHandlers;

    @Inject
    private FilterHolder filterHolder;

    private static final String DEFAULT_COMMIT_MESSAGE = "Data modeller generated action.";

    public DataModelerServiceImpl() {
    }

    @Override
    public EditorModelContent loadContent(Path path) {
        return loadContent(path,
                           true);
    }

    @Override
    public EditorModelContent loadContent(final Path path,
                                          boolean includeTypesInfo) {
        EditorModelContent editorModelContent = super.loadContent(path);
        if (includeTypesInfo) {
            editorModelContent.setPropertyTypes(getBasePropertyTypes());
            editorModelContent.setAnnotationDefinitions(getAnnotationDefinitions());
        }
        return editorModelContent;
    }

    @Override
    public DataModel loadModel(final KieProject project) {
        Pair<DataModel, ModelDriverResult> resultPair = loadModel(project,
                                                                  true);
        return resultPair != null ? resultPair.getK1() : null;
    }

    @Override
    public Path createJavaFile(final Path context,
                               final String fileName,
                               final String comment) {
        return createJavaFile(context,
                              fileName,
                              comment,
                              null);
    }

    @Override
    public Path createJavaFile(final Path context,
                               final String fileName,
                               final String comment,
                               final Map<String, Object> options) {

        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(context).resolve(fileName);
        final Path newPath = Paths.convert(nioPath);

        if (ioService.exists(nioPath)) {
            throw new FileAlreadyExistsException(nioPath.toString());
        }

        try {

            final Package currentPackage = projectService.resolvePackage(context);
            String packageName = currentPackage.getPackageName();
            String className = fileName.substring(0,
                                                  fileName.indexOf(".java"));

            final KieProject currentProject = projectService.resolveProject(context);

            DataObject dataObject = new DataObjectImpl(packageName,
                                                       className);

            Iterator<DomainHandler> it = domainHandlers != null ? domainHandlers.iterator() : null;
            while (it != null && it.hasNext()) {
                it.next().setDefaultValues(dataObject,
                                           options);
            }

            String source = createJavaSource(dataObject);

            ioService.write(nioPath,
                            source,
                            serviceHelper.makeCommentedOption(comment));

            dataObjectCreatedEvent.fire(new DataObjectCreatedEvent(currentProject,
                                                                   dataObject));

            return newPath;
        } catch (Exception e) {
            //uncommon error.
            logger.error("It was not possible to create Java file, for path: " + context.toURI() + ", fileName: " + fileName,
                         e);
            throw new ServiceException("It was not possible to create Java file, for path: " + context.toURI() + ", fileName: " + fileName,
                                       e);
        }
    }

    @Override
    protected EditorModelContent constructContent(Path path,
                                                  Overview overview) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading editor model from path: " + path.toURI());
        }

        Long startTime = System.currentTimeMillis();
        EditorModelContent editorModelContent = new EditorModelContent();

        try {
            KieProject project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("File : " + path.toURI() + " do not belong to a valid project");
                return editorModelContent;
            }

            Pair<DataModel, ModelDriverResult> resultPair = loadModel(project,
                                                                      false);
            String className = calculateClassName(project,
                                                  path);

            editorModelContent.setCurrentProject(project);
            editorModelContent.setPath(path);
            editorModelContent.setCurrentProjectPackages(serviceHelper.resolvePackages(project));
            editorModelContent.setDataModel(resultPair.getK1());
            editorModelContent.setDataObject(resultPair.getK1().getDataObject(className));
            editorModelContent.setDataObjectPaths(resultPair.getK2().getClassPaths());

            editorModelContent.setOriginalClassName(className);
            editorModelContent.setOriginalPackageName(NamingUtils.extractPackageName(className));

            //Read the sources for the file being edited.
            if (ioService.exists(Paths.convert(path))) {
                String source = ioService.readAllString(Paths.convert(path));
                editorModelContent.setSource(source);
            }

            if (resultPair.getK2().hasErrors()) {
                editorModelContent.setErrors(serviceHelper.toDataModelerError(resultPair.getK2().getErrors()));
            }

            editorModelContent.setOverview(overview);

            editorModelContent.setElapsedTime(System.currentTimeMillis() - startTime);
            if (logger.isDebugEnabled()) {
                logger.debug("Time elapsed when loading editor model from:" + path + " : " + editorModelContent.getElapsedTime() + " ms");
            }

            return editorModelContent;
        } catch (Exception e) {
            logger.error("Editor model couldn't be loaded from path: " + (path != null ? path.toURI() : path) + ".",
                         e);
            throw new ServiceException("Editor model couldn't be loaded from path: " + (path != null ? path.toURI() : path) + ".",
                                       e);
        }
    }

    private Pair<DataModel, ModelDriverResult> loadModel(final KieProject project,
                                                         boolean processErrors) {

        if (logger.isDebugEnabled()) {
            logger.debug("Loading data model from path: " + project.getRootPath());
        }

        Long startTime = System.currentTimeMillis();

        DataModel dataModel = null;
        Path projectPath = null;
        Package defaultPackage = null;

        try {
            projectPath = project.getRootPath();
            defaultPackage = projectService.resolveDefaultPackage(project);
            if (logger.isDebugEnabled()) {
                logger.debug("Current project path is: " + projectPath);
            }

            ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(project);

            ModelDriver modelDriver = new JavaRoasterModelDriver(ioService,
                                                                 Paths.convert(defaultPackage.getPackageMainSrcPath()),
                                                                 classLoader,
                                                                 filterHolder);
            ModelDriverResult result = modelDriver.loadModel();
            dataModel = result.getDataModel();

            if (processErrors && result.hasErrors()) {
                processErrors(project,
                              result);
            }

            //by now we still use the DMO to calculate project external dependencies.
            ProjectDataModelOracle projectDataModelOracle = dataModelService.getProjectDataModel(projectPath);
            ProjectDataModelOracleUtils.loadExternalDependencies(dataModel,
                                                                 projectDataModelOracle,
                                                                 classLoader);

            Long endTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Time elapsed when loading " + projectPath.getFileName() + ": " + (endTime - startTime) + " ms");
            }

            return new Pair<DataModel, ModelDriverResult>(dataModel,
                                                          result);
        } catch (Exception e) {
            logger.error("Data model couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".",
                         e);
            throw new ServiceException("Data model couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".",
                                       e);
        }
    }

    public TypeInfoResult loadJavaTypeInfo(final String source) {

        try {
            JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();
            TypeInfoResult result = new TypeInfoResult();
            org.kie.workbench.common.services.datamodeller.driver.TypeInfoResult driverResult = modelDriver.loadJavaTypeInfo(source);
            result.setJavaTypeInfo(driverResult.getTypeInfo());
            if (driverResult.hasErrors()) {
                result.setErrors(serviceHelper.toDataModelerError(driverResult.getErrors()));
            }
            return result;
        } catch (Exception e) {
            logger.error("JavaTypeInfo object couldn't be loaded for source: " + source,
                         e);
            throw new ServiceException("JavaTypeInfo object couldn't be loaded for source.",
                                       e);
        }
    }

    public GenerationResult loadDataObject(final Path projectPath,
                                           final String source,
                                           final Path sourcePath) {

        if (logger.isDebugEnabled()) {
            logger.debug("Loading data object from projectPath: " + projectPath.toURI());
        }

        KieProject project;
        DataObject dataObject = null;

        try {

            project = projectService.resolveProject(projectPath);
            if (project == null) {
                return new GenerationResult(null,
                                            null,
                                            new ArrayList<DataModelerError>());
            }

            ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(project);
            JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver(ioService,
                                                                            null,
                                                                            classLoader,
                                                                            filterHolder);
            ModelDriverResult driverResult = modelDriver.loadDataObject(source,
                                                                        Paths.convert(sourcePath));

            if (!driverResult.hasErrors()) {
                if (driverResult.getDataModel().getDataObjects().size() > 0) {
                    dataObject = driverResult.getDataModel().getDataObjects().iterator().next();
                }
                return new GenerationResult(source,
                                            dataObject,
                                            new ArrayList<DataModelerError>());
            } else {
                return new GenerationResult(source,
                                            null,
                                            serviceHelper.toDataModelerError(driverResult.getErrors()));
            }
        } catch (Exception e) {
            logger.error("Data object couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".",
                         e);
            throw new ServiceException("Data object couldn't be loaded, path: " + projectPath + ", projectPath: " + projectPath + ".",
                                       e);
        }
    }

    /**
     * Updates Java code provided in the source parameter with the data object values provided in the dataObject
     * parameter. This method does not write any changes in the file system.
     * @param source Java code to be updated.
     * @param path Path to the java file. (used for error messages adf and project )
     * @param dataObject Data object definition.
     * @return returns a GenerationResult object with the updated Java code and the dataObject parameter as is.
     */
    @Override
    public GenerationResult updateSource(final String source,
                                         final Path path,
                                         final DataObject dataObject) {

        GenerationResult result = new GenerationResult();
        KieProject project;

        try {

            project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("File : " + path.toURI() + " do not belong to a valid project");
                result.setSource(source);
                return result;
            }

            ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(project);
            Pair<String, List<DataModelerError>> updateResult = updateJavaSource(source,
                                                                                 dataObject,
                                                                                 new HashMap<String, String>(),
                                                                                 new ArrayList<String>(),
                                                                                 classLoader);
            result.setSource(updateResult.getK1());
            result.setDataObject(dataObject);
            result.setErrors(updateResult.getK2());

            return result;
        } catch (Exception e) {
            logger.error("Source file for data object: " + dataObject.getClassName() + ", couldn't be updated",
                         e);
            throw new ServiceException("Source file for data object: " + dataObject.getClassName() + ", couldn't be updated",
                                       e);
        }
    }

    /**
     * Updates data object provided in the dataObject parameter with the Java code provided in the source parameter.
     * This method does not write changes in the file system.
     * @param dataObject Data object definition to be updated.
     * @param source Java code to use for the update.
     * @param path Path to the java file. (used for error messages adf)
     * @return returns a GenerationResult object with the updated data object and the source and path parameter as is.
     */
    @Override
    public GenerationResult updateDataObject(final DataObject dataObject,
                                             final String source,
                                             final Path path) {
        //Resolve the dataobject update in memory

        GenerationResult result = new GenerationResult();
        KieProject project;

        try {
            result.setSource(source);
            project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("File : " + path.toURI() + " do not belong to a valid project");
                result.setSource(source);
                return result;
            }

            ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(project);
            JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver(ioService,
                                                                            Paths.convert(path),
                                                                            classLoader,
                                                                            filterHolder);
            ModelDriverResult driverResult = modelDriver.loadDataObject(source,
                                                                        Paths.convert(path));

            if (driverResult.hasErrors()) {
                result.setErrors(serviceHelper.toDataModelerError(driverResult.getErrors()));
            } else {
                if (driverResult.getDataModel().getDataObjects().size() > 0) {
                    result.setDataObject(driverResult.getDataModel().getDataObjects().iterator().next());
                }
            }

            return result;
        } catch (Exception e) {
            logger.error("Source file for data object: " + dataObject.getClassName() + ", couldn't be parsed",
                         e);
            throw new ServiceException("Source file for data object: " + dataObject.getClassName() + ", couldn't be parsed",
                                       e);
        }
    }

    @Override
    public GenerationResult saveSource(final String source,
                                       final Path path,
                                       final DataObject dataObject,
                                       final Metadata metadata,
                                       final String commitMessage) {
        return saveSource(source,
                          path,
                          dataObject,
                          metadata,
                          commitMessage,
                          null,
                          null);
    }

    @Override
    public GenerationResult saveSource(final String source,
                                       final Path path,
                                       final DataObject dataObject,
                                       final Metadata metadata,
                                       final String commitMessage,
                                       final String newPackageName,
                                       final String newFileName) {

        boolean onBatch = false;

        try {

            GenerationResult result = resolveSaveSource(source,
                                                        path,
                                                        dataObject);

            Package currentPackage = projectService.resolvePackage(path);
            Package targetPackage = currentPackage;
            String targetName = path.getFileName();
            org.uberfire.java.nio.file.Path targetPath = Paths.convert(path);

            boolean packageChanged = false;
            boolean nameChanged = false;

            if (newPackageName != null && (currentPackage == null || !newPackageName.equals(currentPackage.getPackageName()))) {
                //make sure destination package exists.
                targetPackage = serviceHelper.ensurePackageStructure(projectService.resolveProject(path),
                                                                     newPackageName);
                packageChanged = true;
            }

            if (newFileName != null && !(newFileName + ".java").equals(path.getFileName())) {
                targetName = newFileName + ".java";
                nameChanged = true;
            }

            fireMetadataSocialEvents(path,
                                     metadataService.getMetadata(path),
                                     metadata);

            ioService.startBatch(targetPath.getFileSystem());
            onBatch = true;

            if (packageChanged) {
                targetPath = Paths.convert(targetPackage.getPackageMainSrcPath()).resolve(targetName);
                ioService.write(Paths.convert(path),
                                result.getSource(),
                                metadataService.setUpAttributes(path,
                                                                metadata),
                                serviceHelper.makeCommentedOption(commitMessage));

                //deleteService.delete( path, commitMessage );
                ioService.move(Paths.convert(path),
                               targetPath,
                               serviceHelper.makeCommentedOption(commitMessage));
                result.setPath(Paths.convert(targetPath));
            } else if (nameChanged) {
                ioService.write(Paths.convert(path),
                                result.getSource(),
                                metadataService.setUpAttributes(path,
                                                                metadata),
                                serviceHelper.makeCommentedOption(commitMessage));

                Path newPath = renameService.rename(path,
                                                    newFileName,
                                                    commitMessage);
                result.setPath(newPath);
            } else {
                ioService.write(Paths.convert(path),
                                result.getSource(),
                                metadataService.setUpAttributes(path,
                                                                metadata),
                                serviceHelper.makeCommentedOption(commitMessage));
                result.setPath(path);
            }

            if (saveHelperInstance != null) {
                for (DataModelerSaveHelper saveHelper : saveHelperInstance) {
                    saveHelper.postProcess(path,
                                           result.getPath());
                }
            }

            return result;
        } catch (Exception e) {
            logger.error("Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + (dataObject != null ? dataObject.getClassName() : null) + ".",
                         e);
            throw new ServiceException("Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + (dataObject != null ? dataObject.getClassName() : null) + ".",
                                       e);
        } finally {
            if (onBatch) {
                ioService.endBatch();
            }
        }
    }

    private GenerationResult resolveSaveSource(final String source,
                                               final Path path,
                                               final DataObject dataObject) {
        GenerationResult result = new GenerationResult();
        KieProject project;
        String updatedSource;

        try {

            project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("File : " + path.toURI() + " do not belong to a valid project");
                result.setSource(source);
                return result;
            }

            if (dataObject != null) {
                //the source needs to be updated with the DataObject definition prior to save
                result = updateSource(source,
                                      path,
                                      dataObject);
                updatedSource = result.getSource();
            } else {
                //if the dataObject wasn't provided the source is already prepared to be saved and likely
                //it's not parsed at the ui. So we will save the provided source and try to parse the data object
                updatedSource = source;
            }

            if (dataObject == null) {
                ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(project);
                JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver(ioService,
                                                                                Paths.convert(path),
                                                                                classLoader,
                                                                                filterHolder);
                ModelDriverResult driverResult = modelDriver.loadDataObject(source,
                                                                            Paths.convert(path));

                if (driverResult.hasErrors()) {
                    result.setErrors(serviceHelper.toDataModelerError(driverResult.getErrors()));
                } else {
                    if (driverResult.getDataModel().getDataObjects().size() > 0) {
                        result.setDataObject(driverResult.getDataModel().getDataObjects().iterator().next());
                    }
                }
            }

            result.setSource(updatedSource);

            return result;
        } catch (Exception e) {
            logger.error("Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + (dataObject != null ? dataObject.getClassName() : null) + ".",
                         e);
            throw new ServiceException("Source file couldn't be updated, path: " + path.toURI() + ", dataObject: " + (dataObject != null ? dataObject.getClassName() : null) + ".",
                                       e);
        }
    }

    public Path copy(final Path path,
                     final String newName,
                     final String newPackageName,
                     final Path targetDirectory,
                     final String comment,
                     final boolean refactor) {
        Path targetPath = null;
        if (refactor) {
            try {
                GenerationResult refactoringResult = refactorClass(path,
                                                                   newPackageName,
                                                                   newName);
                if (!refactoringResult.hasErrors()) {
                    targetPath = Paths.convert(Paths.convert(targetDirectory).resolve(newName + ".java"));
                    copyHelper.addRefactoredPath(targetPath,
                                                 refactoringResult.getSource(),
                                                 comment);
                    KieProject project = projectService.resolveProject(targetPath);
                    if (project != null) {
                        dataObjectCreatedEvent.fire(new DataObjectCreatedEvent(project,
                                                                               refactoringResult.getDataObject()));
                    }
                }
            } catch (Exception e) {
                //if the refactoring fails for whatever reason the file still needs to be copied.
                logger.error("An error was produced during class refactoring at file copying for file: " + path + ". The file copying will continue without class refactoring",
                             e);
            }
        }
        try {
            return copyService.copy(path,
                                    newName,
                                    targetDirectory,
                                    comment);
        } finally {
            if (targetPath != null) {
                copyHelper.removeRefactoredPath(targetPath);
            }
        }
    }

    public Path rename(final Path path,
                       final String newName,
                       String comment,
                       final boolean refactor,
                       final boolean saveCurrentChanges,
                       final String source,
                       final DataObject dataObject,
                       final Metadata metadata) {

        GenerationResult saveResult = null;
        if (saveCurrentChanges) {
            saveResult = resolveSaveSource(source,
                                           path,
                                           dataObject);
            ioService.write(Paths.convert(path),
                            saveResult.getSource(),
                            metadataService.setUpAttributes(path,
                                                            metadata),
                            serviceHelper.makeCommentedOption(comment));
        }

        Path targetPath = null;
        String newContent = null;
        if (refactor) {
            String sourceToRefactor;
            if (saveCurrentChanges) {
                sourceToRefactor = (saveResult != null && !saveResult.hasErrors()) ? saveResult.getSource() : null;
            } else {
                sourceToRefactor = source;
            }

            if (sourceToRefactor != null) {
                try {
                    GenerationResult refactoringResult = refactorClass(sourceToRefactor,
                                                                       path,
                                                                       null,
                                                                       newName);
                    if (!refactoringResult.hasErrors()) {
                        targetPath = Paths.convert(Paths.convert(path).resolveSibling(newName + ".java"));
                        renameHelper.addRefactoredPath(targetPath,
                                                       refactoringResult.getSource(),
                                                       comment);
                        //TODO send data object renamed event.
                        newContent = refactoringResult.getSource();
                    }
                } catch (Exception e) {
                    //if the refactoring fails for whatever reason the file still needs to be renamed.
                    logger.error("An error was produced during class refactoring at file renaming for file: " + path + ". The file renaming will continue without class refactoring",
                                 e);
                }
            }
        }
        try {

            //TODO we need to investigate why we have a DeleteEvent, and a CreateEvent for the case of .java files.
            boolean workaround = true;
            if (!workaround) {
                return renameService.rename(path,
                                            newName,
                                            comment);
            } else {
                //I will implement the rename here as a workaround
                //remove this workaround when we can find the error.
                Path updatedPath = renameWorkaround(path,
                                                    newName,
                                                    newContent,
                                                    comment);
                dataObjectRenamedEvent.fire((DataObjectRenamedEvent) new DataObjectRenamedEvent().withPath(updatedPath));
                return updatedPath;
            }
        } finally {
            if (targetPath != null) {
                renameHelper.removeRefactoredPath(targetPath);
            }
        }
    }

    public Path renameWorkaround(final Path path,
                                 final String newName,
                                 final String newContent,
                                 final String comment) {
        try {

            final org.uberfire.java.nio.file.Path _path = Paths.convert(path);

            String originalFileName = _path.getFileName().toString();
            final String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            final org.uberfire.java.nio.file.Path _target = _path.resolveSibling(newName + extension);
            final Path targetPath = Paths.convert(_target);

            try {

                if (newContent != null) {
                    //first overwrite the content with the new content, then we can rename the file.
                    //this is the workaround.
                    ioService.write(_path,
                                    newContent,
                                    serviceHelper.makeCommentedOption(comment));
                }

                ioService.startBatch(new FileSystem[]{_target.getFileSystem()});

                ioService.move(_path,
                               _target,
                               serviceHelper.makeCommentedOption("File [" + path.toURI() + "] renamed to [" + targetPath.toURI() + "].")
                );

                if (renameHelperInstance != null) {
                    for (DataModelerRenameWorkaroundHelper renameHelper : renameHelperInstance) {
                        renameHelper.postProcess(path,
                                                 targetPath);
                    }
                }
            } catch (final Exception e) {
                throw e;
            } finally {
                ioService.endBatch();
            }

            return Paths.convert(_target);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public String getSource(Path path) {
        org.uberfire.java.nio.file.Path convertedPath = Paths.convert(path);
        return ioService.readAllString(convertedPath);
    }

    private void processErrors(KieProject project,
                               ModelDriverResult result) {
        PublishBatchMessagesEvent publishEvent = new PublishBatchMessagesEvent();
        publishEvent.setCleanExisting(true);
        publishEvent.setUserId(identity != null ? identity.getIdentifier() : null);
        publishEvent.setMessageType("DataModeler");

        SystemMessage systemMessage;
        for (DriverError error : result.getErrors()) {
            systemMessage = new SystemMessage();
            systemMessage.setMessageType("DataModeler");
            systemMessage.setLevel(Level.ERROR);
            systemMessage.setId(error.getId());
            systemMessage.setText(error.getMessage());
            systemMessage.setColumn(error.getColumn());
            systemMessage.setLine(error.getLine());
            systemMessage.setPath(error.getFile());
            publishEvent.getMessagesToPublish().add(systemMessage);
        }

        publishBatchMessagesEvent.fire(publishEvent);
    }

    @Override
    public GenerationResult saveModel(final DataModel dataModel,
                                      final KieProject project,
                                      final boolean overwrite,
                                      final String commitMessage) {

        Long startTime = System.currentTimeMillis();
        boolean onBatch = false;

        try {

            //Start IOService bath processing. IOService batch processing causes a blocking operation on the file system
            //to it must be treated carefully.
            CommentedOption option = serviceHelper.makeCommentedOption(commitMessage);
            ioService.startBatch(Paths.convert(project.getRootPath()).getFileSystem());
            onBatch = true;

            generateModel(dataModel,
                          project,
                          option);

            onBatch = false;
            ioService.endBatch();

            Long endTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Time elapsed when saving " + project.getProjectName() + ": " + (endTime - startTime) + " ms");
            }

            GenerationResult result = new GenerationResult();
            result.setGenerationTime(endTime - startTime);
            return result;
        } catch (Exception e) {
            logger.error("An error was produced during data model adf, dataModel: " + dataModel + ", path: " + project.getRootPath(),
                         e);
            if (onBatch) {
                try {
                    logger.warn("IOService batch method is still on, trying to end batch processing.");
                    ioService.endBatch();
                    logger.warn("IOService batch method is was successfully finished. The user will still get the exception, but the batch processing was finished.");
                } catch (Exception ex) {
                    logger.error("An error was produced when the IOService.endBatch processing was executed.",
                                 ex);
                }
            }
            throw new ServiceException("Data model couldn't be generated due to the following error. " + e);
        }
    }

    @Override
    public GenerationResult saveModel(DataModel dataModel,
                                      final KieProject project) {

        return saveModel(dataModel,
                         project,
                         false,
                         DEFAULT_COMMIT_MESSAGE);
    }

    @Override
    public void delete(final Path path,
                       final String comment) {
        try {
            KieProject project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("File : " + path.toURI() + " do not belong to a valid project");
                return;
            }
            deleteService.delete(path,
                                 comment);
            String className = calculateClassName(project,
                                                  path);
            DataObject dataObject = new DataObjectImpl(
                    NamingUtils.extractPackageName(className),
                    NamingUtils.extractClassName(className));
            dataObjectDeletedEvent.fire(new DataObjectDeletedEvent(project,
                                                                   dataObject));
        } catch (final Exception e) {
            logger.error("File: " + path.toURI() + " couldn't be deleted due to the following error. ",
                         e);
            throw new ServiceException("File: " + path.toURI() + " couldn't be deleted due to the following error. " + e.getMessage());
        }
    }

    @Override
    public GenerationResult refactorClass(final Path path,
                                          final String newPackageName,
                                          final String newClassName) {
        final String source = ioService.readAllString(Paths.convert(path));
        return refactorClass(source,
                             path,
                             newPackageName,
                             newClassName);
    }

    private GenerationResult refactorClass(final String source,
                                           final Path path,
                                           final String newPackageName,
                                           final String newClassName) {
        GenerationResult result = loadDataObject(path,
                                                 source,
                                                 path);
        if ((result.getErrors() == null || result.getErrors().isEmpty()) && result.getDataObject() != null) {

            final DataObject dataObject = result.getDataObject();

            if (newPackageName != null) {
                dataObject.setPackageName(newPackageName);
            }
            if (newClassName != null) {
                dataObject.setName(newClassName);
            }

            return updateSource(source,
                                path,
                                dataObject);
        } else {
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ValidationMessage> validate(final String source,
                                            final Path path,
                                            final DataObject dataObject) {

        try {
            String validationSource = null;
            List<ValidationMessage> validations = new ArrayList<ValidationMessage>();

            KieProject project = projectService.resolveProject(path);
            if (project == null) {
                logger.warn("File : " + path.toURI() + " do not belong to a valid project");
                ValidationMessage validationMessage = new ValidationMessage();
                validationMessage.setPath(path);
                validationMessage.setText("File do no belong to a valid project");
                validationMessage.setLevel(Level.ERROR);
                validations.add(new ValidationMessage());
                return validations;
            }

            if (dataObject != null) {
                //the source needs to be updated with the DataObject definition prior to validation calculation.
                //we must to the same processing as if the file was about to be saved.
                GenerationResult result = updateSource(source,
                                                       path,
                                                       dataObject);
                if (!result.hasErrors()) {
                    validationSource = result.getSource();
                } else {
                    //it was not possible to update the source with the data object definition.
                    return serviceHelper.toValidationMessage(result.getErrors());
                }
            } else {
                validationSource = source;
            }

            return genericValidator.validate(path,
                                             validationSource != null ? validationSource : "");
        } catch (Exception e) {
            logger.error("An error was produced during validation",
                         e);
            throw new ServiceException("An error was produced during validation",
                                       e);
        }
    }

    private void generateModel(DataModel dataModel,
                               KieProject project,
                               CommentedOption option) throws Exception {

        org.uberfire.java.nio.file.Path targetFile;
        org.uberfire.java.nio.file.Path javaRootPath;
        String newSource;

        //ensure java sources directory exists.
        Path projectPath = project.getRootPath();
        javaRootPath = ensureProjectJavaPath(Paths.convert(projectPath));

        for (DataObject dataObject : dataModel.getDataObjects()) {
            targetFile = calculateFilePath(dataObject.getClassName(),
                                           javaRootPath);
            if (logger.isDebugEnabled()) {
                logger.debug("Data object: " + dataObject.getClassName() + " java source code will be generated from scratch and written into file: " + targetFile);
            }
            newSource = createJavaSource(dataObject);
            ioService.write(targetFile,
                            newSource,
                            option);
        }
    }

    private Pair<String, List<DataModelerError>> updateJavaSource(String originalSource,
                                                                  DataObject dataObject,
                                                                  Map<String, String> renames,
                                                                  List<String> deletions,
                                                                  ClassLoader classLoader) throws Exception {

        String newSource;
        ClassTypeResolver classTypeResolver;
        List<DataModelerError> errors = new ArrayList<DataModelerError>();

        if (logger.isDebugEnabled()) {
            logger.debug("Starting java source update for class: " + dataObject.getClassName());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("original source is: " + originalSource);
        }

        JavaType<?> javaType = Roaster.parse(originalSource);
        if (javaType.isClass()) {
            if (javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                //if a file has parsing errors it will be skipped.
                errors.addAll(serviceHelper.toDataModelerError(javaType.getSyntaxErrors(),
                                                               null));
                newSource = originalSource;
            } else {
                JavaClassSource javaClassSource = (JavaClassSource) javaType;
                classTypeResolver = DriverUtils.createClassTypeResolver(javaClassSource,
                                                                        classLoader);
                updateJavaClassSource(dataObject,
                                      javaClassSource,
                                      renames,
                                      deletions,
                                      classTypeResolver);
                newSource = javaClassSource.toString();
            }
        } else {
            logger.debug("No Class definition was found for source: " + originalSource + ", original source won't be modified.");
            newSource = originalSource;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("updated source is: " + newSource);
        }
        return new Pair<String, List<DataModelerError>>(newSource,
                                                        errors);
    }

    private void updateJavaClassSource(DataObject dataObject,
                                       JavaClassSource javaClassSource,
                                       Map<String, String> renames,
                                       List<String> deletions,
                                       ClassTypeResolver classTypeResolver) throws Exception {

        if (javaClassSource == null || !javaClassSource.isClass()) {
            logger.warn("A null javaClassSource or javaClassSouce is not a Class, no processing will be done. javaClassSource: " + javaClassSource + " className: " + (javaClassSource != null ? javaClassSource.getName() : null));
            return;
        }

        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver(filterHolder);
        UpdateInfo updateInfo = new UpdateInfo();

        //prepare additional update info prior update

        if (renames != null) {
            for (Map.Entry<String, String> entry : renames.entrySet()) {
                updateInfo.addClassRename(entry.getKey(),
                                          entry.getValue());
            }
        }
        if (deletions != null) {
            for (String deletion : deletions) {
                updateInfo.addDeletedClass(deletion);
            }
        }

        modelDriver.updateSource(javaClassSource,
                                 dataObject,
                                 updateInfo,
                                 classTypeResolver);
    }

    private String createJavaSource(DataObject dataObject) throws Exception {

        GenerationContext generationContext = new GenerationContext(null);
        String source;
        GenerationEngine engine;

        try {
            engine = GenerationEngine.getInstance();
            source = engine.generateJavaClassString(generationContext,
                                                    dataObject);
        } catch (Exception e) {
            logger.error("Java source for dataObject: " + dataObject.getClassName() + " couldn't be created.",
                         e);
            throw e;
        }
        return source;
    }

    @Override
    public List<Path> findClassUsages(Path currentPath,
                                      String className) {

        KieProject project = projectService.resolveProject(currentPath);

        if (project == null) {
            return Collections.emptyList();
        }

        String branch = "master";
        if (currentPath instanceof SegmentedPath) {
            branch = ((SegmentedPath) currentPath).getSegmentId();
        }

        QueryOperationRequest request = QueryOperationRequest
                .references(className,
                            ResourceType.JAVA)
                .inProjectRootPathURI(project.getRootPath().toURI())
                .onBranch(branch);
        return executeReferencesQuery(request);
    }

    @Override
    public List<Path> findFieldUsages(Path currentPath,
                                      String className,
                                      String fieldName) {

        KieProject project = projectService.resolveProject(currentPath);

        String branch = "master";
        if (currentPath instanceof SegmentedPath) {
            branch = ((SegmentedPath) currentPath).getSegmentId();
        }

        QueryOperationRequest request = QueryOperationRequest
                .referencesPart(className,
                                fieldName,
                                PartType.FIELD)
                .inProjectRootPathURI(project.getRootPath().toURI())
                .onBranch(branch);
        return executeReferencesQuery(request);
    }

    @Override
    public List<String> findPersistableClasses(final Path path) {
        List<String> classes = new ArrayList<String>();
        KieProject project = projectService.resolveProject(path);
        if (project != null) {
            DataModel dataModel = loadModel(project);
            if (dataModel != null) {
                for (DataObject dataObject : dataModel.getDataObjects()) {
                    if (dataObject.getAnnotation(Entity.class.getName()) != null) {
                        classes.add(dataObject.getClassName());
                    }
                }
            }
        }
        return classes;
    }

    @Override
    public Boolean isPersistableClass(String className,
                                      Path path) {
        //check the project class path to see if the class is defined likely in a project dependency or in curren project.
        KieProject project = projectService.resolveProject(path);
        if (project != null) {
            ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(project);
            try {
                classLoader.loadClass(className);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private List<Path> executeReferencesQuery(QueryOperationRequest request) {

        List<Path> results = new ArrayList<Path>();
        try {

            final List<RefactoringPageRow> queryResults = queryService.queryToList(request);
            if (queryResults != null) {
                for (RefactoringPageRow row : queryResults) {
                    results.add((org.uberfire.backend.vfs.Path) row.getValue());
                }
            }
            return results;
        } catch (Exception e) {
            String msg = "Unable to query lucene index for resource references: " + e.getMessage();
            logger.error(msg);
            throw new ServiceException(msg,
                                       e);
        }
    }

    @Override
    public List<PropertyType> getBasePropertyTypes() {
        List<PropertyType> types = new ArrayList<PropertyType>();
        types.addAll(PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes());
        return types;
    }

    @Override
    public Map<String, AnnotationDefinition> getAnnotationDefinitions() {
        Map<String, AnnotationDefinition> annotations = new HashMap<String, AnnotationDefinition>();

        //add additional annotations configured by external domains
        Iterator<DomainHandler> it = domainHandlers != null ? domainHandlers.iterator() : null;
        DomainHandler domainHandler;
        List<List<AnnotationDefinition>> allDomainsAnnotations = new ArrayList<List<AnnotationDefinition>>();

        while (it != null && it.hasNext()) {
            domainHandler = it.next();
            allDomainsAnnotations.add(domainHandler.getManagedAnnotations());
        }

        List<AnnotationDefinition> coreAnnotationDefinitions = (new JavaRoasterModelDriver()).getConfiguredAnnotations();
        allDomainsAnnotations.add(coreAnnotationDefinitions);

        for (List<AnnotationDefinition> annotationDefinitionList : allDomainsAnnotations) {
            if (annotationDefinitionList != null) {
                for (AnnotationDefinition annotationDefinition : annotationDefinitionList) {
                    annotations.put(annotationDefinition.getClassName(),
                                    annotationDefinition);
                }
            }
        }

        return annotations;
    }

    @Override
    public Boolean exists(Path path) {
        return ioService.exists(Paths.convert(path));
    }

    @Override
    public AnnotationSourceResponse resolveSourceRequest(AnnotationSourceRequest sourceRequest) {
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();
        return modelDriver.resolveSourceRequest(sourceRequest);
    }

    @Override
    public List<ValidationMessage> validateValuePair(String annotationClassName,
                                                     ElementType target,
                                                     String valuePairName,
                                                     String literalValue) {
        //Currently we only validate the syntax but additional checks may be added.
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();
        Pair<AnnotationSource<JavaClassSource>, List<DriverError>> parseResult =
                modelDriver.parseAnnotationWithValuePair(annotationClassName,
                                                         target,
                                                         valuePairName,
                                                         literalValue);
        if (parseResult.getK2() != null && parseResult.getK2().size() > 0) {
            ValidationMessage validationMessage;
            for (DriverError driverError : parseResult.getK2()) {
                validationMessage = new ValidationMessage();
                validationMessage.setText(driverError.getMessage());
                validationMessage.setColumn(driverError.getColumn());
                validationMessage.setLine(driverError.getLine());
                validationMessage.setLevel(Level.ERROR);
                validationMessages.add(validationMessage);
            }
        }
        return validationMessages;
    }

    @Override
    public AnnotationParseResponse resolveParseRequest(AnnotationParseRequest parseRequest,
                                                       KieProject kieProject) {
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();
        Pair<Annotation, List<DriverError>> driverResult = modelDriver.parseAnnotationWithValuePair(
                parseRequest.getAnnotationClassName(),
                parseRequest.getTarget(),
                parseRequest.getValuePairName(),
                parseRequest.getValuePairLiteralValue(),
                classLoaderHelper.getProjectClassLoader(kieProject));

        AnnotationParseResponse response = new AnnotationParseResponse(driverResult.getK1());
        response.withErrors(driverResult.getK2());
        return response;
    }

    @Override
    public AnnotationDefinitionResponse resolveDefinitionRequest(AnnotationDefinitionRequest definitionRequest,
                                                                 KieProject kieProject) {

        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();
        ClassLoader classLoader = classLoaderHelper.getProjectClassLoader(kieProject);
        ClassTypeResolver classTypeResolver = DriverUtils.createClassTypeResolver(classLoader);
        AnnotationDefinitionResponse definitionResponse = new AnnotationDefinitionResponse();

        try {
            AnnotationDefinition annotationDefinition = modelDriver.buildAnnotationDefinition(definitionRequest.getClassName(),
                                                                                              classTypeResolver);
            definitionResponse.withAnnotationDefinition(annotationDefinition);
        } catch (ModelDriverException e) {
            DriverError driverError = new DriverError(e.getMessage());
            definitionResponse.addError(driverError);
        }
        return definitionResponse;
    }

    private org.uberfire.java.nio.file.Path ensureProjectJavaPath(org.uberfire.java.nio.file.Path projectPath) {
        org.uberfire.java.nio.file.Path javaPath = projectPath.resolve("src");
        if (!ioService.exists(javaPath)) {
            javaPath = ioService.createDirectory(javaPath);
        }
        javaPath = javaPath.resolve("main");
        if (!ioService.exists(javaPath)) {
            javaPath = ioService.createDirectory(javaPath);
        }
        javaPath = javaPath.resolve("java");
        if (!ioService.exists(javaPath)) {
            javaPath = ioService.createDirectory(javaPath);
        }

        return javaPath;
    }

    /**
     * Given a path within a project calculates the expected class name for the given class.
     */
    private String calculateClassName(Project project,
                                      Path path) {

        String rootPathURI = project.getRootPath().toURI();
        String pathURI = path.toURI();
        String strPath = null;

        if (!pathURI.startsWith(rootPathURI)) {
            return null;
        }

        pathURI = pathURI.substring(rootPathURI.length() + 1,
                                    pathURI.length());

        if (pathURI.startsWith(ProjectResourcePaths.MAIN_SRC_PATH)) {
            strPath = pathURI.substring(ProjectResourcePaths.MAIN_SRC_PATH.length() + 1,
                                        pathURI.length());
        } else if (pathURI.startsWith(ProjectResourcePaths.TEST_SRC_PATH)) {
            strPath = pathURI.substring(ProjectResourcePaths.TEST_SRC_PATH.length() + 1,
                                        pathURI.length());
        }

        if (strPath == null) {
            return null;
        }

        strPath = strPath.replace("/",
                                  ".");
        strPath = strPath.substring(0,
                                    strPath.indexOf(".java"));

        return strPath;
    }

    /**
     * Given a className calculates the path to the java file allocating the corresponding pojo.
     */
    private org.uberfire.java.nio.file.Path calculateFilePath(String className,
                                                              org.uberfire.java.nio.file.Path javaPath) {

        String name = NamingUtils.extractClassName(className);
        String packageName = NamingUtils.extractPackageName(className);
        org.uberfire.java.nio.file.Path filePath = javaPath;

        if (packageName != null) {
            List<String> packageNameTokens = tokenizePackageName(packageName);
            for (String token : packageNameTokens) {
                filePath = filePath.resolve(token);
            }
        }

        filePath = filePath.resolve(name + ".java");
        return filePath;
    }

    public List<String> tokenizePackageName(final String packageName) {
        List<String> tokens = new ArrayList<String>();

        if (packageName != null) {
            StringTokenizer st = new StringTokenizer(packageName,
                                                     ".");
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }
        }
        return tokens;
    }
}
