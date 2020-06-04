/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.screens.view;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.HomeScreen;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
@Templated
public class HomeScreenView implements HomeScreen.View {

    private static final AppConstants i18n = AppConstants.INSTANCE;
    
    @Inject
    @DataField 
    HTMLDivElement sampleAction;
    
    @Inject
    @DataField 
    HTMLDivElement datasetsAction;
    
    @Inject
    @DataField 
    HTMLDivElement designAction;
    
    @Inject
    @DataField 
    HTMLDivElement transferAction;

    @Inject
    @DataField
    HTMLDivElement homePage;
    
    private HomeScreen presenter;

    @Override
    public void init(HomeScreen presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return homePage;
    }
    
    @EventHandler("sampleAction")
    public void onSampleAction(ClickEvent e) {
        presenter.goToSample();
    }
    
    @EventHandler("datasetsAction")
    public void onDatasetsAction(ClickEvent e) {
        presenter.goToDataset();
        
    }
    
    @EventHandler("designAction")
    public void onDesignAction(ClickEvent e) {
        presenter.goToDesign();
    }
    
    @EventHandler("transferAction")
    public void onTransferAction(ClickEvent e) {
        presenter.goToTransfer();
    }

}