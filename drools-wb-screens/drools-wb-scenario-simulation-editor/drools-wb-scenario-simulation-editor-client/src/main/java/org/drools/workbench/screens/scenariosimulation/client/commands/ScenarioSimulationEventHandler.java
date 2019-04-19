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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioSimulationCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DeleteColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DeleteRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DisableTestToolsCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DuplicateInstanceCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DuplicateRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.EnableTestToolsCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.ImportCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.InsertColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.InsertRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.PrependColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.PrependRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.ReloadTestToolsCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.RunSingleScenarioCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetGridCellValueCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetHeaderCellValueCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetInstanceHeaderCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetPropertyHeaderCommand;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateInstanceEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RunSingleScenarioEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetHeaderCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UnsupportedDMNEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AppendColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AppendRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DeleteColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DeleteRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DisableTestToolsEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DuplicateInstanceEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DuplicateRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.EnableTestToolsEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ImportEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.PrependColumnEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.PrependRowEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.RedoEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ReloadTestToolsEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.RunSingleScenarioEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioGridReloadEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioNotificationEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetGridCellValueEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetHeaderCellValueEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetInstanceHeaderEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetPropertyHeaderEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.UndoEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.UnsupportedDMNEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.scenariosimulation.service.ImportExportType.CSV;

/**
 * This class is meant to be a centralized listener for events fired up by UI, responding to them issuing specific <code>Command</code>s.
 * <p>
 * It follows the GWT standard Event/Handler mechanism
 */
