/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.shared.kmodule.AssertBehaviorOption;
import org.kie.workbench.common.services.shared.kmodule.EventProcessingOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.screens.projecteditor.client.widgets.Form;

public class KBaseForm
        implements Form<KBaseModel>, KBaseFormView.Presenter {

    private final KBaseFormView view;
    private KBaseModel model;

    @Inject
    public KBaseForm(KBaseFormView view) {
        this.view = view;
        view.setPresenter(this);
        view.setReadOnly();
    }

    @Override
    public void setModel(KBaseModel knowledgeBaseConfiguration) {

        clear();

        view.makeEditable();

        this.model = knowledgeBaseConfiguration;

        view.setName(knowledgeBaseConfiguration.getName());
        view.setDefault(knowledgeBaseConfiguration.isDefault());

        for (String include : model.getIncludes()) {
            view.addIncludedKBase(include);
        }

        for (String packageName : model.getPackages()) {
            view.addPackageName(packageName);
        }

        setEqualsBehaviour(knowledgeBaseConfiguration);

        setEventProcessingMode(knowledgeBaseConfiguration);

        setSessions(knowledgeBaseConfiguration);
    }

    public void clear() {
        model = null;
        view.clear();
    }

    @Override
    public void makeReadOnly() {
        view.setReadOnly();
    }

    private void setSessions(KBaseModel knowledgeBaseConfiguration) {
        view.setStatefulSessions(knowledgeBaseConfiguration.getKSessions());
    }

    private void setEventProcessingMode(KBaseModel knowledgeBaseConfiguration) {
        switch (knowledgeBaseConfiguration.getEventProcessingMode()) {
            case CLOUD:
                view.setEventProcessingModeCloud();
                break;

            case STREAM:
                view.setEventProcessingModeStream();
                break;
        }
    }

    private void setEqualsBehaviour(KBaseModel knowledgeBaseConfiguration) {
        switch (knowledgeBaseConfiguration.getEqualsBehavior()) {
            case EQUALITY:
                view.setEqualsBehaviorEquality();
                break;

            case IDENTITY:
                view.setEqualsBehaviorIdentity();
                break;
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onEqualsBehaviorEqualitySelect() {
        model.setEqualsBehavior(AssertBehaviorOption.EQUALITY);
    }

    @Override
    public void onEqualsBehaviorIdentitySelect() {
        model.setEqualsBehavior(AssertBehaviorOption.IDENTITY);
    }

    @Override
    public void onEventProcessingModeStreamSelect() {
        model.setEventProcessingMode(EventProcessingOption.STREAM);
    }

    @Override
    public void onEventProcessingModeCloudSelect() {
        model.setEventProcessingMode(EventProcessingOption.CLOUD);
    }

    @Override
    public void onDeletePackage(String itemName) {
        model.getPackages().remove(itemName);
    }

    @Override
    public void onAddPackage(String packageName) {
        model.getPackages().add(packageName);
    }

    @Override
    public void onDeleteIncludedKBase(String itemName) {
        model.getIncludes().remove(itemName);
    }

    @Override
    public void onAddIncludedKBase(String itemName) {
        model.getIncludes().add(itemName);
    }
}
