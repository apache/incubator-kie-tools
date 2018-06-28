/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Items that require a Name and Path
 */
public abstract class DefaultNewResourceHandler implements NewResourceHandler {

    protected final List<Pair<String, ? extends IsWidget>> extensions = new LinkedList<Pair<String, ? extends IsWidget>>();

    @Inject
    protected WorkspaceProjectContext context;

    @Inject
    protected Caller<KieModuleService> moduleService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Event<NotificationEvent> notificationEvent;

    @Inject
    protected Event<NewResourceSuccessEvent> newResourceSuccessEvent;

    @Inject
    protected BusyIndicatorView busyIndicatorView;

    //Package-protected constructor for tests. In an ideal world we'd move to Constructor injection
    //however that would require every sub-class of this abstract class to also have Constructor
    //injection.. and that's a lot of refactoring just to be able to test.
    DefaultNewResourceHandler(final WorkspaceProjectContext context,
                              final Caller<KieModuleService> moduleService,
                              final Caller<ValidationService> validationService,
                              final PlaceManager placeManager,
                              final Event<NotificationEvent> notificationEvent,
                              final BusyIndicatorView busyIndicatorView) {
        this.context = context;
        this.moduleService = moduleService;
        this.validationService = validationService;
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.busyIndicatorView = busyIndicatorView;
    }

    public DefaultNewResourceHandler() {
        //Zero argument constructor for CDI proxies
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        return this.extensions;
    }

    @Override
    public void validate(final String baseFileName,
                         final ValidatorWithReasonCallback callback) {

        final String fileName = buildFileName(baseFileName,
                                              getResourceType());

        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean response) {
                if (Boolean.TRUE.equals(response)) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(CommonConstants.INSTANCE.InvalidFileName0(baseFileName));
                }
            }
        }).isFileNameValid(fileName);
    }

    @Override
    public void acceptContext(final Callback<Boolean, Void> callback) {
        callback.onSuccess(context != null && context.getActiveModule().isPresent());
    }

    @Override
    public Command getCommand(final NewResourcePresenter newResourcePresenter) {
        return new Command() {
            @Override
            public void execute() {
                newResourcePresenter.show(DefaultNewResourceHandler.this);
            }
        };
    }

    protected String buildFileName(final String baseFileName,
                                   final ResourceTypeDefinition resourceType) {
        final String suffix = resourceType.getSuffix();
        final String prefix = resourceType.getPrefix();
        final String extension = !(suffix == null || "".equals(suffix)) ? "." + resourceType.getSuffix() : "";
        if (baseFileName.endsWith(extension)) {
            return prefix + baseFileName;
        }
        return prefix + baseFileName + extension;
    }

    protected void notifySuccess() {
        notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCreatedSuccessfully(),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    protected RemoteCallback<Path> getSuccessCallback(final NewResourcePresenter presenter) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                busyIndicatorView.hideBusyIndicator();
                presenter.complete();
                notifySuccess();
                newResourceSuccessEvent.fire(new NewResourceSuccessEvent(path));
                placeManager.goTo(path);
            }
        };
    }

    @Override
    public boolean canCreate() {
        return true;
    }
}
