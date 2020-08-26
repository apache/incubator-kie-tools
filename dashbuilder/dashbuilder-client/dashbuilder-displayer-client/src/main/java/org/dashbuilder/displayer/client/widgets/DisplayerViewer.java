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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class DisplayerViewer extends Composite {

    protected DisplayerSettings displayerSettings;
    protected Panel container = new FlowPanel();
    protected Label label = new Label();
    protected Displayer displayer;
    protected Boolean isShowRendererSelector = false;
    @Inject
    protected DisplayerErrorWidget errorWidget;
    protected boolean error = true;
    protected DisplayerLocator displayerLocator;
    protected RendererSelector rendererSelector;
    ClientRuntimeError displayerInitializationError;
    CommonConstants i18n = CommonConstants.INSTANCE;

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
        }
    };
    

    @Inject
    public DisplayerViewer(DisplayerLocator displayerLocator,
                           RendererSelector rendererSelector) {
        this.displayerLocator = displayerLocator;
        this.rendererSelector = rendererSelector;
        initWidget(container);
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public void setIsShowRendererSelector(Boolean isShowRendererSelector) {
        this.isShowRendererSelector = isShowRendererSelector;
    }

    public Displayer getDisplayer() {
        return displayer;
    }

    public void init(DisplayerSettings displayerSettings) {
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
        }
    }

    protected void show() {
        // Add the displayer into a container
        container.clear();
        final FlowPanel displayerContainer = new FlowPanel();
        displayerContainer.add(displayer);

        // Add the renderer selector (if enabled)
        if (isShowRendererSelector) {
            rendererSelector.init(displayerSettings,
                                  RendererSelector.SelectorType.TAB,
                                  300,
                                  new Command() {
                                      public void execute() {
                                          displayerSettings.setRenderer(rendererSelector.getRendererLibrary().getUUID());
                                          displayer = displayerLocator.lookupDisplayer(displayerSettings);
                                          displayer.draw();

                                          displayerContainer.clear();
                                          displayerContainer.add(displayer);
                                      }
                                  });
            container.add(rendererSelector);
        }
        container.add(displayerContainer);
        error = false;
    }

    public Displayer draw() {
        if (displayerInitializationError != null ) {
            error(displayerInitializationError, i18n.displayerviewer_displayer_not_created());
        } else {
            try {
                // Draw the displayer
                displayer.draw();
            } catch (Exception e) {
                error(new ClientRuntimeError(e));
            }
        }
        return displayer;
    }

    public Displayer redraw() {
        try {
            checkNotNull("displayerSettings",
                         displayerSettings);
            checkNotNull("displayer",
                         displayer);

            displayer.setDisplayerSettings(displayerSettings);
            displayer.redraw();
        } catch (Exception e) {
            error(new ClientRuntimeError(e));
        }
        return displayer;
    }

    
    public void error(ClientRuntimeError e) {
        String message = e.getMessage();
        if (e.getThrowable() != null) {
            message = e.getThrowable().getMessage();
        }
        error(e, message);
    }
    
    public void error(ClientRuntimeError e, String message) {
        container.clear();
        container.add(errorWidget);
        errorWidget.show(message, e.getThrowable());
        error = true;
        GWT.log(e.getMessage(),
                e.getThrowable());
    }
}
