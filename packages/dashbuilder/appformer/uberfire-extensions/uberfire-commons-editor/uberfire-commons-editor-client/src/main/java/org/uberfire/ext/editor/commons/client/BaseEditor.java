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

package org.uberfire.ext.editor.commons.client;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DOWNLOAD;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.VALIDATE;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;

import elemental2.promise.Promise;

public abstract class BaseEditor<T, M> {

    protected boolean isReadOnly;

    protected BaseEditorView baseView;

    protected ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    protected Menus menus;

    protected Promise<Void> makeMenuBarPromise;

    protected boolean saveWithComments = true;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected ManagedInstance<BasicFileMenuBuilder> menuBuilderManagedInstance;

    @Inject
    protected BasicFileMenuBuilder menuBuilder;

    @Inject
    protected DefaultFileNameValidator fileNameValidator;

    @Inject
    private DownloadMenuItemBuilder downloadMenuItemBuilder;

    @Inject
    protected Promises promises;

    @Inject
    protected DeletePopUpPresenter deletePopUpPresenter;

    @Inject
    protected SaveAndRenameCommandBuilder<T, M> saveAndRenameCommandBuilder;

    protected ConcurrentChangePopup concurrentChangePopup;

    protected Set<MenuItems> menuItems = new HashSet<>();

    protected PlaceRequest place;
    protected ClientResourceType type;
    protected Integer originalHash;
    protected Integer metadataOriginalHash;
    protected boolean isValidationRunning = false;
    protected ObservablePath path;

    //for test purposes only
    BaseEditor(BaseEditorView baseView,
               BasicFileMenuBuilder menuBuilder,
               Event<ChangeTitleWidgetEvent> changeTitleNotification) {
        this.baseView = baseView;
        this.menuBuilder = menuBuilder;
        this.changeTitleNotification = changeTitleNotification;
    }

    protected BaseEditor() {}

