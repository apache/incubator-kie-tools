/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.backend.file.LinkedRegularFileFilter;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.kie.soup.xstream.XStreamUtils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemOperation;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.VFSLockServiceImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.concurrent.Managed;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;

import static java.util.Collections.emptyList;

public class ExplorerServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplorerServiceHelper.class);
    private ExecutorService executorService;

    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();
    private LinkedRegularFileFilter regularFileFilter = new LinkedRegularFileFilter(dotFileFilter);
    private XStream xs;

    private KieModuleService moduleService;
    private FolderListingResolver folderListingResolver;
    private IOService ioService;
    private IOService ioServiceConfig;
    private VFSLockServiceImpl lockService;
    private MetadataService metadataService;
    private UserServicesImpl userServices;

    private DeleteService deleteService;
    private RenameService renameService;
    private CopyService copyService;

    public ExplorerServiceHelper() {
        //WELD proxy support
        xs = XStreamUtils.createTrustingXStream();
    }

    @Inject
    public ExplorerServiceHelper(final KieModuleService moduleService,
                                 final FolderListingResolver folderListingResolver,
                                 @Named("ioStrategy") final IOService ioService,
                                 @Named("configIO") final IOService ioServiceConfig,
                                 final VFSLockServiceImpl lockService,
                                 final MetadataService metadataService,
                                 final UserServicesImpl userServices,
                                 final DeleteService deleteService,
                                 final RenameService renameService,
                                 final CopyService copyService,
                                 @Managed final ExecutorService executorService) {
        this();
        this.moduleService = moduleService;
        this.folderListingResolver = folderListingResolver;
        this.ioService = ioService;
        this.ioServiceConfig = ioServiceConfig;
        this.lockService = lockService;
        this.metadataService = metadataService;
        this.userServices = userServices;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.copyService = copyService;
        this.executorService = executorService;
    }

    public FolderItem toFolderItem(final org.guvnor.common.services.project.model.Package pkg) {
        if (pkg == null) {
            return null;
        }
        return new FolderItem(pkg,
                              pkg.getRelativeCaption(),
                              FolderItemType.FOLDER);
    }

    public FolderItem toFolderItem(final org.uberfire.java.nio.file.Path path) {
        if (Files.isRegularFile(path)) {
            final org.uberfire.backend.vfs.Path p = Paths.convert(path);
            return new FolderItem(p,
                                  p.getFileName(),
                                  FolderItemType.FILE,
                                  false,
                                  Paths.readLockedBy(p),
                                  Collections.<String>emptyList(),
                                  getRestrictedOperations(p));
        } else if (Files.isDirectory(path)) {
            final org.uberfire.backend.vfs.Path p = Paths.convert(path);
            return new FolderItem(p,
                                  p.getFileName(),
                                  FolderItemType.FOLDER);
        }

        return null;
    }

    public List<FolderItem> getPackageSegments(final Package _pkg) {
        List<FolderItem> result = new ArrayList<FolderItem>();
        Package pkg = _pkg;
        while (pkg != null) {
            final Package parent = moduleService.resolveParentPackage(pkg);
            if (parent != null) {
                result.add(toFolderItem(parent));
            }
            pkg = parent;
        }

        return Lists.reverse(result);
    }

    public FolderListing getFolderListing(final FolderItem selectedItem,
                                          final Module selectedModule,
                                          final Package selectedPackage,
                                          final ActiveOptions options) {
        return folderListingResolver.resolve(selectedItem,
                                             selectedModule,
                                             selectedPackage,
                                             this,
                                             options);
    }

    public FolderListing getFolderListing(final Package pkg,
                                          final ActiveOptions options) {
        return new FolderListing(toFolderItem(pkg),
                                 getItems(pkg,
                                          options),
                                 getPackageSegments(pkg));
    }

    public FolderListing getFolderListing(final FolderItem item,
                                          final ActiveOptions options) {

        FolderListing result = null;
        if (item.getItem() instanceof Path) {
            result = getFolderListing((Path) item.getItem(),
                                      options);
        } else if (item.getItem() instanceof Package) {
            result = getFolderListing((Package) item.getItem(),
                                      options);
        }

        return result;
    }

    public FolderListing getFolderListing(final Path path,
                                          final ActiveOptions options
    ) {
        //Get list of files and folders contained in the path
        final List<FolderItem> folderItems = new ArrayList<FolderItem>();
        final boolean includeTags = options.contains(Option.SHOW_TAG_FILTER);

        //Scan upwards until the path exists (as the current path could have been deleted)
        org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        while (!Files.exists(nioPath)) {
            nioPath = nioPath.getParent();
        }
        final Path basePath = Paths.convert(nioPath);
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioPaths = ioService.newDirectoryStream(nioPath,
                                                                                                       dotFileFilter);
        for (org.uberfire.java.nio.file.Path np : nioPaths) {
            if (Files.isRegularFile(np)) {
                final org.uberfire.backend.vfs.Path p = Paths.convert(np);
                final String lockedBy = Paths.readLockedBy(p);
                final FolderItem folderItem = new FolderItem(p,
                                                             p.getFileName(),
                                                             FolderItemType.FILE,
                                                             false,
                                                             lockedBy,
                                                             includeTags ? metadataService.getTags(p) : Collections.<String>emptyList(),
                                                             getRestrictedOperations(p));
                folderItems.add(folderItem);
            } else if (Files.isDirectory(np)) {
                final org.uberfire.backend.vfs.Path p = Paths.convert(np);
                boolean lockedItems = !lockService.retrieveLockInfos(Paths.convert(np),
                                                                     true).isEmpty();
                final FolderItem folderItem = new FolderItem(p,
                                                             p.getFileName(),
                                                             FolderItemType.FOLDER,
                                                             lockedItems,
                                                             null,
                                                             Collections.<String>emptyList(),
                                                             getRestrictedOperations(p));
                folderItems.add(folderItem);
            }
        }

        Collections.sort(folderItems,
                         Sorters.ITEM_SORTER);

        return new FolderListing(toFolderItem(nioPath),
                                 folderItems,
                                 getPathSegments(basePath));
    }

    public boolean hasAssets(final Package pkg) {
        if (pkg == null) {
            return false;
        }

        if (hasAssets(pkg.getPackageMainSrcPath())
                || hasAssets(pkg.getPackageTestSrcPath())
                || hasAssets(pkg.getPackageMainResourcesPath())
                || hasAssets(pkg.getPackageTestResourcesPath())) {
            return true;
        }

        final Set<Package> childPackages = moduleService.resolvePackages(pkg);
        for (final Package childPackage : childPackages) {

            if (!childPackage.equals(pkg) && hasAssets(childPackage)) {
                return true;
            }
        }

        return false;
    }

    boolean hasAssets(final Path packagePath) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(packagePath);

        if (Files.exists(nioPackagePath)) {
            final DirectoryStream<org.uberfire.java.nio.file.Path> nioPaths = ioService.newDirectoryStream(nioPackagePath,
                                                                                                           regularFileFilter);
            for (org.uberfire.java.nio.file.Path nioPath : nioPaths) {
                return true;
            }
        }

        return false;
    }

    public List<FolderItem> getItems(final Package pkg,
                                     final ActiveOptions options) {
        final List<FolderItem> folderItems = new ArrayList<FolderItem>();
        if (pkg == null) {
            return emptyList();
        }

        final Set<Package> childPackages = moduleService.resolvePackages(pkg);
        for (final Package childPackage : childPackages) {
            folderItems.add(toFolderItem(childPackage));
        }

        folderItems.addAll(getItems(pkg.getPackageMainSrcPath(),
                                    options));
        folderItems.addAll(getItems(pkg.getPackageTestSrcPath(),
                                    options));
        folderItems.addAll(getItems(pkg.getPackageMainResourcesPath(),
                                    options));
        folderItems.addAll(getItems(pkg.getPackageTestResourcesPath(),
                                    options));

        Collections.sort(folderItems,
                         Sorters.ITEM_SORTER);

        return folderItems;
    }

    List<FolderItem> getPathSegments(final Path path) {
        org.uberfire.java.nio.file.Path nioSegmentPath = Paths.convert(path);

        final int segmentCount = nioSegmentPath.getNameCount();
        if (segmentCount < 1) {
            return new ArrayList<>();
        }

        final FolderItem[] segments = new FolderItem[segmentCount];
        for (int idx = segmentCount; idx > 0; idx--) {
            nioSegmentPath = nioSegmentPath.getParent();
            segments[idx - 1] = toFolderItem(nioSegmentPath);
        }

        return Arrays.asList(segments);
    }

    private List<FolderItem> getItems(final Path packagePath,
                                      final ActiveOptions options) {
        final List<FolderItem> folderItems = new ArrayList<FolderItem>();
        final boolean includeTags = options.contains(Option.SHOW_TAG_FILTER);
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(packagePath);
        if (Files.exists(nioPackagePath)) {
            final DirectoryStream<org.uberfire.java.nio.file.Path> nioPaths = ioService.newDirectoryStream(nioPackagePath,
                                                                                                           regularFileFilter);
            for (org.uberfire.java.nio.file.Path nioPath : nioPaths) {
                final org.uberfire.backend.vfs.Path path = Paths.convert(nioPath);
                if (Paths.isLock(path)) {
                    continue;
                }

                final String lockedBy = Paths.readLockedBy(path);
                final FolderItem folderItem = new FolderItem(path,
                                                             path.getFileName(),
                                                             FolderItemType.FILE,
                                                             false,
                                                             lockedBy,
                                                             includeTags ? metadataService.getTags(path) : Collections.<String>emptyList(),
                                                             getRestrictedOperations(path));
                folderItems.add(folderItem);
            }
        }

        return folderItems;
    }

    public void store(final WorkspaceProject project,
                      final Module selectedModule,
                      final FolderListing folderListing,
                      final Package selectedPackage,
                      final ActiveOptions options) {

        final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath("explorer",
                                                                                   "user.nav");
        final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath("explorer",
                                                                                       "last.user.nav");
        final Module _selectedModule = selectedModule;
        final FolderItem _selectedItem = folderListing.getItem();
        final org.guvnor.common.services.project.model.Package _selectedPackage;
        if (selectedPackage != null) {
            _selectedPackage = selectedPackage;
        } else if (folderListing.getItem().getItem() instanceof Package) {
            _selectedPackage = (Package) folderListing.getItem().getItem();
        } else {
            _selectedPackage = null;
        }

        this.executorService.execute(new DescriptiveRunnable() {
            @Override
            public String getDescription() {
                return "Serialize Navigation State";
            }

            @Override
            public void run() {
                try {
                    store(userNavPath,
                          lastUserNavPath,
                          project,
                          _selectedModule,
                          _selectedPackage,
                          _selectedItem,
                          options);
                } catch (final Exception e) {
                    LOGGER.error("Can't serialize user's state navigation",
                                 e);
                }
            }
        });
    }

    public void store(final org.uberfire.java.nio.file.Path userNav,
                      final org.uberfire.java.nio.file.Path lastUserNav,
                      final WorkspaceProject project,
                      final Module module,
                      final Package pkg,
                      final FolderItem item,
                      final ActiveOptions options) {
        final UserExplorerData content;
        final UserExplorerData _content = loadUserContent(userNav);
        if (_content == null) {
            content = new UserExplorerData();
        } else {
            content = _content;
        }
        final UserExplorerLastData lastContent = new UserExplorerLastData();
        if (project != null) {
            content.addRepository(project.getOrganizationalUnit(),
                                  project.getRepository());
        }
        if (module != null && project != null) {
            content.addModule(project.getOrganizationalUnit(),
                              project.getRepository(),
                              module);
        }
        if (item != null && project != null && project.getBranch() != null && module != null) {
            lastContent.setFolderItem(project.getRepository(),
                                      project.getBranch().getName(),
                                      module,
                                      item);
            content.addFolderItem(project.getOrganizationalUnit(),
                                  project.getRepository(),
                                  module,
                                  item);
        }
        if (pkg != null && project != null && project.getBranch() != null && module != null) {
            lastContent.setPackage(project.getRepository(),
                                   project.getBranch().getName(),
                                   module,
                                   pkg);
            content.addPackage(project.getOrganizationalUnit(),
                               project.getRepository(),
                               module,
                               pkg);
        }
        if (options != null && !options.isEmpty()) {
            lastContent.setOptions(options);
        }
        if (!content.isEmpty()) {
            try {
                ioServiceConfig.startBatch(userNav.getFileSystem());
                ioServiceConfig.write(userNav,
                                      xs.toXML(content));
                ioServiceConfig.write(lastUserNav,
                                      xs.toXML(lastContent));
            } finally {
                ioServiceConfig.endBatch();
            }
        }
    }

    public UserExplorerData loadUserContent(final org.uberfire.java.nio.file.Path path) {
        try {
            if (ioServiceConfig.exists(path)) {
                final String xml = ioServiceConfig.readAllString(path);
                return (UserExplorerData) xs.fromXML(xml);
            }
        } catch (final Exception ignored) {
        }
        return null;
    }

    public UserExplorerData loadUserContent() {
        final UserExplorerData userExplorerData = loadUserContent(userServices.buildPath("explorer",
                                                                                         "user.nav"));
        if (userExplorerData != null) {
            return userExplorerData;
        }
        return new UserExplorerData();
    }

    public UserExplorerLastData getLastContent() {
        try {
            final UserExplorerLastData lastData = getLastContent(userServices.buildPath("explorer",
                                                                                        "last.user.nav"));
            if (lastData != null) {
                return lastData;
            }
        } catch (final Exception ignored) {
        }
        return new UserExplorerLastData();
    }

    public UserExplorerLastData getLastContent(final org.uberfire.java.nio.file.Path path) {
        try {
            if (ioServiceConfig.exists(path)) {
                final String xml = ioServiceConfig.readAllString(path);
                return (UserExplorerLastData) xs.fromXML(xml);
            }
        } catch (final Exception ignored) {
        }
        return null;
    }

    public List<FolderItemOperation> getRestrictedOperations(final Path path) {
        final List<FolderItemOperation> restrictedOperations = new ArrayList<FolderItemOperation>();

        if (copyService.hasRestriction(path)) {
            restrictedOperations.add(FolderItemOperation.COPY);
        }

        if (renameService.hasRestriction(path)) {
            restrictedOperations.add(FolderItemOperation.RENAME);
        }

        if (deleteService.hasRestriction(path)) {
            restrictedOperations.add(FolderItemOperation.DELETE);
        }

        return restrictedOperations;
    }
}
