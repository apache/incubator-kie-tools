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
package org.drools.workbench.screens.testscenario.client.page.settings;

import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.refactoring.service.ScoreCardServiceLoader;
import org.kie.workbench.common.workbench.client.EditorIds;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

public class ScorecardSelector
        implements IsWidget {

    private final String NONE = "- " + TestScenarioConstants.INSTANCE.SelectScoreCard() + " -";

    private Caller<ScoreCardServiceLoader> scoreCardServiceLoader;
    private ScorecardSelectorView view;
    private Scenario scenario;

    public ScorecardSelector() {
    }

    @Inject
    public ScorecardSelector(final Caller<ScoreCardServiceLoader> scoreCardServiceLoader,
                             final AuthorizationManager authorizationManager,
                             final SessionInfo sessionInfo,
                             final ScorecardSelectorView view) {
        this.scoreCardServiceLoader = scoreCardServiceLoader;
        this.view = view;
        this.view.init(this);
        if (!authorizationManager.authorize(new ResourceRef(EditorIds.GUIDED_SCORE_CARD,
                                                            ActivityResourceType.EDITOR),
                                            ResourceAction.READ,
                                            sessionInfo.getIdentity())) {
            view.hide();
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init(final Path path,
                     final Scenario scenario) {
        this.scenario = scenario;
        scoreCardServiceLoader.call(
                (RemoteCallback<Set<String>>) response -> {
                    view.clear();
                    view.add(NONE);
                    for (String modelName : response) {
                        view.add(modelName);
                    }

                    if (scenario.getModelName() != null) {
                        view.setSelected(scenario.getModelName());
                    }
                }).find(path,
                        scenario.getPackageName());
    }

    public void onValueSelected(final String modelName) {
        if (Objects.equals(NONE, modelName)) {
            scenario.setModelName(null);
        } else {
            scenario.setModelName(modelName);
        }
    }
}
