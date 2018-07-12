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

import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.framework.AbstractRpcProxy;
import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.UncaughtExceptionHandler;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.resources.RoundedCornersResource;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.uberfire.client.mvp.ActivityBeansCache;

public abstract class DefaultWorkbenchEntryPoint {

    protected Caller<AppConfigService> appConfigService;

    protected ActivityBeansCache activityBeansCache;

    private DefaultWorkbenchErrorCallback defaultWorkbenchErrorCallback;

    @Inject
    private GenericErrorPopup genericErrorPopup;

    @Inject
    public DefaultWorkbenchEntryPoint(final Caller<AppConfigService> appConfigService,
                                      final ActivityBeansCache activityBeansCache,
                                      final DefaultWorkbenchErrorCallback defaultWorkbenchErrorCallback) {

        this.appConfigService = appConfigService;
        this.activityBeansCache = activityBeansCache;
        this.defaultWorkbenchErrorCallback = defaultWorkbenchErrorCallback;
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
    private void handleUncaughtException(final Throwable t) {
        defaultWorkbenchErrorCallback.error(t);
    }

    void loadPreferences() {
        appConfigService.call((final Map<String, String> response) -> {
            ApplicationPreferences.setUp(response);
            setupMenu();
            setupAdminPage();
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
        setupRpcDefaultErrorCallback();
        loadPreferences();
        loadStyles();
    }

    private void setupRpcDefaultErrorCallback() {
        //FIXME: Some RPC calls are made before this callback has the chance to be registered. Investigate and fix.
        final ErrorCallback<Message> originalRpcErrorCallback = AbstractRpcProxy.DEFAULT_RPC_ERROR_CALLBACK;

        AbstractRpcProxy.DEFAULT_RPC_ERROR_CALLBACK = (final Message m, final Throwable t) -> {

            //Removing parameter/return information because sensitive data might be encoded.
            //Also, this is safe because this message is not going to be used anymore.
            m.remove("MethodParms");
            m.remove("MethodReply");

            genericErrorPopup.setup(BusToolsCli.encodeMessage(m));
            genericErrorPopup.show();

            //This will send a message to Errai Bus' error channel.
            return originalRpcErrorCallback.error(m, t);
        };
    }
}
