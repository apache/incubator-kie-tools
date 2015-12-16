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

package org.drools.workbench.screens.guided.scorecard.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.client.resources.GuidedScoreCardResources;
import org.drools.workbench.screens.guided.scorecard.client.resources.i18n.GuidedScoreCardConstants;
import org.drools.workbench.screens.guided.scorecard.client.type.GuidedScoreCardResourceType;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Guided Score Cards
 */
@ApplicationScoped
public class NewGuidedScoreCardHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<GuidedScoreCardEditorService> scoreCardService;

    @Inject
    private GuidedScoreCardResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return GuidedScoreCardConstants.INSTANCE.newGuidedScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedScoreCardResources.INSTANCE.images().typeGuidedScoreCard() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( baseFileName );
        model.setPackageName( pkg.getPackageName() );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        scoreCardService.call( getSuccessCallback( presenter ),
                               new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                       buildFileName( baseFileName,
                                                                                                                      resourceType ),
                                                                                                       model,
                                                                                                       "" );
    }

}
