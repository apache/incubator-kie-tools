/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.function.BiConsumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasWorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.modals.HasList;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.workitems.WorkItemParametersWidget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class WorkItemPage<T extends HasWorkItemPage & DecisionTableColumnPlugin> extends BaseDecisionTableColumnPage<T> {

    private View view;

    private boolean parametersEnabled = false;

    @Inject
    public WorkItemPage(final View view,
                        final TranslationService translationService) {
        super(translationService);
        this.view = view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.WorkItemPage_WorkItem);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(plugin().isWorkItemSet());
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public void prepareView() {
        view.init(this);

        markAsViewed();
        setupWorkItemsList();
    }

    void setupWorkItemsList() {
        view.setupWorkItemList();

        forEachWorkItem((name, key) -> view.addItem(name,
                                                    key));

        if (hasWorkItems()) {
            view.selectWorkItem(currentWorkItem());

            showParameters();
        } else {
            view.setupEmptyWorkItemList();
        }
    }

    public void enableParameters() {
        parametersEnabled = true;
    }

    void forEachWorkItem(final BiConsumer<String, String> biConsumer) {
        plugin().forEachWorkItem(biConsumer);
    }

    boolean hasWorkItems() {
        return view.workItemsCount() > 1;
    }

    void selectWorkItem(final String selectedWorkItem) {
        plugin().setWorkItem(selectedWorkItem);

        showParameters();
    }

    void showParameters() {
        final boolean hasWorkItemDefinition = workItemDefinition() != null;

        if (isParametersEnabled() && hasWorkItemDefinition) {
            view.showParameters(parametersWidget());
        } else {
            view.hideParameters();
        }
    }

    String currentWorkItem() {
        return plugin.getWorkItem();
    }

    boolean isParametersEnabled() {
        return parametersEnabled;
    }

    private WorkItemParametersWidget parametersWidget() {
        final WorkItemParametersWidget parametersWidget = new WorkItemParametersWidget(presenter,
                                                                                       false);
        parametersWidget.setParameters(workItemDefinition().getParameters());

        return parametersWidget;
    }

    private PortableWorkDefinition workItemDefinition() {
        return plugin().getWorkItemDefinition();
    }

    void markAsViewed() {
        plugin().setWorkItemPageAsCompleted();
    }

    public interface View extends HasList,
                                  UberElement<WorkItemPage> {

        String getSelectedWorkItem();

        void setupWorkItemList();

        void setupEmptyWorkItemList();

        void selectWorkItem(String currentWorkItem);

        void showParameters(final WorkItemParametersWidget parametersWidget);

        void hideParameters();

        int workItemsCount();
    }
}
