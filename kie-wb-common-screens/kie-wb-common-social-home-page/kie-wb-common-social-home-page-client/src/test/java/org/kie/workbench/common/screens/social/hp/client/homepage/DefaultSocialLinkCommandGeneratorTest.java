/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DefaultSocialLinkCommandGeneratorTest {


    DefaultSocialLinkCommandGenerator generator;
    private ParameterizedCommand<LinkCommandParams> command;
    private boolean hasAccessRightsForFeature = false;


    @Mock
    private PlaceManager placeManager;
    @Mock
    private EventSourceMock<SocialFileSelectedEvent> eventSourceMock;

    @Before
    public void setUp() throws Exception {
        generator = new DefaultSocialLinkCommandGenerator( placeManager, eventSourceMock, mock( SessionInfo.class ), mock( KieWorkbenchACL.class ) ) {
            @Override
            void generateNoRightsPopup() {
            }

            @Override
            boolean hasAccessRightsForFeature( String feature ) {
                return hasAccessRightsForFeature;
            }
        };

        command = generator.generateLinkCommand();

    }

    @Test
    public void setHasAccessRightsForAuthoringPerspectiveFeatureVFSLink() throws Exception {
        hasAccessRightsForFeature = true;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( true );
        command.execute( parameter );

        verify( placeManager ).goTo( "AuthoringPerspective" );
        verify( eventSourceMock ).fire( any( SocialFileSelectedEvent.class ) );

    }

    @Test
    public void setHasNoAccessRightsForAuthoringPerspectiveFeatureVFSLink() throws Exception {
        hasAccessRightsForFeature = false;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( true );
        command.execute( parameter );

        verify( placeManager, never() ).goTo( "AuthoringPerspective" );
        verify( eventSourceMock, never() ).fire( any( SocialFileSelectedEvent.class ) );

    }


    @Test
    public void setHasAccessRightsForAuthoringPerspectiveFeatureOUEvent() throws Exception {
        hasAccessRightsForFeature = true;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT.name() );
        command.execute( parameter );

        verify( placeManager ).goTo( "AdministrationPerspective" );
        verify( placeManager ).goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );

    }

    @Test
    public void setHasNoAccessRightsForAuthoringPerspectiveFeatureOUEvent() throws Exception {
        hasAccessRightsForFeature = false;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT.name() );
        command.execute( parameter );

        verify( placeManager, never() ).goTo( "AdministrationPerspective" );
        verify( placeManager, never() ).goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );

    }

    @Test
    public void setHasAccessRightsForAuthoringPerspectiveFeatureProjectEvent() throws Exception {
        hasAccessRightsForFeature = true;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( ProjectEventType.NEW_PROJECT.name() );
        command.execute( parameter );

        verify( placeManager ).goTo( "AuthoringPerspective" );
        verify( eventSourceMock ).fire( any( SocialFileSelectedEvent.class ) );

    }

    @Test
    public void setHasNoAccessRightsForAuthoringPerspectiveFeatureProjectEvent() throws Exception {
        hasAccessRightsForFeature = false;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( ProjectEventType.NEW_PROJECT.name() );
        command.execute( parameter );

        verify( placeManager, never() ).goTo( "AdministrationPerspective" );
        verify( placeManager, never() ).goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );

    }


}