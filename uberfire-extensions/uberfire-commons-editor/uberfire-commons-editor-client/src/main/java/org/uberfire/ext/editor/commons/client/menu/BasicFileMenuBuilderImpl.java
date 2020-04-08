/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper.LockSyncMenuStateHelper.Operation;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.newSimpleItem;

public class BasicFileMenuBuilderImpl implements BasicFileMenuBuilder {

    private RestoreVersionCommandProvider restoreVersionCommandProvider;
    private Event<NotificationEvent> notification;
    private BusyIndicatorView busyIndicatorView;
    private DeletePopUpPresenter deletePopUpPresenter;
    private CopyPopUpPresenter copyPopUpPresenter;
    private RenamePopUpPresenter renamePopUpPresenter;
    private Command saveCommand = null;
    private MenuItem saveMenuItem;
    private Command deleteCommand = null;
    private MenuItem deleteMenuItem;
    private Command renameCommand = null;
    private MenuItem renameMenuItem;
    private Command copyCommand = null;
    private Command validateCommand = null;
    private Command restoreCommand = null;
    private MenuItem restoreMenuItem;
    private List<Pair<String, Command>> otherCommands = new ArrayList<Pair<String, Command>>();
    private List<MenuItem> topLevelMenus = new ArrayList<MenuItem>();
    private List<MenuItem> menuItemsSyncedWithLockState = new ArrayList<MenuItem>();
    private LockSyncMenuStateHelper lockSyncMenuStateHelper = new BasicFileMenuBuilder.BasicLockSyncMenuStateHelper();

    @Inject
    public BasicFileMenuBuilderImpl(final DeletePopUpPresenter deletePopUpPresenter,
                                    final CopyPopUpPresenter copyPopUpPresenter,
                                    final RenamePopUpPresenter renamePopUpPresenter,
                                    final BusyIndicatorView busyIndicatorView,
                                    final Event<NotificationEvent> notification,
                                    final RestoreVersionCommandProvider restoreVersionCommandProvider) {
        this.deletePopUpPresenter = deletePopUpPresenter;
        this.copyPopUpPresenter = copyPopUpPresenter;
        this.renamePopUpPresenter = renamePopUpPresenter;
        this.busyIndicatorView = busyIndicatorView;
        this.notification = notification;
        this.restoreVersionCommandProvider = restoreVersionCommandProvider;
    }

    @Override
    public BasicFileMenuBuilder addSave(final MenuItem menuItem) {
        saveMenuItem = menuItem;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addSave(final Command command) {
        this.saveCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addDelete(final Path path,
                                          final Caller<? extends SupportsDelete> deleteCaller) {
        return addDelete(path,
                         deleteCaller,
                         null);
    }

    @Override
    public BasicFileMenuBuilder addDelete(final Path path,
                                          final Caller<? extends SupportsDelete> deleteCaller,
                                          final Validator validator) {
        return addDelete(() -> {
            deletePopUpPresenter.show(validator,
                                      (String comment) -> {
                                          busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Deleting());
                                          deleteCaller.call(getDeleteSuccessCallback(),
                                                            new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).delete(path,
                                                                                                                                comment);
                                      });
        });
    }

    @Override
    public BasicFileMenuBuilder addDelete(final PathProvider provider,
                                          final Caller<? extends SupportsDelete> deleteCaller) {
        return addDelete(provider,
                         deleteCaller,
                         null);
    }

    @Override
    public BasicFileMenuBuilder addDelete(final PathProvider provider,
                                          final Caller<? extends SupportsDelete> deleteCaller,
                                          final Validator validator) {
        return addDelete(() -> {
            final Path path = provider.getPath();
            deletePopUpPresenter.show(validator,
                                      (String comment) -> {
                                          busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Deleting());
                                          deleteCaller.call(getDeleteSuccessCallback(),
                                                            new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).delete(path,
                                                                                                                                comment);
                                      });
        });
    }

    private RemoteCallback<Void> getDeleteSuccessCallback() {
        return (Void v) -> {
            busyIndicatorView.hideBusyIndicator();
            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
        };
    }

