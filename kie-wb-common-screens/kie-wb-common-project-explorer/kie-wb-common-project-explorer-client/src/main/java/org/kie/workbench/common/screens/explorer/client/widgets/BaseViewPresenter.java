/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.utils.URLHelper;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagChangedEvent;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public abstract class BaseViewPresenter {

    @Inject
    protected User identity;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Caller<ExplorerService> explorerService;

    @Inject
    protected Caller<BuildService> buildService;

    @Inject
    protected Caller<VFSService> vfsService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected Event<BuildResults> buildResultsEvent;

    @Inject
    protected ActiveContextOptions activeOptions;
    @Inject
    protected DeletePopUpPresenter deletePopUpPresenter;
    @Inject
    protected RenamePopUpPresenter renamePopUpPresenter;
    @Inject
    protected CopyPopUpPresenter copyPopUpPresenter;
    @Inject
    protected ValidationPopup validationPopup;
    protected Set<String> activeContentTags = new TreeSet<String>();
    protected String currentTag = null;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private ActiveContextItems activeContextItems;
    @Inject
    private ActiveContextManager activeContextManager;
    @Inject
    private WorkspaceProjectContext context;
    private boolean isOnLoading = false;
    private BaseViewImpl baseView;
    @Inject
    private UberfireDocks uberfireDocks;

    public BaseViewPresenter(BaseViewImpl baseView) {
        this.baseView = baseView;
    }

    @PostConstruct
    public void init() {
        activeContextManager.init(baseView,
                                  getContentCallback());
        baseView.init(this);
    }

    public void onActiveOptionsChange(final @Observes ActiveOptionsChangedEvent changedEvent) {
        final boolean isVisible = isViewVisible();
        setVisible(isVisible);
        if (isVisible) {
            initialiseViewForActiveContext(context);
        }
    }

    protected abstract boolean isViewVisible();

    public void update() {
        baseView.showHiddenFiles(activeOptions.areHiddenFilesVisible());

        baseView.setNavType(getNavType());

        if (activeOptions.isHeaderNavigationHidden()) {
            baseView.hideHeaderNavigator();
        } else {
            baseView.showHeaderNavigator();
        }

        if (activeOptions.canShowTag()) {
            activeContextManager.refresh();
        } else {
            if (activeContextItems.getActiveContent() != null) {
                baseView.setItems(activeContextItems.getActiveContent());
            }
        }
    }

    private Explorer.NavType getNavType() {
        if (activeOptions.isTreeNavigatorVisible()) {
            return Explorer.NavType.TREE;
        } else {
            return Explorer.NavType.BREADCRUMB;
        }
    }

    public void refresh() {
        baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        activeContextManager.refresh();
    }

    public void loadContent(final FolderItem item) {
        explorerService.call(new RemoteCallback<FolderListing>() {
            @Override
            public void callback(FolderListing fl) {
                baseView.getExplorer()
                        .loadContent(fl);
            }
        })
                .getFolderListing(activeContextItems.getActiveProject(),
                                  activeContextItems.getActiveModule(),
                                  item,
                                  activeOptions.getOptions());
    }

    public FolderListing getActiveContent() {
        return activeContextItems.getActiveContent();
    }

    public void deleteItem(final FolderItem folderItem) {
        final Path path = getFolderItemPath(folderItem);

        validationService.call(messages -> {
            if (((List<ValidationMessage>) messages).isEmpty()) {
                showDeletePopup(folderItem);
            } else {
                validationPopup.showDeleteValidationMessages(() -> showDeletePopup(folderItem),
                                                             () -> {
                                                             },
                                                             (List<ValidationMessage>) messages);
            }
        }).validateForDelete(path);
    }

    private void showDeletePopup(final FolderItem folderItem) {
        deletePopUpPresenter.show(new ParameterizedCommand<String>() {
            @Override
            public void execute(final String comment) {
                baseView.showBusyIndicator(CommonConstants.INSTANCE.Deleting());
                explorerService.call(new RemoteCallback<Object>() {
                                         @Override
                                         public void callback(Object o) {
                                             notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
                                             activeContextManager.refresh();
                                         }
                                     },
                                     new HasBusyIndicatorDefaultErrorCallback(baseView))
                        .deleteItem(folderItem,
                                    comment);
            }
        });
    }

    public void renameItem(final FolderItem folderItem) {
        final Path path = getFolderItemPath(folderItem);
        renamePopUpPresenter.show(path,
                                  new Validator() {
                                      @Override
                                      public void validate(final String value,
                                                           final ValidatorCallback callback) {
                                          validationService.call(new RemoteCallback<Object>() {
                                              @Override
                                              public void callback(Object response) {
                                                  if (Boolean.TRUE.equals(response)) {
                                                      callback.onSuccess();
                                                  } else {
                                                      callback.onFailure();
                                                  }
                                              }
                                          })
                                                  .isFileNameValid(path,
                                                                   value);
                                      }
                                  },
                                  new CommandWithFileNameAndCommitMessage() {
                                      @Override
                                      public void execute(final FileNameAndCommitMessage details) {
                                          baseView.showBusyIndicator(CommonConstants.INSTANCE.Renaming());
                                          explorerService.call(
                                                  getRenameSuccessCallback(getRenameView()),
                                                  getRenameErrorCallback(getRenameView())
                                          )
                                                  .renameItem(folderItem,
                                                              details.getNewFileName(),
                                                              details.getCommitMessage());
                                      }
                                  }
        );
    }

    protected RemoteCallback<Void> getRenameSuccessCallback(final RenamePopUpPresenter.View renamePopupView) {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(final Void o) {
                renamePopupView.hide();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRenamedSuccessfully()));
                baseView.hideBusyIndicator();
                refresh();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback(final RenamePopUpPresenter.View renamePopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(baseView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                renamePopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    public void copyItem(final FolderItem folderItem) {
        final Path path = getFolderItemPath(folderItem);

        validationService.call(messages -> {
            if (((List<ValidationMessage>) messages).isEmpty()) {
                showCopyPopup(folderItem,
                              path);
            } else {
                validationPopup.showCopyValidationMessages(() -> showCopyPopup(folderItem,
                                                                               path),
                                                           () -> {
                                                           },
                                                           (List<ValidationMessage>) messages);
            }
        }).validateForCopy(path);
    }

    private void showCopyPopup(final FolderItem folderItem,
                               final Path path) {
        copyPopUpPresenter.show(path,
                                new Validator() {
                                    @Override
                                    public void validate(final String value,
                                                         final ValidatorCallback callback) {
                                        validationService.call(new RemoteCallback<Object>() {
                                            @Override
                                            public void callback(Object response) {
                                                if (Boolean.TRUE.equals(response)) {
                                                    callback.onSuccess();
                                                } else {
                                                    callback.onFailure();
                                                }
                                            }
                                        })
                                                .isFileNameValid(path,
                                                                 value);
                                    }
                                },
                                new CommandWithFileNameAndCommitMessage() {
                                    @Override
                                    public void execute(final FileNameAndCommitMessage details) {
                                        baseView.showBusyIndicator(CommonConstants.INSTANCE.Copying());
                                        explorerService.call(getCopySuccessCallback(getCopyView()),
                                                             getCopyErrorCallback(getCopyView()))
                                                .copyItem(folderItem,
                                                          details.getNewFileName(),
                                                          getCopyView().getTargetPath(),
                                                          details.getCommitMessage());
                                    }
                                }
        );
    }

    protected RemoteCallback<Void> getCopySuccessCallback(final CopyPopUpPresenter.View copyPopupView) {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(final Void o) {
                copyPopupView.hide();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
                baseView.hideBusyIndicator();
                refresh();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback(final CopyPopUpPresenter.View copyPopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(baseView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                copyPopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    public void uploadArchivedFolder(final FolderItem folderItem) {
        if (folderItem.getItem() instanceof Path) {
            final Path path = (Path) folderItem.getItem();

            Window.open(URLHelper.getDownloadUrl(path),
                        "downloading",
                        "resizable=no,scrollbars=yes,status=no");
        }
    }

    private Path getFolderItemPath(final FolderItem folderItem) {
        if (folderItem.getItem() instanceof Package) {
            final Package pkg = ((Package) folderItem.getItem());
            return pkg.getPackageMainSrcPath();
        } else if (folderItem.getItem() instanceof Path) {
            return (Path) folderItem.getItem();
        }
        return null;
    }

    private void loadContent(final FolderListing content) {
        if (!activeContextItems.getActiveContent()
                .equals(content)) {
            setActiveContent(content);
            baseView.getExplorer()
                    .loadContent(content);
        }
    }

    protected void setActiveContent(FolderListing activeContent) {
        activeContextItems.setActiveContent(activeContent);
        resetTags(false);
    }

    protected void resetTags(boolean maintainSelection) {
        if (!activeOptions.canShowTag()) {
            return;
        }
        if (!maintainSelection) {
            currentTag = null;
        }
        activeContentTags.clear();
        for (FolderItem item : activeContextItems.getActiveContent()
                .getContent()) {
            if (item.getTags() != null) {
                activeContentTags.addAll(item.getTags());
            }
        }
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public Set<String> getActiveContentTags() {
        return activeContentTags;
    }

    private RemoteCallback<ProjectExplorerContent> getContentCallback() {
        return new RemoteCallback<ProjectExplorerContent>() {
            @Override
            public void callback(final ProjectExplorerContent content) {
                doContentCallback(content);
            }
        };
    }

    //Process callback in separate method to better support testing
    void doContentCallback(final ProjectExplorerContent content) {

        boolean buildSelectedModule = false;

        boolean signalChange = false;

        if (activeContextItems.setupActiveProject(content)) {
            signalChange = true;
        }

        if (activeContextItems.setupActiveModule(content)) {
            signalChange = true;
            buildSelectedModule = true;
        }

        boolean folderChange = activeContextItems.setupActiveFolderAndPackage(
                content);
        if (signalChange || folderChange) {
            activeContextItems.fireContextChangeEvent();
        }

        if (buildSelectedModule) {
            buildModule(activeContextItems.getActiveModule());
        }

        setActiveContent(content.getFolderListing());

        baseView.getExplorer()
                .clear();
        baseView.setContent(activeContextItems.getActiveModule(),
                            activeContextItems.getActiveContent(),
                            content.getSiblings());

        if (activeContextItems.getActiveFolderItem() == null) {
            activeContextItems.setupActiveFolderAndPackage(content);
        }
        baseView.hideBusyIndicator();
    }

    private void buildModule(final Module module) {
        //Don't build automatically if disabled
        if (ApplicationPreferences.getBooleanPref(ExplorerService.BUILD_PROJECT_PROPERTY_NAME)) {
            return;
        }
        if (module == null) {
            return;
        }
        buildService.call(
                new RemoteCallback<BuildResults>() {
                    @Override
                    public void callback(final BuildResults results) {
                        buildResultsEvent.fire(results);
                    }
                })
                .build(module);
    }

    public void onModuleSelected(final Module module) {
        if (Utils.hasModuleChanged(module,
                                   activeContextItems.getActiveModule())) {
            baseView.getExplorer()
                    .clear();
            activeContextManager.initActiveContext(activeContextItems.getActiveProject().getRepository(),
                                                   activeContextItems.getActiveProject().getBranch(),
                                                   module);
        }
    }

    public void onActiveFolderItemSelected(final FolderItem item) {
        if (!isOnLoading && Utils.hasFolderItemChanged(item,
                                                       activeContextItems.getActiveFolderItem())) {
            activeContextItems.setActiveFolderItem(item);
            activeContextItems.fireContextChangeEvent();

            //Show busy popup. Once Items are loaded it is closed
            baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());
            explorerService.call(new RemoteCallback<FolderListing>() {
                                     @Override
                                     public void callback(final FolderListing folderListing) {
                                         isOnLoading = true;
                                         loadContent(folderListing);
                                         baseView.setItems(folderListing);
                                         baseView.hideBusyIndicator();
                                         isOnLoading = false;
                                     }
                                 },
                                 new HasBusyIndicatorDefaultErrorCallback(baseView))
                    .getFolderListing(activeContextItems.getActiveProject(),
                                      activeContextItems.getActiveModule(),
                                      item,
                                      activeOptions.getOptions());
        }
    }

    public void onItemSelected(final FolderItem folderItem) {
        final Object _item = folderItem.getItem();
        if (_item == null) {
            return;
        }
        if (folderItem.getType()
                .equals(FolderItemType.FILE) && _item instanceof Path) {
            placeManager.goTo((Path) _item);
        } else {
            onActiveFolderItemSelected(folderItem);
        }
    }

    public void setVisible(final boolean visible) {
        baseView.setVisible(visible);
    }

    public void onTagFilterChanged(@Observes TagChangedEvent event) {
        if (!baseView.isVisible()) {
            return;
        }
        if (!activeOptions.canShowTag()) {
            return;
        }
        filterByTag(event.getTag());
    }

    protected void filterByTag(String tag) {
        currentTag = tag;
        List<FolderItem> filteredItems = new ArrayList<FolderItem>();

        for (FolderItem item : activeContextItems.getActiveContent()
                .getContent()) {
            if (tag == null || item.getTags()
                    .contains(tag) || item.getType()
                    .equals(FolderItemType.FOLDER)) {
                filteredItems.add(item);
            }
        }

        FolderListing filteredContent = new FolderListing(activeContextItems.getActiveContent()
                                                                  .getItem(),
                                                          filteredItems,
                                                          activeContextItems.getActiveContent()
                                                                  .getSegments());
        baseView.renderItems(filteredContent);
    }

    // Refresh when a Resource has been updated, if it exists in the active package
    public void onResourceUpdated(@Observes final ResourceUpdatedEvent event) {
        refresh(event.getPath());
    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded(@Observes final ResourceAddedEvent event) {
        refresh(event.getPath());
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted(@Observes final ResourceDeletedEvent event) {
        refresh(event.getPath());
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied(@Observes final ResourceCopiedEvent event) {
        refresh(event.getDestinationPath());
    }

    // Refresh when a lock status changes has occurred, if it affects the active package
    public void onLockStatusChange(@Observes final LockInfo lockInfo) {
        refresh(lockInfo.getFile(),
                true);
    }

    private void refresh(final Path resource) {
        refresh(resource,
                false);
    }

    private void refresh(final Path resource,
                         boolean force) {
        if (!baseView.isVisible()) {
            return;
        }
        if (resource == null || activeContextItems.getActiveModule() == null) {
            return;
        }
        if (!force && !Utils.isInFolderItem(activeContextItems.getActiveFolderItem(),
                                            resource)) {
            return;
        }

        explorerService.call(new RemoteCallback<FolderListing>() {
            @Override
            public void callback(final FolderListing folderListing) {
                activeContextItems.setActiveContent(folderListing);
                if (activeOptions.canShowTag()) {
                    resetTags(true);
                    filterByTag(currentTag);
                } else {
                    baseView.setItems(folderListing);
                }
            }
        })
                .getFolderListing(activeContextItems.getActiveProject(),
                                  activeContextItems.getActiveModule(),
                                  activeContextItems.getActiveFolderItem(),
                                  activeOptions.getOptions());
    }

    public void initialiseViewForActiveContext(final WorkspaceProjectContext context) {
        activeContextManager.initActiveContext(context);
    }

    public void initialiseViewForActiveContext(String initPath) {
        activeContextManager.initActiveContext(initPath);
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed(@Observes final ResourceRenamedEvent event) {
        if (!baseView.isVisible()) {
            return;
        }
        final Path sourcePath = event.getPath();
        final Path destinationPath = event.getDestinationPath();

        boolean refresh = false;
        if (Utils.isInFolderItem(activeContextItems.getActiveFolderItem(),
                                 sourcePath)) {
            refresh = true;
        } else if (Utils.isInFolderItem(activeContextItems.getActiveFolderItem(),
                                        destinationPath)) {
            refresh = true;
        }

        if (refresh) {
            explorerService.call(new RemoteCallback<FolderListing>() {
                @Override
                public void callback(final FolderListing folderListing) {
                    baseView.setItems(folderListing);
                }
            })
                    .getFolderListing(activeContextItems.getActiveProject(),
                                      activeContextItems.getActiveModule(),
                                      activeContextItems.getActiveFolderItem(),
                                      activeOptions.getOptions());
        }
    }

    RenamePopUpPresenter.View getRenameView() {
        return renamePopUpPresenter.getView();
    }

    CopyPopUpPresenter.View getCopyView() {
        return copyPopUpPresenter.getView();
    }

    public boolean canShowTags() {
        return activeOptions.canShowTag();
    }
}
