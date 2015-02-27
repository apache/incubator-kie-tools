/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

public class KSessionSelector
        implements IsWidget,
                   KSessionSelectorView.Presenter {

    private static String DEFAULT_KIE_BASE    = "defaultKieBase";
    private static String DEFAULT_KIE_SESSION = "defaultKieSession";
    private static String NON_EXISTING_KBASE = "---";

    private KSessionSelectorView      view;
    private Caller<KieProjectService> projectService;

    private Caller<KModuleService> kModuleService;
    private KModuleModel           kmodule;
    private Scenario               scenario;

    @Inject
    public KSessionSelector(final KSessionSelectorView view,
                            final Caller<KieProjectService> projectService,
                            final Caller<KModuleService> kModuleService) {
        this.view = view;
        this.projectService = projectService;
        this.kModuleService = kModuleService;

        view.setPresenter(this);
    }

    public void init(Path path,
                     Scenario scenario) {
        this.scenario = scenario;
        projectService.call(getSuccessfulResolveProjectCallback()).resolveProject(path);
    }

    private RemoteCallback<KieProject> getSuccessfulResolveProjectCallback() {
        return new RemoteCallback<KieProject>() {
            @Override
            public void callback(KieProject project) {
                kModuleService.call(getSuccessfulLoadKModuleCallback()).load(project.getKModuleXMLPath());
            }
        };
    }

    private RemoteCallback<KModuleModel> getSuccessfulLoadKModuleCallback() {
        return new RemoteCallback<KModuleModel>() {
            @Override
            public void callback(KModuleModel kmodule) {
                KSessionSelector.this.kmodule = kmodule;

                initKBases();
                selectCurrentKBaseAndKSession();
            }
        };
    }

    private void selectCurrentKBaseAndKSession() {
        if (scenarioHasKSessionDefined() && kmoduleContainsCurrentKSession(kmodule)) {
            selectFromModel();
        } else {
            selectFirst();
        }
    }

    private void initKBases() {
        if (kmodule.getKBases().isEmpty()) {
            addMockKBaseModel(DEFAULT_KIE_BASE, DEFAULT_KIE_SESSION);
            view.addKBase(DEFAULT_KIE_BASE);
        } else {
            for (KBaseModel kBase : kmodule.getKBases().values()) {
                view.addKBase(kBase.getName());
            }
        }
        if (scenarioHasKSessionDefined() && !kmoduleContainsCurrentKSession(kmodule)) {
            addMockKBaseModel(NON_EXISTING_KBASE, getKSessionName());
            view.addKBase(NON_EXISTING_KBASE);
            view.showWarningSelectedKSessionDoesNotExist();
        }
    }

    private void addMockKBaseModel(String kbaseName, String ksessionsName) {
        KBaseModel kbaseModel = new KBaseModel();
        kbaseModel.setName(kbaseName);
        KSessionModel ksessionModel = new KSessionModel();
        ksessionModel.setName(ksessionsName);
        kbaseModel.getKSessions().add(ksessionModel);
        kmodule.getKBases().put(kbaseName, kbaseModel);
    }

    private boolean scenarioHasKSessionDefined() {
        return !scenario.getKSessions().isEmpty();
    }

    private boolean kmoduleContainsCurrentKSession(KModuleModel kmodule) {
        if (!scenario.getKSessions().isEmpty()) {
            for (KBaseModel kbase : kmodule.getKBases().values()) {
                for (KSessionModel ksession : kbase.getKSessions()) {
                    if (ksession.getName().equals(getKSessionName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onKBaseSelected(String kbaseName) {
        setKSessions(kmodule.getKBases().get(kbaseName).getKSessions());
    }

    @Override
    public void onKSessionSelected(String ksession) {
        scenario.getKSessions().clear();
        scenario.getKSessions().add(ksession);
    }

    private void selectFromModel() {
        for (KBaseModel kbase : kmodule.getKBases().values()) {
            for (KSessionModel ksession : kbase.getKSessions()) {
                if (ksession.getName().equals(getKSessionName())) {
                    setKSessions(kmodule.getKBases().get(kbase.getName()).getKSessions());
                    view.setSelected(kbase.getName(), getKSessionName());
                    break;
                }
            }
        }
    }

    private String getKSessionName() {
        return scenario.getKSessions().get(0);
    }

    private void selectFirst() {

        KBaseModel firstKBase = kmodule.getKBases().values().iterator().next();

        List<KSessionModel> kSessions = firstKBase.getKSessions();

        setKSessions(kSessions);

        String ksession = kSessions.iterator().next().getName();
        view.setSelected(firstKBase.getName(), ksession);

        scenario.getKSessions().add(ksession);
    }

    private void setKSessions(List<KSessionModel> kSessions) {
        List<String> ksessions = new ArrayList<String>();
        for (KSessionModel kSession : kSessions) {
            ksessions.add(kSession.getName());
        }
        view.setKSessions(ksessions);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
