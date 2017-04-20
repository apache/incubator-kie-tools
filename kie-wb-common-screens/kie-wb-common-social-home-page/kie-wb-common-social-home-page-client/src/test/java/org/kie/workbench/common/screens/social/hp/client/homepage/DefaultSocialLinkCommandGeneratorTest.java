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
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ext.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.ext.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DefaultSocialLinkCommandGeneratorTest {


    DefaultSocialLinkCommandGenerator generator;
    private ParameterizedCommand<LinkCommandParams> command;
    private boolean hasAccessToPerspective = false;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<SocialFileSelectedEvent> eventSourceMock;

    @Before
    public void setUp() throws Exception {
        generator = new DefaultSocialLinkCommandGenerator( authorizationManager, placeManager, eventSourceMock, mock( SessionInfo.class ) ) {
            @Override
            void generateNoRightsPopup() {
            }

            @Override
            boolean hasAccessToPerspective(String perspectiveId) {
                return hasAccessToPerspective;
            }
        };

        command = generator.generateLinkCommand();

    }

    @Test
    public void setHasAccessRightsForAuthoringPerspectiveFeatureVFSLink() throws Exception {
        hasAccessToPerspective = true;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( true );
        command.execute( parameter );

        verify( eventSourceMock ).fire( any( SocialFileSelectedEvent.class ) );

    }

    @Test
    public void setHasNoAccessRightsForAuthoringPerspectiveFeatureVFSLink() throws Exception {
        hasAccessToPerspective = false;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( true );
        command.execute( parameter );

        verify( placeManager, never() ).goTo( PerspectiveIds.LIBRARY );
        verify( eventSourceMock, never() ).fire( any( SocialFileSelectedEvent.class ) );

    }


    @Test
    public void setHasAccessRightsForAuthoringPerspectiveFeatureOUEvent() throws Exception {
        hasAccessToPerspective = true;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT.name() );
        command.execute( parameter );

        verify( placeManager ).goTo( PerspectiveIds.ADMINISTRATION);
        verify( placeManager ).goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );

    }

    @Test
    public void setHasNoAccessRightsForAuthoringPerspectiveFeatureOUEvent() throws Exception {
        hasAccessToPerspective = false;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT.name() );
        command.execute( parameter );

        verify( placeManager, never() ).goTo( PerspectiveIds.ADMINISTRATION);
        verify( placeManager, never() ).goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );

    }

    @Test
    public void setHasAccessRightsForAuthoringPerspectiveFeatureProjectEvent() throws Exception {
        hasAccessToPerspective = true;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( ProjectEventType.NEW_PROJECT.name() );
        command.execute( parameter );

        verify( eventSourceMock ).fire( any( SocialFileSelectedEvent.class ) );

    }

    @Test
    public void setHasNoAccessRightsForAuthoringPerspectiveFeatureProjectEvent() throws Exception {
        hasAccessToPerspective = false;

        final LinkCommandParams parameter = mock( LinkCommandParams.class );
        when( parameter.isVFSLink() ).thenReturn( false );
        when( parameter.getEventType() ).thenReturn( ProjectEventType.NEW_PROJECT.name() );
        command.execute( parameter );

        verify( placeManager, never() ).goTo( PerspectiveIds.ADMINISTRATION );
        verify( placeManager, never() ).goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );

    }


}