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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.defaulteditor.client.editor.resources.i18n.GuvnorDefaultEditorConstants;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorNewFileUpload;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewFileUploader
        extends DefaultNewResourceHandler {

    private PlaceManager placeManager;
    private DefaultEditorNewFileUpload options;
    private AnyResourceTypeDefinition resourceType;
    private BusyIndicatorView busyIndicatorView;
    private Caller<KieModuleService> moduleService;

    public NewFileUploader() {
        //Zero-argument constructor for CDI proxies
    }

    @Inject
    public NewFileUploader(final PlaceManager placeManager,
                           final DefaultEditorNewFileUpload options,
                           final AnyResourceTypeDefinition resourceType,
                           final BusyIndicatorView busyIndicatorView,
                           final Caller<KieModuleService> moduleService) {
        this.placeManager = placeManager;
        this.options = options;
        this.resourceType = resourceType;
        this.busyIndicatorView = busyIndicatorView;
        this.moduleService = moduleService;
    }

    @PostConstruct
    private void setupExtensions() {
        extensions.add(Pair.newPair("", // not needed DefaultEditorNewFileUpload describes the extension itself
                                    options));
    }

    @Override
    public String getDescription() {
        return GuvnorDefaultEditorConstants.INSTANCE.NewFileDescription();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create(final org.guvnor.common.services.project.model.Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {

        //See https://bugzilla.redhat.com/show_bug.cgi?id=1091204
        //If the User-provided file name has an extension use that; otherwise use the same extension as the original (OS FileSystem) extension
        String targetFileName;
        String extension;
        final String originalFileName = options.getFormFileName();
        final String providedFileName = baseFileName;

        if (originalFileName == null || "".equals(originalFileName)) {
            Window.alert(CommonConstants.INSTANCE.UploadSelectAFile());
            return;
        }

        if (providedFileName.contains(".")) {
            targetFileName = providedFileName;
            extension = getExtension(providedFileName);
        } else {
            extension = getExtension(originalFileName);
            targetFileName = providedFileName + "." + extension;
        }

        busyIndicatorView.showBusyIndicator(GuvnorDefaultEditorConstants.INSTANCE.Uploading());

        moduleService.call(getResolvePathSuccessCallback(targetFileName, presenter),
                            new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).resolveDefaultPath(pkg, extension);
    }

    private RemoteCallback<Path> getResolvePathSuccessCallback(final String targetFileName,
                                                               final NewResourcePresenter presenter) {
        return path -> {
            final Path newPath = PathFactory.newPathBasedOn(targetFileName,
                                                            encode(path.toURI() + "/" + targetFileName),
                                                            path);

            options.setFolderPath(path);
            options.setFileName(targetFileName);

            options.upload(result -> {
                               busyIndicatorView.hideBusyIndicator();
                               presenter.complete();
                               notifySuccess();
                               newResourceSuccessEvent.fire(new NewResourceSuccessEvent(newPath));
                               placeManager.goTo(newPath);
                           },
                           result -> {
                                busyIndicatorView.hideBusyIndicator();
                                if (result.equals("CONFLICT")) {
                                    notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother(),
                                                                                 NotificationEvent.NotificationType.ERROR));
                                }
                           });
        };
    }

    String encode(final String uri) {
        return URL.encode(uri);
    }

    private String getExtension(final String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    @Override
    public boolean isProjectAsset() {
        return false;
    }
}