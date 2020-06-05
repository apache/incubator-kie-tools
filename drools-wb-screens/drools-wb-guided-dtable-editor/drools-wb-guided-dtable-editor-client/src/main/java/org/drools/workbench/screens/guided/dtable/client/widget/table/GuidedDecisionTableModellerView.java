/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Observes;

import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableColumnSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.mvp.PlaceRequest;

public interface GuidedDecisionTableModellerView extends UberView<GuidedDecisionTableModellerView.Presenter>,
                                                         RequiresResize,
                                                         ProvidesResize,
                                                         GridSelectionManager,
                                                         HasKeyDownHandlers,
                                                         HasMouseDownHandlers,
                                                         HasContextMenuHandlers,
                                                         ViewMenuBuilder.SupportsZoom {

    void clear();

    void addDecisionTable(final GuidedDecisionTableView gridWidget);

    void removeDecisionTable(final GuidedDecisionTableView gridWidget,
                             final Command afterRemovalCommand);

    void refreshScrollPosition();

    GridLayer getGridLayerView();

    GridLienzoPanel getGridPanel();

    Bounds getBounds();

    void setPinnedModeIndicatorVisibility(final boolean visible, final double headerCaptionWidth);

    void showGenericVetoMessage();

    void showUnableToDeleteColumnMessage(final ConditionCol52 column);

    void showUnableToDeleteColumnMessage(final ActionCol52 column);

    interface Presenter extends GridPinnedModeManager,
                                ViewMenuBuilder.SupportsZoom {

        void onClose();

        void releaseDecisionTables();

        void releaseHandlerRegistrations();

        GuidedDecisionTableView.Presenter addDecisionTable(final ObservablePath path,
                                                           final PlaceRequest placeRequest,
                                                           final GuidedDecisionTableEditorContent content,
                                                           final boolean isReadOnly,
                                                           final Double x,
                                                           final Double y);

        GuidedDecisionTableView.Presenter refreshDecisionTable(final GuidedDecisionTableView.Presenter dtPresenter,
                                                               final ObservablePath path,
                                                               final PlaceRequest placeRequest,
                                                               final GuidedDecisionTableEditorContent content,
                                                               final boolean isReadOnly);

        void removeDecisionTable(final GuidedDecisionTableView.Presenter dtPresenter);

        Optional<GuidedDecisionTableView.Presenter> getActiveDecisionTable();

        Set<GuidedDecisionTableView.Presenter> getAvailableDecisionTables();

        boolean isDecisionTableAvailable(final GuidedDecisionTableView.Presenter dtPresenter);

        boolean isActiveDecisionTableEditable();

        GuidedDecisionTableModellerView getView();

        void onDecisionTableSelected(@Observes DecisionTableSelectedEvent event);

        void onDecisionTableLinkedColumnSelected(@Observes DecisionTableColumnSelectedEvent event);

        void updateRadar();

        void onViewPinned(final boolean isPinned);

        void refreshScrollPosition();

        void updateLinks();

        void analysisReportScreen(AnalysisReportScreen analysisReportScreen);

    }
}
