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

package org.drools.workbench.screens.guided.template.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.client.resources.GuidedTemplateEditorResources;
import org.drools.workbench.screens.guided.template.client.resources.i18n.GuidedTemplateEditorConstants;
import org.drools.workbench.screens.guided.template.client.type.GuidedRuleTemplateResourceType;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Guided Templates
 */
@ApplicationScoped
public class NewGuidedRuleTemplateHandler extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedRuleTemplateEditorService> service;

    @Inject
    private GuidedRuleTemplateResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return GuidedTemplateEditorConstants.INSTANCE.NewGuidedRuleTemplateDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedTemplateEditorResources.INSTANCE.images().typeGuidedRuleTemplate() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final TemplateModel templateModel = new TemplateModel();
        templateModel.name = baseFileName;
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( presenter ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                              buildFileName( baseFileName,
                                                                                                             resourceType ),
                                                                                              templateModel,
                                                                                              "" );
    }

}