@Dependent
public class ScenarioSimulationEventHandler implements AppendColumnEventHandler,
                                                       AppendRowEventHandler,
                                                       DeleteColumnEventHandler,
                                                       DeleteRowEventHandler,
                                                       DisableTestToolsEventHandler,
                                                       DuplicateInstanceEventHandler,
                                                       DuplicateRowEventHandler,
                                                       EnableTestToolsEventHandler,
                                                       ImportEventHandler,
                                                       InsertColumnEventHandler,
                                                       InsertRowEventHandler,
                                                       PrependColumnEventHandler,
                                                       PrependRowEventHandler,
                                                       RedoEventHandler,
                                                       ReloadTestToolsEventHandler,
                                                       RunSingleScenarioEventHandler,
                                                       ScenarioGridReloadEventHandler,
                                                       ScenarioNotificationEventHandler,
                                                       SetGridCellValueEventHandler,
                                                       SetHeaderCellValueEventHandler,
                                                       SetInstanceHeaderEventHandler,
                                                       SetPropertyHeaderEventHandler,
                                                       UndoEventHandler,
                                                       UnsupportedDMNEventHandler {

    protected DeletePopupPresenter deletePopupPresenter;
    protected PreserveDeletePopupPresenter preserveDeletePopupPresenter;
    protected ConfirmPopupPresenter confirmPopupPresenter;
    protected FileUploadPopupPresenter fileUploadPopupPresenter;

    protected EventBus eventBus;

    protected List<HandlerRegistration> handlerRegistrationList = new ArrayList<>();

    protected Event<NotificationEvent> notificationEvent;

    protected ScenarioSimulationContext context;

    protected ScenarioCommandRegistry scenarioCommandRegistry;

    protected ScenarioCommandManager scenarioCommandManager;

    public ScenarioSimulationEventHandler() {
        // CDI
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        registerHandlers();
    }

    public void setDeletePopupPresenter(DeletePopupPresenter deletePopupPresenter) {
        this.deletePopupPresenter = deletePopupPresenter;
    }

    public void setPreserveDeletePopupPresenter(PreserveDeletePopupPresenter preserveDeletePopupPresenter) {
        this.preserveDeletePopupPresenter = preserveDeletePopupPresenter;
    }

    public void setConfirmPopupPresenter(ConfirmPopupPresenter confirmPopupPresenter) {
        this.confirmPopupPresenter = confirmPopupPresenter;
    }

    public void setFileUploadPopupPresenter(FileUploadPopupPresenter fileUploadPopupPresenter) {
        this.fileUploadPopupPresenter = fileUploadPopupPresenter;
    }

    public void setNotificationEvent(Event<NotificationEvent> notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    public void setContext(ScenarioSimulationContext context) {
        this.context = context;
    }

    public void setScenarioCommandRegistry(ScenarioCommandRegistry scenarioCommandRegistry) {
        this.scenarioCommandRegistry = scenarioCommandRegistry;
    }

    public void setScenarioCommandManager(ScenarioCommandManager scenarioCommandManager) {
        this.scenarioCommandManager = scenarioCommandManager;
    }

    @PreDestroy
    public void unregisterHandlers() {
        handlerRegistrationList.forEach(HandlerRegistration::removeHandler);
    }

    @Override
    public void onEvent(AppendColumnEvent event) {
        context.getStatus().setColumnId(String.valueOf(new Date().getTime()));
        context.getStatus().setColumnGroup(event.getColumnGroup());
        commonExecution(context, new AppendColumnCommand());
    }

    @Override
    public void onEvent(AppendRowEvent event) {
        commonExecution(context, new AppendRowCommand());
    }

    @Override
    public void onEvent(DeleteColumnEvent event) {
        context.getStatus().setColumnIndex(event.getColumnIndex());
        context.getStatus().setColumnGroup(event.getColumnGroup());
        commonExecution(context, new DeleteColumnCommand());
    }

    @Override
    public void onEvent(DeleteRowEvent event) {
        context.getStatus().setRowIndex(event.getRowIndex());
        commonExecution(context, new DeleteRowCommand());
    }

    @Override
    public void onEvent(DisableTestToolsEvent event) {
        commonExecution(context, new DisableTestToolsCommand());
    }

    @Override
    public void onEvent(DuplicateInstanceEvent event) {
        context.getStatus().setColumnId(String.valueOf(new Date().getTime()));
        context.getStatus().setColumnIndex(event.getColumnIndex());
        context.getStatus().setRight(true);
        context.getStatus().setAsProperty(false);
        commonExecution(context, new DuplicateInstanceCommand());
    }

    @Override
    public void onEvent(DuplicateRowEvent event) {
        context.getStatus().setRowIndex(event.getRowIndex());
        commonExecution(context, new DuplicateRowCommand());
    }

    @Override
    public void onEvent(EnableTestToolsEvent event) {
        context.getStatus().setFilterTerm(event.getFilterTerm());
        context.getStatus().setPropertyName(event.getPropertyName());
        context.getStatus().setNotEqualsSearch(event.isNotEqualsSearch());
        commonExecution(context, new EnableTestToolsCommand());
    }

    @Override
    public void onEvent(ImportEvent event) {
        org.uberfire.mvp.Command okImportCommand = () -> {
            ImportCommand importCommand = new ImportCommand();
            importCommand.setFileContent(fileUploadPopupPresenter.getFileContents());
            commonExecution(context, importCommand);
        };
        fileUploadPopupPresenter.show(Collections.singletonList(CSV.getExtension()),
                                      ScenarioSimulationEditorConstants.INSTANCE.selectImportFile(),
                                      ScenarioSimulationEditorConstants.INSTANCE.importLabel(),
                                      okImportCommand);
    }

    @Override
    public void onEvent(InsertColumnEvent event) {
        context.getStatus().setColumnId(String.valueOf(new Date().getTime()));
        context.getStatus().setColumnIndex(event.getColumnIndex());
        context.getStatus().setRight(event.isRight());
        context.getStatus().setAsProperty(event.isAsProperty());
        commonExecution(context, new InsertColumnCommand());
    }

    @Override
    public void onEvent(InsertRowEvent event) {
        context.getStatus().setRowIndex(event.getRowIndex());
        commonExecution(context, new InsertRowCommand());
    }

    @Override
    public void onEvent(PrependColumnEvent event) {
        context.getStatus().setColumnId(String.valueOf(new Date().getTime()));
        context.getStatus().setColumnGroup(event.getColumnGroup());
        commonExecution(context, new PrependColumnCommand());
    }

    @Override
    public void onEvent(PrependRowEvent event) {
        commonExecution(context, new PrependRowCommand());
    }

    @Override
    public void onEvent(RedoEvent event) {
        final CommandResult<ScenarioSimulationViolation> status = scenarioCommandRegistry.redo(context);
        if (Objects.equals(CommandResult.Type.ERROR, status.getType())) {
            commonNotifyError(status, ScenarioSimulationEditorConstants.INSTANCE.redo());
        }
    }

    @Override
    public void onEvent(ReloadTestToolsEvent event) {
        context.getStatus().setDisable(event.isDisable());
        context.getStatus().setOpenDock(event.isOpenDock());
        commonExecution(context, new ReloadTestToolsCommand());
    }

    @Override
    public void onEvent(RunSingleScenarioEvent event) {
        context.getStatus().setRowIndex(event.getRowIndex());
        commonExecution(context, new RunSingleScenarioCommand());
    }

    @Override
    public void handle(ScenarioGridReloadEvent event) {
        context.getScenarioGridPanel().onResize();
    }

    @Override
    public void onEvent(ScenarioNotificationEvent event) {
        notificationEvent.fire(new NotificationEvent(event.getMessage(), event.getType()));
    }

    @Override
    public void onEvent(SetGridCellValueEvent event) {
        context.getStatus().setRowIndex(event.getRowIndex());
        context.getStatus().setColumnIndex(event.getColumnIndex());
        context.getStatus().setCellValue(event.getCellValue());
        commonExecution(context, new SetGridCellValueCommand());
    }

    @Override
    public void onEvent(SetHeaderCellValueEvent event) {
        context.getStatus().setRowIndex(event.getRowIndex());
        context.getStatus().setColumnIndex(event.getColumnIndex());
        context.getStatus().setCellValue(event.getCellValue());
        commonExecution(context, new SetHeaderCellValueCommand(event.isInstanceHeader(), event.isPropertyHeader()));
    }

    @Override
    public void onEvent(SetInstanceHeaderEvent event) {
        if (context.getModel().getSelectedColumn() == null) {
            return;
        }
        if (context.getModel().isSameSelectedColumnType(event.getClassName())) {
            return;
        }
        context.getStatus().setFullPackage(event.getFullPackage());
        context.getStatus().setClassName(event.getClassName());
        if (((ScenarioGridColumn) context.getModel().getSelectedColumn()).isInstanceAssigned()) {
            org.uberfire.mvp.Command okPreserveCommand = () -> commonExecution(context, new SetInstanceHeaderCommand());
            deletePopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainTitle(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeText1(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextDanger(),
                                      ScenarioSimulationEditorConstants.INSTANCE.changeType(),
                                      okPreserveCommand);
        } else {
            commonExecution(context, new SetInstanceHeaderCommand());
        }
        context.getScenarioGridPanel().setFocus(true);
    }

    @Override
    public void onEvent(SetPropertyHeaderEvent event) {
        if (context.getModel().getSelectedColumn() == null) {
            return;
        }
        if (context.getModel().isAlreadyAssignedProperty(event.getValue())) {
            onEvent(new ScenarioNotificationEvent("Property \"" + event.getValue() + "\" already assigned", NotificationEvent.NotificationType.ERROR));
            return;
        }
        context.getStatus().setFullPackage(event.getFullPackage());
        context.getStatus().setValue(event.getValue());
        context.getStatus().setValueClassName(event.getValueClassName());
        if (context.getModel().isSelectedColumnEmpty()) {
            commonExecution(context, new SetPropertyHeaderCommand());
        } else if (context.getModel().isSameSelectedColumnProperty(event.getValue())) {
            return;
        } else if (context.getModel().isSameSelectedColumnType(event.getValueClassName())) {
            org.uberfire.mvp.Command okDeleteCommand = () -> {
                context.getStatus().setKeepData(false);
                commonExecution(context, new SetPropertyHeaderCommand());
            };
            org.uberfire.mvp.Command okPreserveCommand = () -> {
                context.getStatus().setKeepData(true);
                commonExecution(context, new SetPropertyHeaderCommand());
            };
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
        } else if (!context.getModel().isSameSelectedColumnType(event.getValueClassName())) {
            org.uberfire.mvp.Command okPreserveCommand = () -> {
                context.getStatus().setKeepData(false);
                commonExecution(context, new SetPropertyHeaderCommand());
            };
            deletePopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainTitle(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioText1(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextQuestion(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextDanger(),
                                      ScenarioSimulationEditorConstants.INSTANCE.deleteValues(),
                                      okPreserveCommand);
        }

        context.getScenarioGridPanel().setFocus(true);
    }

    @Override
    public void onEvent(UndoEvent event) {
        final CommandResult<ScenarioSimulationViolation> status = scenarioCommandRegistry.undo(context);
        if (Objects.equals(CommandResult.Type.ERROR, status.getType())) {
            commonNotifyError(status, ScenarioSimulationEditorConstants.INSTANCE.undo());
        }
    }

    @Override
    public void onEvent(UnsupportedDMNEvent event) {
        confirmPopupPresenter.show("Unsupported DMN asset", event.getMessage());
    }

    /**
     * Common method to execute the given <code>Command</code> inside the given <code>ScenarioSimulationContext</code>
     * If successful, it adds the command to the <code>ScenarioCommandRegistry</code>, otherwise it fire a new <code>ScenarioNotificationEvent</code>
     * with error details
     * @param context
     * @param command
     */
    protected void commonExecution(ScenarioSimulationContext context, AbstractScenarioSimulationCommand command) {
        final CommandResult<ScenarioSimulationViolation> status = scenarioCommandManager.execute(context, command);
        if (Objects.equals(CommandResult.Type.ERROR, status.getType())) {
            String operation = new StringBuilder()
                    .append("Command ")
                    .append(command.getClass().getSimpleName())
                    .append(" failure")
                    .toString();
            commonNotifyError(status, operation);
        } else if (Objects.equals(CommandResultBuilder.SUCCESS, status) && command.isUndoable()) {
            scenarioCommandRegistry.register(context, command);
        }
    }

    protected void commonNotifyError(CommandResult<ScenarioSimulationViolation> status, String operation) {
        String violations = StreamSupport.stream(status.getViolations().spliterator(), false)
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining("\r\n"));
        String message = new StringBuilder()
                .append(operation + ": " + status.getType())
                .append("\r\n")
                .append(violations)
                .toString();
        notificationEvent.fire(new NotificationEvent(message, NotificationEvent.NotificationType.ERROR));
    }

    protected void registerHandlers() {
        handlerRegistrationList.add(eventBus.addHandler(AppendColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(AppendRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DeleteColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DeleteRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DisableTestToolsEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DuplicateInstanceEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(DuplicateRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(EnableTestToolsEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ImportEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(InsertColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(InsertRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(PrependColumnEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(PrependRowEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(RedoEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ReloadTestToolsEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(RunSingleScenarioEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ScenarioGridReloadEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(ScenarioNotificationEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(SetGridCellValueEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(SetHeaderCellValueEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(SetInstanceHeaderEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(SetPropertyHeaderEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(UndoEvent.TYPE, this));
        handlerRegistrationList.add(eventBus.addHandler(UnsupportedDMNEvent.TYPE, this));
    }
}
