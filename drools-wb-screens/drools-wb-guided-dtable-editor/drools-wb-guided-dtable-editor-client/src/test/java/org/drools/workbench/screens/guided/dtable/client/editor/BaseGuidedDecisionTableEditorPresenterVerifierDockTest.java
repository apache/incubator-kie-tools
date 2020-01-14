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

package org.drools.workbench.screens.guided.dtable.client.editor;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBase;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBaseView;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BaseGuidedDecisionTableEditorPresenterVerifierDockTest extends BaseGuidedDecisionTablePresenterTest<BaseGuidedDecisionTableEditorPresenter> {

    @Captor
    ArgumentCaptor<Command> argumentCaptor;

    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType(new Decision());

    @Mock
    private SaveAndRenameCommandBuilder<GuidedDecisionTable52, Metadata> saveAndRenameCommandBuilder;

    @Mock
    private ObservablePath path;

    @Mock
    private PlaceRequest placeRequest;

    @Mock
    private AnalysisReportScreen analysisReportScreen;

    @Mock
    private Widget analysisReportScreenWidget;

    @Mock
    private DockPlaceHolderBase placeHolderBase;

    @Before
    public void setUp() throws Exception {
        super.setup();

        doReturn(analysisReportScreenWidget).when(analysisReportScreen).asWidget();
    }

    @Override
    protected GuidedDecisionTableEditorPresenter getPresenter() {
        return new GuidedDecisionTableEditorPresenter(view,
                                                      dtServiceCaller,
                                                      docks,
                                                      perspectiveManager,
                                                      notification,
                                                      decisionTableSelectedEvent,
                                                      mock(GuidedDecisionTableDocksHandler.class),
                                                      analysisReportScreen,
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
                                                      downloadMenuItemBuilder,
                                                      editorSearchIndex,
                                                      searchBarComponent,
                                                      searchableElementFactory,
                                                      null) {
            {
                promises = BaseGuidedDecisionTableEditorPresenterVerifierDockTest.this.promises;
                projectController = BaseGuidedDecisionTableEditorPresenterVerifierDockTest.this.projectController;
            }

            @Override
            protected Command getSaveAndRenameCommand() {
                return mock(Command.class);
            }
        };
    }

    @Test
    public void setupTheVerifierDock() {
        doReturn(GuidedDecisionTableEditorPresenter.IDENTIFIER).when(placeRequest).getIdentifier();
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        makeDecisionTable(path, path, placeRequest, content);

        setUpPlaceManager();

        presenter.onStartup(path,
                            placeRequest);

        verify(placeManager).registerOnOpenCallback(any(),
                                                    argumentCaptor.capture());

        presenter.onShowDiagramEditorDocks(new PlaceGainFocusEvent(placeRequest));

        // Since docs for this editor are closed, the activity should not be fetched
        argumentCaptor.getValue().execute();
        verify(placeHolderBase).setView(analysisReportScreenWidget);
    }

    @Test
    public void doNotShowTheDockForThisIfFocusIsNotThere() {
        doReturn(GuidedDecisionTableEditorPresenter.IDENTIFIER).when(placeRequest).getIdentifier();
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        makeDecisionTable(path, path, placeRequest, content);

        setUpPlaceManager();

        presenter.onStartup(path,
                            placeRequest);

        verify(placeManager).registerOnOpenCallback(any(),
                                                    argumentCaptor.capture());

        presenter.onShowDiagramEditorDocks(new PlaceGainFocusEvent(placeRequest));

        argumentCaptor.getValue().execute();

        presenter.onHideDocks(new PlaceHiddenEvent(placeRequest));

        reset(placeHolderBase);

        // Since docs for this editor are closed, the activity should not be fetched
        argumentCaptor.getValue().execute();
        verify(placeHolderBase, never()).setView(analysisReportScreenWidget);
    }

    private void setUpPlaceManager() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(any(PlaceRequest.class));
        final AbstractWorkbenchActivity activity = mock(AbstractWorkbenchActivity.class);
        final DockPlaceHolderBaseView placeHolderBaseView = mock(DockPlaceHolderBaseView.class);
        doReturn(placeHolderBaseView).when(activity).getWidget();
        doReturn(placeHolderBase).when(placeHolderBaseView).getPresenter();
        doReturn(activity).when(placeManager).getActivity(any());
    }
}
