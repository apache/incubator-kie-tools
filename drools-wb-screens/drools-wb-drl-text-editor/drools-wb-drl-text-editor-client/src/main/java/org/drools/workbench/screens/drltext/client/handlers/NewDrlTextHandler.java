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

package org.drools.workbench.screens.drltext.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.drltext.client.resources.DRLTextEditorResources;
import org.drools.workbench.screens.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.drools.workbench.screens.drltext.client.type.DRLResourceType;
import org.drools.workbench.screens.drltext.client.type.DSLRResourceType;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewDrlTextHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DRLTextEditorService> drlTextService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private DRLResourceType resourceTypeDRL;

    @Inject
    private DSLRResourceType resourceTypeDSLR;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private CheckBox useDSLCheckbox = new CheckBox( DRLTextEditorConstants.INSTANCE.useDSL() );

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, CheckBox>( DRLTextEditorConstants.INSTANCE.useDSL(),
                                                    useDSLCheckbox ) );
    }

    @Override
    public String getDescription() {
        return DRLTextEditorConstants.INSTANCE.NewDrlDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( DRLTextEditorResources.INSTANCE.images().typeDRL() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        final boolean useDSL = useDSLCheckbox.getValue();
        final ClientResourceType resourceType = ( useDSL ? resourceTypeDSLR : resourceTypeDRL );
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final boolean useDSL = useDSLCheckbox.getValue();
        final ClientResourceType resourceType = ( useDSL ? resourceTypeDSLR : resourceTypeDRL );

        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        drlTextService.call( getSuccessCallback( presenter ),
                             new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                     buildFileName( baseFileName,
                                                                                                                    resourceType ),
                                                                                                     "",
                                                                                                     "" );
    }

}
