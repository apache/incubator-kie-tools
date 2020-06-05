/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.editor.page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordion;
import org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.AttributeColumnConfigRow;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.ColumnLabelWidget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.ColumnManagementView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.DeleteColumnManagementAnchorWidget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.ACTION;
import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.ATTRIBUTE;
import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.CONDITION;
import static org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordionItem.Type.METADATA;

public class ColumnsPagePresenter {

    // Injected

    private final View view;

    private final GuidedDecisionTableAccordion accordion;

    private final ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance;

    private final TranslationService translationService;

    private final ManagedInstance<DeleteColumnManagementAnchorWidget> deleteColumnManagementAnchorWidgets;

    private final ManagedInstance<AttributeColumnConfigRow> attributeColumnConfigRow;

    private final ColumnManagementView conditionsPanel;

    private final ColumnManagementView actionsPanel;

    // Constructed

    private GuidedDecisionTableModellerView.Presenter modeller;

    private RuleSelector ruleSelector;

    private VerticalPanel attributeWidget;

    private VerticalPanel metaDataWidget;

    private VerticalPanel conditionsWidget;

    private VerticalPanel actionsWidget;

    private ShowRuleNameOptionPresenter showRuleNameOptionPresenter;

    @Inject
    public ColumnsPagePresenter(final View view,
                                final GuidedDecisionTableAccordion accordion,
                                final ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance,
                                final TranslationService translationService,
                                final ManagedInstance<DeleteColumnManagementAnchorWidget> deleteColumnManagementAnchorWidgets,
                                final ManagedInstance<AttributeColumnConfigRow> attributeColumnConfigRow,
                                final ColumnManagementView conditionsPanel,
                                final ColumnManagementView actionsPanel,
                                final ShowRuleNameOptionPresenter showRuleNameOptionPresenter) {

        this.view = view;
        this.accordion = accordion;
        this.wizardManagedInstance = wizardManagedInstance;
        this.translationService = translationService;
        this.deleteColumnManagementAnchorWidgets = deleteColumnManagementAnchorWidgets;
        this.attributeColumnConfigRow = attributeColumnConfigRow;
        this.conditionsPanel = conditionsPanel;
        this.actionsPanel = actionsPanel;
        this.showRuleNameOptionPresenter = showRuleNameOptionPresenter;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void init(final GuidedDecisionTableModellerView.Presenter modeller) {

        this.modeller = modeller;

        setupAccordion();
        setupRuleInheritance();
        setupUseRuleNames();
        setupColumnsNoteInfo(modeller);
        setupConditionsPanel(modeller);
        setupActionsPanel(modeller);
    }

    void setupConditionsPanel(final GuidedDecisionTableModellerView.Presenter modeller) {
        getConditionsPanel().init(modeller);
    }

    void setupActionsPanel(final GuidedDecisionTableModellerView.Presenter modeller) {
        getActionsPanel().init(modeller);
    }

    void setupAccordionWidgets() {
        setupAccordionWidget(ATTRIBUTE, this::setAttributeWidget);
        setupAccordionWidget(METADATA, this::setMetaDataWidget);
        setupAccordionWidget(CONDITION, this::setConditionsWidget);
        setupAccordionWidget(ACTION, this::setActionsWidget);
    }

    void setupAccordionWidget(final GuidedDecisionTableAccordionItem.Type accordionType,
                              final Consumer<VerticalPanel> setWidget) {

        final VerticalPanel defaultPanel = makeDefaultPanel();

        setWidget.accept(defaultPanel);

        accordion.addItem(accordionType, defaultPanel);
    }

    VerticalPanel getAttributeWidget() {
        return attributeWidget;
    }

    private void setAttributeWidget(final VerticalPanel attributeWidget) {
        this.attributeWidget = attributeWidget;
    }

    VerticalPanel getMetaDataWidget() {
        return metaDataWidget;
    }

    private void setMetaDataWidget(final VerticalPanel metaDataWidget) {
        this.metaDataWidget = metaDataWidget;
    }

    VerticalPanel getConditionsWidget() {
        return conditionsWidget;
    }

    private void setConditionsWidget(final VerticalPanel conditionsWidget) {
        this.conditionsWidget = conditionsWidget;
    }

    VerticalPanel getActionsWidget() {
        return actionsWidget;
    }

    private void setActionsWidget(final VerticalPanel actionsWidget) {
        this.actionsWidget = actionsWidget;
    }

    VerticalPanel makeDefaultPanel() {

        final VerticalPanel verticalPanel = new VerticalPanel();

        verticalPanel.add(blankSlate());

        return verticalPanel;
    }

    Label blankSlate() {

        final String disabledLabelStyle = "text-muted";
        final String noColumns = GuidedDecisionTableConstants.INSTANCE.NoColumnsAvailable();

        return new Label() {{
            setText(noColumns);
            setStyleName(disabledLabelStyle);
        }};
    }

    void refreshAttributeWidget(final List<AttributeCol52> attributeColumns) {

        final Optional<GuidedDecisionTableModellerView.Presenter> optionalModeller = Optional.ofNullable(getModeller());
        final VerticalPanel attributeWidget = getAttributeWidget();

        if (!optionalModeller.isPresent()) {
            return;
        }

        final GuidedDecisionTableModellerView.Presenter modeller = optionalModeller.get();

        attributeWidget.clear();

        if (attributeColumns.isEmpty()) {
            attributeWidget.add(blankSlate());
            return;
        }

        for (final AttributeCol52 attributeColumn : attributeColumns) {

            final AttributeColumnConfigRow columnConfigRow = attributeColumnConfigRow.get();

            columnConfigRow.init(attributeColumn, modeller);
            attributeWidget.add(columnConfigRow.getView());
        }
    }

    void refreshMetaDataWidget(final List<MetadataCol52> metaDataColumns) {

        final Optional<GuidedDecisionTableModellerView.Presenter> optionalModeller = Optional.ofNullable(getModeller());
        final VerticalPanel metaDataWidget = getMetaDataWidget();

        if (!optionalModeller.isPresent()) {
            return;
        }

        final GuidedDecisionTableModellerView.Presenter modeller = optionalModeller.get();

        metaDataWidget.clear();

        if (metaDataColumns.isEmpty()) {
            metaDataWidget.add(blankSlate());
            return;
        }

        for (MetadataCol52 metaDataColumn : metaDataColumns) {
            metaDataWidget.add(makeMetaDataWidget(modeller, metaDataColumn));
        }
    }

    HorizontalPanel makeMetaDataWidget(final GuidedDecisionTableModellerView.Presenter modeller,
                                       final MetadataCol52 metaDataColumn) {

        final HorizontalPanel horizontalPanel = makeHorizontalPanel();
        final ColumnLabelWidget label = makeColumnLabel(metaDataColumn);
        final boolean isEditable = modeller.isActiveDecisionTableEditable();

        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        horizontalPanel.add(label);
        horizontalPanel.add(hideColumnCheckBox(modeller, metaDataColumn));

        if (isEditable) {
            horizontalPanel.add(deleteMetaDataColumnAnchor(modeller, metaDataColumn));
        }

        return horizontalPanel;
    }

    HorizontalPanel makeHorizontalPanel() {
        return new HorizontalPanel();
    }

    DeleteColumnManagementAnchorWidget deleteMetaDataColumnAnchor(final GuidedDecisionTableModellerView.Presenter modeller,
                                                                  final MetadataCol52 metaDataColumn) {

        final DeleteColumnManagementAnchorWidget deleteWidget = deleteColumnManagementAnchorWidgets.get();
        final Command deleteMetadataCommand = deleteMetadataCommand(modeller, metaDataColumn);

        deleteWidget.init(metaDataColumn.getMetadata(), deleteMetadataCommand);

        return deleteWidget;
    }

    Command deleteMetadataCommand(final GuidedDecisionTableModellerView.Presenter modeller,
                                  final MetadataCol52 metaDataColumn) {
        return () -> {
            try {
                final Optional<GuidedDecisionTableView.Presenter> dtPresenter = modeller.getActiveDecisionTable();
                if (dtPresenter.isPresent()) {
                    dtPresenter.get().deleteColumn(metaDataColumn);
                }
            } catch (ModelSynchronizer.VetoException veto) {
                showGenericVetoMessage();
            }
        };
    }

    GuidedDecisionTableModellerView.Presenter getModeller() {
        return modeller;
    }

    CheckBox hideColumnCheckBox(final GuidedDecisionTableModellerView.Presenter modeller,
                                final MetadataCol52 metaDataColumn) {

        final String label = GuidedDecisionTableConstants.INSTANCE.HideThisColumn() + GuidedDecisionTableConstants.COLON;
        final CheckBox hideColumnCheckBox = makeCheckBox(label);

        hideColumnCheckBox.setValue(metaDataColumn.isHideColumn());
        hideColumnCheckBox.addClickHandler(hideMetadataClickHandler(modeller, hideColumnCheckBox, metaDataColumn));

        return hideColumnCheckBox;
    }

    CheckBox makeCheckBox(final String label) {
        return new CheckBox(label);
    }

    ClickHandler hideMetadataClickHandler(final GuidedDecisionTableModellerView.Presenter modeller,
                                          final CheckBox chkHideColumn,
                                          final MetadataCol52 metaDataColumn) {

        return event -> {

            final MetadataCol52 editedColumn = metaDataColumn.cloneColumn();

            editedColumn.setHideColumn(chkHideColumn.getValue());

            try {
                final Optional<GuidedDecisionTableView.Presenter> dtPresenter = modeller.getActiveDecisionTable();
                if (dtPresenter.isPresent()) {
                    dtPresenter.get().updateColumn(metaDataColumn, editedColumn);
                }
            } catch (ModelSynchronizer.VetoException veto) {
                showGenericVetoMessage();
            }
        };
    }

    void refreshConditionsWidget(final List<CompositeColumn<? extends BaseColumn>> conditionColumns) {

        final VerticalPanel conditionsWidget = getConditionsWidget();

        conditionsWidget.clear();

        if (conditionColumns.isEmpty()) {
            getAccordion().getItem(CONDITION).setOpen(false);
            conditionsWidget.add(blankSlate());
            return;
        }

        conditionsWidget.add(getConditionsPanel());

        final Map<String, List<BaseColumn>> columnGroups = groupByTitle(conditionColumns);

        getConditionsPanel().renderColumns(columnGroups);
    }

    <T extends BaseColumn> Map<String, List<BaseColumn>> groupByTitle(final List<T> columns) {
        return columns
                .stream()
                .collect(Collectors.groupingBy(DecisionTableColumnViewUtils::getColumnManagementGroupTitle,
                                               Collectors.toList()));
    }

    ColumnManagementView getConditionsPanel() {
        return conditionsPanel;
    }

    void refreshActionsWidget(final List<ActionCol52> actionColumns) {

        final VerticalPanel actionsWidget = getActionsWidget();

        actionsWidget.clear();

        if (actionColumns.isEmpty()) {
            getAccordion().getItem(ACTION).setOpen(false);
            actionsWidget.add(blankSlate());
            return;
        }

        actionsWidget.add(getActionsPanel());

        final Map<String, List<BaseColumn>> columnGroups = groupByTitle(actionColumns);

        getActionsPanel().renderColumns(columnGroups);
    }

    ColumnManagementView getActionsPanel() {
        return actionsPanel;
    }

    public void onUpdatedLockStatusEvent(final @Observes UpdatedLockStatusEvent event) {

        if (!hasActiveDecisionTable()) {
            return;
        }

        getActiveDecisionTable().ifPresent(dt -> {
            final ObservablePath currentPath = dt.getCurrentPath();
            if (currentPath.equals(event.getFile())) {
                refresh();
            }
        });
    }

    private Optional<GuidedDecisionTableView.Presenter> getActiveDecisionTable() {
        return getModeller().getActiveDecisionTable();
    }

    boolean hasActiveDecisionTable() {

        final Optional<GuidedDecisionTableModellerView.Presenter> modeller = Optional.ofNullable(getModeller());

        if (!modeller.isPresent()) {
            return false;
        }

        return Optional.ofNullable(getActiveDecisionTable()).isPresent();
    }

    public void onRefreshAttributesPanelEvent(final @Observes RefreshAttributesPanelEvent event) {
        refreshAttributeWidget(event.getColumns());
        refreshColumnsNoteInfo(event.getPresenter());
    }

    public void onRefreshMetaDataPanelEvent(final @Observes RefreshMetaDataPanelEvent event) {
        refreshMetaDataWidget(event.getColumns());
        refreshColumnsNoteInfo(event.getPresenter());
    }

    public void onRefreshConditionsPanelEvent(final @Observes RefreshConditionsPanelEvent event) {
        refreshConditionsWidget(event.getColumns());
        refreshColumnsNoteInfo(event.getPresenter());
    }

    public void onRefreshActionsPanelEvent(final @Observes RefreshActionsPanelEvent event) {
        refreshActionsWidget(event.getColumns());
        refreshColumnsNoteInfo(event.getPresenter());
    }

    void showGenericVetoMessage() {
        ErrorPopup.showMessage(translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_GenericVetoError));
    }

