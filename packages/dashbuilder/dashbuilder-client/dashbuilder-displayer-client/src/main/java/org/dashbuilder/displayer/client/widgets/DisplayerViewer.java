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
package org.dashbuilder.displayer.client.widgets;

import java.util.function.Consumer;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.IOC;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class DisplayerViewer {

    private static final int ERROR_RETRY_MS = 4000;
    protected DisplayerSettings displayerSettings;
    protected HTMLElement container;
    protected Displayer displayer;
    @Inject
    protected DisplayerErrorWidget errorWidget;
    protected boolean error = true;
    protected DisplayerLocator displayerLocator;
    ClientRuntimeError displayerInitializationError;
    CommonConstants i18n = CommonConstants.INSTANCE;
    @Inject
    Elemental2DomUtil domUtil;

    DisplayerListener displayerListener = new AbstractDisplayerListener() {

        public void onDraw(Displayer displayer) {
            if (error) {
                show();
            }
        }

        public void onRedraw(Displayer displayer) {
            if (error) {
                show();
            }
        }

        public void onError(Displayer displayer,
                            ClientRuntimeError error) {
            error(error);
            retry();
        }
    };
    private double currentRetry;

    @Inject
    public DisplayerViewer(DisplayerLocator displayerLocator) {
        this.displayerLocator = displayerLocator;
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public Displayer getDisplayer() {
        return displayer;
    }

    public void init(DisplayerSettings displayerSettings) {
        container = (HTMLElement) DomGlobal.document.createElement("div");
        try {
            // Lookup the displayer
            checkNotNull("displayerSettings",
                    displayerSettings);
            this.displayerSettings = displayerSettings;
            this.displayer = displayerLocator.lookupDisplayer(displayerSettings);
            this.displayer.addListener(displayerListener);
            // Make the displayer visible
            show();
        } catch (Exception e) {
            displayerInitializationError = new ClientRuntimeError(e);
            error(displayerInitializationError);
            retry();
        }
    }

    protected void show() {
        // Add the displayer into a container
        domUtil.removeAllElementChildren(container);
        var element = displayer.getElement();
        container.appendChild(element);
        error = false;
    }

    public Displayer draw() {
        if (displayerInitializationError != null) {
            error(displayerInitializationError, i18n.displayerviewer_displayer_not_created());
        } else {
            try {
                // Draw the displayer
                displayer.draw();
                if (error) {
                    error = false;
                    show();
                }
            } catch (Exception e) {
                error(new ClientRuntimeError(e));
                retry();
            }
        }
        return displayer;
    }

    public void error(ClientRuntimeError e) {
        error(e, e.getMessage());
    }

    public void error(ClientRuntimeError e, String message) {
        domUtil.removeAllElementChildren(container);
        container.appendChild(errorWidget.getElement());
        errorWidget.show(message, e.getThrowable());
        error = true;
        GWT.log(e.getMessage(),
                e.getThrowable());
    }

    public void retry() {
        Consumer<Object> initAction = e -> this.init(displayerSettings);
        Consumer<Object> drawAction = e -> this.draw();
        Consumer<Object> retryAction;
        if (displayerInitializationError != null) {
            displayerInitializationError = null;
            retryAction = initAction.andThen(drawAction);
        } else {
            retryAction = drawAction;
        }
        DomGlobal.clearTimeout(currentRetry);
        currentRetry = DomGlobal.setTimeout(retryAction::accept, ERROR_RETRY_MS);
    }

    public HTMLElement getDisplayerContainer() {
        return container;
    }

    @PreDestroy
    void destroy() {
        DomGlobal.clearTimeout(currentRetry);
        if (container != null) {
            container.remove();
        }
        if (displayer != null) {
            displayer.close();
            IOC.getBeanManager().destroyBean(displayer);
            this.displayer = null;
        }
    }
}
