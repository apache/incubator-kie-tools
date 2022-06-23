/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.alerts;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class AlertsView
        extends Composite
        implements Alerts.View {

    private final String HIDDEN = "hidden";
    private final String VISIBLE = "visible";
    private final String VISIBILITY = "visibility";

    @Inject
    @DataField
    Button infoButton;

    @Inject
    @DataField
    Span infoText;

    @Inject
    @DataField
    Button warningButton;

    @Inject
    @DataField
    Span warningText;

    @Inject
    @DataField
    Button errorButton;

    @Inject
    @DataField
    Span errorText;

    private Alerts presenter;

    @Override
    public void init(final Alerts presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setInfoText(String text) {
        infoText.setTextContent(text);
    }

    @Override
    public void setInfoTooltip(String text) {
        setTooltip(infoButton, text);
    }

    @Override
    public void setInfoEnabled(boolean enabled) {
        infoButton.setDisabled(!enabled);
    }

    @Override
    public void setInfoVisible(boolean visible) {
        if (visible) {
            infoButton.getStyle().setProperty(VISIBILITY, VISIBLE);
        } else {
            infoButton.getStyle().setProperty(VISIBILITY, HIDDEN);
        }
    }

    @Override
    public void setWarningsText(String text) {
        warningText.setTextContent(text);
    }

    @Override
    public void setWarningsTooltip(String text) {
        setTooltip(warningButton, text);
    }

    @Override
    public void setWarningsEnabled(boolean enabled) {
        warningButton.setDisabled(!enabled);
    }

    @Override
    public void setWarningsVisible(boolean visible) {
        if (visible) {
            warningButton.getStyle().setProperty(VISIBILITY, VISIBLE);
        } else {
            warningButton.getStyle().setProperty(VISIBILITY, HIDDEN);
        }
    }

    @Override
    public void setErrorsText(String text) {
        errorText.setTextContent(text);
    }

    @Override
    public void setErrorsTooltip(String text) {
        setTooltip(errorButton, text);
    }

    @Override
    public void setErrorsEnabled(boolean enabled) {
        errorButton.setDisabled(!enabled);
    }

    @Override
    public void setErrorsVisible(boolean visible) {
        if (visible) {
            errorButton.getStyle().setProperty(VISIBILITY, VISIBLE);
        } else {
            errorButton.getStyle().setProperty(VISIBILITY, HIDDEN);
        }
    }

    @EventHandler("infoButton")
    void onShowInfos(ClickEvent event) {
        presenter.onShowInfos();
    }

    @EventHandler("warningButton")
    void onShowWarnings(ClickEvent event) {
        presenter.onShowWarnings();
    }

    @EventHandler("errorButton")
    void onShowErrors(ClickEvent event) {
        presenter.onShowErrors();
    }

    @PreDestroy
    public void destroy() {
        presenter = null;
    }

    private static void setTooltip(final Button button,
                                   final String text) {
        button.setAttribute("data-placement", "top");
        button.setTitle(text);
    }
}
