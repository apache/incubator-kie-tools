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

package org.kie.workbench.common.screens.datamodeller.client.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ResourceOptions;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.javaeditor.client.resources.JavaEditorResources;
import org.kie.workbench.common.screens.javaeditor.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.javaeditor.client.type.JavaResourceType;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewJavaFileTextHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DataModelerService> dataModelerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private JavaResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private SyncBeanManager iocBeanManager;

    private List<ResourceOptions> resourceOptions = new ArrayList<ResourceOptions>();

    @Inject
    private DomainHandlerRegistry domainHandlerRegistry;

    @PostConstruct
    private void setupExtensions() {

        ResourceOptions options;
        for (DomainHandler handler : domainHandlerRegistry.getDomainHandlers()) {
            options = handler.getResourceOptions(false);
            if (options != null) {
                resourceOptions.add(options);
                extensions.add(new Pair<String, Widget>(handler.getName(),
                                                        options.getWidget()));
            }
        }
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newJavaFile();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(JavaEditorResources.INSTANCE.images().typeJava());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create(final org.guvnor.common.services.project.model.Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {

        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());

        Map<String, Object> params = new HashMap<String, Object>();
        for (ResourceOptions options : resourceOptions) {
            params.putAll(options.getOptions());
        }

        dataModelerService.call(getSuccessCallback(presenter),
                                new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).createJavaFile(
                pkg.getPackageMainSrcPath(),
                buildFileName(baseFileName,
                              resourceType),
                "",
                params);
    }

    @Override
    public void validate(final String javaFileName,
                         final ValidatorWithReasonCallback callback) {

        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean response) {
                if (Boolean.TRUE.equals(response)) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(CommonConstants.INSTANCE.InvalidFileName0(javaFileName));
                }
            }
        }).isJavaFileNameValid(javaFileName + ".java");
    }

    @Override
    public Command getCommand(NewResourcePresenter newResourcePresenter) {
        for (ResourceOptions options : resourceOptions) {
            options.restoreOptionsDefaults();
        }
        return super.getCommand(newResourcePresenter);
    }

    @Override
    public boolean supportsDefaultPackage() {
        return false;
    }

    @Override
    public int order() {
        return -10;
    }
}
