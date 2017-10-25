/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.provider.status.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.provider.status.runtime.actions.RuntimeActionItemPresenter;
import org.guvnor.ala.ui.client.provider.status.runtime.actions.RuntimeActionItemSeparatorPresenter;
import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.widget.pipeline.PipelinePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.stage.StagePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.transition.TransitionPresenter;
import org.guvnor.ala.ui.events.PipelineExecutionChangeEvent;
import org.guvnor.ala.ui.events.PipelineStatusChangeEvent;
import org.guvnor.ala.ui.events.RuntimeChangeEvent;
import org.guvnor.ala.ui.events.StageStatusChangeEvent;
import org.guvnor.ala.ui.model.Pipeline;
import org.guvnor.ala.ui.model.PipelineError;
import org.guvnor.ala.ui.model.PipelineExecutionTrace;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.Runtime;
import org.guvnor.ala.ui.model.RuntimeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.RuntimeStatus;
import org.guvnor.ala.ui.model.Stage;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenterHelper.buildIconStyle;
import static org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenterHelper.buildRuntimeStatus;
import static org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenterHelper.buildStageState;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionAlreadyStoppedMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionConfirmDeleteMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionConfirmDeleteTitle;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionConfirmStopMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionConfirmStopTitle;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionDeleteAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionDeleteSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionShowErrorAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionStopAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionStopSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeConfirmDeleteMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeConfirmDeleteTitle;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeConfirmForcedDeleteMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeConfirmForcedDeleteTitle;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeConfirmStopMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeConfirmStopTitle;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeleteAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeleteFailedMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeleteFailedTitle;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeleteSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeletingForcedMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeletingMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStartAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStartSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStartingMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStopAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStopSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStoppingMessage;
import static org.guvnor.ala.ui.model.RuntimeStatus.READY;
import static org.guvnor.ala.ui.model.RuntimeStatus.RUNNING;
import static org.guvnor.ala.ui.model.RuntimeStatus.STOPPED;

@Dependent
public class RuntimePresenter {

    public interface View
            extends UberElement<RuntimePresenter> {

        void setup(final String name,
                   final String date,
                   final String pipeline);

        void setEndpoint(String endpoint);

        void setStatus(final Collection<String> strings);

        void setStatusTitle(final String title);

        void addExpandedContent(final IsElement element);

        void addActionItem(final IsElement element);

        void clearActionItems();
    }

    private static final String SYSTEM_PIPELINE_NAME = "<system>";

    private final View view;
    private final PipelinePresenter pipelinePresenter;
    private final ManagedInstance<StagePresenter> stagePresenterInstance;
    private final ManagedInstance<TransitionPresenter> transitionPresenterInstance;
    private final ManagedInstance<RuntimeActionItemPresenter> actionItemPresenterInstance;
    private final ManagedInstance<RuntimeActionItemSeparatorPresenter> actionItemSeparatorPresenterInstance;
    private final Caller<RuntimeService> runtimeService;
    protected Event<NotificationEvent> notification;
    private final PopupHelper popupHelper;
    private final TranslationService translationService;

    private final List<Stage> currentStages = new ArrayList<>();
    private final Map<Stage, StagePresenter> stagePresenters = new HashMap<>();
    private final List<TransitionPresenter> currentTransitions = new ArrayList<>();

    private RuntimeListItem item;

    private RuntimeActionItemPresenter startAction;
    private RuntimeActionItemPresenter stopAction;
    private RuntimeActionItemPresenter deleteAction;
    private RuntimeActionItemSeparatorPresenter separator;
    private RuntimeActionItemSeparatorPresenter secondarySeparator;
    private RuntimeActionItemPresenter showErrorAction;

    @Inject
    public RuntimePresenter(final View view,
                            final PipelinePresenter pipelinePresenter,
                            final ManagedInstance<StagePresenter> stagePresenterInstance,
                            final ManagedInstance<TransitionPresenter> transitionPresenterInstance,
                            final ManagedInstance<RuntimeActionItemPresenter> actionItemPresenterInstance,
                            final ManagedInstance<RuntimeActionItemSeparatorPresenter> actionItemSeparatorPresenterInstance,
                            final Caller<RuntimeService> runtimeService,
                            final Event<NotificationEvent> notification,
                            final PopupHelper popupHelper,
                            final TranslationService translationService) {
        this.view = view;
        this.pipelinePresenter = pipelinePresenter;
        this.stagePresenterInstance = stagePresenterInstance;
        this.transitionPresenterInstance = transitionPresenterInstance;
        this.actionItemPresenterInstance = actionItemPresenterInstance;
        this.actionItemSeparatorPresenterInstance = actionItemSeparatorPresenterInstance;
        this.runtimeService = runtimeService;
        this.notification = notification;
        this.popupHelper = popupHelper;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        startAction = newActionItemPresenter();
        stopAction = newActionItemPresenter();
        deleteAction = newActionItemPresenter();
        separator = newSeparatorItem();
        secondarySeparator = newSeparatorItem();
        showErrorAction = newActionItemPresenter();
    }

