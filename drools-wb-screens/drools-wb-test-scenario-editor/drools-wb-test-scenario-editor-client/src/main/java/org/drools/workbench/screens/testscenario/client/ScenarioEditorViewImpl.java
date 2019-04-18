/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
public class ScenarioEditorViewImpl
        extends KieEditorViewImpl
        implements ScenarioEditorView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, ScenarioEditorViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Widget layout;

    @UiField(provided = true)
    FixtureLayout fixtureLayout;

    @Inject
    public ScenarioEditorViewImpl(final FixtureLayout fixtureLayout) {
        this.fixtureLayout = fixtureLayout;

        layout = uiBinder.createAndBindUi(this);
        layout.setWidth("100%");
        layout.getElement().getStyle().setMargin(5, Style.Unit.PX);
    }

    @Override
    /**
     * Overriding this since initWidget(...) breaks the UI.
     */
    public Widget asWidget() {
        return layout;
    }

    @Override
    public MenuItem getRunScenarioMenuItem() {
        return new SimpleMenuItem(TestScenarioConstants.INSTANCE.RunScenario(), new com.google.gwt.user.client.Command() {
            @Override
            public void execute() {
                presenter.onRunScenario();
            }
        });
    }

    public void renderEditor() {
        presenter.onRedraw();
    }

    @Override
    public void renderFixtures(Path path,
                               AsyncPackageDataModelOracle oracle,
                               Scenario scenario) {
        fixtureLayout.reset(this,
                            path,
                            oracle,
                            scenario);
    }

    @Override
    public void showResults() {
        fixtureLayout.showResults();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
