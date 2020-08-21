/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import org.dashbuilder.client.error.DefaultRuntimeErrorCallback;
import org.dashbuilder.client.perspective.NotFoundPerspective;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.shared.model.RuntimeModel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.UncaughtExceptionHandler;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Workbench;

import static org.dashbuilder.client.screens.NotFoundScreen.TARGET_PARAM;

@EntryPoint
@ApplicationScoped
@Bundle("resources/i18n/AppConstants.properties")
public class RuntimeEntryPoint {

    private static final String PERSPECTIVE_PARAM = "perspective";

    private static AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    Workbench workbench;

    @Inject
    RuntimeClientLoader modelLoader;

    @Inject
    PlaceManager placeManager;

    @Inject
    RuntimeCommunication runtimeCommunication;
    
    @Inject
    DefaultRuntimeErrorCallback defaultRuntimeErrorCallback;
    
    private String perspective;

    @PostConstruct
    public void startup() {
        workbench.addStartupBlocker(RuntimeEntryPoint.class);

        Map<String, List<String>> params = Window.Location.getParameterMap();
        boolean isStandalone = params.containsKey("standalone");
        List<String> perspectiveParams = params.get(PERSPECTIVE_PARAM);

        if (isStandalone && perspectiveParams != null && !perspectiveParams.isEmpty()) {
            perspective = perspectiveParams.get(0);
            modelLoader.loadModel(this::foundRuntimeModel,
                                  this::notFound,
                                  this::error);
        } else {
            this.hideLoading();
        }
    }

    private void foundRuntimeModel(RuntimeModel runtimeModel) {
        boolean perspectiveNotFound = runtimeModel.getLayoutTemplates().stream()
                                                  .noneMatch(lt -> lt.getName().equals(perspective));
        if (perspectiveNotFound) {
            notFound();
        } 
        
        this.hideLoading();
    }

    public void notFound() {
        String newUrl = GWT.getHostPageBaseURL() + "?standalone&" +
                        PERSPECTIVE_PARAM + "=" + NotFoundPerspective.ID + "&" +
                        TARGET_PARAM + "=" + perspective;
        DomGlobal.window.history.pushState(null,
                                           "Dashbuilder Runtime | Not Found",
                                           newUrl);
    }

    public void error(Object e, Throwable t) {
        runtimeCommunication.showError(i18n.errorLoadingDashboards(), t);
        this.hideLoading();
    }
    
    @UncaughtExceptionHandler
    public void generalErrorHandling(final Throwable t) {
        defaultRuntimeErrorCallback.error(t);
    }

    private void hideLoading() {
        workbench.removeStartupBlocker(RuntimeEntryPoint.class);
        Element loading = DomGlobal.document.getElementById("loading");
        if (loading != null) {
            loading.remove();
        }
    }

}