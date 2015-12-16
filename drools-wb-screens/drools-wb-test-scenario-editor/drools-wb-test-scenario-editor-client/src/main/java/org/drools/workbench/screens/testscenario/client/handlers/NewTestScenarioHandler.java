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

package org.drools.workbench.screens.testscenario.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewTestScenarioHandler
        extends DefaultNewResourceHandler {

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Caller<ScenarioTestEditorService> service;

    @Inject
    private TestScenarioResourceType resourceType;

    @Override
    public String getDescription() {
        return TestScenarioConstants.INSTANCE.NewTestScenarioDescription();
    }

    @Override
    public IsWidget getIcon() {
        return TestScenarioAltedImages.INSTANCE.typeTestScenario();
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );

        service.call(
                getSuccessCallback( presenter ),
                new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageTestResourcesPath(),
                                                                                        buildFileName( baseFileName,
                                                                                                       resourceType ),
                                                                                        new Scenario( pkg.getPackageName(),
                                                                                                      baseFileName ),
                                                                                        "" );
    }

}