    ColumnLabelWidget makeColumnLabel(final MetadataCol52 metaDataColumn) {

        final ColumnLabelWidget label = new ColumnLabelWidget(metaDataColumn.getMetadata());

        ColumnUtilities.setColumnLabelStyleWhenHidden(label, metaDataColumn.isHideColumn());

        return label;
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key, args);
    }

    Optional<GuidedDecisionTable52> getGuidedDecisionTable52() {
        final Optional<GuidedDecisionTableView.Presenter> activeDecisionTable = getActiveDecisionTable();
        if (activeDecisionTable.isPresent()) {
            return Optional.of(activeDecisionTable.get().getModel());
        }
        return Optional.empty();
    }

    public GuidedDecisionTableAccordion getAccordion() {
        return accordion;
    }

    void setupColumnsNoteInfo(final GuidedDecisionTableModellerView.Presenter modeller) {
        final Optional<GuidedDecisionTableView.Presenter> activeDecisionTable = modeller.getActiveDecisionTable();
        if (activeDecisionTable.isPresent()) {
            if (activeDecisionTable.get().hasColumnDefinitions()) {
                view.setColumnsNoteInfoAsHidden();
            } else {
                view.setColumnsNoteInfoAsVisible();
            }
        } else {
            view.setColumnsNoteInfoAsVisible();
        }
    }

    void refreshColumnsNoteInfo(final GuidedDecisionTableView.Presenter presenter) {
        setupColumnsNoteInfo(presenter.getModellerPresenter());
    }

    void setupAccordion() {

        accordion.clear();

        setupAccordionWidgets();

        view.setAccordion(accordion);
    }

    void setupRuleInheritance() {
        view.setRuleInheritanceWidget(new FlowPanel() {{
            add(ruleInheritanceWidget());
        }});
    }

    void setupUseRuleNames() {

        getActiveDecisionTable().ifPresent(this::setupRuleName);

        showRuleNameOptionPresenter.addOptionChangeCallback(
                result -> getActiveDecisionTable().ifPresent(dt -> dt.setShowRuleName(result))
        );
        view.setRuleNameOptionWidget(showRuleNameOptionPresenter);
    }

    private void setupRuleName(GuidedDecisionTableView.Presenter presenter) {
        if (presenter == null) {
            return;
        }

        final GuidedDecisionTable52 model = presenter.getModel();
        showRuleNameOptionPresenter.setShowRuleName(!model.getRuleNameColumn().isHideColumn());
    }

    Widget ruleInheritanceWidget() {

        final FlowPanel result = makeFlowPanel();

        result.setStyleName(GuidedDecisionTableResources.INSTANCE.css().ruleInheritance());
        result.add(ruleInheritanceLabel());
        result.add(ruleSelector());

        return result;
    }

    FlowPanel makeFlowPanel() {
        return new FlowPanel();
    }

    Widget ruleSelector() {
        this.ruleSelector = makeRuleSelector();

        ruleSelector.addValueChangeHandler(e -> {
            getActiveDecisionTable().ifPresent(dt -> dt.setParentRuleName(e.getValue()));
        });

        getActiveDecisionTable().ifPresent(this::setupRuleSelector);

        return ruleSelector;
    }

    Label ruleInheritanceLabel() {
        final Label label = new Label(GuidedDecisionTableConstants.INSTANCE.AllTheRulesInherit());

        label.setStyleName(GuidedDecisionTableResources.INSTANCE.css().ruleInheritanceLabel());

        return label;
    }

    RuleSelector makeRuleSelector() {
        return new RuleSelector();
    }

    void setupRuleSelector(final GuidedDecisionTableView.Presenter presenter) {

        if (presenter == null) {
            return;
        }

        final GuidedDecisionTable52 model = presenter.getModel();
        final String ruleName = model.getParentName();

        presenter.getPackageParentRuleNames(availableParentRuleNames -> {
            ruleSelector.setRuleName(ruleName);
            ruleSelector.setRuleNames(availableParentRuleNames);
        });
    }

    void openNewGuidedDecisionTableColumnWizard() {
        if (!isColumnCreationEnabledToActiveDecisionTable()) {
            return;
        }
        getActiveDecisionTable().ifPresent(dt -> {
            final NewGuidedDecisionTableColumnWizard wizard = wizardManagedInstance.get();
            wizard.init(dt);
            wizard.start();
        });
    }

    boolean isColumnCreationEnabledToActiveDecisionTable() {
        return hasActiveDecisionTable() && isColumnCreationEnabled(getActiveDecisionTable());
    }

    boolean isColumnCreationEnabled(final Optional<GuidedDecisionTableView.Presenter> dtPresenter) {
        if (!dtPresenter.isPresent()) {
            return false;
        }

        final GuidedDecisionTableView.Presenter dt = dtPresenter.get();
        final boolean decisionTableIsEditable = !dt.isReadOnly();
        final boolean decisionTableHasEditableColumns = dt.hasEditableColumns();

        return decisionTableHasEditableColumns && decisionTableIsEditable;
    }

    public void onDecisionTableSelected(final @Observes DecisionTableSelectedEvent event) {
        if (!hasActiveDecisionTable()) {
            return;
        }

        final Optional<GuidedDecisionTableView.Presenter> dtPresenter = event.getPresenter();

        if (!dtPresenter.isPresent()) {
            return;
        }

        final GuidedDecisionTableView.Presenter presenter = dtPresenter.get();

        getActiveDecisionTable().ifPresent(dt -> {
            if (!presenter.equals(dt)) {
                setupRuleSelector(presenter);
            }
        });
    }

    public void refresh() {
        getGuidedDecisionTable52().ifPresent(model -> {
            refreshAttributeWidget(model.getAttributeCols());
            refreshMetaDataWidget(model.getMetadataCols());
            refreshConditionsWidget(model.getConditions());
            refreshActionsWidget(model.getActionCols());
        });
    }

    public interface View extends UberElement<ColumnsPagePresenter> {

        void setAccordion(final GuidedDecisionTableAccordion accordion);

        void setRuleInheritanceWidget(final IsWidget isWidget);

        void setColumnsNoteInfoAsVisible();

        void setColumnsNoteInfoAsHidden();

        void setRuleNameOptionWidget(final ShowRuleNameOptionPresenter showRuleNameOptionPresenter);
    }
}