    @Override
    public BasicFileMenuBuilder addDelete(final Command command) {
        this.deleteCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRename(final Command command) {
        this.renameCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRename(final Path path,
                                          final Caller<? extends SupportsRename> renameCaller) {
        return addRename(() -> {
            CommandWithFileNameAndCommitMessage command = getRenamePopupCommand(renameCaller,
                                                                                path,
                                                                                renamePopUpPresenter.getView());
            renamePopUpPresenter.show(path,
                                      command);
        });
    }

    @Override
    public BasicFileMenuBuilder addRename(final Path path,
                                          final Validator validator,
                                          final Caller<? extends SupportsRename> renameCaller) {
        return addRename(() -> {
            CommandWithFileNameAndCommitMessage command = getRenamePopupCommand(renameCaller,
                                                                                path,
                                                                                renamePopUpPresenter.getView());
            renamePopUpPresenter.show(path,
                                      validator,
                                      command);
        });
    }

    @Override
    public BasicFileMenuBuilder addRename(final PathProvider provider,
                                          final Validator validator,
                                          final Caller<? extends SupportsRename> renameCaller) {
        return addRename(() -> {
            final Path path = provider.getPath();
            final CommandWithFileNameAndCommitMessage command = getRenamePopupCommand(renameCaller,
                                                                                      path,
                                                                                      renamePopUpPresenter.getView());
            renamePopUpPresenter.show(path,
                                      validator,
                                      command);
        });
    }

    private CommandWithFileNameAndCommitMessage getRenamePopupCommand(final Caller<? extends SupportsRename> renameCaller,
                                                                      final Path path,
                                                                      final RenamePopUpPresenter.View renamePopupView) {
        return (FileNameAndCommitMessage details) -> {
            busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Renaming());
            renameCaller.call(getRenameSuccessCallback(renamePopupView),
                              getRenameErrorCallback(renamePopupView,
                                                     busyIndicatorView)).rename(path,
                                                                                details.getNewFileName(),
                                                                                details.getCommitMessage());
        };
    }

    private RemoteCallback<Path> getRenameSuccessCallback(final RenamePopUpPresenter.View renamePopupView) {
        return (Path path) -> {
            renamePopupView.hide();
            busyIndicatorView.hideBusyIndicator();
            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRenamedSuccessfully()));
        };
    }

    private HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback(final RenamePopUpPresenter.View renamePopupView,
                                                                        BusyIndicatorView busyIndicatorView) {
        return new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                if (fileAlreadyExists(throwable)) {
                    hideBusyIndicator();
                    renamePopupView.handleDuplicatedFileName();
                    return false;
                }

                renamePopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addCopy(final Command command) {
        this.copyCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addCopy(final Path path,
                                        final Caller<? extends SupportsCopy> copyCaller) {
        return addCopy(() -> copyPopUpPresenter.show(path,
                                                     getCopyPopupCommand(copyCaller,
                                                                         path,
                                                                         copyPopUpPresenter.getView())));
    }

    @Override
    public BasicFileMenuBuilder addCopy(final Path path,
                                        final Validator validator,
                                        final Caller<? extends SupportsCopy> copyCaller) {
        return addCopy(() -> copyPopUpPresenter.show(path,
                                                     validator,
                                                     getCopyPopupCommand(copyCaller,
                                                                         path,
                                                                         copyPopUpPresenter.getView())));
    }

    public BasicFileMenuBuilder addCopy(final PathProvider provider,
                                        final Validator validator,
                                        final Caller<? extends SupportsCopy> copyCaller) {
        return addCopy(() -> {
            final Path path = provider.getPath();
            copyPopUpPresenter.show(path,
                                    validator,
                                    getCopyPopupCommand(copyCaller,
                                                        path,
                                                        copyPopUpPresenter.getView()));
        });
    }

    private CommandWithFileNameAndCommitMessage getCopyPopupCommand(final Caller<? extends SupportsCopy> copyCaller,
                                                                    final Path path,
                                                                    final CopyPopUpPresenter.View copyPopupView) {
        return (FileNameAndCommitMessage details) -> {
            busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Copying());
            copyCaller.call(getCopySuccessCallback(copyPopupView),
                            getCopyErrorCallback(copyPopupView,
                                                 busyIndicatorView)).copy(path,
                                                                          details.getNewFileName(),
                                                                          copyPopupView.getTargetPath(),
                                                                          details.getCommitMessage());
        };
    }

    private RemoteCallback<Path> getCopySuccessCallback(final CopyPopUpPresenter.View copyPopupView) {
        return (final Path path) -> {
            copyPopupView.hide();
            busyIndicatorView.hideBusyIndicator();
            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
        };
    }

    public HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback(final CopyPopUpPresenter.View copyPopupView,
                                                                     BusyIndicatorView busyIndicatorView) {
        return new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                if (fileAlreadyExists(throwable)) {
                    hideBusyIndicator();
                    copyPopupView.handleDuplicatedFileName();
                    return false;
                }

                copyPopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    private boolean fileAlreadyExists(final Throwable throwable) {
        return throwable != null && throwable.getMessage() != null && throwable.getMessage().contains("FileAlreadyExistsException");
    }

    @Override
    public BasicFileMenuBuilder addValidate(final Command validateCommand) {
        this.validateCommand = validateCommand;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRestoreVersion(final Path path,
                                                  final CurrentBranch currentBranch) {
        this.restoreCommand = restoreVersionCommandProvider.getCommand(path,
                                                                       currentBranch);
        return this;
    }

    @Override
    public BasicFileMenuBuilder addCommand(final String caption,
                                           final Command command) {
        this.otherCommands.add(new Pair<String, Command>(caption,
                                                         command));
        return this;
    }

    @Override
    public Menus build() {
        final Map<Object, MenuItem> menuItems = new LinkedHashMap<Object, MenuItem>();
        if (saveCommand != null) {
            menuItems.put(MenuItems.SAVE,
                          MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Save())
                                  .respondsWith(saveCommand)
                                  .endMenu()
                                  .build().getItems().get(0));
        } else if (saveMenuItem != null) {
            menuItems.put(MenuItems.SAVE,
                          saveMenuItem);
            menuItemsSyncedWithLockState.add(saveMenuItem);
        }

        if (deleteCommand != null) {
            if (deleteMenuItem == null) {
                deleteMenuItem = MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Delete())
                        .respondsWith(deleteCommand)
                        .endMenu()
                        .build().getItems().get(0);
            }
            menuItems.put(MenuItems.DELETE,
                          deleteMenuItem);
            menuItemsSyncedWithLockState.add(deleteMenuItem);
        }

        if (renameCommand != null) {
            if (renameMenuItem == null) {
                renameMenuItem = MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Rename())
                        .respondsWith(renameCommand)
                        .endMenu()
                        .build().getItems().get(0);
            }
            menuItems.put(MenuItems.RENAME,
                          renameMenuItem);
            menuItemsSyncedWithLockState.add(renameMenuItem);
        }

        if (copyCommand != null) {
            menuItems.put(MenuItems.COPY,
                          MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Copy())
                                  .respondsWith(copyCommand)
                                  .endMenu()
                                  .build().getItems().get(0));
        }

        if (validateCommand != null) {
            menuItems.put(MenuItems.VALIDATE,
                          MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Validate())
                                  .respondsWith(validateCommand)
                                  .endMenu()
                                  .build().getItems().get(0));
        }

        if (restoreCommand != null) {
            if (restoreMenuItem == null) {
                restoreMenuItem = MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Restore())
                        .respondsWith(restoreCommand)
                        .endMenu()
                        .build().getItems().get(0);
            }
            menuItemsSyncedWithLockState.add(restoreMenuItem);
        }

        if (!(otherCommands == null || otherCommands.isEmpty())) {
            final List<MenuItem> otherMenuItems = new ArrayList<MenuItem>();
            for (Pair<String, Command> other : otherCommands) {
                otherMenuItems.add(newSimpleItem(other.getK1())
                                           .respondsWith(other.getK2())
                                           .endMenu().build().getItems().get(0));
            }
            final MenuItem item = MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Other())
                    .withItems(otherMenuItems)
                    .endMenu()
                    .build().getItems().get(0);
            menuItems.put(item,
                          item);
        }

        for (MenuItem menuItem : topLevelMenus) {
            menuItems.put(menuItem,
                          menuItem);
        }

        return new Menus() {

            @Override
            public List<MenuItem> getItems() {
                return new ArrayList<MenuItem>() {{
                    for (final MenuItem menuItem : menuItems.values()) {
                        add(menuItem);
                    }
                }};
            }

            @Override
            public Map<Object, MenuItem> getItemsMap() {
                return menuItems;
            }

            @Override
            public void accept(MenuVisitor visitor) {
                if (visitor.visitEnter(this)) {
                    for (final MenuItem item : menuItems.values()) {
                        item.accept(visitor);
                    }
                    visitor.visitLeave(this);
                }
            }

            @Override
            public int getOrder() {
                return 0;
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addNewTopLevelMenu(MenuItem menu) {
        topLevelMenus.add(menu);
        return this;
    }

    @Override
    public void setLockSyncMenuStateHelper(final LockSyncMenuStateHelper lockSyncMenuStateHelper) {
        this.lockSyncMenuStateHelper = PortablePreconditions.checkNotNull("lockSyncMenuStateHelper",
                                                                          lockSyncMenuStateHelper);
    }

    void onEditorLockInfo(@Observes UpdatedLockStatusEvent lockInfo) {
        final Operation op = lockSyncMenuStateHelper.enable(lockInfo.getFile(),
                                                            lockInfo.isLocked(),
                                                            lockInfo.isLockedByCurrentUser());
        switch (op) {
            case ENABLE:
            case DISABLE:
                for (MenuItem menuItem : menuItemsSyncedWithLockState) {
                    menuItem.setEnabled(op == Operation.ENABLE);
                }
                break;
            case VETO:
                //Do nothing
        }
    }
}
