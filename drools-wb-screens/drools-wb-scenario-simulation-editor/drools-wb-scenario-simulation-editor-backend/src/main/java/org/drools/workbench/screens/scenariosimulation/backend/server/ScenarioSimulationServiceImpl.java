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

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.backend.runner.ScenarioJunitActivator;
import org.drools.scenariosimulation.backend.util.ImpossibleToFindDMNException;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioSimulationBuilder;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
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

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;

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

    @Inject
    protected ScenarioSimulationBuilder scenarioSimulationBuilder;

    @Inject
    protected DMNTypeService dmnTypeService;

    private SafeSessionInfo safeSessionInfo;

    private Properties props = new Properties();

    private static final String KIE_VERSION = "kie.version";
    private static final String junitActivatorPackageName = "testscenario";

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
    public SimulationRunResult runScenario(final Path path,
                                           final SimulationDescriptor simulationDescriptor,
                                           final List<ScenarioWithIndex> scenarios) {
        return scenarioRunnerService.runTest(user.getIdentifier(),
                                             path,
                                             simulationDescriptor,
                                             scenarios);
    }

    @Override
    public Path create(final Path context,
                       final String fileName,
                       final ScenarioSimulationModel content,
                       final String comment) {
        return create(context, fileName, content, comment, Type.RULE, null);
    }

    @Override
    public Path create(Path context, String fileName, ScenarioSimulationModel content, String comment, Type type, String value) {
        try {
            content.setSimulation(scenarioSimulationBuilder.createSimulation(context, type, value));
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

            ScenarioSimulationModel scenarioSimulationModel = unmarshalInternal(content);
            Simulation simulation = scenarioSimulationModel.getSimulation();
            if(simulation != null && Type.DMN.equals(simulation.getSimulationDescriptor().getType())) {
                try {
                    dmnTypeService.initializeNameAndNamespace(simulation,
                                                              path,
                                                              simulation.getSimulationDescriptor().getDmnFilePath());
                } catch (ImpossibleToFindDMNException e) {
                    // this error is not thrown so user can fix the file path manually
                    logger.error(e.getMessage(), e);
                }
            }
            return scenarioSimulationModel;
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
            ioService.write(Paths.convert(resource),
                            ScenarioSimulationXMLPersistence.getInstance().marshal(content),
                            metadataService.setUpAttributes(resource,
                                                            metadata),
                            commentedOptionFactory.makeCommentedOption(comment));

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

    protected void createActivatorIfNotExist(Path context) {
        KieModule kieModule = kieModuleService.resolveModule(context);

        Package junitActivatorPackage = getOrCreateJunitActivatorPackage(kieModule);
        final org.uberfire.java.nio.file.Path activatorPath = getActivatorPath(junitActivatorPackage);

        boolean needMigrateActivatorIfExists = ensureDependencies(kieModule);

        // junit activator needs to be created if the project has old dependencies or activator doesn't exist
        if (needMigrateActivatorIfExists || !ioService.exists(activatorPath)) {
            // first remove existing activators (if exist)
            removeOldActivatorIfExists(activatorPath, kieModule);

            ioService.write(activatorPath,
                            ScenarioJunitActivator.ACTIVATOR_CLASS_CODE.apply(junitActivatorPackageName),
                            commentedOptionFactory.makeCommentedOption(""));
        }
    }

    protected Package getOrCreateJunitActivatorPackage(KieModule kieModule) {
        Package rootPackage = kieModuleService.resolveDefaultPackage(kieModule);
        Path junitActivatorPackagePath = Paths.convert(getJunitActivatorPackagePath(rootPackage));
        Package junitActivatorPackage = kieModuleService.resolvePackage(junitActivatorPackagePath);
        if (junitActivatorPackage == null) {
            junitActivatorPackage = kieModuleService.newPackage(rootPackage, junitActivatorPackageName);
        }
        return junitActivatorPackage;
    }

    /**
     * This routine looks for existing activators to migrate
     * @param activatorPath
     * @param kieModule
     */
    protected void removeOldActivatorIfExists(org.uberfire.java.nio.file.Path activatorPath, KieModule kieModule) {

        // migration step after Test Scenario runner modules split DROOLS-3389
        ioService.deleteIfExists(activatorPath);

        // migration step after Test Scenario activator package fix RHPAM-1923
        String targetPackageName = kieModule.getPom().getGav().getGroupId();

        Optional<Package> packageFound = kieModuleService.resolvePackages(kieModule).stream()
                .filter(elem -> targetPackageName.equals(elem.getPackageName()))
                .findFirst();
        packageFound.ifPresent(pkg -> {
            org.uberfire.java.nio.file.Path oldActivatorPath = getActivatorPath(pkg);
            ioService.deleteIfExists(oldActivatorPath);
        });
    }

    /**
     * Verify if the project contains all the needed dependencies removing the old ones if available
     * @param module
     * @return boolean that specify if there was old dependencies
     */
    protected boolean ensureDependencies(KieModule module) {
        POM projectPom = module.getPom();
        Dependencies dependencies = projectPom.getDependencies();

        String kieVersion = props.getProperty(KIE_VERSION);

        boolean toSave = false;
        Path modulePomXMLPath = module.getPomXMLPath();

        for (GAV oldDependency : getOldDependencies()) {
            toSave |= removeFromPomIfNecessary(dependencies, oldDependency);
        }

        boolean oldDependenciesExist = toSave;

        for (GAV gav : getDependencies(kieVersion)) {
            toSave |= editPomIfNecessary(dependencies, gav);
        }

        if (toSave) {
            pomService.save(modulePomXMLPath, projectPom, null, "");
        }

        return oldDependenciesExist;
    }

    protected boolean removeFromPomIfNecessary(Dependencies dependencies, GAV oldDependency) {
        for (Dependency dependency : dependencies) {
            if (dependency.getGroupId().equals(oldDependency.getGroupId()) &&
                    dependency.getArtifactId().equals(oldDependency.getArtifactId())) {
                dependencies.remove(dependency);
                return true;
            }
        }
        return false;
    }

    protected boolean editPomIfNecessary(Dependencies dependencies, GAV gav) {
        Dependency scenarioDependency = new Dependency(gav);
        scenarioDependency.setScope("test");
        if (!dependencies.containsDependency(gav)) {
            dependencies.add(scenarioDependency);
            return true;
        }
        return false;
    }

    protected org.uberfire.java.nio.file.Path getActivatorPath(Package rootModulePackage) {
        return internalGetPath(rootModulePackage, ScenarioJunitActivator.ACTIVATOR_CLASS_NAME + ".java");
    }

    protected org.uberfire.java.nio.file.Path getJunitActivatorPackagePath(Package rootModulePackage) {
        return internalGetPath(rootModulePackage, junitActivatorPackageName);
    }

    protected List<GAV> getOldDependencies() {
        return Arrays.asList(new GAV("org.drools", "drools-wb-scenario-simulation-editor-api", null),
                             new GAV("org.drools", "drools-wb-scenario-simulation-editor-backend", null));
    }

    protected List<GAV> getDependencies(String kieVersion) {
        return Arrays.asList(new GAV("org.drools", "drools-scenario-simulation-api", kieVersion),
                             new GAV("org.drools", "drools-scenario-simulation-backend", kieVersion),
                             new GAV("org.drools", "drools-compiler", kieVersion),
                             // needed to compile guided decision table
                             new GAV("org.drools", "drools-workbench-models-guided-dtable", kieVersion),
                             new GAV("org.kie", "kie-dmn-feel", kieVersion),
                             new GAV("org.kie", "kie-dmn-api", kieVersion),
                             new GAV("org.kie", "kie-dmn-core", kieVersion));
    }

    protected ScenarioSimulationModel unmarshalInternal(String content) {
        return ScenarioSimulationXMLPersistence.getInstance().unmarshal(content);
    }

    private org.uberfire.java.nio.file.Path internalGetPath(Package pkg, String path) {
        org.uberfire.java.nio.file.Path packagePath = Paths.convert(pkg.getPackageTestSrcPath());
        return packagePath.resolve(path);
    }
}
