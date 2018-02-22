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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.Files;
import org.uberfire.spaces.Space;

import static java.util.Collections.emptyList;

public class ProjectExplorerContentResolver {

    @Inject
    protected User identity;
    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();
    private KieModuleService moduleService;
    private ExplorerServiceHelper helper;
    private ExplorerServiceHelper explorerServiceHelper;
    private WorkspaceProjectService projectService;

    @Inject
    public ProjectExplorerContentResolver(final KieModuleService moduleService,
                                          final ExplorerServiceHelper helper,
                                          final ExplorerServiceHelper explorerServiceHelper,
                                          final WorkspaceProjectService projectService) {
        this.moduleService = moduleService;
        this.helper = helper;
        this.explorerServiceHelper = explorerServiceHelper;
        this.projectService = projectService;
    }

    public ProjectExplorerContent resolve(final ProjectExplorerContentQuery query) {

        final Content content = setupSelectedItems(query);

        //Content may contain invalid state, e.g. Repository deleted, Module deleted etc so validate and reset as appropriate
        setSelectedModule(content);
        setSelectedPackage(content);
        setSelectedItem(content);

        if (content.getSelectedProject() == null || content.getSelectedModule() == null) {
            return emptyModuleExplorerContent(content);
        } else {
            return moduleExplorerContentWithSelections(content,
                                                       query.getOptions());
        }
    }

    private ProjectExplorerContent moduleExplorerContentWithSelections(final Content content,
                                                                       final ActiveOptions options) {

        setFolderListing(content,
                         options);

        setSiblings(content);

        helper.store(content.getSelectedProject(),
                     content.getSelectedModule(),
                     content.getFolderListing(),
                     content.getSelectedPackage(),
                     options);

        return new ProjectExplorerContent(
                content.getSelectedProject(),
                content.getSelectedModule(),
                content.getFolderListing(),
                content.getSiblings()
        );
    }

    private void setFolderListing(final Content content,
                                  final ActiveOptions options) {
        content.setFolderListing(helper.getFolderListing(content.getSelectedItem(),
                                                         content.getSelectedModule(),
                                                         content.getSelectedPackage(),
                                                         options));
    }

    private void setSiblings(final Content content) {
        if (content.getFolderListing().getSegments().size() > 1) {
            final ListIterator<FolderItem> li = content.getFolderListing().getSegments().listIterator(content.getFolderListing().getSegments().size());
            while (li.hasPrevious()) {
                final FolderItem currentItem = li.previous();
                final List<FolderItem> result = new ArrayList<>();
                result.add(currentItem);

                if (currentItem.getItem() instanceof Package) {
                    result.addAll(getSegmentSiblings((Package) currentItem.getItem()));
                } else if (currentItem.getItem() instanceof Path) {
                    result.addAll(getSegmentSiblings((Path) currentItem.getItem()));
                }
                content.getSiblings().put(currentItem,
                                          result);
            }
        }

        if (content.getSelectedItem() != null && content.getSelectedItem().getType().equals(FolderItemType.FOLDER) &&
                !content.getSiblings().containsKey(content.getSelectedItem())) {
            final List<FolderItem> result = new ArrayList<>();
            result.add(content.getSelectedItem());

            if (content.getSelectedItem().getItem() instanceof Package) {
                result.addAll(getSegmentSiblings((Package) content.getSelectedItem().getItem()));
            } else if (content.getSelectedItem().getItem() instanceof Path) {
                result.addAll(getSegmentSiblings((Path) content.getSelectedItem().getItem()));
            }
            content.getSiblings().put(content.getSelectedItem(),
                                      result);
        }

        if (content.getFolderListing().getItem().getType().equals(FolderItemType.FOLDER) &&
                !content.getSiblings().containsKey(content.getFolderListing().getItem())) {
            final List<FolderItem> result = new ArrayList<>();
            result.add(content.getFolderListing().getItem());

            if (content.getFolderListing().getItem().getItem() instanceof Package) {
                result.addAll(getSegmentSiblings((Package) content.getFolderListing().getItem().getItem()));
            } else if (content.getFolderListing().getItem().getItem() instanceof Path) {
                result.addAll(getSegmentSiblings((Path) content.getFolderListing().getItem().getItem()));
            }
            if (!result.isEmpty()) {
                content.getSiblings().put(content.getFolderListing().getItem(),
                                          result);
            }
        }

        //Sort sibling lists before returning to client
        for (Map.Entry<FolderItem, List<FolderItem>> e : content.getSiblings().entrySet()) {
            Collections.sort(e.getValue(),
                             Sorters.ITEM_SORTER);
        }
    }

    private ProjectExplorerContent emptyModuleExplorerContent(final Content content) {
        return new ProjectExplorerContent(
                content.getSelectedProject(),
                content.getSelectedModule(),
                new FolderListing(null,
                                  Collections.<FolderItem>emptyList(),
                                  Collections.<FolderItem>emptyList()),
                Collections.<FolderItem, List<FolderItem>>emptyMap()
        );
    }

