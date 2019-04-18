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

package org.kie.workbench.common.workbench.client.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@Dependent
public class TestRunnerReportingPanel
        implements TestRunnerReportingView.Presenter {

    private TestRunnerReportingView view;
    private List<SystemMessage> systemMessages = new ArrayList<>();

    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    public TestRunnerReportingPanel() {
        //Zero argument constructor for CDI
    }

    @Inject
    public TestRunnerReportingPanel(final TestRunnerReportingView view,
                                    final Event<PublishBatchMessagesEvent> publishBatchMessagesEvent) {
        this.view = view;
        this.publishBatchMessagesEvent = publishBatchMessagesEvent;
        view.setPresenter(this);

        view.resetDonut();
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    public void reset() {
        systemMessages = new ArrayList<>();
        view.reset();
    }

    @Override
    public void onViewAlerts() {

        PublishBatchMessagesEvent messagesEvent = new PublishBatchMessagesEvent();
        messagesEvent.setCleanExisting(true);
        messagesEvent.setMessagesToPublish(systemMessages);
        publishBatchMessagesEvent.fire(messagesEvent);
    }

    public void onTestRun(final TestResultMessage testResultMessage) {
        reset();

        if (testResultMessage.wasSuccessful()) {
            view.showSuccess();
        } else {
            if (testResultMessage.getFailures() != null) {
                systemMessages = testResultMessage.getFailures().stream().map(this::convert).collect(Collectors.toList());
            }
            view.showFailure();
        }

        int failures = testResultMessage.getFailures().size();
        view.showSuccessFailureDiagram((testResultMessage.getRunCount() - failures),
                                       failures);

        view.setRunStatus(getCompletedAt(),
                          getScenariosRun(testResultMessage),
                          getDuration(testResultMessage));
    }

    private SystemMessage convert(final Failure failure) {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setMessageType("TestResults");
        systemMessage.setLevel(Level.ERROR);
        systemMessage.setText(makeMessage(failure));
        return systemMessage;
    }

    private String getCompletedAt() {
        DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss.SSS");
        return timeFormat.format(new Date());
    }

    private String getScenariosRun(final TestResultMessage testResultMessage) {
        return String.valueOf(testResultMessage.getRunCount());
    }

    private String getDuration(final TestResultMessage testResultMessage) {
        final Long runTime = testResultMessage.getRunTime();
        final Date runtime = new Date(runTime);

        String milliseconds = formatMilliseconds(DateTimeFormat.getFormat("SSS").format(runtime)) + " milliseconds";
        String seconds = DateTimeFormat.getFormat("s").format(runtime) + " seconds";
        String minutes = DateTimeFormat.getFormat("m").format(runtime) + " minutes";

        if (runTime < 1000) {
            return milliseconds;
        } else if (runTime < 60000) {
            return seconds + " and " + milliseconds;
        } else {
            return minutes + " and " + seconds;
        }
    }

    String formatMilliseconds(final String originalFormat) {
        return originalFormat.replaceFirst("^0+(?!$)", "");
    }

    private String makeMessage(Failure failure) {
        final String displayName = failure.getDisplayName();
        final String message = failure.getMessage();
        return displayName + (!(message == null || message.isEmpty()) ? " : " + message : "");
    }
}