    @PreDestroy
    public void destroy() {
        clearPipeline();
        actionItemPresenterInstance.destroy(startAction);
        actionItemPresenterInstance.destroy(stopAction);
        actionItemPresenterInstance.destroy(deleteAction);
        actionItemSeparatorPresenterInstance.destroy(separator);
        actionItemSeparatorPresenterInstance.destroy(secondarySeparator);
        actionItemPresenterInstance.destroy(showErrorAction);
    }

    public View getView() {
        return view;
    }

    public void setup(final RuntimeListItem runtimeListItem) {
        this.item = runtimeListItem;
        clearPipeline();
        if (item.isRuntime()) {
            setupRuntime(runtimeListItem);
        } else {
            setupPipelineTrace(runtimeListItem);
        }
        view.addExpandedContent(pipelinePresenter.getView());
    }

    public RuntimeListItem getItem() {
        return item;
    }

    private void setupRuntime(RuntimeListItem item) {
        String itemLabel = item.getItemLabel();
        String pipelineName = SYSTEM_PIPELINE_NAME;
        String createdDate = item.getRuntime().getCreatedDate();
        String endpoint = "";

        Runtime runtime = item.getRuntime();
        PipelineExecutionTrace trace = runtime.getPipelineTrace();

        if (trace != null) {
            pipelineName = trace.getPipeline().getKey().getId();
            setupPipeline(trace);
        }
        view.setup(itemLabel,
                   createdDate,
                   pipelineName);
        if (runtime.getEndpoint() != null) {
            endpoint = runtime.getEndpoint();
        }
        view.setEndpoint(endpoint);
        processRuntimeStatus(runtime);
    }

    private void setupPipelineTrace(RuntimeListItem item) {
        PipelineExecutionTrace trace = item.getPipelineTrace();
        String itemLabel = item.getItemLabel();
        String pipelineName = trace.getPipeline().getKey().getId();
        String createdDate = "";

        view.setup(itemLabel,
                   createdDate,
                   pipelineName);
        setupPipeline(trace);
        processPipelineStatus(trace.getPipelineStatus());
    }

    private void setupPipeline(final PipelineExecutionTrace trace) {
        clearPipeline();
        boolean showStep = true;
        Pipeline pipeline = trace.getPipeline();
        for (int i = 0; showStep && i < pipeline.getStages().size(); i++) {
            Stage stage = pipeline.getStages().get(i);
            PipelineStatus stageStatus = trace.getStageStatus(stage.getName());
            showStep = showStage(stageStatus);
            if (showStep) {
                if (i > 0) {
                    TransitionPresenter transitionPresenter = newTransitionPresenter();
                    currentTransitions.add(transitionPresenter);
                    pipelinePresenter.addStage(transitionPresenter.getView());
                }
                final StagePresenter stagePresenter = newStagePresenter();
                stagePresenter.setup(stage);
                stagePresenter.setState(buildStageState(stageStatus));
                pipelinePresenter.addStage(stagePresenter.getView());
                currentStages.add(stage);
                stagePresenters.put(stage,
                                    stagePresenter);
            }
        }
    }

    private boolean showStage(final PipelineStatus stageStatus) {
        return stageStatus == PipelineStatus.RUNNING ||
                stageStatus == PipelineStatus.FINISHED ||
                stageStatus == PipelineStatus.ERROR ||
                stageStatus == PipelineStatus.STOPPED;
    }

