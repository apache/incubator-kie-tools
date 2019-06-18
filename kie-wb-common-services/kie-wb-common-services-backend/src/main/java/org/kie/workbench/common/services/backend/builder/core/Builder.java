/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.scanner.KieModuleMetaData;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.EncodingUtil;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.services.backend.builder.core.BaseFileNameResolver.getBaseFileName;
import static org.kie.workbench.common.services.backend.builder.core.BuildMessageBuilder.makeErrorMessage;
import static org.kie.workbench.common.services.backend.builder.core.BuildMessageBuilder.makeWarningMessage;
import static org.kie.workbench.common.services.backend.builder.core.MessageConverter.convertMessages;
import static org.kie.workbench.common.services.backend.builder.core.MessageConverter.convertValidationMessages;

public class Builder implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Builder.class);

    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found.\n" +
            "Please check the necessary external dependencies for this project are configured correctly.";

    private final GAV projectGAV;
    private final KieServices kieServices;
    private final KieFileSystem kieFileSystem;

    private final Module project;
    private final Path projectRoot;
    private final IOService ioService;
    private final String projectPrefix;

    private final Handles handles = new Handles();
    private final KieModuleService moduleService;
    private final ProjectImportsService importsService;
    private final List<BuildValidationHelper> buildValidationHelpers;
    private final Map<Path, BuildValidationHelper> nonKieResourceValidationHelpers = new HashMap<Path, BuildValidationHelper>();
    private final Map<Path, List<ValidationMessage>> nonKieResourceValidationHelperMessages = new HashMap<Path, List<ValidationMessage>>();
    private final DirectoryStream.Filter<Path> javaResourceFilter = new JavaFileFilter();
    private final DirectoryStream.Filter<Path> dotFileFilter = new DotFileFilter();
    private final Set<String> javaResources = new HashSet<String>();
    private final Predicate<String> classFilter;
    private KieBuilder kieBuilder;
    private LRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache;
    private LRUPomModelCache pomModelCache;
    private PackageNameWhiteListService packageNameWhiteListService;

    public Builder(final Module project,
                   final IOService ioService,
                   final KieModuleService moduleService,
                   final ProjectImportsService importsService,
                   final List<BuildValidationHelper> buildValidationHelpers,
                   final LRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache,
                   final LRUPomModelCache pomModelCache,
                   final PackageNameWhiteListService packageNameWhiteListService,
                   final Predicate<String> classFilter,
                   final KieBuilder kieBuilder,
                   final KieFileSystem kieFileSystem) {
        this.project = project;
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.importsService = importsService;
        this.buildValidationHelpers = buildValidationHelpers;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.classFilter = classFilter;

        this.projectGAV = project.getPom().getGav();
        this.projectRoot = Paths.convert(project.getRootPath());
        this.projectPrefix = projectRoot.toUri().toString();
        this.kieServices = KieServices.Factory.get();
        this.kieBuilder = kieBuilder;
        this.kieFileSystem = kieFileSystem;
        this.dependenciesClassLoaderCache = dependenciesClassLoaderCache;
        this.pomModelCache = pomModelCache;

        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(projectRoot);
        visitPaths(directoryStream);
    }

    public Builder(final Module project,
                   final IOService ioService,
                   final KieModuleService moduleService,
                   final ProjectImportsService importsService,
                   final List<BuildValidationHelper> buildValidationHelpers,
                   final LRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache,
                   final LRUPomModelCache pomModelCache,
                   final PackageNameWhiteListService packageNameWhiteListService,
                   final Predicate<String> classFilter) {
        this(project,
             ioService,
             moduleService,
             importsService,
             buildValidationHelpers,
             dependenciesClassLoaderCache,
             pomModelCache,
             packageNameWhiteListService,
             classFilter,
             null,
             KieServices.Factory.get().newKieFileSystem());
    }

    public Builder clone() {
        final KieFileSystem kieFileSystemClone = kieFileSystemClone();
        final KieBuilderImpl kieBuilder = kieBuilderClone(kieFileSystemClone);

        return new Builder(project,
                           ioService,
                           moduleService,
                           importsService,
                           buildValidationHelpers,
                           dependenciesClassLoaderCache,
                           pomModelCache,
                           packageNameWhiteListService,
                           classFilter,
                           kieBuilder,
                           kieFileSystemClone);
    }

    private KieFileSystem kieFileSystemClone() {
        return ((KieFileSystemImpl) this.kieFileSystem).clone();
    }

    private KieBuilderImpl kieBuilderClone(final KieFileSystem kieFileSystemClone) {
        KieBuilderImpl kieBuilder = null;

        if (this.kieBuilder != null) {
            kieBuilder = createKieBuilder(kieFileSystemClone);
            kieBuilder.setkModule((MemoryKieModule) ((KieBuilderImpl) this.kieBuilder).getKieModuleIgnoringErrors());
            kieBuilder.setTrgMfs(((KieFileSystemImpl) kieFileSystemClone).getMfs());
        }

        return kieBuilder;
    }

    public BuildResults build() {
        final BuildResults results = new BuildResults(projectGAV);
        synchronized (kieFileSystem) {
            //KieBuilder is not re-usable for successive "full" builds
            kieBuilder = createKieBuilder(kieFileSystem);

            //Record RTEs from KieBuilder - that can fail if a rule uses an inaccessible class
            try {
                final Results kieResults = ((InternalKieBuilder) kieBuilder).buildAll(classFilter).getResults();
                results.addAllBuildMessages(convertMessages(kieResults.getMessages(),
                                                            handles));
            } catch (LinkageError e) {
                final String msg = MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                                        e.getLocalizedMessage());
                logger.warn(msg);
                results.addBuildMessage(makeWarningMessage(msg));
            } catch (Throwable e) {
                final String msg = e.getLocalizedMessage();
                logger.error(msg,
                             e);
                results.addBuildMessage(makeErrorMessage(msg));
            } finally {
                final PomModel pomModel = ((KieBuilderImpl) kieBuilder).getPomModel();
                if (pomModel != null) {
                    pomModelCache.setEntry(project,
                                           pomModel);
                }
            }
        }

        //Add validate messages from external helpers
        for (Map.Entry<Path, BuildValidationHelper> e : nonKieResourceValidationHelpers.entrySet()) {
            final org.uberfire.backend.vfs.Path vfsPath = Paths.convert(e.getKey());
            final List<ValidationMessage> validationMessages = e.getValue().validate(vfsPath);
            nonKieResourceValidationHelperMessages.put(e.getKey(),
                                                       validationMessages);
            results.addAllBuildMessages(convertValidationMessages(validationMessages));
        }

        //Check external imports are available. These are loaded when a DMO is requested, but it's better to report them early
        final Path nioExternalImportsPath = projectRoot.resolve("project.imports");
        if (Files.exists(nioExternalImportsPath)) {
            final org.uberfire.backend.vfs.Path externalImportsPath = Paths.convert(nioExternalImportsPath);
            final ProjectImports projectImports = importsService.load(externalImportsPath);
            final Imports imports = projectImports.getImports();
            for (final Import item : imports.getImports()) {
                final String fullyQualifiedClassName = item.getType();
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass(item.getType());
                } catch (ClassNotFoundException cnfe) {
                    logger.warn(cnfe.getMessage());
                    final String msg = MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                                            fullyQualifiedClassName);
                    results.addBuildMessage(makeWarningMessage(msg));
                }
            }
        }

        //Store the project dependencies ClassLoader for optimization purposes.
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(getKieModuleIgnoringErrors(),
                                                                                                   DependencyFilter.COMPILE_FILTER);
        updateDependenciesClassLoader(project,
                                      kieModuleMetaData);

        results.addAllBuildMessages(verifyClasses(kieModuleMetaData));

        return results;
    }

    public BuildResults build(final Path resource,
                              final InputStream inputStream) {
        synchronized (kieFileSystem) {
            final String destinationPath = destinationPath(resource);
            final Resource inputStreamResource = KieServices.Factory.get().getResources().newInputStreamResource(new BufferedInputStream(inputStream));

            kieFileSystem.write(destinationPath,
                                inputStreamResource);
        }

        return build();
    }

    private List<BuildMessage> verifyClasses(final KieModuleMetaData kieModuleMetaData) {
        //At the end we are interested to ensure that external .jar files referenced as dependencies don't have
        // referential inconsistencies. We will at least provide a basic algorithm to ensure that if an external class
        // X references another external class Y, Y is also accessible by the class loader.
        return new ClassVerifier(kieModuleMetaData,
                                 getTypeSourceResolver(kieModuleMetaData)).verify(getWhiteList(kieModuleMetaData));
    }

    private WhiteList getWhiteList(final KieModuleMetaData kieModuleMetaData) {
        return packageNameWhiteListService.filterPackageNames(project,
                                                              kieModuleMetaData.getPackages());
    }

    private KieBuilderImpl createKieBuilder(final KieFileSystem kieFileSystem) {
        PomModel pomModel;
        final KieBuilderImpl kieBuilder = (KieBuilderImpl) kieServices.newKieBuilder(kieFileSystem);
        if ((pomModel = pomModelCache.getEntry(project)) != null) {
            kieBuilder.setPomModel(pomModel);
        }
        return kieBuilder;
    }

    private void updateDependenciesClassLoader(final Module project,
                                               final KieModuleMetaData kieModuleMetaData) {
        KieModule kieModule = moduleService.resolveModule(project.getPomXMLPath());
        if (kieModule != null) {
            dependenciesClassLoaderCache.setDependenciesClassLoader(kieModule,
                                                                    LRUModuleDependenciesClassLoaderCache.buildClassLoader(kieModule,
                                                                                                                           kieModuleMetaData));
        }
    }

    public IncrementalBuildResults addResource(final Path resource) {
        return addResource(resource,
                           ioService.newInputStream(resource));
    }

    private IncrementalBuildResults addResource(final Path resource,
                                                final InputStream inputStream) {
        checkNotNull("resource",
                     resource);

        //Only files can be processed
        if (!Files.isRegularFile(resource)) {
            return new IncrementalBuildResults(projectGAV);
        }

        checkAFullBuildHasBeenPerformed();

        //Resource Type might require "external" validation (i.e. it's not covered by Kie)
        final IncrementalBuildResults results = new IncrementalBuildResults(projectGAV);
        final BuildValidationHelper validator = getBuildValidationHelper(resource);
        if (validator != null) {
            final List<ValidationMessage> addedValidationMessages = validator.validate(Paths.convert(resource));

            results.addAllAddedMessages(convertValidationMessages(addedValidationMessages));
            results.addAllRemovedMessages(convertValidationMessages(nonKieResourceValidationHelperMessages.remove(resource)));

            nonKieResourceValidationHelpers.put(resource,
                                                validator);
            nonKieResourceValidationHelperMessages.put(resource,
                                                       addedValidationMessages);
        }

        synchronized (kieFileSystem) {
            addNewResource(resource,
                           inputStream);
            buildIncrementally(results,
                               destinationPath(resource));
        }

        return results;
    }

    private void addNewResource(final Path path,
                                final InputStream inputStream) {
        final String destinationPath = destinationPath(path);
        final Resource inputStreamResource = KieServices.Factory.get().getResources().newInputStreamResource(new BufferedInputStream(inputStream));

        kieFileSystem.write(destinationPath,
                            inputStreamResource);
        handles.put(getBaseFileName(destinationPath),
                    Paths.convert(path));
        addJavaClass(path);
    }

    public IncrementalBuildResults deleteResource(final Path resource) {
        checkNotNull("resource",
                     resource);

        checkAFullBuildHasBeenPerformed();

        //Resource Type might have been validated "externally" (i.e. it's not covered by Kie). Clear any errors.
        final IncrementalBuildResults results = new IncrementalBuildResults(projectGAV);
        final BuildValidationHelper validator = getBuildValidationHelper(resource);
        if (validator != null) {
            nonKieResourceValidationHelpers.remove(resource);
            results.addAllRemovedMessages(convertValidationMessages(nonKieResourceValidationHelperMessages.remove(resource)));
        }

        synchronized (kieFileSystem) {
            removeResource(resource);
            buildIncrementally(results,
                               destinationPath(resource));
        }

        return results;
    }

    private void removeResource(final Path resource) {
        kieFileSystem.delete(destinationPath(resource));
        removeJavaClass(resource);
    }

    private String destinationPath(final Path resource) {
        final String destinationPath = EncodingUtil.decode(resource.toUri().toString().substring(projectPrefix.length()));

        if (destinationPath.startsWith("/")) {
            // File in sub module
            return destinationPath.substring(1);
        } else {
            // File in main module
            return destinationPath;
        }
    }

    public IncrementalBuildResults updateResource(final Path resource) {
        return addResource(resource);
    }

    public IncrementalBuildResults updateResource(final Path resource,
                                                  final InputStream inputStream) {
        return addResource(resource,
                           inputStream);
    }

    public IncrementalBuildResults applyBatchResourceChanges(final Map<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> changes) {
        checkNotNull("changes",
                     changes);

        checkAFullBuildHasBeenPerformed();

        //Add all changes to KieFileSystem before executing the build
        final List<String> changedFilesKieBuilderPaths = new ArrayList<String>();
        final List<ValidationMessage> nonKieResourceValidatorAddedMessages = new ArrayList<ValidationMessage>();
        final List<ValidationMessage> nonKieResourceValidatorRemovedMessages = new ArrayList<ValidationMessage>();
        final IncrementalBuildResults results = new IncrementalBuildResults(projectGAV);

        synchronized (kieFileSystem) {
            for (final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> pathCollectionEntry : changes.entrySet()) {
                for (final ResourceChange change : pathCollectionEntry.getValue()) {
                    final ResourceChangeType type = change.getType();
                    final Path resource = Paths.convert(pathCollectionEntry.getKey());

                    checkNotNull("type",
                                 type);
                    checkNotNull("Builder.resource§",
                                 resource);

                    final String destinationPath = destinationPath(resource);
                    changedFilesKieBuilderPaths.add(destinationPath);
                    switch (type) {
                        case ADD:
                        case UPDATE:
                            //Only files can be processed
                            if (!Files.isRegularFile(resource)) {
                                continue;
                            }

                            update(nonKieResourceValidatorAddedMessages,
                                   nonKieResourceValidatorRemovedMessages,
                                   resource);

                            break;
                        case DELETE:
                            delete(nonKieResourceValidatorRemovedMessages,
                                   resource);
                    }
                }
            }

            //Perform the Incremental build and get messages from incremental build
            buildIncrementally(results,
                               toArray(changedFilesKieBuilderPaths));
        }

        //Copy in BuildMessages for non-KIE resources
        results.addAllAddedMessages(convertValidationMessages(nonKieResourceValidatorAddedMessages));
        results.addAllRemovedMessages(convertValidationMessages(nonKieResourceValidatorRemovedMessages));

        return results;
    }

    private String[] toArray(List<String> stringList) {
        final String[] stringArray = new String[stringList.size()];
        stringList.toArray(stringArray);
        return stringArray;
    }

    private void update(final List<ValidationMessage> nonKieResourceValidatorAddedMessages,
                        final List<ValidationMessage> nonKieResourceValidatorRemovedMessages,
                        final Path resource) {
        //Resource Type might require "external" validation (i.e. it's not covered by Kie)
        final BuildValidationHelper validator = getBuildValidationHelper(resource);
        if (validator != null) {
            final List<ValidationMessage> addedValidationMessages = validator.validate(Paths.convert(resource));

            if (!(addedValidationMessages == null || addedValidationMessages.isEmpty())) {
                for (ValidationMessage validationMessage : addedValidationMessages) {
                    nonKieResourceValidatorAddedMessages.add(validationMessage);
                }
            }

            final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove(resource);
            if (!(removedValidationMessages == null || removedValidationMessages.isEmpty())) {
                for (ValidationMessage validationMessage : removedValidationMessages) {
                    nonKieResourceValidatorRemovedMessages.add(validationMessage);
                }
            }
            nonKieResourceValidationHelpers.put(resource,
                                                validator);
            nonKieResourceValidationHelperMessages.put(resource,
                                                       addedValidationMessages);
        }

        addNewResource(resource,
                       ioService.newInputStream(resource));
    }

    private void delete(final List<ValidationMessage> nonKieResourceValidatorRemovedMessages,
                        final Path resource) {
        //Resource Type might have been validated "externally" (i.e. it's not covered by Kie). Clear any errors.
        nonKieResourceValidationHelpers.remove(resource);
        final List<ValidationMessage> removedValidationMessages = nonKieResourceValidationHelperMessages.remove(resource);
        if (!(removedValidationMessages == null || removedValidationMessages.isEmpty())) {
            for (ValidationMessage validationMessage : removedValidationMessages) {
                nonKieResourceValidatorRemovedMessages.add(validationMessage);
            }
        }

        removeResource(resource);
    }

    private void buildIncrementally(final IncrementalBuildResults results,
                                    final String... destinationPath) {
        try {
            final IncrementalResults incrementalResults = ((InternalKieBuilder) kieBuilder).createFileSet(Message.Level.WARNING, destinationPath).build();
            results.addAllAddedMessages(convertMessages(incrementalResults.getAddedMessages(),
                                                        handles));
            results.addAllRemovedMessages(convertMessages(incrementalResults.getRemovedMessages(),
                                                          handles));

            //Tidy-up removed message handles
            for (Message message : incrementalResults.getRemovedMessages()) {
                handles.remove(Handles.RESOURCE_PATH + "/" + getBaseFileName(message.getPath()));
            }
        } catch (LinkageError e) {
            final String msg = MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                                    e.getLocalizedMessage());
            logger.warn(msg);
            results.addAddedMessage(makeWarningMessage(msg));
        } catch (Throwable e) {
            final String msg = e.getLocalizedMessage();
            logger.error(msg,
                         e);
            results.addAddedMessage(makeErrorMessage(msg));
        }
    }

    private void checkAFullBuildHasBeenPerformed() {
        if (!isBuilt()) {
            throw new IllegalStateException("A full build needs to be performed before any incremental operations.");
        }
    }

    public org.kie.api.builder.KieModule getKieModule() {
        //Kie classes are only available once built
        if (!isBuilt()) {
            build();
        }
        synchronized (kieFileSystem) {
            return kieBuilder.getKieModule();
        }
    }

    public org.kie.api.builder.KieModule getKieModuleIgnoringErrors() {
        //Kie classes are only available once built
        if (!isBuilt()) {
            build();
        }
        synchronized (kieFileSystem) {
            return ((InternalKieBuilder) kieBuilder).getKieModuleIgnoringErrors();
        }
    }

    public KieModuleMetaData getKieModuleMetaDataIgnoringErrors() {
        return KieModuleMetaData.Factory.newKieModuleMetaData(getKieModuleIgnoringErrors(),
                                                              DependencyFilter.COMPILE_FILTER);
    }

    public TypeSourceResolver getTypeSourceResolver(KieModuleMetaData kieModuleMetaData) {
        return new TypeSourceResolver(kieModuleMetaData,
                                      javaResources);
    }

    public KieContainer getKieContainer() {
        BuildResults results = null;

        //Kie classes are only available once built
        if (!isBuilt()) {
            results = build();
        } else {
            results = new BuildResults();
            results.addAllBuildMessages(convertMessages(kieBuilder.getResults().getMessages(),
                                                        handles));
        }
        //It's impossible to retrieve a KieContainer if the KieModule contains errors
        if (results.getErrorMessages().isEmpty()) {
            // Do not retrieve the KieContainer with KieServices.newKieContainer(releaseId) since this looks-up the KieModule to
            // create the KieContainer from KieRepository. This holds the most recent KieModule (for the ReleaseId) that was built with
            // kieBuilder.buildAll() which *may* be a KieModule created during asset validation and hence will lack many assets.
            // See https://bugzilla.redhat.com/show_bug.cgi?id=1202551
            final org.kie.api.builder.KieModule kieModule = kieBuilder.getKieModule();
            final ReleaseId releaseId = kieModule.getReleaseId();
            final org.drools.compiler.kie.builder.impl.KieProject kieProject = new KieModuleKieProject((InternalKieModule) kieBuilder.getKieModule(),
                                                                                                       null);
            final KieContainer kieContainer = new KieContainerImpl(kieProject,
                                                                   KieServices.Factory.get().getRepository(),
                                                                   releaseId);
            return kieContainer;
        } else {
            return null;
        }
    }

    public boolean isBuilt() {
        return kieBuilder != null;
    }

    private void visitPaths(final DirectoryStream<Path> directoryStream) {
        for (final Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitPaths(Files.newDirectoryStream(path));
            } else {
                //Don't process dotFiles
                if (!dotFileFilter.accept(path)) {

                    //Resource Type might require "external" validation (i.e. it's not covered by Kie)
                    final BuildValidationHelper validator = getBuildValidationHelper(path);
                    if (validator != null) {
                        nonKieResourceValidationHelpers.put(path,
                                                            validator);
                    }

                    addNewResource(path,
                                   ioService.newInputStream(path));
                }
            }
        }
    }

    private void addJavaClass(final Path path) {
        if (!javaResourceFilter.accept(path)) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName(path);
        if (fullyQualifiedClassName != null) {
            javaResources.add(fullyQualifiedClassName);
        }
    }

    private void removeJavaClass(final Path path) {
        if (!javaResourceFilter.accept(path)) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName(path);
        if (fullyQualifiedClassName != null) {
            javaResources.remove(fullyQualifiedClassName);
        }
    }

    private String getFullyQualifiedClassName(final Path path) {
        final Package pkg = moduleService.resolvePackage(Paths.convert(path));
        final String packageName = pkg.getPackageName();
        if (packageName == null) {
            return null;
        }
        final String className = path.getFileName().toString().replace(".java",
                                                                       "");
        return (packageName.equals("") ? className : packageName + "." + className);
    }

    private BuildValidationHelper getBuildValidationHelper(final Path nioResource) {
        for (BuildValidationHelper validator : buildValidationHelpers) {
            final org.uberfire.backend.vfs.Path resource = Paths.convert(nioResource);
            if (validator.accepts(resource)) {
                return validator;
            }
        }
        return null;
    }

    KieFileSystem getKieFileSystem() {
        return kieFileSystem;
    }
}
