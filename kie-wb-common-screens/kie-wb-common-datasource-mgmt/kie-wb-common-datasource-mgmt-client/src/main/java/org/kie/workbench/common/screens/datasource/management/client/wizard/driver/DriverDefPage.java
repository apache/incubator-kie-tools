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

package org.kie.workbench.common.screens.datasource.management.client.wizard.driver;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.screens.datasource.management.client.editor.driver.DriverDefEditorHelper;
import org.kie.workbench.common.screens.datasource.management.client.editor.driver.DriverDefMainPanel;
import org.kie.workbench.common.screens.datasource.management.client.editor.driver.DriverDefMainPanelView;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class DriverDefPage
        implements WizardPage,
                   DriverDefPageView.Presenter {

    private DriverDefPageView view;

    private DriverDefMainPanel mainPanel;

    private DriverDefEditorHelper editorHelper;

    private Event<WizardPageStatusChangeEvent> statusChangeEvent;

    @Inject
    public DriverDefPage(final DriverDefPageView view,
                         final DriverDefMainPanel mainPanel,
                         final DriverDefEditorHelper editorHelper,
                         final Event<WizardPageStatusChangeEvent> statusChangeEvent) {
        this.view = view;
        this.mainPanel = mainPanel;
        this.editorHelper = editorHelper;
        this.statusChangeEvent = statusChangeEvent;
        view.init(this);
        editorHelper.init(mainPanel);

        editorHelper.setHandler(new DriverDefMainPanelView.Handler() {
            @Override
            public void onNameChange() {
                DriverDefPage.this.notifyChange();
            }

            @Override
            public void onDriverClassChange() {
                DriverDefPage.this.notifyChange();
            }

            @Override
            public void onGroupIdChange() {
                DriverDefPage.this.notifyChange();
            }

            @Override
            public void onArtifactIdChange() {
                DriverDefPage.this.notifyChange();
            }

            @Override
            public void onVersionChange() {
                DriverDefPage.this.notifyChange();
            }
        });
    }

    @PostConstruct
    private void init() {
        view.setMainPanel(mainPanel);
    }

    public void setDriverDef(DriverDef driverDef) {
        editorHelper.setDriverDef(driverDef);
    }

    @Override
    public String getTitle() {
        return view.getPageTitle();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        boolean complete = editorHelper.isNameValid() &&
                editorHelper.isDriverClassValid() &&
                editorHelper.isGroupIdValid() &&
                editorHelper.isArtifactIdValid() &&
                editorHelper.isVersionValid();

        callback.callback(complete);
    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public void notifyChange() {
        final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(this);
        statusChangeEvent.fire(event);
    }

    public void setComplete(boolean complete) {
        editorHelper.setValid(complete);
    }

    public void clear() {
        mainPanel.clear();
    }
}
