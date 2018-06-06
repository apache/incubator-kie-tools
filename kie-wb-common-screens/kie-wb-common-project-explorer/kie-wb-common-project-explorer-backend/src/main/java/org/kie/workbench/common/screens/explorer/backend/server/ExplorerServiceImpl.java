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
package org.kie.workbench.common.screens.explorer.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.project.events.DeleteModuleEvent;
import org.guvnor.common.services.project.events.ModuleUpdatedEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.xstream.XStreamUtils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.concurrent.Managed;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Collections.emptyList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

@Service
@Dependent
public class ExplorerServiceImpl
        implements ExplorerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplorerServiceImpl.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    private KieModuleService moduleService;

    @Inject
    private ExplorerServiceHelper helper;

    @Inject
    private UserServicesImpl userServices;

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private ProjectExplorerContentResolver projectExplorerContentResolver;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private CopyService copyService;

    @Inject
    private WorkspaceProjectService projectService;

    @Inject
    //@AppResourcesAuthz
    private AuthorizationManager authorizationManager;

    @Inject
    @Managed
    private ExecutorService executorService;

    private XStream xs;

    public ExplorerServiceImpl() {
        xs = XStreamUtils.createTrustingXStream();
    }

    @Override
    public WorkspaceProject resolveProject(final String path) {
        return projectService.resolveProject(Paths.convert(ioService.get(URI.create(path.trim()))));
    }

    @Override
    public ProjectExplorerContent getContent(final String _path,
                                             final ActiveOptions activeOptions) {
        checkNotEmpty("path",
                      _path);

        final Path path = Paths.convert(ioService.get(URI.create(_path.trim())));
        final Module module = moduleService.resolveModule(path);

        final Path convertedPath = Paths.convert(Paths.convert(path).getRoot());
        final Repository repo = repositoryService.getRepository(convertedPath);

        final Branch branch = getBranch(repo,
                                        convertedPath);

        return getContent(new ProjectExplorerContentQuery(repo,
                                                          branch,
                                                          module,
                                                          activeOptions));
    }

    private Branch getBranch(final Repository repository,
                             final Path convertedPath) {
        for (final Branch branch : repository.getBranches()) {
            if (branch.getPath().equals(convertedPath)) {
                return branch;
            }
        }
        return null;
    }

    @Override
    public ProjectExplorerContent getContent(final ProjectExplorerContentQuery query) {
        return projectExplorerContentResolver.resolve(query);
    }

    @Override
    public FolderListing getFolderListing(final WorkspaceProject project,
                                          final Module module,
                                          final FolderItem item,
                                          final ActiveOptions options) {
        //TODO: BUSINESS_CONTENT, TECHNICAL_CONTENT
        final FolderListing result = helper.getFolderListing(item,
                                                             options);

        if (result != null) {
            final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath("explorer",
                                                                                       "user.nav");
            final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath("explorer",
                                                                                           "last.user.nav");

            this.executorService.execute(new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return "Serialize Navigation State";
                }

                @Override
                public void run() {
                    try {
                        Package pkg = null;
                        if (item.getItem() instanceof Package) {
                            pkg = (Package) item.getItem();
                        }
                        helper.store(userNavPath,
                                     lastUserNavPath,
                                     project,
                                     module,
                                     pkg,
                                     item,
                                     options);
                    } catch (final Exception e) {
                        LOGGER.error("Can't serialize user's state navigation",
                                     e);
                    }
                }
            });
        }

        return result;
    }

    private List<Path> resolvePath(final FolderItem item) {
        if (item == null) {
            return emptyList();
        }

        if (item.getItem() instanceof Package) {
            final Package pkg = ((Package) item.getItem());
            return new ArrayList<Path>(4) {{
                add(pkg.getPackageMainResourcesPath());
                add(pkg.getPackageMainSrcPath());
                add(pkg.getPackageTestResourcesPath());
                add(pkg.getPackageTestSrcPath());
            }};
        }

        if (item.getItem() instanceof Path) {
            //Path could represent a package
            if (item.getType() == FolderItemType.FOLDER) {
                final Package pkg = moduleService.resolvePackage((Path) item.getItem());
                if (pkg == null) {
                    return new ArrayList<Path>(1) {{
                        add((Path) item.getItem());
                    }};
                } else {
                    return new ArrayList<Path>(4) {{
                        add(pkg.getPackageMainResourcesPath());
                        add(pkg.getPackageMainSrcPath());
                        add(pkg.getPackageTestResourcesPath());
                        add(pkg.getPackageTestSrcPath());
                    }};
                }
            } else {
                return new ArrayList<Path>(1) {{
                    add((Path) item.getItem());
                }};
            }
        }

        return emptyList();
    }

    @Override
    public Package resolvePackage(final FolderItem item) {
        if (item == null) {
            return null;
        }
        if (item.getItem() instanceof Package) {
            return (Package) item.getItem();
        }
        if (item.getItem() instanceof Path) {
            return moduleService.resolvePackage((Path) item.getItem());
        }

        return null;
    }

    @Override
    public Set<Option> getLastUserOptions() {
        return helper.getLastContent().getOptions();
    }

    @Override
    public void deleteItem(final FolderItem folderItem,
                           final String comment) {

        final Collection<Path> paths = resolvePath(folderItem);
        deleteService.deleteIfExists(paths,
                                     comment);
    }

    @Override
    public void renameItem(final FolderItem folderItem,
                           final String newName,
                           final String comment) {
        final Collection<Path> paths = resolvePath(folderItem);
        renameService.renameIfExists(paths,
                                     newName,
                                     comment);
    }

    @Override
    public void copyItem(final FolderItem folderItem,
                         final String newName,
                         final Path targetDirectory,
                         final String comment) {
        final List<Path> paths = resolvePath(folderItem);

        if (paths != null && paths.size() == 1) {
            copyService.copy(paths.get(0),
                             newName,
                             targetDirectory,
                             comment);
        } else {
            // when copying packages
            copyService.copyIfExists(paths,
                                     newName,
                                     comment);
        }
    }

    void onModuleUpdate(@Observes final ModuleUpdatedEvent event) {
        cleanup(event.getOldModule());
    }

    void onModuleDelete(@Observes final DeleteModuleEvent event) {
        cleanup(event.getModule());
    }

    private void cleanup(final Module module) {
        final Collection<org.uberfire.java.nio.file.Path> lastNavs = userServicesBackend.getAllUsersData("explorer",
                                                                                                         "last.user.nav");
        final Collection<org.uberfire.java.nio.file.Path> userNavs = userServicesBackend.getAllUsersData("explorer",
                                                                                                         "user.nav");

        try {
            ioServiceConfig.startBatch(fileSystem);

            for (org.uberfire.java.nio.file.Path path : userNavs) {
                final UserExplorerData userContent = helper.loadUserContent(path);
                if (userContent != null) {
                    if (userContent.deleteModule(module)) {
                        ioServiceConfig.write(path,
                                              xs.toXML(userContent));
                    }
                }
            }

            for (org.uberfire.java.nio.file.Path lastNav : lastNavs) {
                final UserExplorerLastData lastUserContent = helper.getLastContent(lastNav);
                if (lastUserContent != null) {
                    if (lastUserContent.deleteModule(module)) {
                        ioServiceConfig.write(lastNav,
                                              xs.toXML(lastUserContent));
                    }
                }
            }
        } finally {
            ioServiceConfig.endBatch();
        }
    }

    public class OrganizationalUnitNotFoundForURI extends RuntimeException {

    }
}
