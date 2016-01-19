package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.HashMap;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserServiceAPI;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserHomepageSelectedEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.matchers.Any;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class UserHomePageSidePresenterTest {

    @InjectMocks
    private UserHomePageSidePresenter presenter;

    @Mock
    private SocialUserServiceAPI socialUserServiceAPI;
    private CallerMock<SocialUserServiceAPI> socialUserServiceAPICaller;

    @Mock
    private SocialUserRepositoryAPI socialUserRepositoryAPI;
    private Caller<SocialUserRepositoryAPI> socialUserRepositoryAPICaller;

    @Mock
    private EventSourceMock<UserHomepageSelectedEvent> selectEvent;

    @Mock
    private UserHomePageSidePresenter.View view;

    private SocialUser dora;

    private SocialUser bento;

    @Before
    public void setup() {

        socialUserServiceAPICaller = new CallerMock<SocialUserServiceAPI>( socialUserServiceAPI );

        socialUserRepositoryAPICaller = new CallerMock<SocialUserRepositoryAPI>( socialUserRepositoryAPI );

        presenter.socialUserService = socialUserServiceAPICaller;
        presenter.socialUserRepositoryAPI = socialUserRepositoryAPICaller;
        presenter.selectedEvent = selectEvent;

        presenter.users = new HashMap<String, SocialUser>();
        presenter.loggedUser = new UserImpl( "dora" );

        dora = new SocialUser( "dora" );
        bento = new SocialUser( "bento" );

        presenter.users.put( "dora", dora );
        presenter.users.put( "bento", bento );
    }

    @Test
    public void followUserShouldUpdateUserCache() {

        //clean cache
        presenter.users = new HashMap<String, SocialUser>();

        when( socialUserRepositoryAPI.findSocialUser( bento.getUserName() ) )
                .thenReturn( bento );

        presenter.followUser( bento );

        verify( selectEvent ).fire( any( UserHomepageSelectedEvent.class ) );
        assertEquals( bento, presenter.users.get( "bento" ) );
    }

    @Test
    public void unfollowUserShouldUpdateUserCache() {

        presenter.users = new HashMap<String, SocialUser>();

        when( socialUserRepositoryAPI.findSocialUser( bento.getUserName() ) )
                .thenReturn( bento );

        presenter.unfollowUser( bento );

        verify( selectEvent ).fire( any( UserHomepageSelectedEvent.class ) );
        assertEquals( bento, presenter.users.get( "bento" ) );
    }

    @Test
    public void refreshPageSuccessfullyTest() {
        UserHomePageSidePresenter presenterSpy = spy( presenter );
        doNothing().when( presenterSpy ).refreshPageWidgets( any( String.class ) );

        presenterSpy.setLastUserOnpage( "dora" );
        presenterSpy.refreshPage( "dora" );

        verify( view ).clear();
    }

    @Test
    public void refreshPageUnsuccessfullyTest() {

        presenter.setLastUserOnpage( "bento" );
        presenter.refreshPage( "dora" );

        verify( view, never() ).clear();
    }

    @Test
    public void refreshPageWithoutUsersTest() {

        presenter.users = null;

        presenter.setLastUserOnpage( "bento" );
        presenter.refreshPage( "dora" );

        verify( view, never() ).clear();
    }
}