    private void setSelectedPackage(final Content content) {
        if (content.getSelectedModule() == null) {
            content.setSelectedPackage(null);
        } else {
            final Module selectedModule = content.getSelectedModule();
            if (content.getSelectedPackage() != null) {
                if (!content.getSelectedPackage().getModuleRootPath().equals(selectedModule.getRootPath())) {
                    content.setSelectedPackage(null);
                    content.setSelectedItem(null);
                }
            }
        }
    }

    private void setSelectedItem(final Content content) {
        if (content.getSelectedModule() == null) {
            content.setSelectedItem(null);
        } else {
            final Module selectedModule = content.getSelectedModule();
            if (content.getSelectedItem() != null) {
                if (content.getSelectedItem().getItem() instanceof Package) {
                    final Package pkg = (Package) content.getSelectedItem().getItem();
                    if (!pkg.getModuleRootPath().equals(selectedModule.getRootPath())) {
                        content.setSelectedPackage(null);
                        content.setSelectedItem(null);
                    }
                } else if (content.getSelectedItem().getItem() instanceof Path) {
                    final Path itemPath = (Path) content.getSelectedItem().getItem();
                    final Module owningModule = moduleService.resolveModule(itemPath);
                    if (!owningModule.getRootPath().equals(selectedModule.getRootPath())) {
                        content.setSelectedPackage(null);
                        content.setSelectedItem(null);
                    }
                }
            }
        }
    }

    private void setSelectedModule(final Content content) {

        if (content.getSelectedModule() == null || (
                content.getSelectedProject().getMainModule() != null &&
                        !content.getSelectedProject().getMainModule().getPom().equals(content.getSelectedModule().getPom()))) {
            content.setSelectedModule(content.getSelectedProject().getMainModule());
        } else {
            content.setSelectedModule(content.getSelectedModule());
        }
    }

    protected Content setupSelectedItems(ProjectExplorerContentQuery query) {

        final Content content = new Content(query,
                                            getProject(query));

        final UserExplorerLastData lastContent = helper.getLastContent();
        final UserExplorerData userContent = helper.loadUserContent();

        if (!lastContent.isDataEmpty()) {
            if (query.getRepository() == null && query.getModule() == null) {
                //If nothing has been selected (i.e. on start-up) set-up Content from last saved state
                if (query.getOptions().contains(Option.BUSINESS_CONTENT) && lastContent.getLastPackage() != null) {
                    Repository lastRepo = lastContent.getLastPackage().getRepository();
                    content.setSelectedProject(projectService.resolveProject(lastRepo.getSpace(),
                                                                             lastRepo.getBranch(lastContent.getLastPackage().getBranch()).get()));
                    content.setSelectedModule(lastContent.getLastPackage().getModule());
                    content.setSelectedPackage(lastContent.getLastPackage().getPkg());
                    content.setSelectedItem(null);
                } else if (query.getOptions().contains(Option.TECHNICAL_CONTENT) && lastContent.getLastFolderItem() != null) {
                    content.setSelectedProject(projectService.resolveProject(lastContent.getLastFolderItem().getRepository().getSpace(),
                                                                             lastContent.getLastFolderItem().getRepository().getBranch(lastContent.getLastFolderItem().getBranch()).get()));
                    content.setSelectedModule(lastContent.getLastFolderItem().getModule());
                    content.setSelectedItem(lastContent.getLastFolderItem().getItem());
                    content.setSelectedPackage(null);
                }
            } else if (query.getOptions().contains(Option.BUSINESS_CONTENT) && lastContent.getLastPackage() != null) {
                if (query.getRepository() != null
                        && !query.getRepository().equals(lastContent.getLastPackage().getRepository())
                        || query.getBranch() != null
                        && !query.getBranch().getName().equals(lastContent.getLastPackage().getBranch())
                        || query.getModule() != null
                        && !query.getModule().equals(lastContent.getLastPackage().getModule())) {
                    //Handle a change in selected Repository or Module in BUSINESS_CONTENT view

                    content.setSelectedProject(loadProject(query.getRepository().getSpace(),
                                                           query.getRepository().getBranch(query.getBranch().getName()).get()));
                    content.setSelectedModule(loadModule(content.getSelectedProject(),
                                                         query.getModule()));
                    content.setSelectedPackage(loadPackage(content.getSelectedProject(),
                                                           content.getSelectedModule(),
                                                           query.getPkg(),
                                                           userContent));
                    content.setSelectedItem(null);
                } else {
                    //Fall back to the last saved state
                    content.setSelectedProject(loadProject(lastContent.getLastPackage().getRepository().getSpace(),
                                                           lastContent.getLastPackage().getRepository().getBranch(lastContent.getLastPackage().getBranch()).get()));
                    content.setSelectedModule(loadModule(content.getSelectedProject(),
                                                         lastContent.getLastPackage().getModule()));
                    content.setSelectedPackage(loadPackage(content.getSelectedProject(),
                                                           content.getSelectedModule(),
                                                           lastContent.getLastPackage().getPkg(),
                                                           userContent));
                    content.setSelectedItem(null);
                }
            } else if (query.getOptions().contains(Option.TECHNICAL_CONTENT) && lastContent.getLastFolderItem() != null) {
                if (lastContent.getOptions().contains(Option.BUSINESS_CONTENT)) {
                    //When switching from BUSINESS_VIEW we cannot use LastFolderItem().getItem() and must use Module root; set by FolderListingResolver.getFolderListing()
                    content.setSelectedProject(loadProject(lastContent.getLastPackage().getRepository().getSpace(),
                                                           lastContent.getLastFolderItem().getRepository().getBranch(lastContent.getLastPackage().getBranch()).get()));
                    content.setSelectedModule(loadModule(content.getSelectedProject(),
                                                         lastContent.getLastFolderItem().getModule()));
                    content.setSelectedItem(null);
                    content.setSelectedPackage(null);
                } else if (
                        query.getRepository() != null && !query.getRepository().equals(lastContent.getLastFolderItem().getRepository()) ||
                                query.getModule() != null && !query.getModule().equals(lastContent.getLastFolderItem().getModule())) {
                    //Handle a change in selected OU, Repository or Module in TECHNICAL_CONTENT view
                    content.setSelectedProject(loadProject(query.getRepository().getSpace(),
                                                           query.getRepository().getBranch(query.getBranch().getName()).get()));
                    content.setSelectedModule(loadModule(content.getSelectedProject(),
                                                         query.getModule()));
                    content.setSelectedItem(null);
                    content.setSelectedPackage(null);
                } else {
                    //Fall back to the last saved state
                    content.setSelectedProject(loadProject(query.getRepository().getSpace(),
                                                           lastContent.getLastFolderItem().getRepository().getBranch(lastContent.getLastFolderItem().getBranch()).get()));
                    content.setSelectedModule(loadModule(content.getSelectedProject(),
                                                         lastContent.getLastFolderItem().getModule()));
                    content.setSelectedItem(loadFolderItem(content.getSelectedProject(),
                                                           content.getSelectedModule(),
                                                           lastContent.getLastFolderItem().getItem(),
                                                           userContent));
                    content.setSelectedPackage(null);
                }
            }
        }

        return content;
    }

