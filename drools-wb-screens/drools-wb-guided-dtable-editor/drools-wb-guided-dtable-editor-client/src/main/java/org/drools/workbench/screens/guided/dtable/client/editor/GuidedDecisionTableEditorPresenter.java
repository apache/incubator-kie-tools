/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.page.ColumnsPage;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableEditorSearchIndex;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableSearchableElement;
import org.drools.workbench.screens.guided.dtable.client.editor.search.SearchableElementFactory;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResult;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.search.common.HasSearchableElements;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.wires.core.grids.client.util.GridHighlightHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.EDITOR_PROVIDED;

/**
 * Guided Decision Table Editor Presenter
 */
@Dependent
@WorkbenchEditor(identifier = "GuidedDecisionTableEditor", supportedTypes = {GuidedDTableResourceType.class}, lockingStrategy = EDITOR_PROVIDED)
public class GuidedDecisionTableEditorPresenter extends BaseGuidedDecisionTableEditorPresenter implements HasSearchableElements<GuidedDecisionTableSearchableElement> {

    private final SaveAndRenameCommandBuilder<GuidedDecisionTable52, Metadata> saveAndRenameCommandBuilder;

    private final GuidedDecisionTableEditorSearchIndex editorSearchIndex;

    private final SearchBarComponent<GuidedDecisionTableSearchableElement> searchBarComponent;
    private final SearchableElementFactory searchableElementFactory;
    private MenuItem convertMenuItem = null;

    @Inject
    public GuidedDecisionTableEditorPresenter(final View view,
                                              final Caller<GuidedDecisionTableEditorService> service,
                                              final AuthoringWorkbenchDocks docks,
                                              final PerspectiveManager perspectiveManager,
                                              final Event<NotificationEvent> notification,
                                              final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent,
                                              final ValidationPopup validationPopup,
                                              final GuidedDTableResourceType resourceType,
                                              final EditMenuBuilder editMenuBuilder,
                                              final ViewMenuBuilder viewMenuBuilder,
                                              final InsertMenuBuilder insertMenuBuilder,
                                              final RadarMenuBuilder radarMenuBuilder,
                                              final GuidedDecisionTableModellerView.Presenter modeller,
                                              final SyncBeanManager beanManager,
                                              final PlaceManager placeManager,
                                              final ColumnsPage columnsPage,
                                              final SaveAndRenameCommandBuilder<GuidedDecisionTable52, Metadata> saveAndRenameCommandBuilder,
                                              final AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder,
                                              final DownloadMenuItemBuilder downloadMenuItemBuilder,
                                              final GuidedDecisionTableEditorSearchIndex editorSearchIndex,
                                              final SearchBarComponent<GuidedDecisionTableSearchableElement> searchBarComponent,
                                              final SearchableElementFactory searchableElementFactory) {
        super(view,
              service,
              docks,
              perspectiveManager,
              notification,
              decisionTableSelectedEvent,
              validationPopup,
              resourceType,
              editMenuBuilder,
              viewMenuBuilder,
              insertMenuBuilder,
              radarMenuBuilder,
              modeller,
              beanManager,
              placeManager,
              columnsPage,
              alertsButtonMenuItemBuilder,
              downloadMenuItemBuilder);

        this.saveAndRenameCommandBuilder = saveAndRenameCommandBuilder;
        this.editorSearchIndex = editorSearchIndex;
        this.searchBarComponent = searchBarComponent;
        this.searchableElementFactory = searchableElementFactory;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        editorSearchIndex.setCurrentAssetHashcodeSupplier(getCurrentHashCodeSupplier());
        editorSearchIndex.setNoResultsFoundCallback(getNoResultsFoundCallback());
        editorSearchIndex.registerSubIndex(this);

        setupSearchComponent();
    }

    protected Supplier<Integer> getCurrentHashCodeSupplier() {
        return () -> currentHashCode(getActiveDocument());
    }

    private void setupSearchComponent() {
        final HTMLElement element = searchBarComponent.getView().getElement();

        searchBarComponent.init(editorSearchIndex);
        getKieEditorWrapperMultiPage().addTabBarWidget(getWidget(element));
    }

    Command getNoResultsFoundCallback() {
        return () -> highlightHelper().clearSelections();
    }

    private GridHighlightHelper highlightHelper() {

        final GuidedDecisionTableModellerView view = modeller.getView();
        final GridWidget gridWidget = view.getGridWidgets().iterator().next();

        return new GridHighlightHelper(view.getGridPanel(), gridWidget);
    }

