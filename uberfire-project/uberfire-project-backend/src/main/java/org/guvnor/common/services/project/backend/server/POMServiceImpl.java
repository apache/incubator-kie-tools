/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.events.ModuleUpdatedEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepository;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScoped;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mvp.Command;

@Service
@WorkspaceScoped
public class POMServiceImpl
        implements POMService {

    public static final String POM_XML = "pom.xml";

    private final Logger logger = LoggerFactory.getLogger(POMServiceImpl.class);
    private IOService ioService;
    private POMContentHandler pomContentHandler;
    private M2RepoService m2RepoService;
    private MetadataService metadataService;
    private Event<ModuleUpdatedEvent> moduleUpdatedEvent;
    private ModuleService<? extends Module> moduleService;
    private MavenXpp3Writer writer;
    private PomEnhancer pomEnhancer;

    private CommentedOptionFactory optionsFactory;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl(final @Named("ioStrategy") IOService ioService,
                          final POMContentHandler pomContentHandler,
                          final M2RepoService m2RepoService,
                          final MetadataService metadataService,
                          final Event<ModuleUpdatedEvent> moduleUpdatedEvent,
                          final ModuleService<? extends Module> moduleService,
                          final CommentedOptionFactory optionsFactory,
                          final @Customizable PomEnhancer pomEnhancer) {
        this.ioService = ioService;
        this.pomContentHandler = pomContentHandler;
        this.m2RepoService = m2RepoService;
        this.metadataService = metadataService;
        this.moduleUpdatedEvent = moduleUpdatedEvent;
        this.moduleService = moduleService;
        this.optionsFactory = optionsFactory;
        writer = new MavenXpp3Writer();
        this.pomEnhancer = pomEnhancer;
    }

    @Override
    public Path create(final Path projectRoot,
                       final POM pomModel) {
        org.uberfire.java.nio.file.Path pathToPOMXML = null;
        try {
            pomModel.addRepository(getRepository());
            pathToPOMXML = Paths.convert(projectRoot).resolve(POM_XML);

            if (ioService.exists(pathToPOMXML)) {
                throw new FileAlreadyExistsException(pathToPOMXML.toString());
            }
            write(pomEnhancer.execute(pomContentHandler.convert(pomModel)),
                  pathToPOMXML,
                  ioService);
            //Don't raise a NewResourceAdded event as this is handled at the Project level in ProjectServices
            return Paths.convert(pathToPOMXML);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private MavenRepository getRepository() {
        final MavenRepository mavenRepository = new MavenRepository();
        mavenRepository.setId("guvnor-m2-repo");
        mavenRepository.setName("Guvnor M2 Repo");
        mavenRepository.setUrl(m2RepoService.getRepositoryURL());
        return mavenRepository;
    }

    private void write(Model model, org.uberfire.java.nio.file.Path pathToPOMXML, IOService ioService) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writer.write(baos, model);
            ioService.write(pathToPOMXML, new String(baos.toByteArray(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public POM load(final Path pomPath) {
        try {
            return pomContentHandler.toModel(loadPomXMLString(pomPath));
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private String loadPomXMLString(final Path pomPath) {
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(pomPath);
        return ioService.readAllString(nioPath);
    }

    @Override
    public Path save(final Path pomPath,
                     final POM pom,
                     final Metadata metadata,
                     final String comment) {
        return save(pomPath,
                    pom,
                    metadata,
                    comment,
                    false);
    }

    @Override
    public Path save(final Path pomPath,
                     final POM pom,
                     final Metadata metadata,
                     final String comment,
                     final boolean updateModules) {
        return new PomSaver(pomPath,
                            pom,
                            metadata,
                            comment).savePOM(updateModules);
    }

    private class PomSaver {

        private final Path pomPath;
        private final POM pom;
        private final Metadata metadata;
        private String comment;
        private List<Command> updates = new ArrayList<>();

        public PomSaver(final Path pomPath,
                        final POM pom,
                        final Metadata metadata,
                        final String comment) {

            this.pomPath = pomPath;
            this.pom = pom;
            this.metadata = metadata;
            this.comment = comment;
        }

        public Path savePOM(final boolean updateModules) {

            try {
                ioService.startBatch(Paths.convert(pomPath).getFileSystem(),
                                     optionsFactory.makeCommentedOption(comment != null ? comment : ""));

                savePOM();

                if (updateModules) {
                    saveSubModulePOMs();
                }

                return pomPath;
            } catch (Exception e) {
                throw ExceptionUtilities.handleException(e);
            } finally {

                ioService.endBatch();

                for (final Command update : updates) {
                    update.execute();
                }
            }
        }

        private void savePOM() throws IOException, XmlPullParserException {
            savePOM(pomPath,
                    pom,
                    metadata);
        }

        private void savePOM(final Path pomPath,
                             final POM pom,
                             final Metadata metadata) throws IOException, XmlPullParserException {
            final Optional<Module> oldModuleForUpdateEvent = getModuleIfPomHasChanges(pomPath,
                                                                                      pom);

            if (metadata == null) {
                ioService.write(Paths.convert(pomPath),
                                pomContentHandler.toString(pom,
                                                           loadPomXMLString(pomPath)));
            } else {
                ioService.write(Paths.convert(pomPath),
                                pomContentHandler.toString(pom,
                                                           loadPomXMLString(pomPath)),
                                metadataService.setUpAttributes(pomPath,
                                                                metadata));
            }

            if (oldModuleForUpdateEvent.isPresent()) {
                updates.add(() -> moduleUpdatedEvent.fire(new ModuleUpdatedEvent(oldModuleForUpdateEvent.get(),
                                                                                 moduleService.resolveModule(pomPath))));
            }
        }

        private Optional<Module> getModuleIfPomHasChanges(final Path pomPath,
                                                          final POM pom) {
            POM load = load(pomPath);
            if (!load.equals(pom)) {
                return Optional.of(moduleService.resolveModule(pomPath));
            } else {
                return Optional.empty();
            }
        }

        private void saveSubModulePOMs() throws IOException, XmlPullParserException {

            if (pom.isMultiModule() &&
                    pom.getModules() != null) {
                for (final String childModuleName : pom.getModules()) {
                    saveGAVChange(pom.getGav(),
                                  childModuleName);
                }
            }
        }

        private void saveGAVChange(final GAV gav,
                                   final String childModuleName) throws IOException, XmlPullParserException {

            final org.uberfire.java.nio.file.Path childPOMPath = Paths.convert(pomPath).getParent().resolve(childModuleName).resolve(POM_XML);

            if (ioService.exists(childPOMPath)) {
                final POM childContent = load(Paths.convert(childPOMPath));
                if (childContent != null) {
                    childContent.setParent(gav);
                    childContent.getGav().setGroupId(gav.getGroupId());
                    childContent.getGav().setVersion(gav.getVersion());

                    savePOM(Paths.convert(childPOMPath),
                            childContent,
                            null);
                }
            }
        }
    }
}
