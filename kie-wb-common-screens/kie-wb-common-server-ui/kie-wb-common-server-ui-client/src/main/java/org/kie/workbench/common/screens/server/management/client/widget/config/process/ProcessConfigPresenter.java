/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.widget.config.process;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.util.ClientMergeMode;
import org.kie.workbench.common.screens.server.management.client.util.ClientRuntimeStrategy;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ProcessConfigPresenter {

    public interface View extends UberView<ProcessConfigPresenter> {

        void setContent( final String runtimeStrategy,
                         final String kbase,
                         final String ksession,
                         final String mergeMode );

        String getKBase();

        String getKSession();

        String getMergeMode();

        String getRuntimeStrategy();

        void disable();

        void clear();

        TranslationService getTranslationService();

        String getConfigPageTitle();
    }

    private final View view;
    private ContainerSpecKey containerSpecKey;
    private ProcessConfig processConfig;

    @Inject
    public ProcessConfigPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final ContainerSpecKey containerSpecKey,
                       final ProcessConfig processConfig ) {
        this.processConfig = checkNotNull( "processConfig", processConfig );
        this.containerSpecKey = checkNotNull( "containerSpecKey", containerSpecKey );
        setupView( processConfig );
    }

    public void clear() {
        processConfig = null;
        containerSpecKey = null;
        view.clear();
    }

    public void setProcessConfig( final ProcessConfig processConfig ) {
        this.processConfig = processConfig;
        setupView( processConfig );
    }

    public ContainerSpecKey getContainerSpecKey() {
        return containerSpecKey;
    }

    public ProcessConfig getProcessConfig() {
        return processConfig;
    }

    private void setupView( final ProcessConfig processConfig ) {
        this.view.setContent( ClientRuntimeStrategy.convert( processConfig.getRuntimeStrategy(), view.getTranslationService() ).getValue( view.getTranslationService() ),
                              processConfig.getKBase(),
                              processConfig.getKSession(),
                              ClientMergeMode.convert( processConfig.getMergeMode(), view.getTranslationService() ).getValue( view.getTranslationService() ) );

    }

    public void disable() {
        view.disable();
    }

    public void cancel() {
        setupView( this.processConfig );
    }

    public ProcessConfig buildProcessConfig() {
        return new ProcessConfig( ClientRuntimeStrategy.convert( view.getRuntimeStrategy(), view.getTranslationService() ).getRuntimeStrategy().toString(),
                                  view.getKBase(),
                                  view.getKSession(),
                                  ClientMergeMode.convert( view.getMergeMode(), view.getTranslationService() ).getMergeMode().toString() );
    }

    public List<String> getRuntimeStrategies() {
        return ClientRuntimeStrategy.listRuntimeStrategiesValues( view.getTranslationService() );
    }

    public List<String> getMergeModes() {
        return ClientMergeMode.listMergeModeValues( view.getTranslationService() );
    }

}