    @Override
    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest placeRequest) {
        super.onStartup(path,
                        placeRequest);

        loadDocument(path,
                     placeRequest);
    }

    @Override
    @OnFocus
    public void onFocus() {
        super.onFocus();
    }

    @Override
    public void loadDocument(final ObservablePath path,
                             final PlaceRequest placeRequest) {
        view.showLoading();
        service.call(getLoadContentSuccessCallback(path,
                                                   placeRequest),
                     getNoSuchFileExceptionErrorCallback()).loadContent(path);
    }

    @Override
    public List<GuidedDecisionTableSearchableElement> getSearchableElements() {

        final List<GuidedDecisionTableSearchableElement> searchableElements = new ArrayList<>();
        final GuidedDecisionTable52 model = getContentSupplier().get();
        final List<List<DTCellValue52>> data = model.getData();

        for (int row = 0, dataSize = data.size(); row < dataSize; row++) {
            for (int column = 0; column < data.get(row).size(); column++) {
                searchableElements.add(makeSearchableElement(data, row, column));
            }
        }

        return searchableElements;
    }

    private GuidedDecisionTableSearchableElement makeSearchableElement(final List<List<DTCellValue52>> data,
                                                                       final int row,
                                                                       final int column) {
        final DTCellValue52 cellValue52 = data.get(row).get(column);
        return searchableElementFactory.makeSearchableElement(row, column, cellValue52, modeller);
    }

    protected RemoteCallback<GuidedDecisionTableEditorContent> getLoadContentSuccessCallback(final ObservablePath path,
                                                                                             final PlaceRequest placeRequest) {
        return (content) -> {
            //Path is set to null when the Editor is closed (which can happen before async calls complete).
            if (path == null) {
                return;
            }

            //Add Decision Table to modeller
            final GuidedDecisionTableView.Presenter dtPresenter = modeller.addDecisionTable(path,
                                                                                            placeRequest,
                                                                                            content,
                                                                                            placeRequest.getParameter("readOnly",
                                                                                                                      null) != null,
                                                                                            null,
                                                                                            null);
            registerDocument(dtPresenter);

            decisionTableSelectedEvent.fire(new DecisionTableSelectedEvent(dtPresenter));

            modeller.getView().getGridPanel().setFocus(true);

            view.hideBusyIndicator();
        };
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @Override
    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    @OnMayClose
    public boolean mayClose() {
        return super.mayClose();
    }

    @Override
    @OnClose
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void onDecisionTableSelected(final @Observes DecisionTableSelectedEvent event) {
        super.onDecisionTableSelected(event);
    }

    @Override
    public Promise<Void> makeMenuBar() {
        if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
            final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
            return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (canUpdateProject) {
                    getFileMenuBuilder()
                            .addSave(getSaveMenuItem())
                            .addCopy(() -> getActiveDocument().getCurrentPath(),
                                     assetUpdateValidator)
                            .addRename(getSaveAndRenameCommand())
                            .addDelete(() -> getActiveDocument().getLatestPath(),
                                       assetUpdateValidator);
                }

                final FileMenuBuilder fileMenuBuilder = getFileMenuBuilder()
                        .addValidate(() -> onValidate(getActiveDocument()))
                        .addNewTopLevelMenu(getEditMenuItem())
                        .addNewTopLevelMenu(getViewMenuItem())
                        .addNewTopLevelMenu(getInsertMenuItem())
                        .addNewTopLevelMenu(getRadarMenuItem())
                        .addNewTopLevelMenu(getVersionManagerMenuItem())
                        .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
                addDownloadMenuItem(fileMenuBuilder);

                fileMenuBuilder.addNewTopLevelMenu(getConvertMenuItem());

                this.menus = fileMenuBuilder.build();

                return promises.resolve();
            });
        }

        return promises.resolve();
    }

    private MenuItem getConvertMenuItem() {
        if (convertMenuItem == null) {

            convertMenuItem = MenuFactory.newTopLevelMenu(GuidedDecisionTableConstants.INSTANCE.Convert())
                    .respondsWith(() -> onConvert())
                    .endMenu()
                    .build()
                    .getItems()
                    .get(0);
        }
        return convertMenuItem;
    }

    public void onConvert() {
        service.call((RemoteCallback<XLSConversionResult>) result -> {
            if (result.isConverted()) {
                view.showConversionSuccess();
            } else {
                view.showConversionMessage(result.getMessage());
            }
        }).convert(versionRecordManager.getCurrentPath());
    }

    protected Command getSaveAndRenameCommand() {

        return saveAndRenameCommandBuilder
                .addPathSupplier(getPathSupplier())
                .addValidator(assetUpdateValidator)
                .addRenameService(service)
                .addMetadataSupplier(getMetadataSupplier())
                .addContentSupplier(getContentSupplier())
                .addIsDirtySupplier(getIsDirtySupplier())
                .build();
    }

    private Supplier<Path> getPathSupplier() {
        return () -> versionRecordManager.getPathToLatest();
    }

    Supplier<GuidedDecisionTable52> getContentSupplier() {
        return () -> getActiveDocument().getModel();
    }

    Supplier<Metadata> getMetadataSupplier() {

        return () -> {

            final Overview overview = getActiveDocument().getOverview();

            return overview.getMetadata();
        };
    }

    Supplier<Boolean> getIsDirtySupplier() {
        return () -> {

            final Integer originalHashCode = originalHashCode(getActiveDocument());
            final Integer currentHashCode = currentHashCode(getActiveDocument());

            return isDirty(originalHashCode, currentHashCode);
        };
    }

    int originalHashCode(final GuidedDecisionTableView.Presenter dtPresenter) {
        return dtPresenter.getOriginalHashCode();
    }

    int currentHashCode(final GuidedDecisionTableView.Presenter dtPresenter) {
        return dtPresenter.getModel().hashCode();
    }

    Set<GuidedDecisionTableView.Presenter> getAvailableDecisionTables() {
        return modeller.getAvailableDecisionTables();
    }

    FileMenuBuilder getFileMenuBuilder() {
        return fileMenuBuilder;
    }

    @Override
    public void onOpenDocumentsInEditor(final List<Path> selectedDocumentPaths) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAvailableDocumentPaths(final Callback<List<Path>> callback) {
        callback.callback(Collections.emptyList());
    }

    @Override
    public void removeDocument(GuidedDecisionTableView.Presenter dtPresenter) {
        super.removeDocument(dtPresenter);
        scheduleClosure(() -> placeManager.forceClosePlace(editorPlaceRequest));
    }

    void onUpdatedLockStatusEvent(final @Observes UpdatedLockStatusEvent event) {
        super.onUpdatedLockStatusEvent(event);
    }

    void scheduleClosure(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }
}
