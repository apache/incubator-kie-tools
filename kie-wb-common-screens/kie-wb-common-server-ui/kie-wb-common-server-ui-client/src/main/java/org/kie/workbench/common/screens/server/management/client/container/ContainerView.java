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

package org.kie.workbench.common.screens.server.management.client.container;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.container.config.process.ContainerProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.config.rules.ContainerRulesConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.client.widget.Div;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ContainerView extends Composite
        implements ContainerPresenter.View {

    private ContainerPresenter presenter;

    private TranslationService translationService;

    @DataField("container-name")
    Element containerName = DOM.createSpan();

    @Inject
    @DataField("container-config-start")
    Button containerStart;

    @Inject
    @DataField("container-config-stop")
    Button containerStop;
    
    @Inject
    @DataField("container-config-activation")
    Button containerActivation;

    @Inject
    @DataField("remove-container")
    Button removeContainer;

    @Inject
    @DataField("group-id")
    Span groupId;

    @Inject
    @DataField("artifact-id")
    Span artifactId;

    @Inject
    @DataField("refresh-container")
    Button refreshContainer;

    @DataField("status-tab")
    Element statusTab = DOM.createElement("li");

    @DataField("status-tab-link")
    Element statusTabLink = DOM.createAnchor();

    @DataField("rules-tab")
    Element rulesTab = DOM.createElement("li");

    @DataField("rules-tab-link")
    Element rulesTabLink = DOM.createAnchor();

    @DataField("process-tab")
    Element processTab = DOM.createElement("li");

    @DataField("process-tab-link")
    Element processTabLink = DOM.createAnchor();

    @DataField("status-pane")
    Element statusPane = DOM.createDiv();

    @Inject
    @DataField("status-content")
    Div statusContent;

    @DataField("rules-pane")
    Element rulesPane = DOM.createDiv();

    @Inject
    @DataField("rules-content")
    Div rulesContent;

    @DataField("process-pane")
    Element processPane = DOM.createDiv();

    @Inject
    @DataField("process-content")
    Div processContent;

    @Inject
    public ContainerView(final TranslationService translationService) {
        super();
        this.translationService = translationService;
    }

    @PostConstruct
    public void initPopovers() {
        setupTooltip(containerName,
                     translationService.getTranslation(Constants.ContainerView_Alias));
    }

    @Override
    public void init(final ContainerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        this.containerName.setInnerText("");
        this.artifactId.setText("");
        this.groupId.setText("");
    }

    @Override
    public void disableRemoveButton() {
        removeContainer.setEnabled(false);
    }

    @Override
    public void enableRemoveButton() {
        removeContainer.setEnabled(true);
    }
    
    @Override
    public void updateToggleActivationButton(boolean activate) {
        if (activate) {
            containerActivation.setText(translationService.format(Constants.ContainerView_ActivateButton));
        } else {
            containerActivation.setText(translationService.format(Constants.ContainerView_DeactivateButton));
        }
    }
    
    @Override
    public void disableToggleActivationButton() {
        containerActivation.setEnabled(false);
    }

    @Override
    public void enableToggleActivationButton() {
        containerActivation.setEnabled(true);
    }

    @Override
    public void setContainerName(final String containerName) {
        this.containerName.setInnerText(containerName);
    }

    @Override
    public void setGroupIp(final String groupIp) {
        this.groupId.setText(groupIp);
    }

    @Override
    public void setArtifactId(final String artifactId) {
        this.artifactId.setText(artifactId);
    }

    @Override
    public void setStatus(final IsWidget view) {
        statusContent.clear();
        statusContent.add(view);
    }

    @Override
    public void setProcessConfig(final ContainerProcessConfigPresenter.View view) {
        processContent.clear();
        processContent.add(view);
    }

    @Override
    public void setRulesConfig(final ContainerRulesConfigPresenter.View view) {
        rulesContent.clear();
        rulesContent.add(view);
    }

    @Override
    public void setContainerStartState(final State state) {
        containerStop.setEnabled(state.equals(State.ENABLED));
        containerStop.setActive(!state.equals(State.ENABLED));
    }

    @Override
    public void confirmRemove(final Command command) {
        final YesNoCancelPopup result = YesNoCancelPopup.newYesNoCancelPopup(getConfirmRemovePopupTitle(),
                                                                             getConfirmRemovePopupMessage(),
                                                                             command,
                                                                             new Command() {
                                                                                 @Override
                                                                                 public void execute() {
                                                                                 }
                                                                             }, null);
        result.clearScrollHeight();
        result.show();
    }

    @Override
    public void setContainerStopState(final State state) {
        containerStart.setEnabled(state.equals(State.ENABLED));
        containerStart.setActive(!state.equals(State.ENABLED));
    }

    @EventHandler("refresh-container")
    public void refresh(final ClickEvent event) {
        presenter.refresh();
    }

    @EventHandler("remove-container")
    public void removeContainer(final ClickEvent event) {
        presenter.removeContainer();
    }

    @EventHandler("container-config-start")
    public void startContainer(final ClickEvent event) {
        presenter.startContainer();
    }

    @EventHandler("container-config-stop")
    public void stopContainer(final ClickEvent event) {
        presenter.stopContainer();
    }
    
    @EventHandler("container-config-activation")
    public void toggleActivationContainer(final ClickEvent event) {
        presenter.toggleActivationContainer();
    }

    @Override
    public String getRemoveContainerSuccessMessage() {
        return translationService.format(Constants.ContainerView_RemoveContainerSuccessMessage);
    }

    @Override
    public String getRemoveContainerErrorMessage() {
        return translationService.format(Constants.ContainerView_RemoveContainerErrorMessage);
    }

    @Override
    public String getStopContainerErrorMessage() {
        return translationService.format(Constants.ContainerView_StopContainerErrorMessage);
    }

    @Override
    public String getStartContainerErrorMessage() {
        return translationService.format(Constants.ContainerView_StartContainerErrorMessage);
    }

    private String getConfirmRemovePopupMessage() {
        return translationService.format(Constants.ContainerView_ConfirmRemovePopupMessage);
    }

    private String getConfirmRemovePopupTitle() {
        return translationService.format(Constants.ContainerView_ConfirmRemovePopupTitle);
    }

    private native void setupTooltip(final Element e,
                                     final String title) /*-{
        $wnd.jQuery(e).tooltip({
            container: 'body',
            placement: 'bottom',
            title: title,
            trigger: 'hover'
        });
    }-*/;
}
