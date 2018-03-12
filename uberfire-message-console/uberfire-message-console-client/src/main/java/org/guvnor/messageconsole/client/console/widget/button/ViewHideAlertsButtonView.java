/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.client.console.widget.button;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ViewHideAlertsButtonView implements ViewHideAlertsButtonPresenter.View {

    private ViewHideAlertsButtonPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("view-alerts")
    private HTMLButtonElement viewAlerts;

    @Inject
    @DataField("hide-alerts")
    private HTMLButtonElement hideAlerts;

    @Override
    public void init(final ViewHideAlertsButtonPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setAlertsActive(boolean active) {
        viewAlerts.hidden = active;
        hideAlerts.hidden = !active;
    }

    @Override
    public void addCssClassToButtons(final String cssClass) {
        viewAlerts.classList.add(cssClass);
        hideAlerts.classList.add(cssClass);
    }

    @EventHandler("view-alerts")
    public void viewAlerts(final ClickEvent event) {
        presenter.viewAlerts();
    }

    @EventHandler("hide-alerts")
    public void hideAlerts(final ClickEvent event) {
        presenter.hideAlerts();
    }
}
