/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.social.hp.client.homepage;

import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.jboss.errai.security.shared.api.Role;
import org.kie.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.kie.workbench.common.screens.social.hp.client.resources.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Set;

@ApplicationScoped
public class DefaultSocialLinkCommandGenerator {

    private PlaceManager placeManager;

    private Event<SocialFileSelectedEvent> socialFileSelectedEvent;

    private SessionInfo sessionInfo;

    private KieWorkbenchACL kieACL;

    @Inject
    public DefaultSocialLinkCommandGenerator( final PlaceManager placeManager,
                                              final Event<SocialFileSelectedEvent> socialFileSelectedEvent,
                                              final SessionInfo sessionInfo,
                                              final KieWorkbenchACL kieACL ) {
        this.placeManager = placeManager;
        this.socialFileSelectedEvent = socialFileSelectedEvent;
        this.sessionInfo = sessionInfo;
        this.kieACL = kieACL;
    }

    public ParameterizedCommand<LinkCommandParams> generateLinkCommand() {

        return new ParameterizedCommand<LinkCommandParams>() {
            @Override
            public void execute( LinkCommandParams parameters ) {
                if ( parameters.isVFSLink() ) {
                    onVFSLinkEvent( parameters );
                } else if ( isOrganizationalUnitEvent( parameters.getEventType() ) ) {
                    onOrganizationalUnitEvent( parameters );
                } else if ( isProjectEvent( parameters.getEventType() ) ) {
                    onProjectEvent( parameters );
                }
            }
        };

    }

    private void onVFSLinkEvent( LinkCommandParams parameters ) {
        if ( hasAccessRightsForFeature( "wb_project_authoring" ) ) {
            placeManager.goTo( "AuthoringPerspective" );
            socialFileSelectedEvent.fire( new SocialFileSelectedEvent( parameters.getEventType(), parameters.getLink() ) );
        } else {
            generateNoRightsPopup();
        }
    }

    private void onOrganizationalUnitEvent( LinkCommandParams parameters ) {
        if ( hasAccessRightsForFeature( "wb_administration" ) ) {
            placeManager.goTo( "AdministrationPerspective" );
            placeManager.goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );
        } else {
            generateNoRightsPopup();
        }
    }

    void generateNoRightsPopup() {
        YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                Constants.INSTANCE.Error_NoAccessRights(),
                null,
                null,
                new Command() {
                    @Override
                    public void execute() {
                        //do nothing, just to show the cancel button.
                    }
                } );
        popup.setClosable( false );
        popup.show();
    }

    private void onProjectEvent( LinkCommandParams parameters ) {
        if ( hasAccessRightsForFeature( "wb_project_authoring" ) ) {
            placeManager.goTo( "AuthoringPerspective" );
            socialFileSelectedEvent.fire( new SocialFileSelectedEvent( parameters.getEventType(), parameters.getLink() ) );
        } else {
            generateNoRightsPopup();
        }
    }

    private boolean isOrganizationalUnitEvent( String eventType ) {

        return OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT.name().equals( eventType ) ||
                OrganizationalUnitEventType.ORGANIZATIONAL_UNIT_UPDATED.name().equals( eventType ) ||
                OrganizationalUnitEventType.REPO_ADDED_TO_ORGANIZATIONAL_UNIT.name().equals( eventType ) ||
                OrganizationalUnitEventType.REPO_REMOVED_FROM_ORGANIZATIONAL_UNIT.name().equals( eventType );

    }

    private boolean isProjectEvent( String eventType ) {
        return ProjectEventType.NEW_PROJECT.name().equals( eventType );
    }

    boolean hasAccessRightsForFeature( String feature ) {
        Set<String> grantedRoles = kieACL.getGrantedRoles( feature );
        if ( sessionInfo != null && sessionInfo.getIdentity() != null && sessionInfo.getIdentity().getRoles() != null ) {
            for ( Role role : sessionInfo.getIdentity().getRoles() ) {
                if ( grantedRoles.contains( role.getName() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

}