    private void processRuntimeStatus(final Runtime runtime) {
        view.clearActionItems();
        enableActions(true,
                      startAction,
                      stopAction,
                      deleteAction);
        view.addActionItem(startAction.getView());
        view.addActionItem(stopAction.getView());
        view.addActionItem(separator.getView());
        view.addActionItem(deleteAction.getView());

        startAction.setup(translationService.getTranslation(RuntimePresenter_RuntimeStartAction),
                          this::startRuntime);
        stopAction.setup(translationService.getTranslation(RuntimePresenter_RuntimeStopAction),
                         this::stopRuntime);
        deleteAction.setup(translationService.getTranslation(RuntimePresenter_RuntimeDeleteAction),
                           this::deleteRuntime);

        RuntimeStatus runtimeStatus = buildRuntimeStatus(runtime.getStatus());
        if (RUNNING == runtimeStatus) {
            startAction.setEnabled(false);
        }
        if (STOPPED == runtimeStatus || READY == runtimeStatus) {
            stopAction.setEnabled(false);
        }
        view.setStatus(buildIconStyle(runtimeStatus));
        view.setStatusTitle(runtime.getStatus());
    }

    private void processPipelineStatus(final PipelineStatus status) {
        view.clearActionItems();
        enableActions(false,
                      startAction,
                      stopAction,
                      deleteAction,
                      showErrorAction);
        view.addActionItem(stopAction.getView());
        view.addActionItem(separator.getView());
        view.addActionItem(deleteAction.getView());

        stopAction.setup(translationService.getTranslation(RuntimePresenter_PipelineExecutionStopAction),
                         this::stopPipeline);
        deleteAction.setup(translationService.getTranslation(RuntimePresenter_PipelineExecutionDeleteAction),
                           this::deletePipeline);
        showErrorAction.setup(translationService.getTranslation(RuntimePresenter_PipelineExecutionShowErrorAction),
                              this::showPipelineError);

        switch (status) {
            case SCHEDULED:
            case RUNNING:
                stopAction.setEnabled(true);
                break;
            case STOPPED:
                deleteAction.setEnabled(true);
                break;
            case ERROR:
                deleteAction.setEnabled(true);
                view.addActionItem(secondarySeparator.getView());
                view.addActionItem(showErrorAction.getView());
                showErrorAction.setEnabled(true);
                break;
            case FINISHED:
                if (item.getRuntime() == null) {
                    deleteAction.setEnabled(true);
                }
                break;
        }
        view.setStatus(buildIconStyle(status));
        view.setStatusTitle(status.name());
    }

    private void enableActions(boolean enabled,
                               RuntimeActionItemPresenter... actions) {
        Arrays.stream(actions).forEach(action -> action.setEnabled(enabled));
    }

    public void onStageStatusChange(@Observes final StageStatusChangeEvent event) {
        if (isFromCurrentPipeline(event.getPipelineExecutionTraceKey())) {
            PipelineExecutionTrace trace = item.getPipelineTrace();
            Stage currentStage = currentStages.stream().
                    filter(step -> event.getStage().equals(step.getName()))
                    .findFirst()
                    .orElse(null);

            if (currentStage != null) {
                StagePresenter stagePresenter = stagePresenters.get(currentStage);
                stagePresenter.setState(buildStageState(event.getStatus()));
            } else {
                Stage stage = new Stage(item.getPipelineTrace().getPipeline().getKey(),
                                        event.getStage());
                PipelineStatus stageStatus = event.getStatus();
                StagePresenter stagePresenter = newStagePresenter();
                stagePresenter.setup(stage);
                stagePresenter.setState(buildStageState(stageStatus));
                if (!currentStages.isEmpty()) {
                    TransitionPresenter transitionPresenter = newTransitionPresenter();
                    currentTransitions.add(transitionPresenter);
                    pipelinePresenter.addStage(transitionPresenter.getView());
                }
                pipelinePresenter.addStage(stagePresenter.getView());

                currentStages.add(stage);
                stagePresenters.put(stage,
                                    stagePresenter);
            }
            trace.setStageStatus(event.getStage(),
                                 event.getStatus());
        }
    }

    public void onPipelineStatusChange(@Observes final PipelineStatusChangeEvent event) {
        if (isFromCurrentPipeline(event.getPipelineExecutionTraceKey())) {
            processPipelineStatus(event.getStatus());
            if ((PipelineStatus.FINISHED.equals(event.getStatus()) &&
                    !PipelineStatus.FINISHED.equals(item.getPipelineTrace().getPipelineStatus()))
                    || PipelineStatus.ERROR.equals(event.getStatus())) {
                refresh(event.getPipelineExecutionTraceKey());
            }
        }
    }

