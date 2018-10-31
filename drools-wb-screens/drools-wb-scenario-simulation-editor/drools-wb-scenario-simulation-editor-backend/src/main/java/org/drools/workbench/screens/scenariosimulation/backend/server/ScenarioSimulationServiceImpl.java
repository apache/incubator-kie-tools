/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioJunitActivator;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioRunnerService;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.drools.workbench.screens.scenariosimulation.type.ScenarioSimulationResourceTypeDefinition;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class ScenarioSimulationServiceImpl
        extends KieService<ScenarioSimulationModelContent>
        implements ScenarioSimulationService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private ScenarioSimulationResourceTypeDefinition resourceTypeDefinition;

    @Inject
    private CommentedOptionFactory commentedOptionFactory;

    @Inject
    private SaveAndRenameServiceImpl<ScenarioSimulationModel, Metadata> saveAndRenameService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private ScenarioRunnerService scenarioRunnerService;

    @Inject
    private User user;

    @Inject
    private POMService pomService;

    @Inject
    private KieModuleService kieModuleService;

    private SafeSessionInfo safeSessionInfo;

    private Properties props = new Properties();

    private static final String KIE_VERSION = "kie.version";

    {
        String propertyFileName = "kie.properties";
        try {
            props.load(ScenarioSimulationServiceImpl.class.getClassLoader().getResourceAsStream(propertyFileName));
        } catch (IOException e) {
            throw new IllegalStateException("Impossible to retrieve property file " + propertyFileName, e);
        }
    }

    public ScenarioSimulationServiceImpl() {
    }

    @Inject
    public ScenarioSimulationServiceImpl(final SessionInfo sessionInfo) {
        safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public ScenarioSimulationModel runScenario(final Path path,
                                               final ScenarioSimulationModel model) {
        return scenarioRunnerService.runTest(user.getIdentifier(),
                                             path,
                                             model);
    }

    @Override
    public Path create(final Path context,
                       final String fileName,
                       final ScenarioSimulationModel content,
                       final String comment) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(context).resolve(fileName);
            final Path newPath = Paths.convert(nioPath);

            if (ioService.exists(nioPath)) {
                throw new FileAlreadyExistsException(nioPath.toString());
            }

            ioService.write(nioPath,
                            ScenarioSimulationXMLPersistence.getInstance().marshal(content),
                            commentedOptionFactory.makeCommentedOption(comment));

            createActivatorIfNotExist(context);

            return newPath;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public ScenarioSimulationModel load(final Path path) {
        try {
            final String content = ioService.readAllString(Paths.convert(path));

            return ScenarioSimulationXMLPersistence.getInstance().unmarshal(content);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    protected ScenarioSimulationModelContent constructContent(final Path path,
                                                              final Overview overview) {

        final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();

        final Set<String> consumedFQCNs = new HashSet<>();

        //Get FQCN's used by Globals
        consumedFQCNs.addAll(oracle.getPackageGlobals().values());

        //Get FQCN's of collections defined in project settings
        //they can be used in From Collect expressions
        consumedFQCNs.addAll(oracle.getModuleCollectionTypes()
                                     .entrySet()
                                     .stream()
                                     .filter(entry -> entry.getValue())
                                     .map(entry -> entry.getKey())
                                     .collect(Collectors.toSet()));

        DataModelOracleUtilities.populateDataModel(oracle,
                                                   dataModel,
                                                   consumedFQCNs);

        //Signal opening to interested parties
        resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                         safeSessionInfo));

        return new ScenarioSimulationModelContent(load(path),
                                                  overview,
                                                  dataModel);
    }

    @Override
    public Path save(final Path resource,
                     final ScenarioSimulationModel content,
                     final Metadata metadata,
                     final String comment) {
        try {
            final Metadata currentMetadata = metadataService.getMetadata(resource);
            ioService.write(Paths.convert(resource),
                            ScenarioSimulationXMLPersistence.getInstance().marshal(content),
                            metadataService.setUpAttributes(resource,
                                                            metadata),
                            commentedOptionFactory.makeCommentedOption(comment));

            fireMetadataSocialEvents(resource,
                                     currentMetadata,
                                     metadata);

            createActivatorIfNotExist(resource);
            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void delete(final Path path,
                       final String comment) {
        try {
            deleteService.delete(path,
                                 comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        try {
            return renameService.rename(path,
                                        newName,
                                        comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final Path targetDirectory,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    targetDirectory,
                                    comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final ScenarioSimulationModel content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }

    void createActivatorIfNotExist(Path context) {
        KieModule kieModule = kieModuleService.resolveModule(context);
        String groupId = kieModule.getPom().getGav().getGroupId();
        Optional<Package> packageFound = kieModuleService.resolvePackages(kieModule).stream()
                .filter(elem -> groupId.equals(elem.getPackageName()))
                .findFirst();
        if (!packageFound.isPresent()) {
            throw new IllegalArgumentException("Impossible to retrieve package information from path: " + context.toURI());
        }
        Package targetPackage = packageFound.get();
        final org.uberfire.java.nio.file.Path activatorPath = getActivatorPath(targetPackage);

        if (!ioService.exists(activatorPath)) {
            ioService.write(activatorPath,
                            ScenarioJunitActivator.ACTIVATOR_CLASS_CODE.apply(groupId),
                            commentedOptionFactory.makeCommentedOption(""));
        }

        ensureDependencies(kieModule);
    }

    void ensureDependencies(KieModule module) {
        POM projectPom = module.getPom();
        Dependencies dependencies = projectPom.getDependencies();

        String kieVersion = props.getProperty(KIE_VERSION);

        getDependecies(kieVersion).forEach(gav -> {
            editPomIfNecessary(module.getPomXMLPath(), projectPom, dependencies, gav);
        });
    }

    void editPomIfNecessary(Path pomPath, POM projectPom, Dependencies dependencies, GAV gav) {
        Dependency scenarioDependency = new Dependency(gav);
        scenarioDependency.setScope("test");
        if (!dependencies.containsDependency(gav)) {
            dependencies.add(scenarioDependency);
            pomService.save(pomPath, projectPom, null, "");
        }
    }

    org.uberfire.java.nio.file.Path getActivatorPath(Package rootModulePackage) {
        org.uberfire.java.nio.file.Path packagePath = Paths.convert(rootModulePackage.getPackageTestSrcPath());
        return packagePath.resolve(ScenarioJunitActivator.ACTIVATOR_CLASS_NAME + ".java");
    }

    List<GAV> getDependecies(String kieVersion) {
        return Arrays.asList(new GAV("org.drools", "drools-wb-scenario-simulation-editor-api", kieVersion),
                             new GAV("org.drools", "drools-wb-scenario-simulation-editor-backend", kieVersion),
                             new GAV("org.drools", "drools-compiler", kieVersion));
    }
}
