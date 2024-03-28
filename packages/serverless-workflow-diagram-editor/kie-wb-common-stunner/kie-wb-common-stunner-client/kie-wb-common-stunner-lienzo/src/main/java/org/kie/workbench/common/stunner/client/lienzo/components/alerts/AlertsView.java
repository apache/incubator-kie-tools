/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.lienzo.components.alerts;

import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.DataField;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.EventHandler;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.ForEvent;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.Templated;

@Dependent
@Templated
public class AlertsView
        implements Alerts.View {

    private final String HIDDEN = "hidden";
    private final String VISIBLE = "visible";
    private final String VISIBILITY = "visibility";

    @Inject
    @DataField
    HTMLButtonElement infoButton;

    @Inject
    @DataField
    @Named("span")
    HTMLElement infoText;

    @Inject
    @DataField
    HTMLButtonElement warningButton;

    @Inject
    @DataField
    @Named("span")
    HTMLElement warningText;

    @Inject
    @DataField
    HTMLButtonElement errorButton;

    @Inject
    @DataField
    @Named("span")
    HTMLElement errorText;

    private Alerts presenter;

    @Override
    public void init(final Alerts presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setInfoText(String text) {
        infoText.textContent = text;
    }

    @Override
    public void setInfoTooltip(String text) {
        setTooltip(infoButton, text);
    }

    @Override
    public void setInfoEnabled(boolean enabled) {
        infoButton.disabled = !enabled;
    }

    @Override
    public void setInfoVisible(boolean visible) {
        if (visible) {
            infoButton.style.setProperty(VISIBILITY, VISIBLE);
        } else {
            infoButton.style.setProperty(VISIBILITY, HIDDEN);
        }
    }

    @Override
    public void setWarningsText(String text) {
        warningText.textContent = text;
    }

    @Override
    public void setWarningsTooltip(String text) {
        setTooltip(warningButton, text);
    }

    @Override
    public void setWarningsEnabled(boolean enabled) {
        warningButton.disabled = !enabled;
    }

    @Override
    public void setWarningsVisible(boolean visible) {
        if (visible) {
            warningButton.style.setProperty(VISIBILITY, VISIBLE);
        } else {
            warningButton.style.setProperty(VISIBILITY, HIDDEN);
        }
    }

    @Override
    public void setErrorsText(String text) {
        errorText.textContent = text;
    }

    @Override
    public void setErrorsTooltip(String text) {
        setTooltip(errorButton, text);
    }

    @Override
    public void setErrorsEnabled(boolean enabled) {
        errorButton.disabled = !enabled;
    }

    @Override
    public void setErrorsVisible(boolean visible) {
        if (visible) {
            errorButton.style.setProperty(VISIBILITY, VISIBLE);
        } else {
            errorButton.style.setProperty(VISIBILITY, HIDDEN);
        }
    }

    @EventHandler("infoButton")
    void onShowInfos(@ForEvent("click") Event event) {
        presenter.onShowInfos();
    }

    @EventHandler("warningButton")
    void onShowWarnings(@ForEvent("click") Event event) {
        presenter.onShowWarnings();
    }

    @EventHandler("errorButton")
    void onShowErrors(@ForEvent("click") Event event) {
        presenter.onShowErrors();
    }

    @PreDestroy
    public void destroy() {
        presenter = null;
    }

    private static void setTooltip(final HTMLButtonElement button,
                                   final String text) {
        button.setAttribute("data-placement", "top");
        button.title = text;
    }
}
