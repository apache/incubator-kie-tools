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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.EditMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.InsertMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.ViewMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.workitems.IBindingProvider;
import org.kie.workbench.common.widgets.metadata.client.KieDocument;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

public interface GuidedDecisionTableView extends GridWidget,
                                                 HasBusyIndicator {

    double ROW_HEIGHT = 24;

    void setLocation(final double x,
                     final double y);

    void showDataCutNotificationEvent();

    void showDataCopiedNotificationEvent();

    void registerNodeDragMoveHandler(final NodeDragMoveHandler handler);

    void registerNodeMouseDoubleClickHandler(final NodeMouseDoubleClickHandler handler);

    boolean isNodeMouseEventOverCaption(final INodeXYEvent event);

    GuidedDecisionTableView.Presenter getPresenter();

    interface Presenter extends IBindingProvider,
                                GridSelectionManager,
                                GridPinnedModeManager,
                                EditMenuBuilder.SupportsEditMenu,
                                ViewMenuBuilder.HasMergedView,
                                ViewMenuBuilder.HasAuditLog,
                                InsertMenuBuilder.SupportsAppendRow,
                                InsertMenuBuilder.SupportsInsertRowAbove,
                                InsertMenuBuilder.SupportsInsertRowBelow,
                                KieDocument {

        Set<PortableWorkDefinition> getWorkItemDefinitions();

        EventBus getEventBus();

        void activate();

        GuidedDecisionTable52 getModel();

        AsyncPackageDataModelOracle getDataModelOracle();

        Overview getOverview();

        GuidedDecisionTableView getView();

        GuidedDecisionTableModellerView.Presenter getModellerPresenter();

        void setContent(final ObservablePath path,
                        final PlaceRequest placeRequest,
                        final AnalysisReportScreen analysisReportScreen,
                        final GuidedDecisionTableEditorContent content,
                        final GuidedDecisionTableModellerView.Presenter parent,
                        final boolean isReadOnly);

        void refreshContent(final ObservablePath path,
                            final PlaceRequest placeRequest,
                            final GuidedDecisionTableEditorContent content,
                            final boolean isReadOnly);

        GuidedDecisionTablePresenter.Access getAccess();

        void onClose();

        void initialiseAnalysis();

        void terminateAnalysis();

        void getPackageParentRuleNames(final ParameterizedCommand<Collection<String>> command);

        void setParentRuleName(final String parentName);

        boolean hasColumnDefinitions();

        List<String> getLHSBoundFacts();

        Map<String, String> getValueListLookups(final BaseColumn column);

        void getEnumLookups(final String factType,
                            final String factField,
                            final DependentEnumsUtilities.Context context,
                            final Callback<Map<String, String>> callback);

        Set<String> getReservedAttributeNames();

        boolean isMetaDataUnique(final String metaDataName);

        void editCondition(final Pattern52 pattern,
                           final ConditionCol52 column);

        void editCondition(final BRLConditionColumn column);

        void editAction(final ActionCol52 column);

        void appendColumn(final AttributeCol52 column);

        void appendColumn(final MetadataCol52 column);

        void appendColumn(final Pattern52 pattern,
                          final ConditionCol52 column);

        void appendColumn(final ConditionCol52 column);

        void appendColumn(final ActionCol52 column);

        void deleteColumn(final AttributeCol52 column) throws VetoException;

        void deleteColumn(final MetadataCol52 column) throws VetoException;

        void deleteColumn(final ConditionCol52 column) throws VetoException;

        void deleteColumn(final ActionCol52 column) throws VetoException;

        void updateColumn(final AttributeCol52 originalColumn,
                          final AttributeCol52 editedColumn) throws VetoException;

        void updateColumn(final MetadataCol52 originalColumn,
                          final MetadataCol52 editedColumn) throws VetoException;

        void updateColumn(final Pattern52 originalPattern,
                          final ConditionCol52 originalColumn,
                          final Pattern52 editedPattern,
                          final ConditionCol52 editedColumn) throws VetoException;

        void updateColumn(final ConditionCol52 originalColumn,
                          final ConditionCol52 editedColumn) throws VetoException;

        void updateColumn(final ActionCol52 originalColumn,
                          final ActionCol52 editedColumn) throws VetoException;

        void link(final Set<GuidedDecisionTableView.Presenter> dtPresenters);

        boolean hasEditableColumns();

        void setShowRuleName(boolean show);
    }
}
