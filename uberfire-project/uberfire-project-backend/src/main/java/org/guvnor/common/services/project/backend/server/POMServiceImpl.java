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

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.MavenRepository;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScoped;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

@Service
@WorkspaceScoped
public class POMServiceImpl
        implements POMService {

    private IOService ioService;
    private POMContentHandler pomContentHandler;
    private M2RepoService m2RepoService;
    private MetadataService metadataService;

    @Inject
    private CommentedOptionFactory optionsFactory;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl(final @Named("ioStrategy") IOService ioService,
                          final POMContentHandler pomContentHandler,
                          final M2RepoService m2RepoService,
                          final MetadataService metadataService) {
        this.ioService = ioService;
        this.pomContentHandler = pomContentHandler;
        this.m2RepoService = m2RepoService;
        this.metadataService = metadataService;
    }

    @Override
    public Path create(final Path projectRoot,
                       final String repositoryWebBaseURL,
                       final POM pomModel) {
        org.uberfire.java.nio.file.Path pathToPOMXML = null;
        try {
            pomModel.addRepository(getRepository(repositoryWebBaseURL));

            final org.uberfire.java.nio.file.Path nioRoot = Paths.convert(projectRoot);
            pathToPOMXML = nioRoot.resolve("pom.xml");

            if (ioService.exists(pathToPOMXML)) {
                throw new FileAlreadyExistsException(pathToPOMXML.toString());
            }
            ioService.write(pathToPOMXML,
                            pomContentHandler.toString(pomModel));

            //Don't raise a NewResourceAdded event as this is handled at the Project level in ProjectServices

            return Paths.convert(pathToPOMXML);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private MavenRepository getRepository(final String baseURL) {
        final MavenRepository mavenRepository = new MavenRepository();
        mavenRepository.setId("guvnor-m2-repo");
        mavenRepository.setName("Guvnor M2 Repo");
        mavenRepository.setUrl(m2RepoService.getRepositoryURL(baseURL));
        return mavenRepository;
    }

    @Override
    public POM load(final Path path) {
        try {
            return pomContentHandler.toModel(loadPomXMLString(path));
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private String loadPomXMLString(final Path path) {
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        return ioService.readAllString(nioPath);
    }

    @Override
    public Path save(final Path path,
                     final POM content,
                     final Metadata metadata,
                     final String comment) {
        try {

            return save(path,
                        content,
                        metadata);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path save(final Path path,
                     final POM content,
                     final Metadata metadata,
                     final String comment,
                     final boolean updateModules) {

        try {

            ioService.startBatch(Paths.convert(path).getFileSystem(),
                                 optionsFactory.makeCommentedOption(comment != null ? comment : ""));

            save(path,
                 content,
                 metadata);

            saveSubModules(path,
                           content,
                           updateModules);

            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            ioService.endBatch();
        }
    }

    private void saveSubModules(final Path path,
                                final POM content,
                                final boolean updateModules) throws IOException, XmlPullParserException {
        if (updateModules &&
                content.isMultiModule() &&
                content.getModules() != null) {
            for (String module : content.getModules()) {

                org.uberfire.java.nio.file.Path childPath = Paths.convert(path).getParent().resolve(module).resolve("pom.xml");

                if (ioService.exists(childPath)) {
                    POM child = load(Paths.convert(childPath));
                    if (child != null) {
                        child.setParent(content.getGav());
                        child.getGav().setGroupId(content.getGav().getGroupId());
                        child.getGav().setVersion(content.getGav().getVersion());

                        save(Paths.convert(childPath),
                             child);
                    }
                }
            }
        }
    }

    private Path save(final Path path,
                      final POM content,
                      final Metadata metadata) throws IOException, XmlPullParserException {
        if (metadata == null) {
            save(path,
                 content);
        } else {
            ioService.write(Paths.convert(path),
                            pomContentHandler.toString(content,
                                                       loadPomXMLString(path)),
                            metadataService.setUpAttributes(path,
                                                            metadata));
        }

        return path;
    }

    private void save(final Path path,
                      final POM content) throws IOException, XmlPullParserException {
        ioService.write(Paths.convert(path),
                        pomContentHandler.toString(content,
                                                   loadPomXMLString(path)));
    }
}
