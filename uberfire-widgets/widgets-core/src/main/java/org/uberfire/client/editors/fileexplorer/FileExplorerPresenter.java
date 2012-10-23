/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.editors.fileexplorer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.VFSTempUtil;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.common.Util;
import org.uberfire.client.editors.repositorieseditor.CloneRepositoryWizard;
import org.uberfire.client.editors.repositorieseditor.NewRepositoryWizard;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.IdentifierUtils;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.CoreImages;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.ToolBarItem;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter {

    @Inject
    private View view;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IdentifierUtils idUtils;

    @Inject
    private IOCBeanManager iocManager;

    private static final String REPOSITORY_ID = "repositories";

    private Command newRepoCommand = null;

    private Command cloneRepoCommand = null;

    public interface View
            extends
            IsWidget {

        TreeItem getRootItem();

        Tree getTree();

        void setFocus();
    }

    private static CoreImages images = GWT.create(CoreImages.class);
    private static final String LAZY_LOAD = "Loading...";

    @PostConstruct
    public void init(){
        this.cloneRepoCommand = new Command() {

            @Override
            public void execute() {
                final CloneRepositoryWizard cloneRepositoryWizard = iocManager.lookupBean(CloneRepositoryWizard.class).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                cloneRepositoryWizard.addCloseHandler(new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        iocManager.destroyBean(cloneRepositoryWizard);
                    }

                });
                cloneRepositoryWizard.show();
            }

        };

        this.newRepoCommand = new Command() {
            @Override
            public void execute() {
                final NewRepositoryWizard newRepositoryWizard = iocManager.lookupBean(NewRepositoryWizard.class).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                newRepositoryWizard.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        iocManager.destroyBean(newRepositoryWizard);
                    }
                });
                newRepositoryWizard.show();
            }
        };
    }

    @OnStart
    public void onStart() {
        view.getRootItem().setUserObject(REPOSITORY_ID);
        view.getRootItem().addItem(LAZY_LOAD);

        view.getRootItem().removeItems();

        rootService.call(new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback(Collection<Root> response) {
                for (final Root root : response) {
                    loadRoot(root);
                }
            }
        }).listRoots();

        view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
            @Override
            public void onOpen(final OpenEvent<TreeItem> event) {
                if (needsLoading(event.getTarget()) && event.getTarget().getUserObject() instanceof Path) {
                    vfsService.call(new RemoteCallback<DirectoryStream<Path>>() {
                        @Override
                        public void callback(DirectoryStream<Path> response) {
                            event.getTarget().getChild(0).remove();
                            for (final Path path : response) {
                                vfsService.call(new RemoteCallback<Map>() {
                                    @Override
                                    public void callback(final Map response) {
                                        final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(response);
                                        final TreeItem item;
                                        if (attrs.isDirectory()) {
                                            item = event.getTarget().addItem(Util.getHeader(images.openedFolder(),
                                                    path.getFileName()));
                                            item.addItem(LAZY_LOAD);
                                        } else {
                                            item = event.getTarget().addItem(Util.getHeader(images.file(),
                                                    path.getFileName()));
                                        }
                                        item.setUserObject(path);
                                    }
                                }).readAttributes(path);
                            }
                        }
                    }).newDirectoryStream((Path) event.getTarget().getUserObject());
                }
            }
        });

        view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                final Object userObject = event.getSelectedItem().getUserObject();
                if (userObject != null && userObject instanceof Path) {
                    final Path path = (Path) userObject;
                    vfsService.call(new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(response);
                            if (attrs.isRegularFile()) {
                                PlaceRequest placeRequest = getPlace(path);
                                placeManager.goTo(placeRequest);
                            }
                        }
                    }).readAttributes(path);
                } else if (event.getSelectedItem().getUserObject() instanceof String && ((String) event.getSelectedItem().getUserObject()).equals(REPOSITORY_ID)) {
                    placeManager.goTo(new DefaultPlaceRequest("RepositoriesEditor"));
                } else if (userObject != null && userObject instanceof Root) {
                    final Root root = (Root) userObject;
                    placeManager.goTo(root.getPlaceRequest());
                }
            }
        });
    }

    private void loadRoot(final Root root) {

        //TODO check if it already exists and cleanup

        final TreeItem repositoryRootItem = view.getRootItem().addItem(Util.getHeader(images.packageIcon(),
                root.getPath().getFileName()));
        repositoryRootItem.setState(true);
        repositoryRootItem.setUserObject(root);

        vfsService.call(new RemoteCallback<DirectoryStream<Path>>() {
            @Override
            public void callback(DirectoryStream<Path> response) {
                for (final Path path : response) {
                    vfsService.call(new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(response);
                            final TreeItem item;
                            if (attrs.isDirectory()) {
                                item = repositoryRootItem.addItem(Util.getHeader(images.openedFolder(),
                                        path.getFileName()));
                                item.addItem(LAZY_LOAD);
                            } else {
                                item = repositoryRootItem.addItem(Util.getHeader(images.file(),
                                        path.getFileName()));
                            }
                            item.setUserObject(path);
                        }
                    }).readAttributes(path);
                }
            }
        }).newDirectoryStream(root.getPath());
    }

    private PlaceRequest getPlace(final Path path) {

        final String fileType = getFileType(path.getFileName());
        if (fileType == null) {
            return defaultPlace(path);
        }

        //Lookup an Activity that can handle the file extension and create a corresponding PlaceRequest.
        //We could simply construct a PlaceRequest for the fileType and leave PlaceManager to determine whether
        //an Activity for the fileType exists however that would place the decision as to what default editor
        //to use within PlaceManager. It is a design decision to let FileExplorer determine the default editor.
        //Consequentially we check for an Activity here and, if none found, define the default editor.
        final Set<IOCBeanDef<Activity>> activityBeans = idUtils.getActivities(fileType);
        if (activityBeans.size() > 0) {
            final PlaceRequest place = new DefaultPlaceRequest(fileType);
            place.addParameter("path:uri",
                    path.toURI()).addParameter("path:name",
                    path.getFileName());
            return place;
        }

        //If a specific handler was not found use a TextEditor
        return defaultPlace(path);
    }

    private PlaceRequest defaultPlace(final Path path) {
        PlaceRequest defaultPlace = new DefaultPlaceRequest("TextEditor");
        defaultPlace.addParameter("path:uri",
                path.toURI()).addParameter("path:name",
                path.getFileName());
        return defaultPlace;
    }

    private String getFileType(final String fileName) {
        final int dotIndex = fileName.indexOf(".");
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "File Explorer";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.WEST;
    }

    @WorkbenchToolBar
    public ToolBar buildToolBar() {
        final ToolBar toolBar = new DefaultToolBar();
        final ToolBarItem clone = new DefaultToolBarItem("image/clone_repo.png",
                "Clone Repo", cloneRepoCommand);
        toolBar.addItem(clone);

        final ToolBarItem newRepo = new DefaultToolBarItem("image/new_repo.png",
                "New Repository", newRepoCommand);
        toolBar.addItem(newRepo);
        return toolBar;
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();
        menuBar.addItem(new DefaultMenuItemSubMenu("Repositories", subMenuBar));

        final MenuItem cloneRepo = new DefaultMenuItemCommand("Clone Repo", cloneRepoCommand);
        final MenuItem newRepo = new DefaultMenuItemCommand("New Repo", newRepoCommand);

        subMenuBar.addItem(cloneRepo);
        subMenuBar.addItem(newRepo);

        return menuBar;
    }

    private boolean needsLoading(TreeItem item) {
        return item.getChildCount() == 1
                && LAZY_LOAD.equals(item.getChild(0).getText());
    }

    public void newRootDirectory(@Observes Root root) {
        loadRoot(root);
    }

}