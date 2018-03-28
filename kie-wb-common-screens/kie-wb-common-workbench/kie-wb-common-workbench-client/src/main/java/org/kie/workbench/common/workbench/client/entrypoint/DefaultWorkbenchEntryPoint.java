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

package org.kie.workbench.common.workbench.client.entrypoint;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.UncaughtExceptionHandler;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.resources.RoundedCornersResource;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.slf4j.Logger;
import org.uberfire.client.mvp.ActivityBeansCache;

public abstract class DefaultWorkbenchEntryPoint {

    @Inject
    private Logger logger;

    protected Caller<AppConfigService> appConfigService;

    protected ActivityBeansCache activityBeansCache;

    private DefaultWorkbenchErrorCallback defaultErrorCallback = new DefaultWorkbenchErrorCallback();

    @Inject
    public DefaultWorkbenchEntryPoint(Caller<AppConfigService> appConfigService,
                                      ActivityBeansCache activityBeansCache) {
        this.appConfigService = appConfigService;
        this.activityBeansCache = activityBeansCache;
    }

    protected abstract void setupMenu();

    /**
     * Should be overwritten to define settings shortcuts.
     */
    protected void setupAdminPage() {
    }

    @AfterInitialization
    public void startDefaultWorkbench() {
        initializeWorkbench();
    }

    @UncaughtExceptionHandler
    private void handleUncaughtException(Throwable t) {
        defaultErrorCallback.error(null,
                                   t);

        logger.error("Uncaught exception encountered",
                     t);
    }

    void loadPreferences() {
        appConfigService.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(final Map<String, String> response) {
                ApplicationPreferences.setUp(response);
                setupMenu();
                setupAdminPage();
            }
        }).loadPreferences();
    }

    void loadStyles() {
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }

    @AfterInitialization
    public void hideLoadingPopup() {
        @SuppressWarnings("GwtToHtmlReferences")
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {
            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

    protected void initializeWorkbench() {
        loadPreferences();
        loadStyles();
    }
}