    public void onPipelineExecutionChange(@Observes final PipelineExecutionChangeEvent event) {
        if (event.isStop() && isFromCurrentPipeline(event.getPipelineExecutionTraceKey())) {
            refresh(event.getPipelineExecutionTraceKey());
        }
    }

    public void onRuntimeChangeEvent(@Observes final RuntimeChangeEvent event) {
        if ((event.isStart() || event.isStop()) && isFromCurrentRuntime(event.getRuntimeKey())) {
            refresh(event.getRuntimeKey());
        }
    }

    protected void refresh(final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        runtimeService.call(getLoadItemSuccessCallback(),
                            getDefaultErrorCallback()).getRuntimeItem(pipelineExecutionTraceKey);
    }

    protected void refresh(final RuntimeKey runtimeKey) {
        runtimeService.call(getLoadItemSuccessCallback(),
                            getDefaultErrorCallback()).getRuntimeItem(runtimeKey);
    }

    private RemoteCallback<RuntimeListItem> getLoadItemSuccessCallback() {
        return runtimeListItem -> {
            if (runtimeListItem != null) {
                setup(runtimeListItem);
            }
        };
    }

    protected void startRuntime() {
        popupHelper.showBusyIndicator(translationService.getTranslation(RuntimePresenter_RuntimeStartingMessage));
        runtimeService.call(getStartRuntimeSuccessCallback(),
                            getDefaultErrorCallback(true)).startRuntime(item.getRuntime().getKey());
    }

    private RemoteCallback<Void> getStartRuntimeSuccessCallback() {
        return aVoid -> {
            popupHelper.hideBusyIndicator();
            notification.fire(new NotificationEvent(translationService.format(RuntimePresenter_RuntimeStartSuccessMessage,
                                                                              item.getRuntime().getKey().getId()),
                                                    NotificationEvent.NotificationType.SUCCESS));
        };
    }

    protected void stopRuntime() {
        confirmAndExecute(translationService.getTranslation(RuntimePresenter_RuntimeConfirmStopTitle),
                          translationService.getTranslation(RuntimePresenter_RuntimeConfirmStopMessage),
                          () -> {
                              popupHelper.showBusyIndicator(translationService.getTranslation(RuntimePresenter_RuntimeStoppingMessage));
                              runtimeService.call(getStopRuntimeSuccessCallback(),
                                                  getDefaultErrorCallback(true)).stopRuntime(item.getRuntime().getKey());
                          });
    }

    protected RemoteCallback<Void> getStopRuntimeSuccessCallback() {
        return aVoid -> {
            popupHelper.hideBusyIndicator();
            notification.fire(new NotificationEvent(translationService.format(RuntimePresenter_RuntimeStopSuccessMessage,
                                                                              item.getRuntime().getKey().getId()),
                                                    NotificationEvent.NotificationType.SUCCESS));
        };
    }

    protected void deleteRuntime() {
        confirmAndExecute(translationService.getTranslation(RuntimePresenter_RuntimeConfirmDeleteTitle),
                          translationService.getTranslation(RuntimePresenter_RuntimeConfirmDeleteMessage),
                          () -> {
                              popupHelper.showBusyIndicator(translationService.getTranslation(RuntimePresenter_RuntimeDeletingMessage));
                              runtimeService.call(getDeleteRuntimeSuccessCallback(),
                                                  getDeleteRuntimeErrorCallback()).deleteRuntime(item.getRuntime().getKey(),
                                                                                                 false);
                          }
        );
    }

    protected void forceDeleteRuntime() {
        confirmAndExecute(translationService.getTranslation(RuntimePresenter_RuntimeConfirmForcedDeleteTitle),
                          translationService.getTranslation(RuntimePresenter_RuntimeConfirmForcedDeleteMessage),
                          () -> {
                              popupHelper.showBusyIndicator(translationService.getTranslation(RuntimePresenter_RuntimeDeletingForcedMessage));
                              runtimeService.call(getDeleteRuntimeSuccessCallback(),
                                                  getDefaultErrorCallback(true)).deleteRuntime(item.getRuntime().getKey(),
                                                                                               true);
                          }
        );
    }

    private RemoteCallback<Void> getDeleteRuntimeSuccessCallback() {
        return aVoid -> {
            popupHelper.hideBusyIndicator();
            notification.fire(new NotificationEvent(translationService.format(RuntimePresenter_RuntimeDeleteSuccessMessage,
                                                                              item.getRuntime().getKey().getId()),
                                                    NotificationEvent.NotificationType.SUCCESS));
        };
    }

