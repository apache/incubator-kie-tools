/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.showcase.client.navigator.DMNVFSService;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants.INSTANCE;

@Dependent
@DiagramEditor
@WorkbenchScreen(identifier = AbstractDMNDiagramEditor.EDITOR_ID)
public class DMNDiagramEditor extends AbstractDMNDiagramEditor {

    public static final String CONTENT_PARAMETER_NAME = "content";
    public static final String FILE_NAME_PARAMETER_NAME = "fileName";

    private final Event<NotificationEvent> notificationEvent;
    private final DMNVFSService vfsService;
    private final ReadOnlyProvider readOnlyProvider;
    private final LazyCanvasFocusUtils lazyCanvasFocusUtils;

    @Inject
    public DMNDiagramEditor(final View view,
                            final PlaceManager placeManager,
                            final MultiPageEditorContainerView containerView,
                            final StunnerEditor stunnerEditor,
                            final DMNEditorSearchIndex editorSearchIndex,
                            final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                            final SessionManager sessionManager,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final @DMNEditor DocumentationView documentationView,
                            final ClientTranslationService translationService,
                            final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                            final DecisionNavigatorDock decisionNavigatorDock,
                            final DiagramEditorPropertiesDock diagramPropertiesDock,
                            final PreviewDiagramDock diagramPreviewAndExplorerDock,
                            final LayoutHelper layoutHelper,
                            final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                            final DataTypesPage dataTypesPage,
                            final KogitoClientDiagramService diagramServices,
                            final MonacoFEELInitializer feelInitializer,
                            final CanvasFileExport canvasFileExport,
                            final Promises promises,
                            final IncludedModelsPage includedModelsPage,
                            final IncludedModelsContext includedModelContext,
                            final GuidedTourBridgeInitializer guidedTourBridgeInitializer,
                            final DRDNameChanger drdNameChanger,
                            final Event<NotificationEvent> notificationEvent,
                            final DMNVFSService vfsService,
                            final ReadOnlyProvider readOnlyProvider,
                            final LazyCanvasFocusUtils lazyCanvasFocusUtils) {
        super(view,
              placeManager,
              containerView,
              stunnerEditor,
              editorSearchIndex,
              searchBarComponent,
              sessionManager,
              sessionCommandManager,
              documentationView,
              translationService,
              refreshFormPropertiesEvent,
              decisionNavigatorDock,
              diagramPropertiesDock,
              diagramPreviewAndExplorerDock,
              layoutHelper,
              openDiagramLayoutExecutor,
              dataTypesPage,
              diagramServices,
              feelInitializer,
              canvasFileExport,
              promises,
              includedModelsPage,
              includedModelContext,
              guidedTourBridgeInitializer,
              drdNameChanger);
        this.notificationEvent = notificationEvent;
        this.vfsService = vfsService;
        this.readOnlyProvider = readOnlyProvider;
        this.lazyCanvasFocusUtils = lazyCanvasFocusUtils;
    }

    @Override
    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);
        final String path = getPlaceRequest().getParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, "");
        final String value = place.getParameter(CONTENT_PARAMETER_NAME, "");
        setContent(path, value);
    }

    @Override
    @WorkbenchMenu
    //AppFormer does not generate menus when the @WorkbenchMenu annotation is on the super class
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    public void initialiseKieEditorForSession(final Diagram diagram) {
        final String title = getPlaceRequest().getParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, "");
        diagram.getMetadata().setTitle(title);

        super.initialiseKieEditorForSession(diagram);
    }

    @Override
    public void onDiagramLoad() {
        final Metadata metadata = stunnerEditor.getCanvasHandler().getDiagram().getMetadata();
        metadata.setPath(makeMetadataPath(metadata));
        decisionNavigatorDock.reload();
        dataTypesPage.reload();
        includedModelsPage.reload();
        lazyCanvasFocusUtils.releaseFocus();
    }

    private Path makeMetadataPath(final Metadata metadata) {
        final Path root = metadata.getRoot();
        final String fileName = metadata.getTitle();
        final String uri = root.toURI();
        return PathFactory.newPath(fileName, uri + "/" + URIUtil.encode(fileName));
    }

    @SuppressWarnings("unchecked")
    void doSave() {
        final Path path = stunnerEditor.getDiagram().getMetadata().getPath();

        getContent().then(xml -> {
            vfsService.saveFile(path,
                                (String) xml,
                                new ServiceCallback<String>() {
                                    @Override
                                    public void onSuccess(final String xml) {
                                        resetContentHash();
                                        notificationEvent.fire(new NotificationEvent(INSTANCE.ItemSavedSuccessfully()));
                                        getBaseEditorView().hideBusyIndicator();
                                    }

                                    @Override
                                    public void onError(final ClientRuntimeError error) {
                                        stunnerEditor.handleError(error);
                                    }
                                });
            return promises.resolve();
        });
    }

    @Override
    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        super.onDataTypePageNavTabActiveEvent(event);
    }

    @Override
    public void onDataTypeEditModeToggle(final @Observes DataTypeEditModeToggleEvent event) {
        super.onDataTypeEditModeToggle(event);
    }

    @Override
    public void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        super.onEditExpressionEvent(event);
    }

    @Override
    public void onMultiPageEditorSelectedPageEvent(final @Observes MultiPageEditorSelectedPageEvent event) {
        super.onMultiPageEditorSelectedPageEvent(event);
    }

    @Override
    public void onRefreshFormPropertiesEvent(final @Observes RefreshFormPropertiesEvent event) {
        super.onRefreshFormPropertiesEvent(event);
    }

    @Override
    public boolean isReadOnly() {
        return readOnlyProvider.isReadOnlyDiagram();
    }
}
