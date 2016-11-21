/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.admin;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.ext.preferences.client.resources.i18n.Constants;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchPerspective(identifier = AdminPagePerspective.IDENTIFIER)
public class AdminPagePerspective {

    public static final String IDENTIFIER = "AdminPagePerspective";

    @Inject
    private TranslationService translationService;

    @Inject
    private PlaceManager placeManager;

    private PerspectiveDefinition perspective;

    private String perspectiveIdentifierToGoBackTo = null;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        if ( perspective == null ) {
            return createPerspectiveDefinition();
        }

        return perspective;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        perspectiveIdentifierToGoBackTo = placeRequest.getParameter( "perspectiveIdentifierToGoBackTo", null );
        perspective = createPerspectiveDefinition();
        configurePerspective( placeRequest );
    }

    PerspectiveDefinition createPerspectiveDefinition() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Admin" );

        return perspective;
    }

    void configurePerspective( final PlaceRequest placeRequest ) {
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( AdminPagePresenter.IDENTIFIER,
                                                                                        placeRequest.getParameters() ) ) );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if ( perspectiveIdentifierToGoBackTo != null ) {
            return MenuFactory
                    .newTopLevelMenu( translationService.format( Constants.AdminPagePerspective_GoBackToThePreviousPage ) )
                    .respondsWith( new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo( perspectiveIdentifierToGoBackTo );
                        }
                    } )
                    .endMenu()
                    .build();
        }

        return null;
    }
}
