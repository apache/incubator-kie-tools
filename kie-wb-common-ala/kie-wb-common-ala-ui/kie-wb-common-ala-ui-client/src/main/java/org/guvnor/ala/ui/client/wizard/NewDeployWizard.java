/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.wizard.pipeline.PipelineDescriptor;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsForm;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsPagePresenter;
import org.guvnor.ala.ui.client.wizard.pipeline.select.SelectPipelinePagePresenter;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_PipelineStartSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_Title;

@ApplicationScoped
public class NewDeployWizard
        extends AbstractMultiPageWizard {

    public static final String RUNTIME_NAME = "runtime-name";

    private final SelectPipelinePagePresenter selectPipelinePage;
    private final ManagedInstance<PipelineParamsPagePresenter> pipelineParamsPageInstance;
    private final Instance<PipelineDescriptor> pipelineDescriptorInstance;
    private final PopupHelper popupHelper;
    private final Caller<RuntimeService> runtimeService;

    private final Event<RefreshRuntimeEvent> refreshRuntimeEvent;

    private Provider provider;

    private List<PipelineParamsForm> paramsForms = new ArrayList<>();

    private List<PipelineParamsPagePresenter> paramsPages = new ArrayList<>();

    @Inject
    public NewDeployWizard(final SelectPipelinePagePresenter selectPipelinePage,
                           final ManagedInstance<PipelineParamsPagePresenter> pipelineParamsPageInstance,
                           final @Any Instance<PipelineDescriptor> pipelineDescriptorInstance,
                           final PopupHelper popupHelper,
                           final TranslationService translationService,
                           final Caller<RuntimeService> runtimeService,
                           final Event<NotificationEvent> notification,
                           final Event<RefreshRuntimeEvent> refreshRuntimeEvent) {
        super(translationService,
              notification);
        this.popupHelper = popupHelper;
        this.selectPipelinePage = selectPipelinePage;
        this.pipelineParamsPageInstance = pipelineParamsPageInstance;
        this.pipelineDescriptorInstance = pipelineDescriptorInstance;
        this.runtimeService = runtimeService;
        this.refreshRuntimeEvent = refreshRuntimeEvent;
    }

    @PostConstruct
    public void init() {
        setDefaultPages();
    }

    public void start(final Provider provider,
                      final Collection<PipelineKey> pipelines) {
        this.provider = provider;
        setDefaultPages();
        selectPipelinePage.setup(pipelines);
        super.start();
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(NewDeployWizard_Title);
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public void complete() {
        final PipelineKey pipeline = selectPipelinePage.getPipeline();

        Map<String, String> params = buildPipelineParams();
        final String runtime = params.get(RUNTIME_NAME);

        runtimeService.call((Void aVoid) -> onPipelineStartSuccess(),
                            popupHelper.getPopupErrorCallback()).createRuntime(provider.getKey(),
                                                                               runtime,
                                                                               pipeline,
                                                                               buildPipelineParams());
    }

    @Override
    public void onStatusChange(final @Observes WizardPageStatusChangeEvent event) {
        boolean restart = false;
        if (event.getPage() == selectPipelinePage) {
            List<PipelineParamsForm> oldParamsForms = new ArrayList<>();
            oldParamsForms.addAll(paramsForms);
            if (selectPipelinePage.getPipeline() != null) {
                paramsForms = getParamsForms(selectPipelinePage.getPipeline());
                if (!paramsForms.isEmpty()) {
                    paramsForms.forEach(PipelineParamsForm::clear);
                    paramsForms.forEach(PipelineParamsForm::initialise);
                    destroyPipelineParamPages(paramsPages);
                    paramsForms.forEach(form -> paramsPages.add(newPipelineParamsPage(form)));
                    setDefaultPages();
                    pages.addAll(paramsPages);
                    restart = true;
                } else if (!oldParamsForms.isEmpty()) {
                    setDefaultPages();
                    restart = true;
                }
            } else if (pages.size() > 1) {
                paramsForms.clear();
                setDefaultPages();
                restart = true;
            }
            if (!oldParamsForms.isEmpty()) {
                oldParamsForms.forEach(PipelineParamsForm::clear);
            }
        }

        if (restart) {
            super.start();
        } else {
            super.onStatusChange(event);
        }
    }

    private List<PipelineParamsForm> getParamsForms(final PipelineKey pipelineKey) {
        Iterator<PipelineDescriptor> pipelineDescriptors = pipelineDescriptorInstance.iterator();
        PipelineDescriptor pipelineDescriptor;
        List<PipelineParamsForm> pipelineForms = new ArrayList<>();
        while (pipelineDescriptors.hasNext()) {
            pipelineDescriptor = pipelineDescriptors.next();
            if (pipelineDescriptor.accept(pipelineKey)) {
                pipelineForms.addAll(pipelineDescriptor.getParamForms());
                break;
            }
        }
        return pipelineForms;
    }

    private void onPipelineStartSuccess() {
        notification.fire(new NotificationEvent(translationService.getTranslation(NewDeployWizard_PipelineStartSuccessMessage),
                                                NotificationEvent.NotificationType.SUCCESS));
        NewDeployWizard.super.complete();
        refreshRuntimeEvent.fire(new RefreshRuntimeEvent(provider.getKey()));
    }

    private Map<String, String> buildPipelineParams() {
        Map<String, String> params = new HashMap<>();
        paramsForms.forEach(form -> params.putAll(form.buildParams()));
        return params;
    }

    private void setDefaultPages() {
        pages.clear();
        pages.add(selectPipelinePage);
    }

    protected PipelineParamsPagePresenter newPipelineParamsPage() {
        return pipelineParamsPageInstance.get();
    }

    protected PipelineParamsPagePresenter newPipelineParamsPage(PipelineParamsForm paramsForm) {
        PipelineParamsPagePresenter paramsPage = newPipelineParamsPage();
        paramsPage.setPipelineParamsForm(paramsForm);
        return paramsPage;
    }

    protected void destroyPipelineParamPages(List<PipelineParamsPagePresenter> paramsPages) {
        paramsPages.forEach(pipelineParamsPageInstance::destroy);
        paramsPages.clear();
    }
}