    protected BaseEditor(final BaseEditorView baseView) {
        this.baseView = baseView;
    }

    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final MenuItems... menuItems) {
        init(path,
                place,
                type,
                true,
                menuItems);
    }

    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final boolean addFileChangeListeners,
                        final MenuItems... menuItems) {

        init(path,
                place,
                type,
                addFileChangeListeners,
                Arrays.asList(menuItems));
    }

    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final boolean addFileChangeListeners,
                        final Collection<MenuItems> menuItems) {
        this.path = path;
        this.place = place;
        this.type = type;
        this.menuItems.addAll(menuItems);

        baseView.showLoading();

        this.isReadOnly = this.place.getParameter("readOnly",
                null) == null ? false : true;

        if (addFileChangeListeners) {
            addFileChangeListeners(path);
        }

        getMenus(menus -> loadContent());

        concurrentUpdateSessionInfo = null;
    }

    protected void showVersions() {

    }

    /**
     * If you want to customize the menu content override this method.
     */
    protected Promise<Void> makeMenuBar() {
        if (menuItems.contains(SAVE)) {
            menuBuilder.addSave(getOnSave());
        }

        if (menuItems.contains(COPY)) {
            menuBuilder.addCopy(path,
                    getCopyValidator(),
                    getCopyServiceCaller());
        }
        if (menuItems.contains(RENAME)) {
            menuBuilder.addRename(getSaveAndRename());
        }

        if (menuItems.contains(DELETE)) {
            menuBuilder.addDelete(path,
                    getDeleteServiceCaller());
        }
        if (menuItems.contains(VALIDATE)) {
            menuBuilder.addValidate(getValidateCommand());
        }
        if (menuItems.contains(DOWNLOAD)) {
            addDownloadMenuItem(menuBuilder);
        }

        return promises.resolve();
    }

    protected void addDownloadMenuItem(final BasicFileMenuBuilder menuBuilder) {
        menuBuilder.addNewTopLevelMenu(downloadMenuItem());
    }

    protected MenuItem downloadMenuItem() {
        return downloadMenuItemBuilder.build(getPathSupplier());
    }

    Command getOnSave() {
        return this::onSave;
    }

    protected Command getBeforeSaveAndRenameCommand() {
        return () -> {
        };
    }

    protected Supplier<Boolean> getSaveValidator() {
        return () -> true;
    }

    protected ParameterizedCommand<Path> onSuccess() {

        return (path) -> {

            final T content = getContentSupplier().get();
            final M metadata = getMetadataSupplier().get();

            setOriginalHash(content.hashCode());

            Optional<M> optionalMetadata = Optional.ofNullable(getMetadataSupplier().get());

            if (optionalMetadata.isPresent()) {
                setMetadataOriginalHash(metadata.hashCode());
            }
        };
    }

    protected Supplier<Path> getPathSupplier() {
        return () -> path;
    }

    /**
     * If you want to customize the menu construction override this method. {@link BaseEditor#makeMenuBar()}
     * should be used to add items to the {@link BasicFileMenuBuilder}. This method then instructs
     * {@link BasicFileMenuBuilder#build()} to create the {@link Menus}
     */
    protected void buildMenuBar() {
        if (menuBuilder != null && menus == null) {
            menus = menuBuilder.build();
        }
    }

    public Validator getRenameValidator() {
        return fileNameValidator;
    }

    public Validator getCopyValidator() {
        return fileNameValidator;
    }

    public void setOriginalHash(Integer originalHash) {
        this.originalHash = originalHash;
    }

    private void addFileChangeListeners(final ObservablePath path) {
        path.onRename(this::onRename);
        path.onDelete(this::onDelete);
    }

    private void onDelete() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                placeManager.forceClosePlace(place);
            }
        });
    }

    protected void onRename() {
        reload(path);
    }

    /**
     * Override this method and use @WorkbenchPartTitleDecoration
     * @return The widget for the title
     */
    protected IsWidget getTitle() {
        refreshTitle(path);
        return getTitleWidget();
    }

    EditorTitle getTitleWidget() {
        return baseView.getTitleWidget();
    }

    public String getTitleText() {
        return getTitleText(path);
    }

    String getTitleText(final ObservablePath observablePath) {
        return observablePath.getFileName() + " - " + getType().getDescription();
    }

    ClientResourceType getType() {
        return type;
    }

    protected void onSave() {

        final boolean isValid = getSaveValidator().get();

        if (isValid) {
            save();
        }
    }

    public RemoteCallback<Path> getSaveSuccessCallback(final int newHash) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                baseView.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
                setOriginalHash(newHash);
            }
        };
    }

    public void reload() {
        concurrentUpdateSessionInfo = null;
        reload(path);
    }

    void reload(final ObservablePath path) {
        refreshTitle(path);
        showBusyIndicator();
        loadContent();
        notifyChangeTitle(path);
    }

    void refreshTitle(final ObservablePath observablePath) {
        baseView.refreshTitle(getTitleText(observablePath));
    }

    void showBusyIndicator() {
        baseView.showBusyIndicator(makeLoading());
    }

    String makeLoading() {
        return CommonConstants.INSTANCE.Loading();
    }

    void notifyChangeTitle(final ObservablePath path) {
        changeTitleNotification.fire(makeChangeTitleWidgetEvent(path));
    }

    ChangeTitleWidgetEvent makeChangeTitleWidgetEvent(final ObservablePath path) {

        final String titleText = getTitleText(path);
        final EditorTitle titleWidget = getTitleWidget();

        return new ChangeTitleWidgetEvent(getPlace(), titleText, titleWidget);
    }

    PlaceRequest getPlace() {
        return place;
    }

    void disableMenus() {
        disableMenuItem(COPY);
        disableMenuItem(MenuItems.RENAME);
        disableMenuItem(MenuItems.DELETE);
        disableMenuItem(MenuItems.VALIDATE);
    }

    public void disableMenuItem(final MenuItems menuItem) {
        setEnableMenuItem(menuItem, false);
    }

    public void enableMenuItem(final MenuItems menuItem) {
        setEnableMenuItem(menuItem, true);
    }

    private void setEnableMenuItem(final MenuItems menuItem,
                                   final boolean isEnabled) {
        getMenus(menus -> {
            if (menus.getItemsMap().containsKey(menuItem)) {
                menus.getItemsMap().get(menuItem).setEnabled(isEnabled);
            }
        });
    }

    public void getMenus(final Consumer<Menus> menusConsumer) {
        if (menus != null) {
            menusConsumer.accept(menus);
            return;
        }

        if (makeMenuBarPromise == null) {
            makeMenuBarPromise = makeMenuBar().then(v -> {
                buildMenuBar();
                menusConsumer.accept(menus);
                return promises.resolve();
            });
        } else {
            makeMenuBarPromise.then(v -> {
                menusConsumer.accept(menus);
                return promises.resolve();
            });
        }
    }

    public Command getValidateCommand() {

        return new Command() {

            @Override
            public void execute() {
                if (!isValidationRunning) {

                    onBeforeValidate();

                    onValidate(new Command() {

                        @Override
                        public void execute() {
                            onAfterValidate();
                        }
                    });
                }
            }
        };
    }

    protected void onBeforeValidate() {
        baseView.showBusyIndicator(CommonConstants.INSTANCE.Validating());
        isValidationRunning = true;
    }

    protected void onAfterValidate() {
        baseView.hideBusyIndicator();
        isValidationRunning = false;
    }

    /**
     * If your editor has validation, overwrite this.
     * @param finished Called when validation is finished.
     */
    protected void onValidate(final Command finished) {

    }

    protected abstract void loadContent();

    protected Supplier<T> getContentSupplier() {
        return () -> null;
    }

    protected Supplier<Boolean> isDirtySupplier() {
        return () -> isContentDirty() || isMetadataDirty();
    }

    boolean isMetadataDirty() {

        final Optional<M> optionalMetadata = Optional.ofNullable(getMetadataSupplier().get());

        if (optionalMetadata.isPresent()) {
            return isMetadataDirty(optionalMetadata.get());
        } else {
            return false;
        }
    }

    private boolean isMetadataDirty(final M metadata) {
        final Integer metadataCurrentHash = metadata.hashCode();
        return !metadataCurrentHash.equals(metadataOriginalHash);
    }

    boolean isContentDirty() {
        return isDirty(getCurrentContentHash());
    }

    protected Integer getCurrentContentHash() {
        try {
            return getContentSupplier().get().hashCode();
        } catch (final Exception e) {
            return null;
        }
    }

    protected Supplier<M> getMetadataSupplier() {
        return () -> null;
    }

    /**
     * Needs to be overwritten for save to work
     */
    protected void save() {

    }

    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return null;
    }

    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return null;
    }

    protected Caller<? extends SupportsSaveAndRename<T, M>> getSaveAndRenameServiceCaller() {

        return null;
    }

    protected Command getSaveAndRename() {

        return saveAndRenameCommandBuilder
                .addPathSupplier(getPathSupplier())
                .addValidator(getRenameValidator())
                .addValidator(getSaveValidator())
                .addRenameService(getSaveAndRenameServiceCaller())
                .addMetadataSupplier(getMetadataSupplier())
                .addContentSupplier(getContentSupplier())
                .addIsDirtySupplier(isDirtySupplier())
                .addSuccessCallback(onSuccess())
                .addBeforeSaveAndRenameCommand(getBeforeSaveAndRenameCommand())
                .build();
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return null;
    }

    public boolean mayClose(Integer currentHash) {
        if (isDirty(currentHash)) {
            return baseView.confirmClose();
        } else {
            return true;
        }
    }

    public boolean isDirty(Integer currentHash) {
        if (originalHash == null) {
            return currentHash != null;
        } else {
            return !originalHash.equals(currentHash);
        }
    }

    public void setMetadataOriginalHash(final Integer metadataOriginalHash) {
        this.metadataOriginalHash = metadataOriginalHash;
    }
}
