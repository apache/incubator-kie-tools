/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AppendColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AppendRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DeleteColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DeleteRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DisableRightPanelEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DuplicateRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.EnableRightPanelEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.PrependColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.PrependRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ReloadRightPanelEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioGridReloadEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioNotificationEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetInstanceHeaderEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetPropertyHeaderEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * This class is meant to be a centralized listener for events fired up by UI, responding to them with specific <code>Command</code>s.
 * <p>
 * It follows the GWT standard Event/Handler mechanism
 */
@Dependent
public class CommandExecutor implements AppendColumnEventHandler,
                                        AppendRowEventHandler,
                                        DeleteColumnEventHandler,
                                        DeleteRowEventHandler,
                                        DisableRightPanelEventHandler,
                                        DuplicateRowEventHandler,
                                        EnableRightPanelEventHandler,
                                        InsertColumnEventHandler,
                                        InsertRowEventHandler,
                                        PrependColumnEventHandler,
                                        PrependRowEventHandler,
                                        ReloadRightPanelEventHandler,
                                        ScenarioGridReloadEventHandler,
                                        ScenarioNotificationEventHandler,
                                        SetInstanceHeaderEventHandler,
                                        SetPropertyHeaderEventHandler {

    protected ScenarioGridModel model;
    protected ScenarioGridPanel scenarioGridPanel;
    protected ScenarioGridLayer scenarioGridLayer;
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    protected RightPanelView.Presenter rightPanelPresenter;
    protected DeletePopupPresenter deletePopupPresenter;
    protected PreserveDeletePopupPresenter preserveDeletePopupPresenter;

    protected EventBus eventBus;

    protected List<HandlerRegistration> handlerRegistrationList = new ArrayList<>();

    protected Event<NotificationEvent> notificationEvent;

    public CommandExecutor() {
        // CDI
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        registerHandlers();
    }

    public void setScenarioSimulationEditorPresenter(ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter) {
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
    }

    public void setRightPanelPresenter(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
    }

    /**
     * This method set <code>ScenarioGridPanel</code>, <code>ScenarioGridLayer</code> and <code>ScenarioGridModel</code>
     * from the give <code>ScenarioGridPanel</code>
     * @param scenarioGridPanel
     */
    public void setScenarioGridPanel(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
        this.scenarioGridLayer = scenarioGridPanel.getScenarioGridLayer();
        this.model = scenarioGridLayer.getScenarioGrid().getModel();
    }

    public void setDeletePopupPresenter(DeletePopupPresenter deletePopupPresenter) {
        this.deletePopupPresenter = deletePopupPresenter;
    }

    public void setPreserveDeletePopupPresenter(PreserveDeletePopupPresenter preserveDeletePopupPresenter) {
        this.preserveDeletePopupPresenter = preserveDeletePopupPresenter;
    }

    public void setNotificationEvent(Event<NotificationEvent> notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    @PreDestroy
    public void unregisterHandlers() {
        handlerRegistrationList.forEach(HandlerRegistration::removeHandler);
    }

    @Override
    public void onEvent(AppendColumnEvent event) {
        commonExecute(new AppendColumnCommand(model, String.valueOf(new Date().getTime()), event.getColumnGroup(), scenarioGridPanel, scenarioGridLayer));
    }

    @Override
    public void onEvent(AppendRowEvent event) {
        commonExecute(new AppendRowCommand(model));
    }

    @Override
    public void onEvent(DeleteColumnEvent event) {
        int deletedColumnIndex = event.getColumnIndex();
        GridColumn<?> selectedColumn = model.getSelectedColumn();
        boolean toDisable = selectedColumn == null || model.getColumns().indexOf(selectedColumn) == deletedColumnIndex;
        commonExecute(new DeleteColumnCommand(model, deletedColumnIndex, event.getColumnGroup(), scenarioGridPanel, scenarioGridLayer));
        if (rightPanelPresenter != null && toDisable) {
            commonExecute(new DisableRightPanelCommand(rightPanelPresenter));
        }
    }

    @Override
    public void onEvent(DeleteRowEvent event) {
        commonExecute(new DeleteRowCommand(model, event.getRowIndex()));
    }

    @Override
    public void onEvent(DisableRightPanelEvent event) {
        if (rightPanelPresenter != null) {
            commonExecute(new DisableRightPanelCommand(rightPanelPresenter));
        }
    }

    @Override
    public void onEvent(DuplicateRowEvent event) {
        commonExecute(new DuplicateRowCommand(model, event.getRowIndex()));
    }

    @Override
    public void onEvent(EnableRightPanelEvent event) {
        if (scenarioSimulationEditorPresenter != null) {
            scenarioSimulationEditorPresenter.expandToolsDock();
        }
        commonExecute(new EnableRightPanelCommand(rightPanelPresenter, event.getFilterTerm(), event.getPropertyName(), event.isNotEqualsSearch()));
    }

    @Override
    public void onEvent(InsertColumnEvent event) {
        commonExecute(new InsertColumnCommand(model, String.valueOf(new Date().getTime()), event.getColumnIndex(), event.isRight(), event.isAsProperty(), scenarioGridPanel, scenarioGridLayer));
    }

    @Override
    public void onEvent(InsertRowEvent event) {
        commonExecute(new InsertRowCommand(model, event.getRowIndex()));
    }

    @Override
    public void onEvent(PrependColumnEvent event) {
        commonExecute(new PrependColumnCommand(model, String.valueOf(new Date().getTime()), event.getColumnGroup(), scenarioGridPanel, scenarioGridLayer));
    }

    @Override
    public void onEvent(PrependRowEvent event) {
        commonExecute(new PrependRowCommand(model));
    }

    @Override
    public void onEvent(ReloadRightPanelEvent event) {
        commonExecute(new ReloadRightPanelCommand(scenarioSimulationEditorPresenter, event.isDisable(), event.isOpenDock()));
    }

    @Override
    public void handle(ScenarioGridReloadEvent event) {
        scenarioGridPanel.onResize();
    }

    @Override
    public void onEvent(ScenarioNotificationEvent event) {
        notificationEvent.fire(new NotificationEvent(event.getMessage(), event.getType()));
    }

    @Override
    public void onEvent(SetInstanceHeaderEvent event) {
        if (model.getSelectedColumn() == null) {
            return;
        }
        if (model.isSameSelectedColumnType(event.getClassName())) {
            return;
        } else if (((ScenarioGridColumn) model.getSelectedColumn()).isInstanceAssigned()) {
            Command okPreserveCommand = () -> commonExecute(new SetInstanceHeaderCommand(model, event.getFullPackage(), event.getClassName(), scenarioGridPanel, scenarioGridLayer));
            deletePopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainTitle(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeText1(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextDanger(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeType(),
                                      okPreserveCommand);
        } else {
            commonExecute(new SetInstanceHeaderCommand(model, event.getFullPackage(), event.getClassName(), scenarioGridPanel, scenarioGridLayer));
        }
    }

    @Override
    public void onEvent(SetPropertyHeaderEvent event) {
        if (model.getSelectedColumn() == null) {
            return;
        }
        if (model.isSelectedColumnEmpty()) {
            commonExecute(new SetPropertyHeaderCommand(model, event.getFullPackage(), event.getValue(), event.getValueClassName(), scenarioGridPanel, scenarioGridLayer, false));
        } else if (model.isSameSelectedColumnProperty(event.getValue())) {
            return;
        } else if (model.isSameSelectedColumnType(event.getValueClassName())) {
            Command okDeleteCommand = () -> commonExecute(new SetPropertyHeaderCommand(model, event.getFullPackage(), event.getValue(), event.getValueClassName(), scenarioGridPanel, scenarioGridLayer, false));
            Command okPreserveCommand = () -> commonExecute(new SetPropertyHeaderCommand(model, event.getFullPackage(), event.getValue(), event.getValueClassName(), scenarioGridPanel, scenarioGridLayer, true));
            preserveDeletePopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioMainTitle(),
                                              ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioMainQuestion(),
                                              ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioText1(),
                                              ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextQuestion(),
                                              ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextOption1(),
                                              ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextOption2(),
                                              ScenarioSimulationEditorConstants.INSTANCE.preserveValues(),
                                              ScenarioSimulationEditorConstants.INSTANCE.deleteValues(),
                                              okPreserveCommand,
                                              okDeleteCommand);
        } else if (!model.isSameSelectedColumnType(event.getValueClassName())) {
            Command okPreserveCommand = () -> commonExecute(new SetPropertyHeaderCommand(model, event.getFullPackage(), event.getValue(), event.getValueClassName(), scenarioGridPanel, scenarioGridLayer, false));
            deletePopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainTitle(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioText1(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextDanger(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteValues(),
                                      okPreserveCommand);
        }
        if (scenarioSimulationEditorPresenter != null) {
            scenarioSimulationEditorPresenter.reloadRightPanel(false);
        }
    }

    void commonExecute(Command toExecute) {
        toExecute.execute();
        scenarioGridPanel.onResize();
        scenarioGridPanel.select();
    }

    void registerHandlers() {
        handlerRegistrationList.add(eventBus.addHandler(AppendColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(AppendRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DeleteColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DeleteRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DisableRightPanelEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DuplicateRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(EnableRightPanelEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(InsertColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(InsertRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(PrependColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(PrependRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ReloadRightPanelEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ScenarioGridReloadEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ScenarioNotificationEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(SetInstanceHeaderEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(SetPropertyHeaderEvent.TYPE, this));
    }
}
