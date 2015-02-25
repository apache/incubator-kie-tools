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

                for (KBaseModel kBase : kmodule.getKBases().values()) {
                    view.addKBase(kBase.getName());
                }

                // Some tricks here, currently we only have one ksession to test against,
                // but in the future it might make sense to use several.
                if (kmodule.getKBases().isEmpty()) {
                    setDefault();
                } else if (scenario.getKSessions().isEmpty()) {
                    setFirst();
                } else {
                    setFromModel();
                }
            }
        };
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

    private void setDefault() {
        view.addKBase(DEFAULT_KIE_BASE);
        ArrayList<String> defaultKieSession = makeKSessionList(DEFAULT_KIE_SESSION);
        view.setKSessions(defaultKieSession);
        view.setSelected(DEFAULT_KIE_BASE, DEFAULT_KIE_SESSION);
        scenario.getKSessions().add(DEFAULT_KIE_SESSION);
    }

    private void setFromModel() {
        String kbaseName = "";
        String ksessionName = scenario.getKSessions().get(0);

        for (KBaseModel kbase : kmodule.getKBases().values()) {
            for (KSessionModel ksession : kbase.getKSessions()) {
                if (ksession.getName().equals(ksessionName)) {
                    kbaseName = kbase.getName();
                }
            }
        }

        List<KSessionModel> kSessions = kmodule.getKBases().get(kbaseName).getKSessions();
        setKSessions(kSessions);

        view.setSelected(kbaseName, ksessionName);
    }

    private void setFirst() {

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

    private ArrayList<String> makeKSessionList(String ksession) {
        ArrayList<String> ksessions = new ArrayList<String>();
        ksessions.add(ksession);
        return ksessions;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
