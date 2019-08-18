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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.page.ColumnsPage;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableEditorSearchIndex;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableGridHighlightHelper;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableSearchableElement;
import org.drools.workbench.screens.guided.dtable.client.editor.search.SearchableElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.fail;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseGuidedDecisionTablePresenterTest<P extends BaseGuidedDecisionTableEditorPresenter> {

    @Mock
    protected BaseGuidedDecisionTableEditorPresenter.View view;

    @Mock
    protected GuidedDecisionTableEditorService dtService;

    @Mock
    protected AuthoringWorkbenchDocks docks;

    @Mock
    protected PerspectiveManager perspectiveManager;

    protected Caller<GuidedDecisionTableEditorService> dtServiceCaller;

    protected Event<NotificationEvent> notification = spy(new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing
        }
    });

    protected Event<DecisionTableSelectedEvent> decisionTableSelectedEvent = spy(new EventSourceMock<DecisionTableSelectedEvent>() {
        @Override
        public void fire(final DecisionTableSelectedEvent event) {
            //Do nothing
        }
    });

    @Mock
    protected ValidationPopup validationPopup;

    @Mock
    protected EditMenuBuilder editMenuBuilder;

    @Mock
    protected MenuItem editMenuItem;

    @Mock
    protected ViewMenuBuilder viewMenuBuilder;

    @Mock
    protected MenuItem viewMenuItem;

    @Mock
    protected InsertMenuBuilder insertMenuBuilder;

    @Mock
    protected MenuItem insertMenuItem;

    @Mock
    protected RadarMenuBuilder radarMenuBuilder;

    @Mock
    protected MenuItem radarMenuItem;

    @Mock
    protected RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder;

    @Mock
    protected MenuItem registeredDocumentsMenuItem;

    @Mock
    protected MenuItem saveMenuItem;

    @Mock
    protected MenuItem versionManagerMenuItem;

    @Mock
    protected GuidedDecisionTableModellerView.Presenter modeller;

    @Mock
    protected GuidedDecisionTableModellerView modellerView;

    @Mock
    protected GridLienzoPanel modellerGridPanel;

    @Mock
    protected KieMultipleDocumentEditorWrapperView kieEditorWrapperView;

    @Mock
    protected OverviewWidgetPresenter overviewWidget;

    @Mock
    protected SavePopUpPresenter savePopUpPresenter;

    @Mock
    protected ImportsWidgetPresenter importsWidget;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleEvent;

    @Mock
    protected WorkspaceProjectContext workbenchContext;

    @Mock
    protected VersionRecordManager versionRecordManager;

    @Mock
    protected VersionService versionService;
    protected CallerMock<VersionService> versionServiceCaller;

    @Mock
    protected EventSourceMock<RestoreEvent> restoreEvent;

    @Mock
    protected DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    protected CopyPopUpPresenter copyPopUpPresenter;

    @Mock
    protected RenamePopUpPresenter renamePopUpPresenter;

    @Mock
    protected BusyIndicatorView busyIndicatorView;

    @Mock
    protected DownloadMenuItemBuilder downloadMenuItemBuilder;

    @Mock
    protected MenuItem downloadMenuItemButton;

    @Spy
    protected RestoreVersionCommandProvider restoreVersionCommandProvider = getRestoreVersionCommandProvider();

    @Spy
    protected BasicFileMenuBuilder basicFileMenuBuilder = getBasicFileMenuBuilder();

    @Spy
    protected FileMenuBuilder fileMenuBuilder = getFileMenuBuilder();

    @Mock
    protected DefaultFileNameValidator fileNameValidator;

    @Mock
    protected AssetUpdateValidator assetUpdateValidator;

    @Mock
    protected SyncBeanManager beanManager;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected ColumnsPage columnsPage;

    @Mock
    protected MultiPageEditor multiPageEditor;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected PerspectiveActivity currentPerspective;

    @Captor
    protected ArgumentCaptor<DecisionTableSelectedEvent> dtSelectedEventCaptor;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected GuidedDecisionTableEditorSearchIndex editorSearchIndex;

    @Mock
    protected SearchBarComponent<GuidedDecisionTableSearchableElement> searchBarComponent;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @Mock
    protected Widget modellerViewWidget;

    @Mock
    protected Element modellerViewWidgetElement;

    @Mock
    protected SearchBarComponent.View searchBarView;

    @Mock
    protected HTMLElement searchBarViewHTMLElement;

    @Mock
    protected ElementWrapperWidget searchBarComponentWidget;

    @Mock
    private GuidedDecisionTableGridHighlightHelper highlightHelper;

    protected Promises promises;

    protected SearchableElementFactory searchableElementFactory;

    protected P presenter;

    @Before
    public void setup() {

        ApplicationPreferences.setUp(new Maps.Builder<String, String>().put(DATE_FORMAT, "dd/mm/yy").build());

        this.promises = new SyncPromises();
        this.dtServiceCaller = new CallerMock<>(dtService);
        this.versionServiceCaller = new CallerMock<>(versionService);
        searchableElementFactory = new SearchableElementFactory(highlightHelper);

        final P wrapped = getPresenter();

        wrapped.setKieEditorWrapperView(kieEditorWrapperView);
        wrapped.setOverviewWidget(overviewWidget);
        wrapped.setSavePopUpPresenter(savePopUpPresenter);
        wrapped.setImportsWidget(importsWidget);
        wrapped.setNotificationEvent(notificationEvent);
        wrapped.setChangeTitleEvent(changeTitleEvent);
        wrapped.setWorkbenchContext(workbenchContext);
        wrapped.setVersionRecordManager(versionRecordManager);
        wrapped.setRegisteredDocumentsMenuBuilder(registeredDocumentsMenuBuilder);
        wrapped.setFileMenuBuilder(fileMenuBuilder);
        wrapped.setFileNameValidator(fileNameValidator);
        wrapped.setAssetUpdateValidator(assetUpdateValidator);

        this.presenter = spy(wrapped);

        when(modeller.getView()).thenReturn(modellerView);
        when(modellerView.getGridPanel()).thenReturn(modellerGridPanel);
        when(versionRecordManager.newSaveMenuItem(any(Command.class))).thenReturn(saveMenuItem);
        when(versionRecordManager.buildMenu()).thenReturn(versionManagerMenuItem);
        when(editMenuBuilder.build()).thenReturn(editMenuItem);
        when(viewMenuBuilder.build()).thenReturn(viewMenuItem);
        when(insertMenuBuilder.build()).thenReturn(insertMenuItem);
        when(radarMenuBuilder.build()).thenReturn(radarMenuItem);
        when(registeredDocumentsMenuBuilder.build()).thenReturn(registeredDocumentsMenuItem);
        when(presenter.getKieEditorWrapperMultiPage()).thenReturn(multiPageEditor);

        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.of(mock(WorkspaceProject.class)));
        when(downloadMenuItemBuilder.build(any())).thenReturn(downloadMenuItemButton);

        when(modellerView.asWidget()).thenReturn(modellerViewWidget);
        when(modellerViewWidget.getElement()).thenReturn(modellerViewWidgetElement);
        when(searchBarComponent.getView()).thenReturn(searchBarView);
        when(searchBarView.getElement()).thenReturn(searchBarViewHTMLElement);

        doReturn(searchBarComponentWidget).when(presenter).getWidget(searchBarViewHTMLElement);
        doReturn(alertsButtonMenuItem).when(alertsButtonMenuItemBuilder).build();
        doReturn(currentPerspective).when(perspectiveManager).getCurrentPerspective();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        presenter.init();
        presenter.setupMenuBar();
    }

    protected abstract P getPresenter();

    protected GuidedDecisionTableView.Presenter makeDecisionTable(final Path originalPath,
                                                                  final ObservablePath path,
                                                                  final PlaceRequest placeRequest,
                                                                  final GuidedDecisionTableEditorContent content) {
        final GuidedDecisionTableView.Presenter dtPresenter = mock(GuidedDecisionTableView.Presenter.class);
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final Overview overview = mock(Overview.class);
        final GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

        when(dtService.loadContent(eq(path))).thenReturn(content);
        when(modeller.addDecisionTable(eq(path),
                                       eq(placeRequest),
                                       eq(content),
                                       any(Boolean.class),
                                       any(Double.class),
                                       any(Double.class))).thenReturn(dtPresenter);
        when(path.getOriginal()).thenReturn(originalPath);
        when(dtPresenter.getLatestPath()).thenReturn(path);
        when(dtPresenter.getCurrentPath()).thenReturn(path);
        when(dtPresenter.getPlaceRequest()).thenReturn(placeRequest);
        when(dtPresenter.getModel()).thenReturn(content.getModel());
        when(dtPresenter.getDataModelOracle()).thenReturn(oracle);
        when(dtPresenter.getOverview()).thenReturn(overview);
        when(dtPresenter.getAccess()).thenReturn(access);
        when(dtPresenter.getView()).thenReturn(mock(GuidedDecisionTableView.class));
        when(overview.getMetadata()).thenReturn(mock(Metadata.class));

        return dtPresenter;
    }

    protected GuidedDecisionTableEditorContent makeDecisionTableContent() {
        return makeDecisionTableContent(0);
    }

    protected GuidedDecisionTableEditorContent makeDecisionTableContent(final int hashCode) {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52() {
            @Override
            public int hashCode() {
                return hashCode;
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }
        };
        final Overview overview = mock(Overview.class);
        final Metadata metadata = mock(Metadata.class);
        when(overview.getMetadata()).thenReturn(metadata);
        return new GuidedDecisionTableEditorContent(model,
                                                    Collections.<PortableWorkDefinition>emptySet(),
                                                    overview,
                                                    mock(PackageDataModelOracleBaselinePayload.class));
    }

    private RestoreVersionCommandProvider getRestoreVersionCommandProvider() {
        final RestoreVersionCommandProvider restoreVersionCommandProvider = new RestoreVersionCommandProvider();
        setField(restoreVersionCommandProvider,
                 "versionService",
                 versionServiceCaller);
        setField(restoreVersionCommandProvider,
                 "restoreEvent",
                 restoreEvent);
        setField(restoreVersionCommandProvider,
                 "busyIndicatorView",
                 view);
        return restoreVersionCommandProvider;
    }

    private BasicFileMenuBuilder getBasicFileMenuBuilder() {
        final BasicFileMenuBuilder basicFileMenuBuilder = new BasicFileMenuBuilderImpl(deletePopUpPresenter,
                                                                                       copyPopUpPresenter,
                                                                                       renamePopUpPresenter,
                                                                                       busyIndicatorView,
                                                                                       notification,
                                                                                       restoreVersionCommandProvider);
        setField(basicFileMenuBuilder,
                 "restoreVersionCommandProvider",
                 restoreVersionCommandProvider);
        setField(basicFileMenuBuilder,
                 "notification",
                 notificationEvent);
        setField(restoreVersionCommandProvider,
                 "busyIndicatorView",
                 view);
        return basicFileMenuBuilder;
    }

    private FileMenuBuilder getFileMenuBuilder() {
        final FileMenuBuilder fileMenuBuilder = new FileMenuBuilderImpl();
        setField(fileMenuBuilder,
                 "menuBuilder",
                 basicFileMenuBuilder);
        return fileMenuBuilder;
    }

    private void setField(final Object o,
                          final String fieldName,
                          final Object value) {
        try {
            final Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o,
                      value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
}
