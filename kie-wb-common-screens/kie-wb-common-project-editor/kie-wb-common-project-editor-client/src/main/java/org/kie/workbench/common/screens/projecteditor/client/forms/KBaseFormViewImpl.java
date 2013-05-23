/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.RadioButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.project.service.model.KSessionModel;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.util.Map;

public class KBaseFormViewImpl
        extends Composite
        implements KBaseFormView {

    private Presenter presenter;

    interface KnowledgeBaseConfigurationFormViewImplBinder
            extends
            UiBinder<Widget, KBaseFormViewImpl> {

    }

    private static KnowledgeBaseConfigurationFormViewImplBinder uiBinder = GWT.create(KnowledgeBaseConfigurationFormViewImplBinder.class);

    @UiField
    Label nameLabel;

    @UiField
    RadioButton equalsBehaviorIdentity;

    @UiField
    RadioButton equalsBehaviorEquality;

    @UiField
    RadioButton eventProcessingModeStream;

    @UiField
    RadioButton eventProcessingModeCloud;

    @UiField(provided = true)
    KSessionsPanel statefulSessionsPanel;

    @UiField(provided = true)
    KSessionsPanel statelessSessionsPanel;

    @Inject
    public KBaseFormViewImpl(@New KSessionsPanel statefulSessionsPanel,
                             @New KSessionsPanel statelessSessionsPanel) {
        this.statefulSessionsPanel = statefulSessionsPanel;
        this.statelessSessionsPanel = statelessSessionsPanel;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(String name) {
        nameLabel.setText(name);
    }

    @Override
    public void setEqualsBehaviorEquality() {
        equalsBehaviorEquality.setValue(true);
    }

    @Override
    public void setEqualsBehaviorIdentity() {
        equalsBehaviorIdentity.setValue(true);
    }

    @Override
    public void setEventProcessingModeStream() {
        eventProcessingModeStream.setValue(true);
    }

    @Override
    public void setEventProcessingModeCloud() {
        eventProcessingModeCloud.setValue(true);
    }

    @Override
    public void setStatefulSessions(Map<String, KSessionModel> statefulSessions) {
        statefulSessionsPanel.setItems(statefulSessions);
    }

    @Override
    public void setStatelessSessions(Map<String, KSessionModel> statefulSessions) {
        statelessSessionsPanel.setItems(statefulSessions);
    }

    @Override
    public void setReadOnly() {
        equalsBehaviorIdentity.setEnabled(false);
        equalsBehaviorEquality.setEnabled(false);
        eventProcessingModeStream.setEnabled(false);
        eventProcessingModeCloud.setEnabled(false);
        statefulSessionsPanel.makeReadOnly();
        statelessSessionsPanel.makeReadOnly();
    }

    @UiHandler("equalsBehaviorIdentity")
    public void onEqualsBehaviorIdentityChange(ValueChangeEvent<Boolean> valueChangeEvent) {
        if (equalsBehaviorIdentity.getValue()) {
            presenter.onEqualsBehaviorIdentitySelect();
        }
    }

    @UiHandler("equalsBehaviorEquality")
    public void onEqualsBehaviorEqualityChange(ValueChangeEvent<Boolean> valueChangeEvent) {
        if (equalsBehaviorEquality.getValue()) {
            presenter.onEqualsBehaviorEqualitySelect();
        }
    }

    @UiHandler("eventProcessingModeStream")
    public void onEventProcessingModeStreamChange(ValueChangeEvent<Boolean> valueChangeEvent) {
        if (eventProcessingModeStream.getValue()) {
            presenter.onEventProcessingModeStreamSelect();
        }
    }

    @UiHandler("eventProcessingModeCloud")
    public void onEventProcessingModeCloudChange(ValueChangeEvent<Boolean> valueChangeEvent) {
        if (eventProcessingModeCloud.getValue()) {
            presenter.onEventProcessingModeCloudSelect();
        }
    }
}
