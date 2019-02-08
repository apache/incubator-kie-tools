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

package org.kie.workbench.common.screens.library.client.screens.project.actions;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLIElement;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ProjectMainActionsViewImpl implements ProjectMainActionsView,
                                                   IsElement {

    private static final String DISABLED_CLASS = "disabled";

    @Inject
    @DataField
    private HTMLDivElement alerts;

    @Inject
    @DataField
    private HTMLButtonElement build;

    @Inject
    @DataField
    private HTMLButtonElement buildCaret;

    @Inject
    @DataField
    private HTMLAnchorElement install;

    @Inject
    @DataField
    private HTMLButtonElement deploy;

    @Inject
    @DataField
    private HTMLButtonElement deployCaret;

    @Inject
    @DataField
    private HTMLLIElement redeployLI;

    @Inject
    @DataField
    private HTMLAnchorElement redeploy;

    @Inject
    private ViewHideAlertsButtonPresenter viewHideAlertsButtonPresenter;

    private Presenter presenter;

    private boolean redeployEnabled = true;

    @PostConstruct
    public void init() {
        alerts.appendChild(viewHideAlertsButtonPresenter.getView().getElement());
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setBuildDropDownEnabled(boolean enabled) {
        this.build.disabled = !enabled;
        this.buildCaret.disabled = !enabled;
        if (enabled) {
            this.install.classList.remove(DISABLED_CLASS);
        } else {
            this.install.classList.add(DISABLED_CLASS);
        }
    }

    @Override
    public void setBuildAndDeployDropDownEnabled(boolean enabled) {
        this.deploy.disabled = !enabled;
        this.deployCaret.disabled = !enabled;
    }

    @Override
    public void setRedeployEnabled(boolean enabled) {
        if (enabled) {
            this.redeployLI.classList.remove(DISABLED_CLASS);
        } else {
            this.redeployLI.classList.add(DISABLED_CLASS);
        }
        redeployEnabled = enabled;
    }

    @EventHandler("build")
    public void onBuild(ClickEvent clickEvent) {
        presenter.triggerBuild();
    }

    @EventHandler("install")
    public void onInstall(ClickEvent clickEvent) {
        presenter.triggerBuildAndInstall();
    }

    @EventHandler("deploy")
    public void onDeploy(ClickEvent clickEvent) {
        presenter.triggerBuildAndDeploy();
    }

    @EventHandler("redeploy")
    public void onRedeploy(ClickEvent clickEvent) {
        if (redeployEnabled) {
            presenter.triggerRedeploy();
        } else {
            clickEvent.stopPropagation();
        }
    }
}
