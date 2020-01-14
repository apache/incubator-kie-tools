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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuView;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.page.ColumnsPage;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableEditorSearchIndex;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableSearchableElement;
import org.drools.workbench.screens.guided.dtable.client.editor.search.SearchableElementFactory;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuView;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuView.DocumentMenuItem;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDivider;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDividerView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeader;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIcon;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableEditorMenusTest {

    @Mock
    protected BaseGuidedDecisionTableEditorPresenter.View view;

    @Mock
    protected GuidedDecisionTableEditorService dtService;
    protected Caller<GuidedDecisionTableEditorService> dtServiceCaller;

    @Mock
    protected EventSourceMock<NotificationEvent> notification;

    @Mock
    protected EventSourceMock<DecisionTableSelectedEvent> decisionTableSelectedEvent;

    @Mock
    protected ValidationPopup validationPopup;

    @Mock
    protected Clipboard clipboard;

    @Mock
    protected TranslationService ts;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewProducer;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewHeaderProducer;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewWithIconProducer;

    @Mock
    private ManagedInstance<MenuItemView> menuItemViewDividerProducer;

    @Mock
    private ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance;

    private MenuItemFactory menuItemFactory;

    protected EditMenuBuilder editMenuBuilder;

    protected InsertMenuBuilder insertMenuBuilder;

    protected ViewMenuBuilder viewMenuBuilder;

    @Mock
    protected RadarMenuView radarMenuView;
    protected RadarMenuBuilder radarMenuBuilder;

    @Mock
    protected RegisteredDocumentsMenuView registeredDocumentsMenuView;

    @Mock
    protected ManagedInstance<DocumentMenuItem> documentMenuItems;
    protected RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder = new RegisteredDocumentsMenuBuilder(registeredDocumentsMenuView,
                                                                                                                 documentMenuItems);

    @Mock
    protected MenuItem saveMenuItem;

    @Mock
    protected MenuItem versionManagerMenuItem;

    @Mock
    protected GuidedDecisionTableModellerView.Presenter modeller;

    @Mock
    protected GuidedDecisionTableModellerView modellerView;

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

    @Spy
    protected RestoreVersionCommandProvider restoreVersionCommandProvider = getRestoreVersionCommandProvider();

    @Spy
    protected BasicFileMenuBuilder basicFileMenuBuilder = getBasicFileMenuBuilder();

    @Spy
    protected FileMenuBuilder fileMenuBuilder = getFileMenuBuilder();

    @Mock
    protected DefaultFileNameValidator fileNameValidator;

    @Mock
    protected SyncBeanManager beanManager;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected ColumnsPage columnsPage;

    @Mock
    protected SaveAndRenameCommandBuilder<GuidedDecisionTable52, Metadata> saveAndRenameCommandBuilder;

    @Mock
    protected MenuItemWithIconView menuItemWithIconView;

    @Mock
    private DecisionTablePopoverUtils popoverUtils;

    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected DownloadMenuItemBuilder downloadMenuItem;

    @Mock
    protected MenuItem downloadMenuItemButton;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @Mock
    protected AuthoringWorkbenchDocks docks;

    @Mock
    protected PerspectiveManager perspectiveManager;

    @Mock
    protected SearchableElementFactory searchableElementFactory;

    @Mock
    protected GuidedDecisionTableEditorSearchIndex editorSearchIndex;

    @Mock
    protected SearchBarComponent<GuidedDecisionTableSearchableElement> searchBarComponent;

    @Mock
    protected Widget modellerViewWidget;

    @Mock
    protected Element modellerViewWidgetElement;

    @Mock
    protected elemental2.dom.HTMLElement searchBarViewHTMLElement;

    @Mock
    protected MultiPageEditor multiPageEditor;

    @Mock
    protected ElementWrapperWidget searchBarComponentWidget;

    @Mock
    protected SearchBarComponent.View searchBarView;

    protected Promises promises;

    private GuidedDecisionTableEditorPresenter presenter;
    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType(new Decision());

    private String[] menuItemIdentifiers = new String[]{
            "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$1#Delete",
            "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$1#Rename",
            "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$1#Copy",
            "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$1#Validate",
            "org.uberfire.workbench.model.menu.impl.DefaultMenuGroup#EditMenu.title",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.cut",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.copy",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.paste",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.deleteCells",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.deleteColumns",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.deleteRows",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#EditMenu.otherwise",
            "org.uberfire.workbench.model.menu.impl.DefaultMenuGroup#ViewMenu.title",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$4$1#ViewMenu.zoom",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#125%",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#100%",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#75%",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#50%",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$6$1#null",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#ViewMenu.merged",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#ViewMenu.auditLog",
            "org.uberfire.workbench.model.menu.impl.DefaultMenuGroup#InsertMenu.title",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#InsertMenu.appendRow",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#InsertMenu.insertRowAbove",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#InsertMenu.insertRowBelow",
            "org.uberfire.ext.widgets.common.client.menu.MenuItemFactory$2$1#InsertMenu.insertColumn",
            "org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder$1#null"};

    @Before
    public void setup() {
        promises = new SyncPromises();
        when(downloadMenuItem.build(any())).thenReturn(downloadMenuItemButton);
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
        when(modeller.getView()).thenReturn(modellerView);
        when(versionRecordManager.newSaveMenuItem(any(Command.class))).thenReturn(saveMenuItem);
        when(versionRecordManager.buildMenu()).thenReturn(versionManagerMenuItem);
        when(ts.getTranslation(any(String.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(menuItemViewProducer.select(any(Annotation.class))).thenAnswer((o) -> {
            final Annotation a = (Annotation) o.getArguments()[0];
            if (a.annotationType().equals(MenuItemHeader.class)) {
                return menuItemViewHeaderProducer;
            } else if (a.annotationType().equals(MenuItemWithIcon.class)) {
                return menuItemViewWithIconProducer;
            } else if (a.annotationType().equals(MenuItemDivider.class)) {
                return menuItemViewDividerProducer;
            }
            throw new IllegalArgumentException("Unexpected MenuItemView");
        });
        when(menuItemViewHeaderProducer.get()).thenReturn(mock(MenuItemHeaderView.class));
        when(menuItemViewDividerProducer.get()).thenReturn(mock(MenuItemDividerView.class));
        when(menuItemViewWithIconProducer.get()).thenReturn(menuItemWithIconView);
        when(menuItemWithIconView.getElement()).thenReturn(mock(HTMLElement.class));
        when(modellerView.asWidget()).thenReturn(modellerViewWidget);
        when(modellerViewWidget.getElement()).thenReturn(modellerViewWidgetElement);
        when(searchBarComponent.getView()).thenReturn(searchBarView);
        when(searchBarView.getElement()).thenReturn(searchBarViewHTMLElement);

        this.dtServiceCaller = new CallerMock<>(dtService);
        this.versionServiceCaller = new CallerMock<>(versionService);
        this.menuItemFactory = new MenuItemFactory(menuItemViewProducer);

        this.editMenuBuilder = new EditMenuBuilder(clipboard,
                                                   ts,
                                                   menuItemFactory,
                                                   popoverUtils);
        this.editMenuBuilder.setup();
        this.insertMenuBuilder = new InsertMenuBuilder(ts, menuItemFactory, wizardManagedInstance);
        this.insertMenuBuilder.setup();
        this.insertMenuBuilder.setModeller(modeller);
        this.viewMenuBuilder = new ViewMenuBuilder(ts,
                                                   menuItemFactory);
        this.viewMenuBuilder.setup();
        this.viewMenuBuilder.setModeller(modeller);

        this.radarMenuBuilder = new RadarMenuBuilder(radarMenuView);
        this.radarMenuBuilder.setup();

        final GuidedDecisionTableEditorPresenter wrapped = new GuidedDecisionTableEditorPresenter(view,
                                                                                                  dtServiceCaller,
                                                                                                  docks,
                                                                                                  perspectiveManager,
                                                                                                  notification,
                                                                                                  decisionTableSelectedEvent,
                                                                                                  mock(GuidedDecisionTableDocksHandler.class),
                                                                                                  mock(AnalysisReportScreen.class),
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
                                                                                                  saveAndRenameCommandBuilder,
                                                                                                  alertsButtonMenuItemBuilder,
                                                                                                  downloadMenuItem,
                                                                                                  editorSearchIndex,
                                                                                                  searchBarComponent,
                                                                                                  searchableElementFactory,
                                                                                                  null) {
            {
                promises = GuidedDecisionTableEditorMenusTest.this.promises;
            }

            @Override
            protected Command getSaveAndRenameCommand() {
                return mock(Command.class);
            }
        };

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

        this.presenter = spy(wrapped);

        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());

        doReturn(searchBarComponentWidget).when(presenter).getWidget(searchBarViewHTMLElement);
        doReturn(multiPageEditor).when(presenter).getKieEditorWrapperMultiPage();

        presenter.init();
        presenter.setupMenuBar();
    }

    @Test
    public void checkMenuStructure() {
        presenter.getMenus(menus -> {
            final AtomicInteger i = new AtomicInteger(0);
            final MenuVisitor visitor = new MenuVisitor() {
                @Override
                public boolean visitEnter(final Menus menus) {
                    return true;
                }

                @Override
                public void visitLeave(final Menus menus) {
                }

                @Override
                public boolean visitEnter(final MenuGroup menuGroup) {
                    assertEquals(menuItemIdentifiers[i.getAndIncrement()],
                                 menuGroup.getIdentifier());
                    return true;
                }

                @Override
                public void visitLeave(final MenuGroup menuGroup) {

                }

                @Override
                public void visit(final MenuItemPlain menuItemPlain) {
                    assertEquals(menuItemIdentifiers[i.getAndIncrement()],
                                 menuItemPlain.getIdentifier());
                }

                @Override
                public void visit(final MenuItemCommand menuItemCommand) {
                    assertEquals(menuItemIdentifiers[i.getAndIncrement()],
                                 menuItemCommand.getIdentifier());
                }

                @Override
                public void visit(final MenuItemPerspective menuItemPerspective) {
                    assertEquals(menuItemIdentifiers[i.getAndIncrement()],
                                 menuItemPerspective.getIdentifier());
                }

                @Override
                public void visit(final MenuCustom<?> menuCustom) {
                    assertEquals(menuItemIdentifiers[i.getAndIncrement()],
                                 menuCustom.getIdentifier());
                }
            };
            menus.accept(visitor);
        });
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
