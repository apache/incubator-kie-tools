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
package org.guvnor.ala.ui.client.wizard.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.ala.ui.client.util.AbstractHasContentChangeHandlers;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsForm;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsPresenter_AddContainerPopupTitle;

@ApplicationScoped
public class ContainerConfigParamsPresenter
        extends AbstractHasContentChangeHandlers
        implements PipelineParamsForm {

    public interface View
            extends UberElement<ContainerConfigParamsPresenter> {

        String getWizardTitle();

        HasData<ContainerConfig> getDisplay();
    }

    private final View view;

    private final ContainerConfigPopup containerConfigPopup;

    private final Event<ContainerConfigParamsChangeEvent> configParamsChangeEvent;

    private final TranslationService translationService;

    private ListDataProvider<ContainerConfig> dataProvider = createDataProvider();

    @Inject
    public ContainerConfigParamsPresenter(final View view,
                                          final ContainerConfigPopup containerConfigPopup,
                                          final Event<ContainerConfigParamsChangeEvent> configParamsChangeEvent,
                                          final TranslationService translationService) {
        this.view = view;
        this.containerConfigPopup = containerConfigPopup;
        this.configParamsChangeEvent = configParamsChangeEvent;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        dataProvider.addDataDisplay(view.getDisplay());
    }

    @Override
    public IsElement getView() {
        return view;
    }

    @Override
    public Map<String, String> buildParams() {
        return new HashMap<>();
    }

    @Override
    public void initialise() {
    }

    @Override
    public void prepareView() {
        dataProvider.refresh();
    }

    @Override
    public void clear() {
        dataProvider.getList().clear();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        boolean complete = !dataProvider.getList().isEmpty();
        callback.callback(complete);
    }

    @Override
    public String getWizardTitle() {
        return view.getWizardTitle();
    }

    protected void onAddContainer() {
        containerConfigPopup.show(translationService.getTranslation(ContainerConfigParamsPresenter_AddContainerPopupTitle),
                                  this::addContainer,
                                  () -> {
                                  },
                                  getCurrentContainerNames());
    }

    protected void onDeleteContainer(ContainerConfig containerConfig) {
        dataProvider.getList().remove(containerConfig);
        onContentChange();
    }

    private void addContainer(ContainerConfig containerConfig) {
        dataProvider.getList().add(containerConfig);
        onContentChange();
    }

    private void onContentChange() {
        configParamsChangeEvent.fire(new ContainerConfigParamsChangeEvent(new ArrayList<>(dataProvider.getList())));
        fireChangeHandlers();
    }

    /**
     * for testing purposes.
     */
    ListDataProvider<ContainerConfig> createDataProvider() {
        return new ListDataProvider<>();
    }

    private List<String> getCurrentContainerNames() {
        return dataProvider.getList().stream()
                .map(ContainerConfig::getName)
                .collect(Collectors.toList());
    }
}