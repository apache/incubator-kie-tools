/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.popups;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class RestorePopUpPresenter {

    protected ParameterizedCommand<String> command;
    private BusyIndicatorView busyIndicatorView;

    private Caller<VersionService> versionService;

    private Event<RestoreEvent> restoreEvent;

    private RestoreUtil restoreUtil;

    private View view;

    @Inject
    public RestorePopUpPresenter(View view,
                                 BusyIndicatorView busyIndicatorView,
                                 Caller<VersionService> versionService,
                                 Event<RestoreEvent> restoreEvent,
                                 RestoreUtil restoreUtil) {
        this.view = view;
        this.busyIndicatorView = busyIndicatorView;
        this.versionService = versionService;
        this.restoreEvent = restoreEvent;
        this.restoreUtil = restoreUtil;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void restore() {
        command.execute(view.getComment());
        view.hide();
    }

    public void show(final ObservablePath currentPath,
                     final String currentVersionRecordUri,
                     final String branchName) {
        command = restoreCommand(currentPath,
                                 currentVersionRecordUri,
                                 branchName);
        view.show();
    }

    public void cancel() {
        view.hide();
    }

    private HasBusyIndicatorDefaultErrorCallback errorCallback() {
        return new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView);
    }

    private RemoteCallback<Path> successCallback(final String currentVersionRecordUri) {
        return restored -> {
            busyIndicatorView.hideBusyIndicator();
            restoreEvent.fire(new RestoreEvent(restoreUtil.createObservablePath(
                    restored,
                    currentVersionRecordUri)));
        };
    }

    public ParameterizedCommand<String> restoreCommand(final ObservablePath currentPath,
                                                       final String currentVersionRecordUri,
                                                       final String branchName) {
        return comment -> {
            busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Restoring());
            versionService.call(successCallback(currentVersionRecordUri),
                                errorCallback()).restore(currentPath,
                                                         comment, branchName);
        };
    }

    public interface View extends UberElement<RestorePopUpPresenter> {
        void show();
        void hide();
        String getComment();
    }
}