    private ErrorCallback<Message> getDeleteRuntimeErrorCallback() {
        return (message, throwable) -> {
            popupHelper.hideBusyIndicator();
            confirmAndExecute(translationService.getTranslation(RuntimePresenter_RuntimeDeleteFailedTitle),
                              translationService.format(RuntimePresenter_RuntimeDeleteFailedMessage,
                                                        throwable.getMessage()),
                              this::forceDeleteRuntime);
            return false;
        };
    }

    protected void stopPipeline() {
        confirmAndExecute(translationService.getTranslation(RuntimePresenter_PipelineExecutionConfirmStopTitle),
                          translationService.getTranslation(RuntimePresenter_PipelineExecutionConfirmStopMessage),
                          () -> {
                              if (item.isRuntime()) {
                                  popupHelper.showInformationPopup(translationService.getTranslation(RuntimePresenter_PipelineExecutionAlreadyStoppedMessage));
                              } else {
                                  runtimeService.call(getStopPipelineSuccessCallback(),
                                                      getDefaultErrorCallback()).stopPipelineExecution(item.getPipelineTrace().getKey());
                              }
                          });
    }

    private RemoteCallback<Void> getStopPipelineSuccessCallback() {
        return aVoid -> notification.fire(new NotificationEvent(translationService.format(RuntimePresenter_PipelineExecutionStopSuccessMessage,
                                                                                          item.getPipelineTrace().getKey().getId()),
                                                                NotificationEvent.NotificationType.SUCCESS));
    }

    protected void deletePipeline() {
        confirmAndExecute(translationService.getTranslation(RuntimePresenter_PipelineExecutionConfirmDeleteTitle),
                          translationService.getTranslation(RuntimePresenter_PipelineExecutionConfirmDeleteMessage),
                          () -> runtimeService.call(getDeletePipelineSuccessCallback(),
                                                    getDefaultErrorCallback()).deletePipelineExecution(item.getPipelineTrace().getKey()));
    }

    private RemoteCallback<Void> getDeletePipelineSuccessCallback() {
        return aVoid -> notification.fire(new NotificationEvent(translationService.format(RuntimePresenter_PipelineExecutionDeleteSuccessMessage,
                                                                                          item.getPipelineTrace().getKey().getId()),
                                                                NotificationEvent.NotificationType.SUCCESS));
    }

    protected void showPipelineError() {
        PipelineError error = item.getPipelineTrace().getPipelineError();
        popupHelper.showErrorPopup(error.getError(),
                                   error.getErrorDetail());
    }

    private void confirmAndExecute(final String title,
                                   final String message,
                                   final Command yesCommand) {
        popupHelper.showYesNoPopup(title,
                                   message,
                                   yesCommand,
                                   () -> {
                                   });
    }

    private ErrorCallback<Message> getDefaultErrorCallback() {
        return popupHelper.getPopupErrorCallback();
    }

    private ErrorCallback<Message> getDefaultErrorCallback(final boolean hideBusyIndicator) {
        return (message, throwable) -> {
            if (hideBusyIndicator) {
                popupHelper.hideBusyIndicator();
            }
            return popupHelper.getPopupErrorCallback().error(message,
                                                             throwable);
        };
    }

    private boolean isFromCurrentPipeline(final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        return item != null &&
                !item.isRuntime() &&
                item.getPipelineTrace().getKey().equals(pipelineExecutionTraceKey);
    }

    private boolean isFromCurrentRuntime(final RuntimeKey runtimeKey) {
        return item != null &&
                item.isRuntime() &&
                item.getRuntime().getKey().equals(runtimeKey);
    }

    private void clearPipeline() {
        pipelinePresenter.clearStages();
        currentStages.clear();
        stagePresenters.values().forEach(stagePresenterInstance::destroy);
        stagePresenters.clear();
        currentTransitions.forEach(transitionPresenterInstance::destroy);
        currentTransitions.clear();
    }

    protected StagePresenter newStagePresenter() {
        return stagePresenterInstance.get();
    }

    protected TransitionPresenter newTransitionPresenter() {
        return transitionPresenterInstance.get();
    }

    protected RuntimeActionItemPresenter newActionItemPresenter() {
        return actionItemPresenterInstance.get();
    }

    protected RuntimeActionItemSeparatorPresenter newSeparatorItem() {
        return actionItemSeparatorPresenterInstance.get();
    }
}