    private WorkspaceProject getProject(final ProjectExplorerContentQuery query) {
        if (query.getBranch() == null) {
            return null;
        } else {
            return projectService.resolveProject(query.getRepository().getSpace(),
                                                 query.getBranch());
        }
    }

    List<FolderItem> getSegmentSiblings(final Path path) {
        final List<FolderItem> result = new ArrayList<>();
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

        if (nioPath.equals(nioPath.getRoot())) {
            result.add(explorerServiceHelper.toFolderItem(nioPath));
        } else {
            final org.uberfire.java.nio.file.Path nioParentPath = nioPath.getParent();
            for (org.uberfire.java.nio.file.Path sibling : getDirectoryIterator(nioParentPath)) {
                result.add(explorerServiceHelper.toFolderItem(sibling));
            }
        }

        return result;
    }

    Iterable<org.uberfire.java.nio.file.Path> getDirectoryIterator(org.uberfire.java.nio.file.Path nioParentPath) {
        return Files.newDirectoryStream(nioParentPath,
                                        dotFileFilter);
    }

    private List<FolderItem> getSegmentSiblings(final Package pkg) {
        final List<FolderItem> result = new ArrayList<>();
        final Package parentPkg = moduleService.resolveParentPackage(pkg);
        if (parentPkg == null) {
            return emptyList();
        }
        final Set<Package> siblings = moduleService.resolvePackages(parentPkg);
        if (siblings != null && !siblings.isEmpty()) {
            for (final Package sibling : siblings) {
                if (!sibling.equals(pkg)) {
                    result.add(explorerServiceHelper.toFolderItem(sibling));
                }
            }
        }

        return result;
    }

    private WorkspaceProject loadProject(Space space,
                                         final Branch branch) {
        if (branch == null) {
            return null;
        }

        return projectService.resolveProject(space,
                                             branch);
    }

    private Module loadModule(final WorkspaceProject project,
                              final Module module) {
        if (project == null) {
            return null;
        }
        if (module != null) {
            return module;
        }

        return project.getMainModule();
    }

    private Package loadPackage(final WorkspaceProject project,
                                final Module module,
                                final Package pkg,
                                final UserExplorerData content) {
        if (module == null) {
            return null;
        }

        if (pkg != null) {
            return pkg;
        }

        return content.getPackage(project,
                                  module);
    }

    private FolderItem loadFolderItem(final WorkspaceProject project,
                                      final Module module,
                                      final FolderItem item,
                                      final UserExplorerData content) {
        if (module == null) {
            return null;
        }

        if (item != null) {
            return item;
        }

        return content.getFolderItem(project,
                                     module);
    }
}
